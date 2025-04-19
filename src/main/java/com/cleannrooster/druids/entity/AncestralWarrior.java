package com.cleannrooster.druids.entity;

import com.cleannrooster.druids.Druids;
import mod.azure.azurelib.common.api.common.animatable.GeoEntity;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.fx.ParticleHelper;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.target.EntityRelations;
import net.spell_engine.internals.target.SpellTarget;
import net.spell_engine.utils.TargetHelper;
import net.spell_engine.utils.WorldScheduler;
import net.spell_power.api.SpellPower;
import org.jetbrains.annotations.Nullable;

public class AncestralWarrior extends PathAwareEntity implements Ownable, TotemSummon, GeoEntity {
    public AncestralWarrior(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.noClip = true;
        this.setNoGravity(true);
    }
    public Vec3d originalLocation;
    public Vec3d originalRot;
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

    protected void initGoals() {


    }
    private Entity owner;
    public double damage = 0;
    public static final TrackedData<Integer> COLOR;
    public static final TrackedData<Integer> OWNER;
    public static Spell spell;
    public static Identifier spellId;

    public SpellHelper.ImpactContext context;

    @Nullable
    private int ownerUuid;
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(COLOR,1);
        builder.add(OWNER,-1);

    }
    private TotemEntity<AncestralWarrior> totem = null;
    @Override
    public TotemEntity<AncestralWarrior> getTotem() {
        return this.totem;
    }
    public void setTotem(TotemEntity totem){
        this.totem = totem;
    }
    protected EntityNavigation createNavigation(World world) {
        return new SpiderNavigation(this, world);
    }



    @Override
    public void tick() {
        if(this.getTarget() != null)
            this.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES,this.getTarget().getPos());
        if(this.firstUpdate && !this.getWorld().isClient() && this.getOwner() instanceof PlayerEntity player) {
            (this).triggerAnim("slam","slam");
            ((WorldScheduler) this.getWorld()).schedule(20, () ->{
                ParticleHelper.sendBatches(this, SpellRegistry.from(this.getWorld()).get(Identifier.of(Druids.MODID,"pound")).release.particles);
                for(Entity entity : TargetHelper.targetsFromArea(this,6,new Spell.Target.Area(),  target1 -> EntityRelations
                        .actionAllowed(SpellTarget.FocusMode.AREA, SpellTarget.Intent.HARMFUL,player,target1)&& !(target1 instanceof Ownable ownable && ownable.getOwner().equals(this.getOwner())))) {
                    SpellHelper.performImpacts(this.getWorld(), player, entity, this, SpellRegistry.from(this.getWorld()).getEntry(Identifier.of(Druids.MODID, "ancestral_totem")).get(),
                            SpellRegistry.from(this.getWorld()).get(Identifier.of(Druids.MODID, "ancestral_totem")).impacts, new SpellHelper.ImpactContext().power(this.getSpellPower()).position(this.getPos()));

                }
                this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE.value());

            });

        }
        if(this.age > 30 && !this.getWorld().isClient()){
            this.discard();
        }
        super.tick();
    }
    static {
        COLOR = DataTracker.registerData(AncestralWarrior.class, TrackedDataHandlerRegistry.INTEGER);
        OWNER = DataTracker.registerData(AncestralWarrior.class, TrackedDataHandlerRegistry.INTEGER);

    }
    public void setOwner(@Nullable Entity entity) {
        if (entity != null) {
            this.ownerUuid = entity.getId();
            this.owner = entity;
            this.dataTracker.set(OWNER,entity.getId());
        }
    }
    public void setDamage(double damage){
        this.damage = damage;
    }
    @Nullable
    public Entity getOwner() {
        if(this.dataTracker.get(OWNER) != -1) {
            return this.getWorld().getEntityById(this.dataTracker.get(OWNER));
        }else{
            if(this.getTotem() != null){
                if(this.getTotem().getOwner() != null){
                    return this.getTotem().getOwner();
                }
            }
            return null;
        }
    }
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("Color", (Integer) this.dataTracker.get(COLOR));
        nbt.putInt("Owner", this.ownerUuid);


    }
    public SpellPower.Result getSpellPower() {

        return this.getTotem() != null ? this.getTotem().getSpellPower() : null;
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
    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return LivingEntity.createLivingAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE,1);
    }
    public static final RawAnimation SLAM = RawAnimation.begin().thenPlayXTimes("animation.mob.heavy.slam", 1);

    public static final RawAnimation IDLE = RawAnimation.begin().thenPlay("animation.mob.idle");
    public static final RawAnimation WALK2H = RawAnimation.begin().thenLoop("animation.unknown.walk_2h");
    public static final RawAnimation IDLE2H = RawAnimation.begin().thenPlay("animation.unknown.idle_2h");

    private PlayState predicate2(AnimationState<AncestralWarrior> state) {
        if(state.isMoving()){
                return state.setAndContinue(WALK2H);




        }

            return state.setAndContinue(IDLE2H);

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<AncestralWarrior>(this,"walk",
                0,this::predicate2)
        );

        controllers.add(
                new AnimationController<>(this, "slam", event -> PlayState.CONTINUE)
                        .triggerableAnim("slam", SLAM));

    }
    private AnimatableInstanceCache factory = AzureLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
}
