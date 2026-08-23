package com.cleannrooster.druids.spells;

import com.cleannrooster.druids.Druids;
import com.cleannrooster.druids.DruidsEffects;
import com.cleannrooster.druids.entity.AncestralGuardianEntity;
import com.cleannrooster.druids.entity.BoundSoulEntity;
import com.cleannrooster.druids.entity.SoulFragmentEntity;
import com.cleannrooster.druids.entity.TotemEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.spell_engine.api.spell.event.SpellHandlers;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;

import java.util.Comparator;
import java.util.List;

import static com.cleannrooster.druids.Druids.MODID;
import static net.spell_engine.api.spell.event.SpellHandlers.registerCustomDelivery;
import static net.spell_engine.api.spell.event.SpellHandlers.registerCustomImpact;

public class SpellsV2 {

    public static void registerDeliveries() {
        registerSpiritBolts();
        registerGuide();
        registerBind();
        registerPainAttunement();
        registerPilgrimage();
        registerAncestralGuardian();
        registerAncestralTotem();
    }

    // -------------------------------------------------------------------------
    // Spirit Bolts
    // On each hit: 10% chance to spawn a soul fragment at the victim's feet.
    // On kill:    100% chance to spawn a soul fragment at the victim's feet.
    // -------------------------------------------------------------------------
    private static void registerSpiritBolts() {
        registerCustomImpact(Identifier.of(MODID, "spirit_bolts"),
                (spell, result, caster, entity, context) -> {
                    if (!(caster.getWorld() instanceof ServerWorld serverWorld)) return new SpellHandlers.ImpactResult(false,false);


                    Entity rawTarget = entity;

                    target :
                    {
                        if (!(rawTarget instanceof LivingEntity living)) break target;
                        // Perform the spell's defined impacts (soul damage)

                        // 100% on kill, 10% on hit
                        boolean killed = living.isDead() || living.getHealth() <= 0.0f;
                        boolean spawnFragment = killed || serverWorld.getRandom().nextFloat() < 0.10f;

                        if (spawnFragment && caster instanceof PlayerEntity player) {
                            spawnFragmentAt(serverWorld, living.getX(), living.getY(), living.getZ(), player);
                        }
                        return new SpellHandlers.ImpactResult(killed,false);

                    }
                    return new SpellHandlers.ImpactResult(false,false);
                });
    }

    // -------------------------------------------------------------------------
    // Guide
    // Finds the nearest soul fragment owned by the caster within 8 blocks,
    // destroys it, and grants one stack of Soul Power for 20 seconds.
    // Each cast adds one amplifier level (+10% soul damage per stack).
    // -------------------------------------------------------------------------
    private static void registerGuide() {
        registerCustomDelivery(Identifier.of(MODID, "guide"),
                (world, spellEntry, caster, targets, context, velocity) -> {
                    if (!(world instanceof ServerWorld serverWorld)) return false;
                    if (!(caster instanceof PlayerEntity player)) return false;

                    SoulFragmentEntity nearest = findNearestFragment(serverWorld, player, 8.0);
                    if (nearest == null) return false;

                    Vec3d fragmentPos = nearest.getPos();
                    nearest.discard();

                    // Soul departure burst
                    serverWorld.spawnParticles(ParticleTypes.SCULK_SOUL,
                            fragmentPos.x, fragmentPos.y + 0.3, fragmentPos.z,
                            20, 0.3, 0.3, 0.3, 0.08);

                    // Stack soul_power: amplifier 0 = +10%, 1 = +20%, ..., 9 = +100%
                    StatusEffectInstance current = player.getStatusEffect(DruidsEffects.SOUL_POWER);
                    int newAmplifier = current != null ? Math.min(current.getAmplifier() + 1, 9) : 0;
                    player.addStatusEffect(new StatusEffectInstance(
                            DruidsEffects.SOUL_POWER,
                            20 * 20,      // 20 seconds
                            newAmplifier,
                            false, true, true
                    ));

                    return true;
                });
    }

