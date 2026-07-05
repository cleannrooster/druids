package com.cleannrooster.druids.entity;

import com.cleannrooster.druids.client.entity.AncestralGuardianAnimationProvider;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class AncestralGuardianEntity extends PathAwareEntity implements Ownable {

    private static final int LIFESPAN_TICKS = 60 * 20; // 60 seconds

    private static final TrackedData<Integer> OWNER_ID =
            DataTracker.registerData(AncestralGuardianEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public AncestralGuardianEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.dispatcher = new AncestralGuardianAnimationProvider.MinibossAnimationDispatcher(this);

    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.3, false));
        this.goalSelector.add(2, new BodyBlockGoal(this, 1.3, 12.0f, 2.5f));
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
    }

    @Override
    public void tick() {
        super.tick();
        animTick();
        if (!this.getWorld().isClient()) {
            if (this.age % 10 == 0 && this.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(
                        ParticleTypes.SCULK_SOUL,
                        this.getX(), this.getY() + 0.5, this.getZ(),
                        1, 0.2, 0.2, 0.2, 0.01
                );
            }
            if (this.age > LIFESPAN_TICKS) {
                if (this.getWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(
                            ParticleTypes.SCULK_SOUL,
                            this.getX(), this.getY() + 0.5, this.getZ(),
                            30, 0.5, 0.5, 0.5, 0.12
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
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0);
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
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("OwnerId")) {
            this.dataTracker.set(OWNER_ID, nbt.getInt("OwnerId"));
        }
    }

    // -------------------------------------------------------------------------
    // Body Block Goal
    // When a target exists: position between owner and target to intercept.
    // When no target: follow owner if too far away.
    // -------------------------------------------------------------------------
    private static class BodyBlockGoal extends Goal {

        private final AncestralGuardianEntity guardian;
        private final double speed;
        private final float startFollowDistance;
        private final float stopFollowDistance;

        BodyBlockGoal(AncestralGuardianEntity guardian, double speed,
                      float startFollowDistance, float stopFollowDistance) {
            this.guardian = guardian;
            this.speed = speed;
            this.startFollowDistance = startFollowDistance;
            this.stopFollowDistance = stopFollowDistance;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            Entity owner = guardian.getOwner();
            if (owner == null) return false;
            // Active when we have a target to intercept, or when straying from owner
            return guardian.getTarget() != null
                    || owner.distanceTo(guardian) > startFollowDistance;
        }

        @Override
        public boolean shouldContinue() {
            Entity owner = guardian.getOwner();
            if (owner == null) return false;
            return guardian.getTarget() != null
                    || owner.distanceTo(guardian) > stopFollowDistance;
        }

        @Override
        public void start() {
            tickGoal();
        }

        @Override
        public void stop() {
            guardian.getNavigation().stop();
        }

        @Override
        public void tick() {
            tickGoal();
        }

        private void tickGoal() {
            Entity owner = guardian.getOwner();
            if (owner == null) return;

            LivingEntity target = guardian.getTarget();

            if (target != null) {
                // Stand between owner and the threat, ~40% of the way from owner to target
                Vec3d ownerPos = owner.getPos();
                Vec3d targetPos = target.getPos();
                Vec3d toTarget = targetPos.subtract(ownerPos);
                double dist = toTarget.length();

                if (dist > 0.1) {
                    Vec3d direction = toTarget.normalize();
                    // Clamp intercept point to at most 2 blocks from owner
                    double interceptDist = Math.min(dist * 0.4, 2.0);
                    Vec3d goal = ownerPos.add(direction.multiply(interceptDist));
                    guardian.getNavigation().startMovingTo(goal.x, goal.y, goal.z, speed);
                }
                guardian.getLookControl().lookAt(target, 30.0f, guardian.getMaxLookPitchChange());
            } else {
                // No threat — follow owner
                guardian.getNavigation().startMovingTo(owner, speed);
                guardian.getLookControl().lookAt(owner, 10.0f, guardian.getMaxLookPitchChange());
            }
        }
    }
    public boolean wasRunningLastTick;
    public float smoothedSpeed = 0.2F;
    Runnable animationRunner;
    Runnable prevAnimationRunner;
    boolean wasRunning;
    boolean isRunning;
    AnimState prevState;
    enum AnimState {
        IDLE, IDLE_AGGRO,
        WALK, WALK_AGGRO,
        RUN,
        DOWN
    }
    public final AncestralGuardianAnimationProvider.MinibossAnimationDispatcher dispatcher;

    public void animTick(){

        if (this.getWorld().isClient) {
            float horizontalSpeed = (float)this.getVelocity().horizontalLength();

            // Asymmetric filter: snap up quickly when accelerating, decay slowly when stopping.
            // Fast ramp-up (0.6) makes new movement feel responsive; slow decay (0.2) prevents
            // a pop-to-idle on momentary zero-velocity frames and smooths the stop tail-off.
            float alpha = horizontalSpeed > this.smoothedSpeed ? 0.6F : 0.2F;
            this.smoothedSpeed = MathHelper.lerp(alpha, this.smoothedSpeed, horizontalSpeed);
            float animSpeed = this.smoothedSpeed / 0.2F;

            // Both checks use smoothedSpeed so a single dropped-velocity frame can't trigger
            // a state change; the hysteresis band on isRunning stays clear of thrash.
            boolean isMoving = this.smoothedSpeed > 0.03F;

            this.isRunning = this.wasRunning
                    ? this.smoothedSpeed > 0.15F
                    : this.smoothedSpeed >= 0.20F;

            this.wasRunning = this.isRunning;
            AnimState newState;
             if (isMoving) {
                if (this.isAttacking()) {
                    newState = this.isRunning ? AnimState.RUN : AnimState.WALK_AGGRO;
                } else {
                    newState =  AnimState.WALK;
                }
            } else {
                newState = this.isAttacking() ? AnimState.IDLE_AGGRO : AnimState.IDLE;
            }
            if (newState != this.prevState) {
                switch (newState) {
                    case RUN -> dispatcher.run(animSpeed);
                    case WALK_AGGRO -> dispatcher.walkAggro(animSpeed);
                    case WALK -> dispatcher.walk(animSpeed);
                    case IDLE_AGGRO -> dispatcher.idleAggro();
                    case IDLE -> dispatcher.idle();
                    case DOWN -> dispatcher.setDown();
                }
                this.prevState = newState;
            }

        }
    }
}
