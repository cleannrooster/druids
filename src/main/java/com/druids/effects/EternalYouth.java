package com.druids.effects;

import com.extraspellattributes.ReabsorptionInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class EternalYouth extends StatusEffect {
    public EternalYouth(StatusEffectCategory category, int color) {
        super(category, color);
    }





    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }


}