    // -------------------------------------------------------------------------
    // Bind
    // Finds the nearest soul fragment owned by the caster within 8 blocks,
    // destroys it, and spawns a Bound Soul to fight for the caster for 20s.
    // -------------------------------------------------------------------------
    private static void registerBind() {
        registerCustomDelivery(Identifier.of(MODID, "bind"),
                (world, spellEntry, caster, targets, context, velocity) -> {
                    if (!(world instanceof ServerWorld serverWorld)) return false;
                    if (!(caster instanceof PlayerEntity player)) return false;

                    SoulFragmentEntity nearest = findNearestFragment(serverWorld, player, 8.0);
                    if (nearest == null) return false;

                    Vec3d spawnPos = nearest.getPos();
                    nearest.discard();

                    // Soul binding burst
                    serverWorld.spawnParticles(ParticleTypes.SCULK_SOUL,
                            spawnPos.x, spawnPos.y + 0.3, spawnPos.z,
                            30, 0.4, 0.4, 0.4, 0.05);

                    // Spawn the bound soul at the fragment's location
                    BoundSoulEntity soul = new BoundSoulEntity(Druids.BOUND_SOUL, world);
                    soul.refreshPositionAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, player.getYaw(), 0);
                    soul.setOwner(player);
                    soul.setTextureVariant(serverWorld.getRandom().nextInt(3));
                    applyMinionScaling(soul, player);
                    serverWorld.spawnEntity(soul);

                    return true;
                });
    }

    // -------------------------------------------------------------------------
    // Pain Attunement
    // Deal up to 10 damage to yourself (never lethal), spawn a soul fragment
    // at your feet.
    // -------------------------------------------------------------------------
    private static void registerPainAttunement() {
        registerCustomDelivery(Identifier.of(MODID, "pain_attunement"),
                (world, spellEntry, caster, targets, context, velocity) -> {
                    if (!(world instanceof ServerWorld serverWorld)) return false;
                    if (!(caster instanceof PlayerEntity player)) return false;

                    // Cap damage so the player always survives with â‰¥0.5 HP
                    float damage = Math.min(5.0f, player.getHealth() - 0.5f);
                    if (damage <= 0) return false;

                    player.damage(player.getDamageSources().magic(), damage);

                    spawnFragmentAt(serverWorld,
                            player.getX(), player.getY(), player.getZ(), player);

                    return true;
                });
    }

    // -------------------------------------------------------------------------
    // Pilgrimage
    // Guide ALL nearby soul fragments owned by the caster to the afterlife.
    // Grants Regeneration 5 and Soul Power (Pilgrimage) for durations scaled
    // by the number of fragments guided (3s and 5s per fragment respectively).
    // -------------------------------------------------------------------------
    private static void registerPilgrimage() {
        registerCustomDelivery(Identifier.of(MODID, "pilgrimage"),
                (world, spellEntry, caster, targets, context, velocity) -> {
                    if (!(world instanceof ServerWorld serverWorld)) return false;
                    if (!(caster instanceof PlayerEntity player)) return false;

                    List<SoulFragmentEntity> fragments = serverWorld.getEntitiesByType(
                            Druids.SOUL_FRAGMENT,
                            player.getBoundingBox().expand(16.0),
                            frag -> player.equals(frag.getOwner())
                    );

                    if (fragments.isEmpty()) return false;

                    int count = fragments.size();

                    // Burst particles at each fragment, then discard
                    for (SoulFragmentEntity frag : fragments) {
                        serverWorld.spawnParticles(ParticleTypes.SCULK_SOUL,
                                frag.getX(), frag.getY() + 0.3, frag.getZ(),
                                12, 0.3, 0.3, 0.3, 0.1);
                        frag.discard();
                    }

                    int regenTicks       = count * 3 * 20;  // 3s per fragment
                    int soulPowerTicks   = count * 3 * 20;  // 3s per fragment

                    // Regeneration 5 (amplifier 4 = Regen V)
                    player.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.REGENERATION, regenTicks, 4, false, true, true));

                    // Soul Power (Pilgrimage) â€” +100% soul damage, flat
                    player.addStatusEffect(new StatusEffectInstance(
                            DruidsEffects.SOUL_POWER_PILGRIMAGE, soulPowerTicks, 0, false, true, true));

                    return true;
                });
    }

    // -------------------------------------------------------------------------
    // Ancestral Guardian
    // At most ONE guardian per player is allowed.
    // â€¢ If no guardian exists â†’ spawn one.
    // â€¢ If one already exists â†’ teleport it back to the caster and reset its
    //   lifespan timer, so the spell acts as a "recall" when needed.
    // -------------------------------------------------------------------------
    private static void registerAncestralGuardian() {
        registerCustomDelivery(Identifier.of(MODID, "ancestral_guardian"),
                (world, spellEntry, caster, targets, context, velocity) -> {
                    if (!(world instanceof ServerWorld serverWorld)) return false;
                    if (!(caster instanceof PlayerEntity player)) return false;

                    // Search generously â€” the guardian may have strayed while fighting
                    List<AncestralGuardianEntity> existing = serverWorld.getEntitiesByType(
                            Druids.ANCESTRAL_GUARDIAN,
                            player.getBoundingBox().expand(256.0),
                            g -> player.equals(g.getOwner())
                    );

                    if (!existing.isEmpty()) {
                        AncestralGuardianEntity guardian = existing.get(0);

                        // Departure burst at current position
                        serverWorld.spawnParticles(ParticleTypes.SCULK_SOUL,
                                guardian.getX(), guardian.getY() + 0.5, guardian.getZ(),
                                25, 0.5, 0.5, 0.5, 0.1);

                        // Teleport to caster, clear AI state, reset lifespan
                        guardian.teleport(player.getX(), player.getY(), player.getZ(),true);
                        guardian.getNavigation().stop();
                        guardian.setTarget(null);
                        guardian.age = 0;

                        // Arrival burst at new position
                        serverWorld.spawnParticles(ParticleTypes.SCULK_SOUL,
                                player.getX(), player.getY() + 0.5, player.getZ(),
                                25, 0.5, 0.5, 0.5, 0.1);

                        return true;
                    }

                    // No existing guardian â€” spawn a fresh one
                    AncestralGuardianEntity guardian =
                            new AncestralGuardianEntity(Druids.ANCESTRAL_GUARDIAN, world);
                    guardian.refreshPositionAndAngles(
                            player.getX(), player.getY(), player.getZ(), player.getYaw(), 0);
                    guardian.setOwner(player);
                    applyMinionScaling(guardian, player);
                    serverWorld.spawnEntity(guardian);

                    return true;
                });
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Ancestral Totem
    // Raycasts from the player's eyes (2Ã— default block reach) to find a
    // placement surface, then spawns a TotemEntity there.  Stats (health,
    // duration) scale with the caster's soul attribute; shockwave damage
    // mirrors the caster's current melee attack damage.
    // -------------------------------------------------------------------------
    private static void registerAncestralTotem() {
        registerCustomDelivery(Identifier.of(MODID, "ancestral_totem"),
                (world, spellEntry, caster, targets, context, velocity) -> {
                    if (!(world instanceof ServerWorld serverWorld)) return false;
                    if (!(caster instanceof PlayerEntity player)) return false;

                    // 2Ã— the default 4.5-block interaction reach â‰ˆ 9 blocks
                    final double REACH = 9.0;
                    HitResult hit = player.raycast(REACH, 0.0f, false);

                    // Resolve a spawn position on the surface the player is aiming at.
                    // If the ray hits a block, place the totem on the struck face.
                    // If it hits only air, place it at the far end of the ray.
                    Vec3d spawnPos;
                    if (hit instanceof BlockHitResult blockHit) {
                        var placeBlock = blockHit.getBlockPos().offset(blockHit.getSide());
                        // ofCenter gives (x+0.5, y+0.5, z+0.5); -0.5 Y â†’ bottom of that block
                        spawnPos = Vec3d.ofCenter(placeBlock).add(0, -0.5, 0);
                    } else {
                        spawnPos = hit.getPos();
                    }

                    TotemEntity totem = new TotemEntity(Druids.SUMMON_TOTEM, world);
                    totem.refreshPositionAndAngles(
                            spawnPos.x, spawnPos.y, spawnPos.z, player.getYaw(), 0);
                    totem.setOwner(player);
                    totem.initializeForCaster(player);
                    serverWorld.spawnEntity(totem);

                    // Summon burst at the placement point
                    serverWorld.spawnParticles(ParticleTypes.SCULK_SOUL,
                            spawnPos.x, spawnPos.y + 0.5, spawnPos.z,
                            20, 0.3, 0.3, 0.3, 0.1);

                    return true;
                });
    }

    /**
     * Scales a freshly-created minion's health and attack damage by the caster's
     * Soul Power stacks.  8 stacks â†’ +100% (i.e. 2Ã— base).  Must be called
     * BEFORE {@code spawnEntity} so the entity enters the world with correct HP.
     *
     * Formula: multiplier = 1 + (amplifier + 1) / 8
     *   - no Soul Power   (amplifier âˆ’1) â†’ Ã—1.00  (no change)
     *   - 1 stack         (amplifier  0) â†’ Ã—1.125
     *   - 4 stacks        (amplifier  3) â†’ Ã—1.50
     *   - 8 stacks        (amplifier  7) â†’ Ã—2.00
     */
    private static void applyMinionScaling(PathAwareEntity minion, PlayerEntity caster) {
        var soulPower = SpellPower.getSpellPower(SpellSchools.SOUL,caster);
        double randomValue = soulPower.randomValue();
        double multiplier = Math.max(1.0, 1F+randomValue / 8.0);

        var healthAttr = minion.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        var dmgAttr    = minion.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (healthAttr != null) healthAttr.setBaseValue(healthAttr.getBaseValue() * multiplier);
        if (dmgAttr    != null) dmgAttr.setBaseValue(dmgAttr.getBaseValue() * multiplier);
        minion.setHealth(minion.getMaxHealth()); // fill to the new maximum
    }

    /** Spawns a SoulFragment at the given world position, owned by {@code owner}. */
    private static void spawnFragmentAt(ServerWorld world, double x, double y, double z, PlayerEntity owner) {
        SoulFragmentEntity fragment = new SoulFragmentEntity(Druids.SOUL_FRAGMENT, world);
        fragment.refreshPositionAndAngles(x, y, z, 0, 0);
        fragment.setOwner(owner);
        world.spawnEntity(fragment);
    }

    /**
     * Returns the nearest SoulFragment owned by {@code owner} within {@code range} blocks,
     * or {@code null} if none is found.
     */
    private static SoulFragmentEntity findNearestFragment(ServerWorld world, PlayerEntity owner, double range) {
        List<SoulFragmentEntity> fragments = world.getEntitiesByType(
                Druids.SOUL_FRAGMENT,
                owner.getBoundingBox().expand(range),
                frag -> owner.equals(frag.getOwner())
        );
        return fragments.stream()
                .min(Comparator.comparingDouble(f -> f.squaredDistanceTo(owner)))
                .orElse(null);
    }
}
