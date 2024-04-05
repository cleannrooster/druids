package com.druids.items;

import com.spellbladenext.Spellblades;
import com.spellbladenext.items.Items;
import net.minecraft.item.Item;

import java.util.HashMap;

public class DruidsItems {
    public static final HashMap<String, Item> entries;
    static {
        entries = new HashMap<>();
        for(var weaponEntry: Weapons.entries) {
            entries.put(weaponEntry.id().toString(), weaponEntry.item());
        }


    }
}
