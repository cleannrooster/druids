package com.druids.entity;

import com.druids.Druids;
import mod.azure.azurelib.common.api.common.animatable.GeoEntity;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.spell_engine.internals.SpellHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class TotemEntity extends Entity implements GeoEntity, Ownable {

    public static final RawAnimation SPINNING = RawAnimation.begin().thenLoop("animation.model.spin");
    private Entity owner;
    @Nullable
    private int ownerUuid;
    public Entity target;
    public ArrayList<DruidCyclone> cyclones  = new ArrayList<>();
    public SpellHelper.ImpactContext context;
    public TotemEntity(EntityType<? extends TotemEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
        this.noClip = true;
    }

    public static final TrackedData<Integer> COLOR;
    public static final TrackedData<Integer> OWNER;

    static {
        COLOR = DataTracker.registerData(TotemEntity.class, TrackedDataHandlerRegistry.INTEGER);
        OWNER = DataTracker.registerData(TotemEntity.class, TrackedDataHandlerRegistry.INTEGER);

    }


    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(COLOR,1);
        builder.add(OWNER,-1);

    }

    public int getColor(){
        return this.dataTracker.get(COLOR);
    }
    public void setColor(int color){
         this.dataTracker.set(COLOR,color);
    }
    @Override
    public void tick() {
        if(this.getOwner() != null && !this.getWorld().isClient) {
            if(this.cyclones.isEmpty()) {
                for (int i = 0; i < 3; i++) {
                    DruidCyclone cyclone = new DruidCyclone(Druids.CYCLONEENTITY, this.getWorld());
                    cyclone.setPosition(this.getPos());
                    cyclone.setOwner(this.getOwner());
                    cyclone.totem = this;
                    cyclone.context = this.context;
                    cyclone.target = this;
                    cyclone.setColor(i);
                    this.cyclones.add(cyclone);
                    this.getWorld().spawnEntity(cyclone);
                }
            }
            this.cyclones.removeIf(cyclones -> !cyclones.isAlive());

        }
        else{
            if(!this.getWorld().isClient) {
                this.discard();
            }
        }
        if(!this.getWorld().isClient && this.age > 16*20) {
            this.discard();
        }
        super.tick();
    }

    @Override
    public boolean canHit() {
        return false;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        /*animationData.add(new AnimationController<>(this, "fly",
                0, this::predicate2)
        );*/
    }
    private <E extends GeoAnimatable> PlayState predicate2(AnimationState<E> event) {

            return event.setAndContinue(SPINNING);
    }
    private AnimatableInstanceCache factory = AzureLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }
    public void setOwner(@Nullable Entity entity) {
        if (entity != null) {
            this.ownerUuid = entity.getId();
            this.owner = entity;
            this.dataTracker.set(OWNER,entity.getId());
        }
    }
    @Nullable
    public Entity getOwner() {
        if(this.dataTracker.get(OWNER) != -1) {
            return this.getWorld().getEntityById(this.dataTracker.get(OWNER));
        }else{
            return null;
        }
    }
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("Color", (Integer) this.dataTracker.get(COLOR));
            nbt.putInt("Owner", this.ownerUuid);


    }
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("Color")) {
            this.dataTracker.set(COLOR, nbt.getInt("Color"));
        }

        if (nbt.containsUuid("Owner")) {
            this.ownerUuid = nbt.getInt("Owner");
            this.owner = null;
        }

    }
}
