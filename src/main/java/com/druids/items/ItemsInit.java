package com.druids.items;

import com.druids.Druids;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spell_engine.api.item.trinket.SpellBooks;
import net.spell_engine.api.item.weapon.Weapon;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.List;

import static com.druids.Druids.MODID;

public class ItemsInit {
    public static final ArrayList<Weapon.Entry> entries = new ArrayList<>();

    public static ItemGroup DRUIDS;

    public static Item FROSTSHARD;
    public static Item ARCANESHARD;
    public static Item FIRESHARD;
    public static Item LIGHTNINGSHARD;

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

        ARCANESHARD = registerItem("shard_arcane",new Shard(Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND,
                () -> Ingredient.ofItems(Items.AMETHYST_SHARD)), new Item.Settings().attributeModifiers(new
                AttributeModifiersComponent(List.of(new AttributeModifiersComponent.Entry(SpellSchools.ARCANE.attributeEntry,
                new EntityAttributeModifier(Identifier.of(MODID,"arcaneshard"),0.15D, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), AttributeModifierSlot.OFFHAND),    new AttributeModifiersComponent.Entry(SpellSchools.SOUL.attributeEntry,
                new EntityAttributeModifier(Identifier.of(MODID,"arcaneshardsoul"),4D, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.OFFHAND)),true)
        ), SpellSchools.ARCANE));
        FROSTSHARD = registerItem("shard_frost",new Shard(Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND,
                () -> Ingredient.ofItems(Items.PRISMARINE_CRYSTALS)), new Item.Settings().attributeModifiers(new
                AttributeModifiersComponent(List.of(new AttributeModifiersComponent.Entry(SpellSchools.FROST.attributeEntry,
                new EntityAttributeModifier(Identifier.of(MODID,"frostshard"),0.15D, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), AttributeModifierSlot.OFFHAND),
                new AttributeModifiersComponent.Entry(SpellSchools.SOUL.attributeEntry,
                        new EntityAttributeModifier(Identifier.of(MODID,"frostshardsoul"),4D, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.OFFHAND)),true)
        ), SpellSchools.FROST));
        FIRESHARD = registerItem("shard_fire",new Shard(Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND,
                () -> Ingredient.ofItems(Items.AMETHYST_SHARD)), new Item.Settings().attributeModifiers(new
                AttributeModifiersComponent(List.of(new AttributeModifiersComponent.Entry(SpellSchools.FIRE.attributeEntry,
                new EntityAttributeModifier(Identifier.of(MODID,"fireshard"),0.15D, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), AttributeModifierSlot.OFFHAND),    new AttributeModifiersComponent.Entry(SpellSchools.SOUL.attributeEntry,
                new EntityAttributeModifier(Identifier.of(MODID,"fireshardsoul"),4D, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.OFFHAND)),true)
        ), SpellSchools.FIRE));
        LIGHTNINGSHARD = registerItem("shard_lightning",new Shard(Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND,
                () -> Ingredient.ofItems(Items.WIND_CHARGE)), new Item.Settings().attributeModifiers(new
                AttributeModifiersComponent(List.of(new AttributeModifiersComponent.Entry(SpellSchools.LIGHTNING.attributeEntry,
                new EntityAttributeModifier(Identifier.of(MODID,"lightningshard"),0.15D, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), AttributeModifierSlot.OFFHAND),    new AttributeModifiersComponent.Entry(SpellSchools.SOUL.attributeEntry,
                new EntityAttributeModifier(Identifier.of(MODID,"lightningshardsoul"),4D, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.OFFHAND)),true)
        ), SpellSchools.LIGHTNING));

        DRUIDS = FabricItemGroup.builder()
                .icon(() -> new ItemStack(ARCANESHARD))
                .displayName(Text.translatable("itemGroup.druids.general"))
                .build();
        Registry.register(Registries.ITEM_GROUP, KEY, DRUIDS);
        SpellBooks.createAndRegister(Identifier.of(MODID,"aetherial_druid"),KEY);
        SpellBooks.createAndRegister(Identifier.of(MODID,"storm_druid"),KEY);
        addItemToGroup(ARCANESHARD);
        addItemToGroup(FROSTSHARD);
        addItemToGroup(FIRESHARD);
        addItemToGroup(LIGHTNINGSHARD);

    }
}
