package com.druids.effects;

import com.druids.Druids;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.internals.casting.SpellCast;
import net.spell_engine.particle.ParticleHelper;
import net.spell_engine.utils.TargetHelper;

public class CleansingFlame extends StatusEffect {
    public CleansingFlame(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        Identifier id = Identifier.of(Druids.MODID,"soulfirebastion");
        Spell spell = SpellRegistry.getSpell(id);
        if(!entity.getWorld().isClient()) {
            ParticleHelper.sendBatches(entity, spell.cast.particles);
        }

        return super.applyUpdateEffect(entity, amplifier);
    }
    @Override
    public void onEntityDamage(LivingEntity entity, int amplifier, DamageSource source, float amount) {
        Identifier id = Identifier.of(Druids.MODID,"soulfirebastionnova");
        Spell spell = SpellRegistry.getSpell(id);

        if(entity.getAbsorptionAmount()<= 0 && entity instanceof PlayerEntity player){
            SpellHelper.performSpell(entity.getWorld(),player, id, TargetHelper.SpellTargetResult.of(TargetHelper.targetsFromArea(player,player.getPos(),
                    spell.range,spell.release.target.area,entity1 -> TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL,player,entity1))), SpellCast.Action.RELEASE,1.0F);
        }
        super.onEntityDamage(entity, amplifier, source, amount);
    }
}
