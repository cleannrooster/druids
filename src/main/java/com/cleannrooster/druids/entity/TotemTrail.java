package com.cleannrooster.druids.entity;

import com.cleannrooster.druids.Druids;
import com.cleannrooster.druids.DruidsAttributes;
import com.extraspellattributes.ReabsorptionInit;
import com.extraspellattributes.api.Sign;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellHelper;
import net.spell_power.api.SpellPower;
import org.jetbrains.annotations.Nullable;

public class TotemTrail<T extends MobEntity> extends PathAwareEntity {
    public TotemTrail(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
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
        this.goalSelector.add(0, new SwimAroundGoal(this,2F,120));

        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(3, new ZapGoal(this, 0.5F));

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
    public boolean isInvisible() {
        return true;
    }
    protected EntityNavigation createNavigation(World world) {
        return new SpiderNavigation(this, world);
    }

    @Override
    public boolean shouldRender(double distance) {

        return false;
    }
    private SpellPower.Result power = null;
    public SpellPower.Result getSpellPower() {
        return power;
    }
    public void setSpellPower(SpellPower.Result result){
        this.power = result;
    }
    private EntityType<T> builder = null;

    public EntityType<T> getBuilder(){
        return this.builder;
    }
    public void setBuilder(EntityType<T> builder){
        this.builder = builder;
    }
    @Override
    public void tick() {
        if (!this.getWorld().isClient()){
                ((ServerWorld)this.getWorld()).spawnParticles(ParticleTypes.CLOUD, this.getX(), this.getY(), this.getZ(),9, this.getRandom().nextFloat() * 0.2, this.getRandom().nextFloat() * 0.2, this.getRandom().nextFloat() * 0.2,0.2F);

    }
        if(this.age % 5 == 0){
            this.playSound(SoundEvents.BLOCK_TUFF_STEP,1,0.1F);
        }
        super.tick();
        if(this.age == 30 && !this.getWorld().isClient() && this.getOwner() instanceof PlayerEntity player){
            TotemEntity totem = new TotemEntity(Druids.TOTEM,this.getWorld());
            totem.setSpellPower(this.getSpellPower());
            totem.setBuilder(this.getBuilder());
            totem.setPosition(this.getPos());
            totem.setOwner(this.getOwner());
            Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> slotMap = HashMultimap.create();
            slotMap.put(EntityAttributes.GENERIC_MAX_HEALTH,new EntityAttributeModifier(Identifier.of(Druids.MODID,"totem_health_modifier"), ReabsorptionInit.applyAttributeModifiers(0,Sign.POSITIVE.wrap(player.getAttributeInstance(DruidsAttributes.TOTEMHEALTH))), EntityAttributeModifier.Operation.ADD_VALUE));
            totem.getAttributes().addTemporaryModifiers(slotMap);
            totem.heal(totem.getMaxHealth());
            this.getWorld().spawnEntity(totem);
            this.discard();
        }
    }
    static {
        COLOR = DataTracker.registerData(TotemTrail.class, TrackedDataHandlerRegistry.INTEGER);
        OWNER = DataTracker.registerData(TotemTrail.class, TrackedDataHandlerRegistry.INTEGER);

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
