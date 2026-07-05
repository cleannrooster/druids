package com.cleannrooster.druids.entity;

import com.cleannrooster.druids.client.entity.AncestralGuardianAnimationProvider;
import com.cleannrooster.druids.client.entity.TotemAnimationProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spell_engine.internals.target.EntityRelations;
import net.spell_engine.internals.target.SpellTarget;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TotemEntity extends MobEntity implements Ownable {

    // ── Phase constants (public so the client renderer can drive animations) ─
    public static final int PHASE_SPAWNING = 0;   // 1.5 s startup
    public static final int PHASE_CHARGING = 1;   // 3.0 s charge
    public static final int PHASE_WINDDOWN = 2;   // 0.5 s after shockwave
    public final TotemAnimationProvider.MinibossAnimationDispatcher dispatcher;

    private static final int SPAWN_DURATION    = 30;   // ticks
    private static final int CHARGE_DURATION   = 60;   // ticks
    private static final int WINDDOWN_DURATION = 10;   // ticks
    private static final double SHOCKWAVE_RADIUS = 8.0;

    // Synced to client for animation
    private static final TrackedData<Integer> OWNER_ID =
            DataTracker.registerData(TotemEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> PHASE =
            DataTracker.registerData(TotemEntity.class, TrackedDataHandlerRegistry.INTEGER);

    // Server-side only — not needed by the client
    private int phaseTicks        = 0;
    private int lifetimeTicks     = 0;
    private int maxLifetimeTicks  = 300;   // 15 s default; scaled by soul power at spawn
    private float shockwaveDamage = 5.0f;  // set from caster's attack damage at spawn
    private boolean durationExpired = false;

    public TotemEntity(EntityType<? extends MobEntity> type, World world) {
        super(type, world);
        this.dispatcher = new TotemAnimationProvider.MinibossAnimationDispatcher(this);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Called by SpellsV2 immediately before spawnEntity().
    // Reads soul power attribute to scale health / duration, and locks in the
    // caster's current weapon damage for every future shockwave.
    // ─────────────────────────────────────────────────────────────────────────
    public void initializeForCaster(PlayerEntity caster) {
        // Soul attribute value: base 1.0; each Guide stack adds 0.1 (ADD_MULTIPLIED_BASE)
        // At 8 stacks → 1.8 → 80 % bonus to health and duration.
        double soulPower = Math.max(1.0D,1.0D+SpellPower.getSpellPower(SpellSchools.SOUL,caster).randomValue()*0.1D);

        double scaledHealth = 20.0 * soulPower;
        maxLifetimeTicks = (int) (300 * soulPower); // 15 s × soulPower

        var healthAttr = getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (healthAttr != null) healthAttr.setBaseValue(scaledHealth);
        this.setHealth((float) scaledHealth);

        // Shockwave deals the caster's full melee attack damage (weapon + modifiers)
        shockwaveDamage = (float) caster.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Goals — none. Totems never move.
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    protected void initGoals() {}

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(OWNER_ID, -1);
        builder.add(PHASE, PHASE_SPAWNING);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // State machine
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void tick() {
        if(firstUpdate){
            this.dispatcher.rise(this);
        }
        super.tick();
        if (getWorld().isClient()) return;

        phaseTicks++;
        lifetimeTicks++;

        // Mark duration expired — do NOT discard yet; the cycle must finish.
        if (!durationExpired && lifetimeTicks >= maxLifetimeTicks) {
            durationExpired = true;
        }

        switch (dataTracker.get(PHASE)) {
            case PHASE_SPAWNING -> {
                // Play the summon sound on the very first server tick
                if (phaseTicks == 1 && getWorld() instanceof ServerWorld sw) {
                    sw.playSound(null, getX(), getY(), getZ(),
                            SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON,
                            SoundCategory.NEUTRAL, 1.0f, 0.85f);
                }
                if (phaseTicks >= SPAWN_DURATION) {
                    this.dispatcher.charge(this);
                    // Play the charge wind-up as soon as the first cycle begins
                    if (getWorld() instanceof ServerWorld sw) {
                        sw.playSound(null, getX(), getY(), getZ(),
                                SoundEvents.ENTITY_WARDEN_SONIC_CHARGE,
                                SoundCategory.NEUTRAL, 0.75f, 1.0f);
                    }
                    transitionTo(PHASE_CHARGING);
                }
            }
            case PHASE_CHARGING -> {
                if (phaseTicks >= CHARGE_DURATION) {
                    this.dispatcher.smash(this);
                    performShockwave();
                    transitionTo(PHASE_WINDDOWN);
                }
            }
            case PHASE_WINDDOWN -> {
                if (phaseTicks >= WINDDOWN_DURATION) {
                    if (durationExpired) {
                        // Full cycle finished after duration elapsed — despawn
                        this.discard();
                    } else {
                        this.dispatcher.charge(this);
                        // Play the charge wind-up for every subsequent cycle
                        if (getWorld() instanceof ServerWorld sw) {
                            sw.playSound(null, getX(), getY(), getZ(),
                                    SoundEvents.ENTITY_WARDEN_SONIC_CHARGE,
                                    SoundCategory.NEUTRAL, 0.75f, 1.0f);
                        }
                        transitionTo(PHASE_CHARGING);
                    }
                }
            }
        }
    }

    private void transitionTo(int newPhase) {
        dataTracker.set(PHASE, newPhase);
        phaseTicks = 0;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Shockwave — damages and pushes all non-friendly entities in range
    // ─────────────────────────────────────────────────────────────────────────
    private void performShockwave() {
        if (!(getWorld() instanceof ServerWorld sw)) return;

        Entity owner = getOwner();
        if(!(owner instanceof PlayerEntity p)) return;
        List<LivingEntity> targets = sw.getEntitiesByClass(
                LivingEntity.class,
                getBoundingBox().expand(SHOCKWAVE_RADIUS),
                e -> e != owner
                        && !MinionGoals.isFriendlyMinion(e, owner)
                        && distanceTo(e) <= SHOCKWAVE_RADIUS
                        && EntityRelations.actionAllowed(SpellTarget.FocusMode.AREA, SpellTarget.Intent.HARMFUL,p,e));


        for (LivingEntity target : targets) {
            target.timeUntilRegen = 0;
            target.damage(getDamageSources().magic(), shockwaveDamage);

            // Outward radial push
            Vec3d away = target.getPos().subtract(this.getPos());
            if (away.lengthSquared() > 0) {
                away = away.normalize().multiply(0.7);
                target.addVelocity(away.x, 0.35, away.z);
                target.velocityModified = true;
            }
        }

        spawnShockwaveParticles(sw);

        // Deep, resonant boom — heard well beyond the AoE radius
        sw.playSound(null, getX(), getY(), getZ(),
                SoundEvents.ENTITY_WARDEN_SONIC_BOOM,
                SoundCategory.NEUTRAL, 1.2f, 0.8f);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Shockwave particle effect
    //
    // Three layers give a clear expanding boundary:
    //   1. Directed ring: particles start at centre and fly outward.
    //      count=0 → spawnParticles sends ONE directed particle with velocity
    //      (dx * speed, dy * speed, dz * speed).  At speed ≈ 0.52, sculk_soul
    //      particles (~16 tick lifetime) travel ~8 blocks — exactly the radius.
    //   2. Edge ring: dense burst at the 8-block boundary marks the hard limit.
    //   3. Centre burst: immediate tactile feedback at the impact point.
    // ─────────────────────────────────────────────────────────────────────────
    private void spawnShockwaveParticles(ServerWorld sw) {
        final double cx = getX();
        final double cy = getY() + 0.1;
        final double cz = getZ();

        // ── Layer 1: expanding ring from centre ───────────────────────────────
        final int   RING_COUNT     = 56;
        final double OUTWARD_SPEED = 0.52;  // blocks/tick → ~8.3 blk in 16 t
        for (int i = 0; i < RING_COUNT; i++) {
            double angle = (2.0 * Math.PI * i) / RING_COUNT;
            double dx    = Math.cos(angle);
            double dz    = Math.sin(angle);
            // Ground-level wave crest
            sw.spawnParticles(ParticleTypes.SCULK_SOUL,
                    cx, cy, cz,
                    0, dx, 0.02, dz, OUTWARD_SPEED);
            // Slightly elevated duplicate — gives the ring visible vertical depth
            if (i % 2 == 0) {
                sw.spawnParticles(ParticleTypes.SCULK_SOUL,
                        cx, cy + 0.55, cz,
                        0, dx, 0.0, dz, OUTWARD_SPEED * 0.96);
            }
        }

        // ── Layer 2: dense burst ring at the hard 8-block boundary ────────────
        final int EDGE_COUNT = 36;
        for (int i = 0; i < EDGE_COUNT; i++) {
            double angle = (2.0 * Math.PI * i) / EDGE_COUNT;
            sw.spawnParticles(ParticleTypes.SCULK_SOUL,
                    cx + SHOCKWAVE_RADIUS * Math.cos(angle),
                    cy,
                    cz + SHOCKWAVE_RADIUS * Math.sin(angle),
                    3, 0.2, 0.35, 0.2, 0.02);
        }

        // ── Layer 3: centre burst for immediate impact feedback ───────────────
        sw.spawnParticles(ParticleTypes.SCULK_SOUL,
                cx, cy + 0.2, cz,
                18, 0.3, 0.6, 0.3, 0.06);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Owner (Ownable) — also used by MinionGoals.isFriendlyMinion so that
    // bound souls / guardians won't attack the totem.
    // ─────────────────────────────────────────────────────────────────────────
    public void setOwner(@Nullable Entity entity) {
        if (entity != null) dataTracker.set(OWNER_ID, entity.getId());
    }

    @Nullable
    @Override
    public Entity getOwner() {
        int id = dataTracker.get(OWNER_ID);
        return id != -1 ? getWorld().getEntityById(id) : null;
    }

    /** Current phase; one of {@link #PHASE_SPAWNING}, {@link #PHASE_CHARGING}, {@link #PHASE_WINDDOWN}. */
    public int getPhase() { return dataTracker.get(PHASE); }

    // ─────────────────────────────────────────────────────────────────────────
    // Damage filter
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public boolean damage(DamageSource source, float amount) {
        Entity attacker = source.getAttacker();
        if (attacker != null && (attacker.equals(getOwner())
                || MinionGoals.isFriendlyMinion(attacker, getOwner()))) {
            return false;
        }
        return super.damage(source, amount);
    }

    @Override public boolean isPushable()  { return false; }
    @Override public boolean isCollidable() { return true; }

    // ─────────────────────────────────────────────────────────────────────────
    // Attributes
    // ─────────────────────────────────────────────────────────────────────────
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // NBT persistence
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("OwnerId",          dataTracker.get(OWNER_ID));
        nbt.putInt("Phase",            dataTracker.get(PHASE));
        nbt.putInt("PhaseTicks",       phaseTicks);
        nbt.putInt("LifetimeTicks",    lifetimeTicks);
        nbt.putInt("MaxLifetimeTicks", maxLifetimeTicks);
        nbt.putFloat("ShockwaveDamage", shockwaveDamage);
        nbt.putBoolean("DurationExpired", durationExpired);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("OwnerId"))          dataTracker.set(OWNER_ID, nbt.getInt("OwnerId"));
        if (nbt.contains("Phase"))            dataTracker.set(PHASE,    nbt.getInt("Phase"));
        if (nbt.contains("PhaseTicks"))       phaseTicks       = nbt.getInt("PhaseTicks");
        if (nbt.contains("LifetimeTicks"))    lifetimeTicks    = nbt.getInt("LifetimeTicks");
        if (nbt.contains("MaxLifetimeTicks")) maxLifetimeTicks = nbt.getInt("MaxLifetimeTicks");
        if (nbt.contains("ShockwaveDamage"))  shockwaveDamage  = nbt.getFloat("ShockwaveDamage");
        if (nbt.contains("DurationExpired"))  durationExpired  = nbt.getBoolean("DurationExpired");
    }
}
