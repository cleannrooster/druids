package com.cleannrooster.druids.entity;

import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpiderNavigation extends MobNavigation {

    public SpiderNavigation(MobEntity mobEntity, World world) {
        super(mobEntity, world);
    }
    public boolean isValidPosition(BlockPos pos) {
        BlockPos blockPos = pos.down();
            return this.world.getBlockState(blockPos).isOpaqueFullCube(this.world, blockPos) || this.world.getBlockState(blockPos).getFluidState() != null;

    }

}
