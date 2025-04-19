package com.cleannrooster.druids.entity;

import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class SwimAroundGoalZap extends WanderAroundGoal {
    public SwimAroundGoalZap(PathAwareEntity pathAwareEntity, double d, int i) {
        super(pathAwareEntity, d, i);
    }

    @Nullable
    protected Vec3d getWanderTarget() {
        return find(this.mob, 10, 7);
    }

    @Nullable
    public static Vec3d find(PathAwareEntity entity, int horizontalRange, int verticalRange) {
        Vec3d vec3d = null;
        if (entity instanceof SpiderLightning lightning && lightning.originalLocation != null && lightning.originalRot != null) {

           vec3d  = lightning.getPos().add(lightning.originalRot.multiply(4));

            for (int i = 0; vec3d != null && !entity.getWorld().getBlockState(BlockPos.ofFloored(vec3d)).canPathfindThrough(NavigationType.WATER) && i++ < 10; vec3d = NoPenaltyTargeting.find(entity, horizontalRange, verticalRange)) {
                if (vec3d.subtract(lightning.originalLocation).normalize().dotProduct((lightning).originalRot) > 0.707) {
                    break;

                }
            }
        }

        return vec3d;
    }
}
