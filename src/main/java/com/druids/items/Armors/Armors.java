package com.druids.items.Armors;

import com.cleannrooster.hexblade.Armors.MagisterArmor;
import com.druids.Druids;
import com.druids.items.ItemsInit;
import com.extraspellattributes.ReabsorptionInit;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.spell_engine.api.item.ItemConfig;
import net.spell_engine.api.item.armor.Armor;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Armors {
    public static final ArrayList<Armor.Entry> entries = new ArrayList<>();
    public static RegistryEntry<ArmorMaterial> material(String name,
                                                        int protectionHead, int protectionChest, int protectionLegs, int protectionFeet,
                                                        int enchantability, RegistryEntry<SoundEvent> equipSound, Supplier<Ingredient> repairIngredient) {
        var material = new ArmorMaterial(
                Map.of(
                        ArmorItem.Type.HELMET, protectionHead,
                        ArmorItem.Type.CHESTPLATE, protectionChest,
                        ArmorItem.Type.LEGGINGS, protectionLegs,
                        ArmorItem.Type.BOOTS, protectionFeet),
                enchantability, equipSound, repairIngredient,
                List.of(new ArmorMaterial.Layer(Identifier.of(Druids.MODID, name))),
                0,0
        );
        return Registry.registerReference(Registries.ARMOR_MATERIAL, Identifier.of(Druids.MODID, name), material);
    }
    private static Armor.Entry create(RegistryEntry<ArmorMaterial> material, Identifier id, int durability, Armor.Set.ItemFactory factory, ItemConfig.ArmorSet defaults) {
        var entry = Armor.Entry.create(
                material,
                id,
                durability,
                factory,
                defaults);
        entries.add(entry);
        return entry;
    }

    public static RegistryEntry<ArmorMaterial> material_druid = material(
            "primalist",
            2, 6, 4, 2,
            10,
            SoundEvents.ITEM_ARMOR_EQUIP_CHAIN,  () -> Ingredient.ofItems(Items.BONE));

    public static final Armor.Set primalist =
            create(
                    material_druid,
                    Identifier.of(Druids.MODID, "primalist"),
                    20,
                    PrimalistArmor::new,
                    ItemConfig.ArmorSet.with(
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply((SpellSchools.SOUL.id), 0.15F),
                                            ItemConfig.Attribute.multiply((SpellSchools.LIGHTNING.id), 0.15F),
                                            ItemConfig.Attribute.multiply(Identifier.of(ReabsorptionInit.MOD_ID,"recoup"), 0.15F)

                                    )),
                            new ItemConfig.ArmorSet.Piece(6)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply((SpellSchools.SOUL.id), 0.15F),
                                            ItemConfig.Attribute.multiply((SpellSchools.LIGHTNING.id), 0.15F),
                                            ItemConfig.Attribute.multiply(Identifier.of(ReabsorptionInit.MOD_ID,"recoup"), 0.15F)

                                    )),
                            new ItemConfig.ArmorSet.Piece(4)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply((SpellSchools.SOUL.id), 0.15F),
                                            ItemConfig.Attribute.multiply((SpellSchools.LIGHTNING.id), 0.15F),
                                            ItemConfig.Attribute.multiply(Identifier.of(ReabsorptionInit.MOD_ID,"recoup"), 0.15F)

                                    )),
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply((SpellSchools.SOUL.id), 0.15F),
                                            ItemConfig.Attribute.multiply((SpellSchools.LIGHTNING.id), 0.15F),
                                            ItemConfig.Attribute.multiply(Identifier.of(ReabsorptionInit.MOD_ID,"recoup"), 0.15F)

                                    ))
                    ))   .armorSet();

    public static void register(Map<String, ItemConfig.ArmorSet> configs) {
        Armor.register(configs, entries, ItemsInit.KEY);

    }
}