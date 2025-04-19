package com.cleannrooster.druids.items.Armors;

import com.cleannrooster.druids.items.ItemsInit;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.spell_engine.api.config.ArmorSetConfig;
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.item.Equipment;
import net.spell_engine.api.item.armor.Armor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.cleannrooster.druids.Druids.MODID;

public class Armors {
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
                List.of(new ArmorMaterial.Layer(Identifier.of(MODID, name))),
                0,0
        );
        return Registry.registerReference(Registries.ARMOR_MATERIAL, Identifier.of(MODID, name), material);
    }
    public static final ArrayList<Armor.Entry> entries = new ArrayList<>();

    private static Armor.Entry create(RegistryEntry<ArmorMaterial> material, Identifier id, int durability, Armor.Set.ItemFactory factory, ArmorSetConfig defaults, int tier) {
        var entry = Armor.Entry.create(
                material,
                id,
                durability,
                factory,
                defaults,
                Equipment.LootProperties.of(tier));
        entries.add(entry);

        return entry;
    }

    public static RegistryEntry<ArmorMaterial> material_Primalist = material(
            "primalist",
            2, 7, 5, 2,
            25,
            SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, () -> Ingredient.ofItems(Items.IRON_INGOT,Items.LEATHER,Items.WITHER_SKELETON_SKULL,Items.BONE));



    public static final Armor.Set primalist = create(
            material_Primalist,
            Identifier.of(MODID, "primalist"),
            25,
            PrimalistArmor::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(2)
                            .addAll(List.of(
                                    AttributeModifier.multiply(Identifier.ofVanilla("generic.attack_damage"), 0.04F),

                                    AttributeModifier.bonus(Identifier.of(MODID,"totem_health"), 3)

                            )),
                    new ArmorSetConfig.Piece(7)
                            .addAll(List.of(
                                    AttributeModifier.multiply(Identifier.ofVanilla("generic.attack_damage"), 0.04F),

                                    AttributeModifier.bonus(Identifier.of(MODID,"totem_health"), 3)
                            )),
                    new ArmorSetConfig.Piece(5)
                            .addAll(List.of(
                                    AttributeModifier.multiply(Identifier.ofVanilla("generic.attack_damage"), 0.04F),

                                    AttributeModifier.bonus(Identifier.of(MODID,"totem_health"), 3)
                            )),
                    new ArmorSetConfig.Piece(2)
                            .addAll(List.of(
                                    AttributeModifier.multiply(Identifier.ofVanilla("generic.attack_damage"), 0.04F),

                                    AttributeModifier.bonus(Identifier.of(MODID,"totem_health"), 3)
                            ))
            ),2)
            .armorSet();
    public static RegistryEntry<ArmorMaterial> material_PrimalistNetherite = material(
            "primalist_netherite",
            3, 8, 6, 3,
            30,
            SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, () -> Ingredient.ofItems(Items.NETHERITE_INGOT));



    public static final Armor.Set primalistNetherite = create(
            material_PrimalistNetherite,
            Identifier.of(MODID, "primalist_netherite"),
            30,
            NetheritePrimalistArmor::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(3)
                            .addAll(List.of(
                                    AttributeModifier.multiply(Identifier.ofVanilla("generic.attack_damage"), 0.06F),

                                    AttributeModifier.bonus(Identifier.of(MODID,"totem_health"), 5)

                            )),
                    new ArmorSetConfig.Piece(8)
                            .addAll(List.of(
                                    AttributeModifier.multiply(Identifier.ofVanilla("generic.attack_damage"), 0.06F),

                                    AttributeModifier.bonus(Identifier.of(MODID,"totem_health"), 5)
                            )),
                    new ArmorSetConfig.Piece(6)
                            .addAll(List.of(
                                    AttributeModifier.multiply(Identifier.ofVanilla("generic.attack_damage"), 0.06F),

                                    AttributeModifier.bonus(Identifier.of(MODID,"totem_health"), 5)
                            )),
                    new ArmorSetConfig.Piece(3)
                            .addAll(List.of(
                                    AttributeModifier.multiply(Identifier.ofVanilla("generic.attack_damage"), 0.06F),

                                    AttributeModifier.bonus(Identifier.of(MODID,"totem_health"), 5)
                            ))
            ),3)
            .armorSet();

    public static void register(Map<String, ArmorSetConfig> configs) {
        Armor.register(configs, entries, ItemsInit.KEY);
    }
}