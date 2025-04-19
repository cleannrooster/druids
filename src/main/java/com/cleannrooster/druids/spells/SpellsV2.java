package com.cleannrooster.druids.spells;

import com.cleannrooster.druids.Druids;
import com.cleannrooster.druids.DruidsEffects;
import com.cleannrooster.druids.entity.SpectralGoat;
import com.cleannrooster.druids.entity.TotemEntity;
import com.cleannrooster.druids.entity.TotemTrail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.event.SpellHandlers;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.fx.ParticleHelper;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.target.EntityRelations;
import net.spell_engine.internals.target.SpellTarget;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.spell_engine.api.spell.event.SpellHandlers.registerCustomDelivery;

public class SpellsV2 {
    public static class  AncestralTotem implements SpellHandlers.CustomDelivery {
        @Override
        public boolean onSpellDelivery(World world, RegistryEntry<Spell> registryEntry, PlayerEntity playerEntity, List<SpellHelper.DeliveryTarget> targets, SpellHelper.ImpactContext impactContext, @Nullable Vec3d vec3d) {
            TotemTrail trail = new TotemTrail(Druids.TOTEM_TRAIL,world);
            trail.setBuilder(Druids.ANCESTRAL_WARRIOR);
            trail.setSpellPower(impactContext.power());
            trail.setOwner(playerEntity);
            Vec3d vec3d1 = playerEntity.getPos().add(new Vec3d(playerEntity.getRotationVec(1).getX(),0, playerEntity.getRotationVec(1).getZ()).normalize());
            trail.setPosition(vec3d1);
            trail.setYaw(playerEntity.getYaw());
            trail.originalLocation = vec3d1;
            trail.originalRot = playerEntity.getRotationVector();
            world.spawnEntity(trail);
            return true;
        }
    }
    public static class  SpectralGoatSpell implements SpellHandlers.CustomDelivery {
        @Override
        public boolean onSpellDelivery(World world, RegistryEntry<Spell> registryEntry, PlayerEntity playerEntity, List<SpellHelper.DeliveryTarget> targets, SpellHelper.ImpactContext impactContext, @Nullable Vec3d vec3d) {
            SpectralGoat trail = new SpectralGoat(Druids.SPECTRAL_GOAT,world);
            trail.setSpellPower(impactContext.power());
            trail.setOwner(playerEntity);

            Vec3d vec3d1 = playerEntity.getPos().add(new Vec3d(playerEntity.getRotationVec(1).getX(),0, playerEntity.getRotationVec(1).getZ()).normalize());
            trail.setPosition(vec3d1);
            trail.setYaw(playerEntity.getYaw());
            trail.originalLocation = vec3d1;
            trail.originalRot = playerEntity.getRotationVector();
            for(SpellHelper.DeliveryTarget entity : targets){
                if(entity.entity() instanceof LivingEntity living){
                    trail.setTarget(living);
                }
            }
            world.spawnEntity(trail);
            return true;
        }
    }
    public static class  AncestralAvatar implements SpellHandlers.CustomDelivery {
        @Override
        public boolean onSpellDelivery(World world, RegistryEntry<Spell> registryEntry, PlayerEntity playerEntity, List<SpellHelper.DeliveryTarget> targets, SpellHelper.ImpactContext impactContext, @Nullable Vec3d vec3d) {
            boolean performed = false;
            int amp = 0;
            for(Entity entity : ((ServerWorld)world).iterateEntities()){
                if (entity instanceof TotemEntity totem && totem.getOwner() != null &&  totem.getOwner().equals(playerEntity)) {
                    ParticleHelper.sendBatches(totem, SpellRegistry.from(world).get(Identifier.of(Druids.MODID, "ancestral_avatar")).release.particles);
                    for (Entity entity2 : TargetHelper.targetsFromArea(totem, 6, new Spell.Target.Area(), target1 -> EntityRelations
                            .actionAllowed(SpellTarget.FocusMode.AREA, SpellTarget.Intent.HARMFUL,playerEntity,target1))) {
                        SpellHelper.performImpacts(world, playerEntity, entity2, totem, SpellRegistry.from(world).getEntry(Identifier.of(Druids.MODID, "ancestral_avatar")).get(),
                                SpellRegistry.from(world).get(Identifier.of(Druids.MODID, "ancestral_avatar")).impacts, new SpellHelper.ImpactContext().power(new SpellPower.Result(SpellSchools.SOUL,totem.getMaxHealth()*0.10,0,100)).position(totem.getPos()));
                    }
                    totem.discard();

                    performed = true;
                    amp++;
                }
            }
            if(amp > 0) {
                playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 8 * 20, amp -1,false,false));
            }
            return performed;
        }
    }
    public static void registerDeliveries() {
        registerCustomDelivery(Identifier.of(Druids.MODID,"ancestral_totem"),new AncestralTotem());
        registerCustomDelivery(Identifier.of(Druids.MODID,"spectral_goat"),new SpectralGoatSpell());
        registerCustomDelivery(Identifier.of(Druids.MODID,"ancestral_avatar"),new AncestralAvatar());

    }
}
