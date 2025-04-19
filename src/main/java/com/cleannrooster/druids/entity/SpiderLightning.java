package com.cleannrooster.druids.entity;

import com.cleannrooster.druids.Druids;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.internals.SpellHelper;
import org.jetbrains.annotations.Nullable;

public class SpiderLightning extends PathAwareEntity {
    public SpiderLightning(EntityType<? extends PathAwareEntity> entityType, World world) {
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


    static {
        COLOR = DataTracker.registerData(SpiderLightning.class, TrackedDataHandlerRegistry.INTEGER);
        OWNER = DataTracker.registerData(SpiderLightning.class, TrackedDataHandlerRegistry.INTEGER);

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
