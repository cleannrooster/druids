package com.cleannrooster.druids.entity;

import com.cleannrooster.druids.Druids;
import mod.azure.azurelib.common.api.common.animatable.GeoEntity;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.fx.ParticleHelper;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.target.EntityRelations;
import net.spell_engine.internals.target.SpellTarget;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.SpellPower;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TotemEntity<T extends MobEntity> extends MobEntity implements GeoEntity, Ownable {

    public static final RawAnimation SPINNING = RawAnimation.begin().thenLoop("animation.model.spin");
    private Entity owner;

    @Nullable
    private int ownerUuid;
    public Entity target;
    public SpellHelper.ImpactContext context;
    private EntityType<T> builder = null;

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
        super.initDataTracker(builder);
        builder.add(COLOR,1);
        builder.add(OWNER,-1);

    }
    public EntityType<T> getBuilder(){
        return this.builder;
    }
    public void setBuilder(EntityType<T> builder){
        this.builder = builder;
    }

    public int getColor(){
        return this.dataTracker.get(COLOR);
    }
    public void setColor(int color){
         this.dataTracker.set(COLOR,color);
    }
    @Override
    public void tick() {
        if(this.getOwner() != null && !this.getWorld().isClient&& this.getBuilder() != null) {
            if(this.age % 40 == 0 ){
                List<LivingEntity> entityList = new ArrayList<>();
                for(Entity entity : ((ServerWorld)this.getWorld()).iterateEntities()){
                    if (entity != this.getOwner() && entity.distanceTo(this) < 8) {

                        if (entity instanceof LivingEntity living && living.canSee(this) && !(entity instanceof TotemSummon<?>) && !(entity instanceof TotemEntity<?>)) {
                            entityList.add(living);
                        }
                    }
                }
                LivingEntity closest = this.getWorld().getClosestEntity(entityList, TargetPredicate.DEFAULT,this    ,this.getX(),this.getY(),this.getZ());
                if( closest != null && getOwner() instanceof PlayerEntity && this.age % 40 == 0) {
                    ParticleHelper.sendBatches(this, SpellRegistry.from(this.getWorld()).get(Identifier.of(Druids.MODID,"pound")).release.particles);
                    for(Entity entity : TargetHelper.targetsFromArea(this,6,new Spell.Target.Area(), target1 -> EntityRelations
                            .actionAllowed(SpellTarget.FocusMode.AREA, SpellTarget.Intent.HARMFUL,(PlayerEntity)this.getOwner(),target1)&& !(target1 instanceof Ownable ownable && ownable.getOwner() != null && ownable.getOwner().equals(this.getOwner())))) {
                        SpellHelper.performImpacts(this.getWorld(), (PlayerEntity)this.getOwner(), entity, this, SpellRegistry.from(this.getWorld()).getEntry(Identifier.of(Druids.MODID, "ancestral_totem")).get(),
                                SpellRegistry.from(this.getWorld()).get(Identifier.of(Druids.MODID, "ancestral_totem")).impacts, new SpellHelper.ImpactContext().power(this.getSpellPower()).position(this.getPos()));

                    }
                    this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE.value());
                }
            }
        }
        if(!this.getWorld().isClient && this.age > 16*20) {
            this.discard();
        }
        super.tick();
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if(this.getOwner() != null && source.getAttacker() != null &&  source.getAttacker().equals(this.getOwner())){
            return false;
        }
        return super.damage(source, amount);
    }

    private SpellPower.Result power = null;
    public SpellPower.Result getSpellPower() {
        return power;
    }
    public void setSpellPower(SpellPower.Result result){
        this.power = result;
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
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("Color", (Integer) this.dataTracker.get(COLOR));
            nbt.putInt("Owner", this.ownerUuid);


    }
    public void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("Color")) {
            this.dataTracker.set(COLOR, nbt.getInt("Color"));
        }

        if (nbt.containsUuid("Owner")) {
            this.ownerUuid = nbt.getInt("Owner");
            this.owner = null;
        }

    }
}
