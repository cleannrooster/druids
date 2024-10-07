package com.druids.config;

import com.cleannrooster.hexblade.Armors.Armors;
import com.cleannrooster.hexblade.item.Items;
import com.druids.items.DruidsItems;
import com.druids.items.ItemsInit;
import com.druids.items.Weapons;
import net.spell_engine.api.item.ItemConfig;
import net.spell_engine.api.item.armor.Armor;
import net.spell_engine.api.item.weapon.Weapon;
import net.spell_engine.api.loot.LootConfig;

import java.util.*;
import java.util.stream.Collectors;

public class Default {
    public static final ItemConfig itemConfig = new ItemConfig();
    public static final LootConfig lootConfig;

    public Default() {
    }

    @SafeVarargs
    private static <T> List<T> joinLists(List<T>... lists) {
        return (List)Arrays.stream(lists).flatMap(Collection::stream).collect(Collectors.toList());
    }

    static {
        Iterator var0 = ItemsInit.entries.iterator();

        while(var0.hasNext()) {
            Weapon.Entry weapon = (Weapon.Entry)var0.next();
            itemConfig.weapons.put(weapon.name(), weapon.defaults());
        }

        var0 = Armors.entries.iterator();

        while(var0.hasNext()) {
            Armor.Entry armor = (Armor.Entry)var0.next();
            itemConfig.armor_sets.put(armor.name(), armor.defaults());
        }

        lootConfig = new LootConfig();
        new LootConfig.Pool();
        String var3 = "weapons";
    }
}