package com.druids;

import com.druids.config.Default;
import com.druids.config.ServerConfig;
import com.druids.config.ServerConfigWrapper;
import com.druids.effects.CleansingFlame;
import com.druids.effects.EternalYouth;
import com.druids.entity.*;
import com.druids.items.Armors.Armors;
import com.druids.items.ItemsInit;
import com.druids.items.Weapons;
import com.druids.spells.Spells;
import com.extraspellattributes.ReabsorptionInit;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.spell_engine.api.item.ItemConfig;
import net.spell_engine.api.loot.LootConfig;
import net.tinyconfig.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.registry.Registries.ENTITY_TYPE;

public class Druids implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("druids");
	public static final String MODID = "druids";
	public static EntityType<DruidCyclone> CYCLONEENTITY;
	public static EntityType<TotemEntity> TOTEM;
	public static ServerConfig config;
	public static EntityType<SoulBombEntity> SoulFlare;
	public static EntityType<ChainLightning> ChainLightning;

	public static EntityType<DruidLightning> DRUIDLIGHTNING;


	public static RegistryEntry.Reference<StatusEffect> SOULFIRE;
	public static ConfigManager<ItemConfig> itemConfig = new ConfigManager<ItemConfig>
			("items_v1", Default.itemConfig)
			.builder()
			.setDirectory(MODID)
			.sanitize(true)
			.build();
	public static ConfigManager<LootConfig> lootConfig = new ConfigManager<LootConfig>
			("loot_v1", Default.lootConfig)
			.builder()
			.setDirectory(MODID)
			.sanitize(true)
			.constrain(LootConfig::constrainValues)
			.build();


	@Override
	public void onInitialize() {
		itemConfig.refresh();
		AutoConfig.register(ServerConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
		config = AutoConfig.getConfigHolder(ServerConfigWrapper.class).getConfig().server;


		SOULFIRE = Registry.registerReference(Registries.STATUS_EFFECT,Identifier.of(MODID,"soulfire"), new CleansingFlame(StatusEffectCategory.BENEFICIAL, 0xff4bdd)
				.addAttributeModifier(ReabsorptionInit.RECOUP, Identifier.of(MODID,"fireshield"),0.6, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE)
				.addAttributeModifier(ReabsorptionInit.WARDING, Identifier.of(MODID,"soulshield"),8, EntityAttributeModifier.Operation.ADD_VALUE));

		ItemsInit.register();
		Weapons.register(itemConfig.value.weapons);
		Armors.register(itemConfig.value.armor_sets);

		CYCLONEENTITY = Registry.register(
				ENTITY_TYPE,
				Identifier.of(MODID, "cycloneentity"),
				FabricEntityTypeBuilder.<DruidCyclone>create(SpawnGroup.MISC, DruidCyclone::new)
						.dimensions(EntityDimensions.fixed(4F, 2F)) // dimensions in Minecraft units of the render
						.trackRangeBlocks(128)
						.trackedUpdateRate(1)
						.build()
		);

		TOTEM = Registry.register(
				ENTITY_TYPE,
				Identifier.of(MODID, "totem"),
				FabricEntityTypeBuilder.<TotemEntity>create(SpawnGroup.MISC, TotemEntity::new)
						.dimensions(EntityDimensions.fixed(2F, 2F)) // dimensions in Minecraft units of the render
						.trackRangeBlocks(128)
						.trackedUpdateRate(1)
						.build()
		);
		SoulFlare = Registry.register(
				ENTITY_TYPE,
				Identifier.of(MODID, "soulflare"),
				FabricEntityTypeBuilder.<SoulBombEntity>create(SpawnGroup.MISC, SoulBombEntity::new)
						.dimensions(EntityDimensions.fixed(1F, 1F)) // dimensions in Minecraft units of the render
						.trackRangeBlocks(128)
						.trackedUpdateRate(1)
						.build()
		);
		ChainLightning = Registry.register(
				ENTITY_TYPE,
				Identifier.of(MODID, "chainlightning"),
				FabricEntityTypeBuilder.<ChainLightning>create(SpawnGroup.MISC, ChainLightning::new)
						.dimensions(EntityDimensions.fixed(1F, 1F)) // dimensions in Minecraft units of the render
						.trackRangeBlocks(128)
						.trackedUpdateRate(1)
						.build()
		);
		DRUIDLIGHTNING = Registry.register(
				ENTITY_TYPE,
				Identifier.of(MODID, "druidlightning"),
				FabricEntityTypeBuilder.<DruidLightning>create(SpawnGroup.MISC, DruidLightning::new)
						.dimensions(EntityDimensions.fixed(1F, 1F)) // dimensions in Minecraft units of the render
						.trackRangeBlocks(128)
						.trackedUpdateRate(1)
						.build()
		);
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		Spells.initializeSpells();

		itemConfig.save();
		//LOGGER.info("Hello Fabric world!");
	}
}