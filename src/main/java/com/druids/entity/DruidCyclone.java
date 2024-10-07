package com.druids.entity;

import com.druids.Druids;
import mod.azure.azurelib.common.api.common.animatable.GeoEntity;
import mod.azure.azurelib.common.internal.client.util.RenderUtils;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.SpellInfo;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.utils.TargetHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class DruidCyclone extends Entity implements GeoEntity, Ownable {

    public static final RawAnimation SPINNING = RawAnimation.begin().thenLoop("animation.model.spin");
    private Entity owner;
    Entity totem;

    @Nullable
    private int ownerUuid;
    public Entity target;
    public int number = 0;
    public SpellHelper.ImpactContext context;
    public DruidCyclone(EntityType<? extends DruidCyclone> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
        this.noClip = true;
    }


    public static final TrackedData<Integer> COLOR;
    public static final TrackedData<Integer> OWNER;

    static {
        COLOR = DataTracker.registerData(DruidCyclone.class, TrackedDataHandlerRegistry.INTEGER);
        OWNER = DataTracker.registerData(DruidCyclone.class, TrackedDataHandlerRegistry.INTEGER);

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
            this.number = dataTracker.get(COLOR);
            if(this.target != null && this.target.isAlive()) {
                double tridentEntity = 0;
                double f = 0;
                float g = (float) (-sin(tridentEntity * ((float) Math.PI / 180)) * cos(f * ((float) Math.PI / 180)));
                float h = (float) -sin(f * ((float) Math.PI / 180));
                float r = (float) (cos(tridentEntity * ((float) Math.PI / 180)) * cos(f * ((float) Math.PI / 180)));
                this.setPosition(this.target.getX() + (3D + target.getBoundingBox().getLengthX()) * 0.5 * (1-0.25*Math.pow((((double) (this.age % 8) / 8D)-1),2)) * cos((((double) (this.age % 40) / 40D)) * (2D * (double) Math.PI) + (2 * Math.PI * (double) (number % 3) / 3D)), (double) this.target.getY(), this.target.getZ()  + (3D + target.getBoundingBox().getLengthX()) * 0.5 * (1-0.25*Math.pow((((double) (this.age % 8) / 8D)-1),2)) * sin((((double) (this.age % 40) / 40D)) * (2 * Math.PI) + (2 * Math.PI * (double) (number % 3) / 3D)));


            }
            else{
                if(!this.getWorld().isClient) {

                    this.discard();
                }
            }
        }
        else{
            if(!this.getWorld().isClient) {
                this.discard();
            }
        }
        super.tick();
        if (this.age % 5 == 0 && this.getOwner() instanceof LivingEntity living) {

            List<LivingEntity> list = this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox(), Entity::isAlive);
            for (LivingEntity entity : list) {
                Spell spell = SpellRegistry.getSpell(Identifier.of(Druids.MODID, "maelstromtotem"));
                SpellInfo info = new SpellInfo(spell, Identifier.of(Druids.MODID,"maelstromtotem"));
                if (spell != null  && this.context != null) {
                    if(TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL,living,entity) || (this.target != null && this.target == entity)) {
                        SpellHelper.performImpacts(entity.getWorld(), living, entity, this.getOwner(), info,
                                this.context, false);
                    }
                }
            }
        }
    }

    @Override
    public boolean canHit() {
        return false;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        animationData.add(new AnimationController<>(this, "fly",
                0, this::predicate2)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public double getTick(Object object) {
        return RenderUtils.getCurrentTick();
    }

    private <E extends GeoAnimatable> PlayState predicate2(AnimationState<E> event) {

            return event.setAndContinue(SPINNING);
    }
    private AnimatableInstanceCache factory = AzureLibUtil.createInstanceCache(this);

    public AnimatableInstanceCache getFactory() {
        return factory;
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
