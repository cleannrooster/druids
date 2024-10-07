package com.druids.entity;

import com.druids.Druids;
import com.druids.api.ChainList;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.SpellInfo;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.internals.WorldScheduler;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ChainLightning extends ThrownItemEntity {
    public SpellPower.Result power;
    public Spell spell;
    public Entity target;
    public SpellHelper.ImpactContext context;
    public ChainList list = new ChainList(new ArrayList<>(List.of()));
    public int remainingchains;

    public ChainLightning(EntityType<? extends ThrownItemEntity> entityType, World level) {
        super(entityType, level);
        this.setNoGravity(true);
        this.setInvisible(true);
        this.noClip = true;
    }
    public ChainLightning(EntityType<? extends ThrownItemEntity> entityType, World level, ChainList list) {
        super(entityType, level);
        this.setNoGravity(true);
        this.setInvisible(true);

        this.noClip = true;
        this.list = list;
    }

    @Override
    protected Item getDefaultItem() {
        return Items.REDSTONE_BLOCK;
    }

    public ChainLightning(EntityType<? extends ThrownItemEntity> entityType, World level, PlayerEntity player) {
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
    public boolean isInvisible() {
        return true;
    }

    @Override
    public ItemStack getStack() {
        return Items.REDSTONE_BLOCK.getDefaultStack();
    }

    @Override
    public void tick() {

        this.getWorld().addParticle(new DustParticleEffect(DustParticleEffect.RED,1.5F),true,this.getX(),this.getY(),this.getZ(),0,0,0);
        this.getWorld().addParticle(new DustParticleEffect(DustParticleEffect.RED,1.5F),true,(this.getX()+this.prevX)/2,(this.getY()+this.prevY)/2,(this.getZ()+this.prevZ)/2,0,0,0);
        this.getWorld().addParticle(new DustParticleEffect(DustParticleEffect.RED,1.5F),true,this.getX(),this.getY(),this.getZ(),0,0,0);
        this.getWorld().addParticle(new DustParticleEffect(DustParticleEffect.RED,1.5F),true,(this.getX()+this.prevX)/2,(this.getY()+this.prevY)/2,(this.getZ()+this.prevZ)/2,0,0,0);
        this.getWorld().addParticle(ParticleTypes.ELECTRIC_SPARK,true,this.getX(),this.getY(),this.getZ(),0,0,0);
        this.getWorld().addParticle(ParticleTypes.ELECTRIC_SPARK,true,(this.getX()+this.prevX)/2,(this.getY()+this.prevY)/2,(this.getZ()+this.prevZ)/2,0,0,0);
        this.getWorld().addParticle(ParticleTypes.ELECTRIC_SPARK,true,this.getX(),this.getY(),this.getZ(),0,0,0);
        this.getWorld().addParticle(ParticleTypes.ELECTRIC_SPARK,true,(this.getX()+this.prevX)/2,(this.getY()+this.prevY)/2,(this.getZ()+this.prevZ)/2,0,0,0);

        if((this.age > 100) && !this.getWorld().isClient()){
            this.discard();
        }
        if (this.target == null) {


            if(!this.getWorld().isClient())
            this.discard();
        } else {
            this.noClip = true;
            Vec3d vec3d = this.target.getEyePos().subtract(this.getPos());


            double d = 0.2+ 0.05 * 10 * 2 * this.age/20;
            this.setVelocity(this.getVelocity().multiply(0.95).add(vec3d.normalize().multiply(d)));
            this.setVelocity(this.getVelocity().normalize().multiply(1));

        }
        super.tick();
    }


    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {

    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if(this.getOwner() != null && this.getOwner().isAlive() && this.target != null) {
            Vec3d pos = this.getOwner().getPos().add(0, this.getOwner().getBoundingBox().getLengthY() / 2, 0);

            for (int i = 0; i < 60; i++) {
                Vec3d pos2 = pos.add(entityHitResult.getEntity().getPos().add(0, this.getBoundingBox().getLengthY() / 2, 0).subtract(pos).multiply((double) i / 15));
                if (this.getWorld() instanceof ServerWorld serverWorld) {
                    for (ServerPlayerEntity player : PlayerLookup.tracking(this)) {
                        //serverWorld.spawnParticles(player,Particles.snowflake.particleType,true, pos2.x, pos2.y, pos2.z, 1,0, 0, 0,0);
                        serverWorld.spawnParticles(player, new DustParticleEffect(DustParticleEffect.RED, 1.0F), true, pos2.x, pos2.y, pos2.z, 1, 0, 0, 0, 0);
                        serverWorld.spawnParticles(player, ParticleTypes.ELECTRIC_SPARK, true, pos2.x, pos2.y, pos2.z, 1, 0, 0, 0, 0);

                    }
                }
            }
            if (entityHitResult.getEntity() == this.target && !this.list.listhit.contains(entityHitResult.getEntity()) && this.getOwner() instanceof PlayerEntity player && this.power != null && this.spell != null && this.context != null) {
                this.list.listhit.add(entityHitResult.getEntity());

                Spell spell = SpellRegistry.getSpell(Identifier.of(Druids.MODID, "chain"));
                SpellInfo info = new SpellInfo(spell, Identifier.of(Druids.MODID, "chain"));

                SpellHelper.performImpacts(this.getWorld(), player, entityHitResult.getEntity(), player, info, context);
                Predicate<Entity> selectionPredicate = (target) -> {
                    return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, player, target)
                            && !this.list.list.contains(target) && target != entityHitResult.getEntity() && !(target instanceof ChainLightning));
                };
                Spell.Release.Target.Area area = new Spell.Release.Target.Area();
                area.angle_degrees = 360;
                List<Entity> targets = TargetHelper.targetsFromArea(entityHitResult.getEntity(), entityHitResult.getEntity().getPos(), 8F, area, selectionPredicate);
                targets.removeIf(target -> target == entityHitResult.getEntity());

                targets.sort((target1, target2) -> (int) (this.distanceTo(target1) - this.distanceTo(target2)));

                targets = targets.subList(0, Math.min(3, targets.size()));
                targets.sort((target1, target2) -> (int) (this.distanceTo(target2) - this.distanceTo(target1)));

                int count = 0;
                if (this.remainingchains > 0) {
                    for (Entity entity : targets
                    ) {
                        ChainLightning lightning = new ChainLightning(Druids.ChainLightning, entityHitResult.getEntity().getWorld(), this.list);
                        lightning.remainingchains = this.remainingchains;
                        if (count == 0) {
                            lightning.remainingchains--;
                        } else {
                            lightning.remainingchains = 0;
                        }
                        lightning.list.list.add(entity);
                        lightning.setPos(entityHitResult.getEntity().getX(), entityHitResult.getEntity().getBoundingBox().getCenter().getY(), entityHitResult.getEntity().getZ());
                        lightning.setOwner(player);
                        lightning.spell = SpellRegistry.getSpell(Identifier.of(Druids.MODID, "chain"));
                        lightning.context = this.context;
                        SpellPower.Vulnerability vulnerability = SpellPower.Vulnerability.none;


                        SpellPower.Result power = SpellPower.getSpellPower(SpellSchools.LIGHTNING, (LivingEntity) this.getOwner());
                        lightning.power = power;
                        lightning.target = entity;
                        lightning.setVelocity(entity.getPos().subtract(entityHitResult.getEntity().getPos().add(0, entityHitResult.getEntity().getBoundingBox().getLengthY() / 2, 0)).normalize());
                        lightning.setVelocity(0,1,0);

                        ProjectileUtil.setRotationFromVelocity(lightning, 360);

                        if (count == 0) {
                            this.getWorld().spawnEntity(lightning);
                        } else {
                            ((WorldScheduler)this.getWorld()).schedule(2,() -> {
                                this.getWorld().spawnEntity(lightning);

                            });
                        }

                        count++;

                    }
                }
                this.discard();

            }

        }

    }
}
