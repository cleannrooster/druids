package com.druids.items;

import com.cleannrooster.hexblade.Hexblade;
import com.druids.Druids;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.spell_engine.api.item.ItemConfig;
import net.spell_engine.api.item.weapon.StaffItem;
import net.spell_engine.api.item.weapon.Weapon;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

public class Weapons {
    public static final ArrayList<Weapon.Entry> entries = new ArrayList<>();

    private static Weapon.Entry entry(String name, Weapon.CustomMaterial material, Weapon.Factory item, ItemConfig.Weapon defaults) {
        return entry(null, name, material, item, defaults);
    }

    private static Weapon.Entry entry(String requiredMod, String name, Weapon.CustomMaterial material, Weapon.Factory item, ItemConfig.Weapon defaults) {
        var entry = new Weapon.Entry(Druids.MODID, name, material, item, defaults, null);
        if (entry.isRequiredModInstalled()) {
            entries.add(entry);
        }
        return entry;
    }

    private static Supplier<Ingredient> ingredient(String idString) {
        return ingredient(idString, Items.DIAMOND);
    }

    private static Supplier<Ingredient> ingredient(String idString, Item fallback) {
        var id = Identifier.of(idString);
        return () -> {
            var item = Registries.ITEM.get(id);
            var ingredient = item != null ? item : fallback;
            return Ingredient.ofItems(ingredient);
        };
    }

    // MARK: Wands

    private static final float wandAttackDamage = 2;
    private static final float wandAttackSpeed = -2.4F;
    private static Weapon.Entry wand(String name, Weapon.CustomMaterial material) {
        var settings = new Item.Settings();

        return entry(name, material, StaffItem::new, new ItemConfig.Weapon(wandAttackDamage, wandAttackSpeed));
    }

 public static final Weapon.Entry lightningWand = wand("wand_lightning",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.GOLD_INGOT)))
            .attribute(ItemConfig.Attribute.bonus(SpellSchools.LIGHTNING.id, 3));

    public static final Weapon.Entry netheriteLightningWand = wand("wand_netherite_lightning",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(ItemConfig.Attribute.bonus(SpellSchools.LIGHTNING.id, 4));

    // MARK: Staves

    private static final float staffAttackDamage = 4;
    private static final float staffAttackSpeed = -3F;

    private static Weapon.Entry staff(String name, Weapon.CustomMaterial material) {
        return staff(null, name, material);
    }

    private static Weapon.Entry staff(String requiredMod, String name, Weapon.CustomMaterial material) {
        var settings = new Item.Settings();
        return entry(requiredMod, name, material, StaffItem::new, new ItemConfig.Weapon(staffAttackDamage, staffAttackSpeed));
    }

         public static final Weapon.Entry lightningStaff = staff("staff_lightning",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.GOLD_INGOT)))
            .attribute(ItemConfig.Attribute.bonus(SpellSchools.LIGHTNING.id, 5));

    public static final Weapon.Entry netheriteLightningStaff = staff("staff_netherite_lightning",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(ItemConfig.Attribute.bonus(SpellSchools.LIGHTNING.id, 6));

    public static void register(Map<String, ItemConfig.Weapon> configs) {
        if (FabricLoader.getInstance().isModLoaded("betterend")) {
            staff("betterend", "staff_crystal_lightning",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, ingredient("betternether:amber_gem",Items.NETHERITE_INGOT)))
                    .attribute(ItemConfig.Attribute.bonus(SpellSchools.LIGHTNING.id, 7));
        }
        Weapon.register(configs, entries, RegistryKey.of(Registries.ITEM_GROUP.getKey(),Identifier.of(Druids.MODID,"generic")));
    }
}