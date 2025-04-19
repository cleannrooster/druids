package com.cleannrooster.druids.entity;

import com.cleannrooster.druids.Druids;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
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
import net.spell_power.api.SpellPower;
import org.jetbrains.annotations.Nullable;

public class SpectralGoat extends PathAwareEntity {
    public SpectralGoat(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        spell = SpellRegistry.from(world).get(Identifier.of(Druids.MODID,"spiderlightning"));;
        spellId =  Identifier.of(Druids.MODID,"spiderlightning");
    }
    public Vec3d originalLocation;
    public Vec3d originalRot;
    @Override
    public boolean isAttackable() {
        return false;
    }
    private SpellPower.Result power = null;
    public SpellPower.Result getSpellPower() {
        return power;
    }
    public void setSpellPower(SpellPower.Result result){
        this.power = result;
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
        this.goalSelector.add(0, new SwimAroundGoal(this,2F,120));

        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(3, new ZapGoal(this, 0.9F));

    }
    private Entity owner;
    public double damage = 0;
    public static final TrackedData<Integer> COLOR;
    public static final TrackedData<Integer> OWNER;
    public static  Spell spell;
    public static  Identifier spellId;

    public SpellHelper.ImpactContext context;


    @Nullable
    private int ownerUuid;
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(COLOR,1);
        builder.add(OWNER,-1);

    }


    @Override
    public void tick() {
        if(this.getOwner() instanceof PlayerEntity player && this.getSpellPower() != null && originalRot != null) {
            if(this.age % 10 == 0){
                ParticleHelper.sendBatches(this, SpellRegistry.from(this.getWorld()).get(Identifier.of(Druids.MODID, "goatpound")).release.particles);
                this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE.value());

            }
        }
        if(this.age > 100 && !this.getWorld().isClient){
            ParticleHelper.sendBatches(this, SpellRegistry.from(this.getWorld()).get(Identifier.of(Druids.MODID, "goatpound")).release.particles);


            this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE.value());

            this.discard();
        }
        super.tick();
    }

    @Override
    protected void pushAway(Entity entity2) {
        if(this.getOwner() instanceof PlayerEntity player && this.getSpellPower() != null && originalRot != null){

            if(EntityRelations.actionAllowed(SpellTarget.FocusMode.AREA, SpellTarget.Intent.HARMFUL,player,entity2) && !(entity2 instanceof Ownable ownable && ownable.getOwner().equals(this.getOwner()))) {
                    SpellHelper.performImpacts(this.getWorld(), player,entity2, this, SpellRegistry.from(this.getWorld()).getEntry(Identifier.of(Druids.MODID, "goatpound")).get(),
                            SpellRegistry.from(this.getWorld()).get(Identifier.of(Druids.MODID, "goatpound")).impacts, new SpellHelper.ImpactContext().power(this.getSpellPower()).position(this.getPos()));



            }
        }
        else if(!this.getWorld().isClient()){
            this.discard();
        }

        super.pushAway(entity2);
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        return;
    }






    static {
        COLOR = DataTracker.registerData(SpectralGoat.class, TrackedDataHandlerRegistry.INTEGER);
        OWNER = DataTracker.registerData(SpectralGoat.class, TrackedDataHandlerRegistry.INTEGER);

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
    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return LivingEntity.createLivingAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE,1);
    }
}
