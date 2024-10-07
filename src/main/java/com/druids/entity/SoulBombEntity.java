package com.druids.entity;

import com.druids.Druids;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.SpellInfo;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.SpellPower;

import java.util.function.Predicate;
import java.util.stream.IntStream;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class SoulBombEntity extends ThrownItemEntity {
    public SpellPower.Result power;
    public Spell spell;
    public SpellHelper.ImpactContext context;

    public SoulBombEntity(EntityType<? extends ThrownItemEntity> entityType, World level) {
        super(entityType, level);
        this.setNoGravity(true);
        this.noClip = true;

    }

    @Override
    protected Item getDefaultItem() {
        return Items.AIR;
    }

    public SoulBombEntity(EntityType<? extends ThrownItemEntity> entityType, World level, PlayerEntity player) {
        super(entityType, level);
        this.setOwner(player);
        this.setNoGravity(true);
        this.noClip = true;
    }
    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public ItemStack getStack() {
        return Items.AIR.getDefaultStack();
    }

    @Override
    public void tick() {
        if((this.age > 50 || this.getVelocity().length() < 0.2) && !this.getWorld().isClient()){
            this.discard();
        }
        if(this.age % 4 == 0) {
            for (int ii = 0; ii < 100; ii++) {
                int i = (10 * ii + (this.age % 10)) % 100;
                if (i <= 100) {

                    double[] indices = IntStream.rangeClosed(0, (int) ((1000)))
                            .mapToDouble(x -> x).toArray();
                    if (i < 0) {
                        return;
                    }
                    double phi = Math.acos(1 - 2 * indices[i] / 1000);
                    double theta = Math.PI * (1 + Math.pow(5, 0.5) * indices[i]);
                    if (phi == Math.toRadians(180) && theta == Math.toRadians(180)) {
                        this.setInvisible(true);
                    }
                    double x = cos(theta) * sin(phi);
                    double y = -cos(phi);
                    double z = Math.sin(theta) * sin(phi);
                    this.getWorld().addParticle(ParticleTypes.FIREWORK, this.getX() +  x, this.getY() + y, this.getZ() + z, 0, 0, 0);
                }
            }
            for (int ii = 0; ii < 100; ii++) {
                int i = (10 * ii + (this.age % 10)) % 1000;
                if (i <= 1000) {

                    double[] indices = IntStream.rangeClosed(0, (int) ((1000)))
                            .mapToDouble(x -> x).toArray();
                    if (i < 0) {
                        return;
                    }
                    double phi = Math.acos(1 - 2 * indices[i] / 1000);
                    double theta = Math.PI * (1 + Math.pow(5, 0.5) * indices[i]);
                    if (phi == Math.toRadians(180) && theta == Math.toRadians(180)) {
                        this.setInvisible(true);
                    }
                    double x = cos(theta) * sin(phi);
                    double y = -cos(phi);
                    double z = Math.sin(theta) * sin(phi);
                    this.getWorld().addParticle(ParticleTypes.SCULK_SOUL, this.getX() + 4 * x, this.getY() + 4 * y, this.getZ() + 4 * z, 0, 0, 0);
                }
            }
        }
        if(this.getOwner() instanceof PlayerEntity player) {
            Predicate<Entity> selectionPredicate = (target) -> {
                return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, player, target)
                        );
            };
            Spell.Release.Target.Area area = new Spell.Release.Target.Area();
            area.angle_degrees = 360;
            for (Entity entity : TargetHelper.targetsFromArea(this, this.getPos(), 4F, area, selectionPredicate)
            ) {
                if (!this.getWorld().isClient() && this.getOwner() instanceof PlayerEntity owner) {
                    double a = 0;
                    if (entity instanceof LivingEntity living2) {
                        a = living2.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).getValue();
                    }
                    if (entity instanceof LivingEntity living2 && !TargetHelper.actionAllowed(TargetHelper.TargetingMode.DIRECT, TargetHelper.Intent.HARMFUL, owner, living2)) {
                        a = 1;
                    }
                    entity.setVelocity(entity.getVelocity().add(this.getVelocity().multiply((1 - a))));
                    if (entity.getVelocity().length() > this.getVelocity().length()) {
                        entity.setVelocity(entity.getVelocity().normalize().multiply(this.getVelocity().length(), this.getVelocity().length(), this.getVelocity().length()));
                    }
                    if (this.power != null && this.spell != null && this.context != null && entity.timeUntilRegen <= 10) {
                        Spell spell = SpellRegistry.getSpell(Identifier.of(Druids.MODID, "soulflare"));
                        SpellInfo info = new SpellInfo(spell, Identifier.of(Druids.MODID,"soulflare"));

                        SpellHelper.performImpacts(this.getWorld(),player, entity,player, info,context);
                        entity.timeUntilRegen = 20;

                    }
                }

                //particleMultiplier = power.criticalDamage() + (double)vulnerability.criticalDamageBonus();
                //aster.setLastHurtMob(target);

            }
        }
        super.tick();
    }


    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        this.discard();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
    }
}
