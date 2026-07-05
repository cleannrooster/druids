package com.cleannrooster.druids.items;

import com.cleannrooster.druids.items.Armors.Armors;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.rpg_series.item.Weapon;
import net.spell_engine.utils.TargetHelper;
import net.spell_engine.utils.VectorHelper;

import java.util.ArrayList;

import static com.cleannrooster.druids.Druids.MODID;

public class ItemsInit {
    public static final ArrayList<Weapon.Entry> entries = new ArrayList<>();

    public static Item SOUL_FRAGMENT_ITEM;

    public static ItemGroup DRUIDS;



    public static RegistryKey<ItemGroup> KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(),Identifier.of(MODID,"generic"));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, Identifier.of(MODID,name),item);

    }


    private static void addItemToGroup(Item item){
        ItemGroupEvents.modifyEntriesEvent(KEY).register((content) -> {
            content.add(item);
        });
    }
    public static void register() {

        DRUIDS = FabricItemGroup.builder()
                .icon(() -> new ItemStack(Armors.primalistNetherite.head))
                .displayName(Text.translatable("itemGroup.soulwarden.general"))
                .build();
        Registry.register(Registries.ITEM_GROUP, KEY, DRUIDS);
      //  SpellBooks.createAndRegister(Identifier.of(MODID,"ancestral_warrior"),KEY);

        SOUL_FRAGMENT_ITEM = registerItem("soul_fragment", new Item(new Item.Settings()));

    }
}
