package com.cleannrooster.druids.entity;

import com.cleannrooster.druids.Druids;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SoulFragmentEntity extends MobEntity implements Ownable {

    public static final TrackedData<Integer> OWNER = DataTracker.registerData(SoulFragmentEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private int ownerUuid = -1;

    public SoulFragmentEntity(EntityType<? extends SoulFragmentEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
        this.noClip = true;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(OWNER, -1);
    }

    @Override
    protected void initGoals() {
        // No AI - soul fragments just sit there
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        // Ambient particles on server
        if (!this.getWorld().isClient() && this.getWorld() instanceof ServerWorld serverWorld) {
            if (this.age % 5 == 0) {
                serverWorld.spawnParticles(
                        ParticleTypes.SCULK_SOUL,
                        this.getX(), this.getY() + 0.3, this.getZ(),
                        2,
                        0.15, 0.1, 0.15,
                        0.02
                );
            }
        }

        // Soul fragments last 60 seconds then fade
        if (!this.getWorld().isClient() && this.age > 60 * 20) {
            this.discard();
        }
    }

    public void setOwner(@Nullable Entity entity) {
        if (entity != null) {
            this.ownerUuid = entity.getId();
            this.dataTracker.set(OWNER, entity.getId());
        }
    }

    @Nullable
    @Override
    public Entity getOwner() {
        int id = this.dataTracker.get(OWNER);
        if (id != -1) {
            return this.getWorld().getEntityById(id);
        }
        return null;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Owner", this.ownerUuid);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("Owner")) {
            this.ownerUuid = nbt.getInt("Owner");
        }
    }
}
