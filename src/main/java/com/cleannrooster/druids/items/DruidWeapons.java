package com.cleannrooster.druids.items;

import com.cleannrooster.druids.Druids;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.spell_engine.rpg_series.config.WeaponConfig;
import net.spell_engine.api.spell.container.SpellContainers;
import net.spell_engine.rpg_series.item.Equipment;
import net.spell_engine.rpg_series.item.Weapon;
import net.spell_engine.rpg_series.item.Weapons;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DruidWeapons {
    private static final String NAMESPACE = Druids.MODID;
    public static final ArrayList<Weapon.Entry> entries = new ArrayList<>();
    private static Weapon.Entry add(Weapon.Entry entry) {
        entries.add(entry);
        return entry;
    }

    private static Supplier<Ingredient> ingredient(String idString, boolean requirement, Item fallback) {
        var id = Identifier.of(idString);
        if (requirement) {
            return () -> {
                return Ingredient.ofItems(fallback);
            };
        } else {
            return () -> {
                var item = Registries.ITEM.get(id);
                var ingredient = item != null ? item : fallback;
                return Ingredient.ofItems(ingredient);
            };
        }
    }

    private static final String AETHER = "aether";
    private static final String BETTER_END = "betterend";
    private static final String BETTER_NETHER = "betternether";

    // MARK: Wands


    public static final Weapon.Entry boneWand = add(Weapons.damageWand(
                    NAMESPACE, "wand_bone",
                    Equipment.Tier.TIER_2, () -> Ingredient.ofItems(Items.GOLD_INGOT),
                    List.of(SpellSchools.SOUL.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(Identifier.of(Druids.MODID,"spirit_bolts")))
    );


    public static final Weapon.Entry boneNetheriteWand = add(Weapons.damageWand(
                    NAMESPACE, "wand_netherite_bone",
                    Equipment.Tier.TIER_3, () -> Ingredient.ofItems(Items.NETHERITE_INGOT),
                    List.of(SpellSchools.SOUL.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(Identifier.of(Druids.MODID,"spirit_bolts")))
    );


    // MARK: Staves


    public static final Weapon.Entry boneStaff = add(Weapons.damageStaff(
                    NAMESPACE, "staff_bone",
                    Equipment.Tier.TIER_2, () -> Ingredient.ofItems(Items.GOLD_INGOT),
                    List.of(SpellSchools.SOUL.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(Identifier.of(Druids.MODID,"spirit_bolts")))
    );


    public static final Weapon.Entry netheriteBoneStaff = add(Weapons.damageStaff(
                    NAMESPACE, "staff_netherite_bone",
                    Equipment.Tier.TIER_3, () -> Ingredient.ofItems(Items.NETHERITE_INGOT),
                    List.of(SpellSchools.SOUL.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(Identifier.of(Druids.MODID,"spirit_bolts")))
    );


    // MARK: Register

    public static void register(Map<String, WeaponConfig> configs) {

        if (Druids.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(BETTER_END)) {
            var repair = ingredient("betterend:aeternium_ingot", FabricLoader.getInstance().isModLoaded(BETTER_END), Items.NETHERITE_INGOT);
            add(Weapons.damageStaff(NAMESPACE, "staff_crystal_bone", Equipment.Tier.TIER_4, repair, List.of(SpellSchools.SOUL.id))
                    .spellContainer(SpellContainers.forMagicWeapon().withSpellId(Identifier.of(Druids.MODID,"spirit_bolts")))
            );

        }

        Weapon.register(configs, entries, ItemsInit.KEY);
    }
}