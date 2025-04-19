package com.cleannrooster.druids.entity;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class ZapGoal extends WanderAroundGoal {
    public static final int DEFAULT_CHANCE = 120;
    protected final PathAwareEntity mob;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected  double speed;
    protected int chance;
    protected boolean ignoringChance;
    private final boolean canDespawn;

    public ZapGoal(PathAwareEntity mob, double speed) {
        this(mob, speed, 120);
        this.speed = speed;
    }
    @Nullable
    protected Vec3d getWanderTarget() {
        if (this.mob.isInsideWaterOrBubbleColumn()) {
            Vec3d vec3d = find(this.mob, 15, 7);
            return vec3d == null ? super.getWanderTarget() : vec3d;
        } else {
            return  find(this.mob, 10, 7);
        }
    }
    public ZapGoal(PathAwareEntity mob, double speed, int chance) {
        this(mob, speed, chance, true);
    }

    public ZapGoal(PathAwareEntity entity, double speed, int chance, boolean canDespawn) {
        super(entity,speed);
        this.mob = entity;
        this.speed = speed;
        this.chance = chance;
        this.canDespawn = canDespawn;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    public boolean canStart() {
        if (this.mob.hasControllingPassenger()) {
            return false;
        } else {


            Vec3d vec3d = this.getWanderTarget();
            if (vec3d == null) {
                return false;
            } else {
                this.targetX = vec3d.x;
                this.targetY = vec3d.y;
                this.targetZ = vec3d.z;
                this.ignoringChance = false;
                return true;
            }
        }
    }


    public boolean shouldContinue() {
        return true;
    }

    @Override
    public void tick() {
            Vec3d vec3d = this.getWanderTarget();
            if (vec3d == null) {

            } else {
                this.targetX = vec3d.x;
                this.targetY = vec3d.y;
                this.targetZ = vec3d.z;
                this.ignoringChance = false;
                this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);

            }

        super.tick();
    }

    public void start() {
    }

    public void stop() {
        this.mob.getNavigation().stop();
        super.stop();
    }
    @Nullable
    public static Vec3d find(PathAwareEntity entity, int horizontalRange, int verticalRange) {
        Vec3d vec3d = null;
        if (entity instanceof TotemTrail lightning && lightning.originalLocation != null && lightning.originalRot != null) {

            vec3d  = lightning.getPos().add(lightning.originalRot.multiply(4));

            for (int i = 0; vec3d != null && !entity.getWorld().getBlockState(BlockPos.ofFloored(vec3d)).canPathfindThrough(NavigationType.WATER) && i++ < 10; vec3d = NoPenaltyTargeting.find(entity, horizontalRange, verticalRange)) {
                if (vec3d.subtract(lightning.originalLocation).normalize().dotProduct((lightning).originalRot) > 0.707) {
                    break;

                }
            }
        }
        if (entity instanceof SpectralGoat lightning && lightning.getOwner() != null) {


             vec3d  = lightning.getPos().add(lightning.originalRot.subtract(0,lightning.originalRot.getY(),0).normalize().multiply(4));
            if(lightning.getTarget() != null) {
                vec3d = lightning.getTarget().getPos();
            }
            lightning.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET,vec3d);

            lightning.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES,vec3d);
            if(lightning.getNavigation().isIdle()){
                lightning.getNavigation().startMovingTo(vec3d.getX(), vec3d.getY(), vec3d.getZ(), 0.8F);
            }

        }

        return vec3d;
    }
    public void ignoreChanceOnce() {
        this.ignoringChance = true;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }
}
