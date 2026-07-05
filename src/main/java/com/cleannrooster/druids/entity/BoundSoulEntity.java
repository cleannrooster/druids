package com.cleannrooster.druids.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class BoundSoulEntity extends PathAwareEntity implements Ownable {

    private static final int LIFESPAN_TICKS = 20 * 20; // 20 seconds

    private static final TrackedData<Integer> OWNER_ID =
            DataTracker.registerData(BoundSoulEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final TrackedData<Integer> TEXTURE_VARIANT =
            DataTracker.registerData(BoundSoulEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public BoundSoulEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.add(2, new FollowOwnerGoal(this, 1.2, 12.0f, 3.0f));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new MinionGoals.DefendOwnerGoal(this));
        this.targetSelector.add(3, new MinionGoals.CopyOwnerTargetGoal(this));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(OWNER_ID, -1);
        builder.add(TEXTURE_VARIANT, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient()) {
            // Ambient soul particles
            if (this.age % 8 == 0 && this.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(
                        ParticleTypes.SCULK_SOUL,
                        this.getX(), this.getY() + 0.5, this.getZ(),
                        1, 0.2, 0.2, 0.2, 0.01
                );
            }
            // Expire after lifespan
            if (this.age > LIFESPAN_TICKS) {
                if (this.getWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(
                            ParticleTypes.SCULK_SOUL,
                            this.getX(), this.getY() + 0.5, this.getZ(),
                            25, 0.4, 0.4, 0.4, 0.1
                    );
                }
                this.discard();
            }
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        Entity attacker = source.getAttacker();
        // Immune to the owner's damage and to damage from sister minions.
        // Blocking here also prevents RevengeGoal from recording sister minions
        // as attackers, so no friendly-fire revenge targeting can occur.
        if (attacker != null && (attacker.equals(this.getOwner())
                || MinionGoals.isFriendlyMinion(attacker, this.getOwner()))) {
            return false;
        }
        return super.damage(source, amount);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.32)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0);
    }

    public void setTextureVariant(int variant) {
        this.dataTracker.set(TEXTURE_VARIANT, variant);
    }

    public int getTextureVariant() {
        return this.dataTracker.get(TEXTURE_VARIANT);
    }

    public void setOwner(@Nullable Entity entity) {
        if (entity != null) {
            this.dataTracker.set(OWNER_ID, entity.getId());
        }
    }

    @Nullable
    @Override
    public Entity getOwner() {
        int id = this.dataTracker.get(OWNER_ID);
        return id != -1 ? this.getWorld().getEntityById(id) : null;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("OwnerId", this.dataTracker.get(OWNER_ID));
        nbt.putInt("TextureVariant", this.dataTracker.get(TEXTURE_VARIANT));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("OwnerId")) {
            this.dataTracker.set(OWNER_ID, nbt.getInt("OwnerId"));
        }
        if (nbt.contains("TextureVariant")) {
            this.dataTracker.set(TEXTURE_VARIANT, nbt.getInt("TextureVariant"));
        }
    }

    // -------------------------------------------------------------------------
    // Inner goal: follow owner when too far away, yield to attack if targeting
    // -------------------------------------------------------------------------
    private static class FollowOwnerGoal extends Goal {

        private final BoundSoulEntity soul;
        private final double speed;
        private final float startFollowDistance;
        private final float stopFollowDistance;

        FollowOwnerGoal(BoundSoulEntity soul, double speed, float startFollowDistance, float stopFollowDistance) {
            this.soul = soul;
            this.speed = speed;
            this.startFollowDistance = startFollowDistance;
            this.stopFollowDistance = stopFollowDistance;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            Entity owner = soul.getOwner();
            // Don't follow if actively attacking something
            if (soul.getTarget() != null) return false;
            return owner != null && owner.distanceTo(soul) > startFollowDistance;
        }

        @Override
        public boolean shouldContinue() {
            Entity owner = soul.getOwner();
            if (soul.getTarget() != null) return false;
            return owner != null && owner.distanceTo(soul) > stopFollowDistance;
        }

        @Override
        public void start() {
            Entity owner = soul.getOwner();
            if (owner != null) {
                soul.getNavigation().startMovingTo(owner, speed);
            }
        }

        @Override
        public void stop() {
            soul.getNavigation().stop();
        }

        @Override
        public void tick() {
            Entity owner = soul.getOwner();
            if (owner != null) {
                soul.getLookControl().lookAt(owner, 10.0f, soul.getMaxLookPitchChange());
                if (soul.getNavigation().isIdle()) {
                    soul.getNavigation().startMovingTo(owner, speed);
                }
            }
        }
    }
}
