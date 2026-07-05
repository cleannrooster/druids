package com.cleannrooster.druids;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.spell_engine.api.effect.CustomStatusEffect;
import net.spell_power.api.SpellSchools;

public class DruidsEffects {

    // Guide: +10% soul power per amplifier level (amplifier 0 = +10%, 1 = +20%, etc.)
    public static RegistryEntry<StatusEffect> SOUL_POWER;

    // Pilgrimage: +100% soul power (flat, separate from Guide so they coexist)
    public static RegistryEntry<StatusEffect> SOUL_POWER_PILGRIMAGE;

    public static void register() {
        SOUL_POWER = Registry.registerReference(
                Registries.STATUS_EFFECT,
                Identifier.of(Druids.MODID, "soul_power"),
                new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0x55FFFF)
                        .addAttributeModifier(
                                SpellSchools.SOUL.getAttributeEntry(),
                                Identifier.of(Druids.MODID, "soul_power"),
                                0.1, // +10% per level
                                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                        )
        );

        SOUL_POWER_PILGRIMAGE = Registry.registerReference(
                Registries.STATUS_EFFECT,
                Identifier.of(Druids.MODID, "soul_power_pilgrimage"),
                new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0xAA77FF)
                        .addAttributeModifier(
                                SpellSchools.SOUL.getAttributeEntry(),
                                Identifier.of(Druids.MODID, "soul_power_pilgrimage"),
                                1.0, // +100%
                                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                        )
        );
    }
}
