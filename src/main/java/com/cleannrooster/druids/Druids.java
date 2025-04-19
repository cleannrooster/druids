package com.cleannrooster.druids;

import com.cleannrooster.druids.config.Default;
import com.cleannrooster.druids.config.DruidsConfig;
import com.cleannrooster.druids.entity.AncestralWarrior;
import com.cleannrooster.druids.entity.SpectralGoat;
import com.cleannrooster.druids.entity.TotemEntity;
import com.cleannrooster.druids.entity.TotemTrail;
import com.cleannrooster.druids.items.Armors.Armors;
import com.cleannrooster.druids.items.ItemsInit;
import com.cleannrooster.druids.spells.SpellsV2;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.fabricmc.fabric.mixin.registry.sync.client.MinecraftClientMixin;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.spell_engine.api.effect.CustomStatusEffect;
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
	public static EntityType<TotemEntity<?>> TOTEM;

	public static EntityType<TotemTrail<?>> TOTEM_TRAIL;

	public static EntityType<AncestralWarrior> ANCESTRAL_WARRIOR;
	public static EntityType<SpectralGoat> SPECTRAL_GOAT;


	public static ConfigManager<DruidsConfig> itemConfig = new ConfigManager<DruidsConfig>
			("equipment", Default.itemConfig)
			.builder()
			.setDirectory(MODID)
			.sanitize(true)
			.build();

	@Override
	public void onInitialize() {
		itemConfig.refresh();
		try {
		DruidsEffects.register();
			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
				System.out.println("Synced ITEM registry to client.");
			}
		} catch (Exception ignore) {
			System.err.println("Error registering or syncing custom effects: " );

		}
		try {

		DruidsAttributes.register();
		} catch (Exception ignore) {
			System.err.println("Error registering or syncing custom attributes: ");
		}
		try {

			ItemsInit.register();
		} catch (Exception ignore) {
			System.err.println("Error registering or syncing custom items: ");
		}
		try {
		Armors.register(itemConfig.value.armor_sets);
		} catch (Exception ignore) {
			System.err.println("Error registering or syncing custom armors: ");
		}
		try {
		TOTEM_TRAIL  = Registry.register(
				ENTITY_TYPE,
				Identifier.of(MODID, "totem_trail"),
				FabricEntityTypeBuilder.<TotemTrail<?>>create(SpawnGroup.MISC, TotemTrail::new)
						.dimensions(EntityDimensions.fixed(0.1F, 0.1F)) // dimensions in Minecraft units of the render
						.trackRangeBlocks(128)
						.trackedUpdateRate(1)
						.build()
		);
		ANCESTRAL_WARRIOR  = Registry.register(
				ENTITY_TYPE,
				Identifier.of(MODID, "ancestral_warrior"),
				FabricEntityTypeBuilder.<AncestralWarrior>create(SpawnGroup.MONSTER, AncestralWarrior::new)
						.dimensions(EntityDimensions.fixed(0.6F, 1.8F)) // dimensions in Minecraft units of the render
						.trackRangeBlocks(128)
						.trackedUpdateRate(1)
						.build()
		);
		TOTEM  = Registry.register(
				ENTITY_TYPE,
				Identifier.of(MODID, "summon_totem"),
				FabricEntityTypeBuilder.<TotemEntity<?>>create(SpawnGroup.MONSTER, TotemEntity::new)
						.dimensions(EntityDimensions.fixed(0.6F, 1.8F)) // dimensions in Minecraft units of the render
						.trackRangeBlocks(128)
						.trackedUpdateRate(1)
						.build()
		);
		SPECTRAL_GOAT  = Registry.register(
				ENTITY_TYPE,
				Identifier.of(MODID, "spectral_goat"),
				FabricEntityTypeBuilder.<SpectralGoat>create(SpawnGroup.MONSTER, SpectralGoat::new)
						.dimensions(EntityDimensions.fixed(3F, 3F)) // dimensions in Minecraft units of the render
						.trackRangeBlocks(128)
						.trackedUpdateRate(1)
						.build()
		);
		} catch (Exception ignore) {
			System.err.println("Error registering or syncing custom entities: ");
		}
		FabricDefaultAttributeRegistry.register(ANCESTRAL_WARRIOR,AncestralWarrior.createMobAttributes().add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,10));
		FabricDefaultAttributeRegistry.register(TOTEM_TRAIL,TotemTrail.createMobAttributes().add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,10));
		FabricDefaultAttributeRegistry.register(SPECTRAL_GOAT,SpectralGoat.createMobAttributes().add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,10));
		FabricDefaultAttributeRegistry.register(TOTEM,TotemEntity.createMobAttributes().add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,10));

		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		SpellsV2.registerDeliveries();
		itemConfig.save();
		//LOGGER.info("Hello Fabric world!");
	}
}