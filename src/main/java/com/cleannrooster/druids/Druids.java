package com.cleannrooster.druids;

import com.cleannrooster.druids.config.Default;
import com.cleannrooster.druids.config.DruidsConfig;
import com.cleannrooster.druids.config.TweaksConfig;
import com.cleannrooster.druids.entity.AncestralGuardianEntity;
import com.cleannrooster.druids.entity.BoundSoulEntity;
import com.cleannrooster.druids.entity.SoulFragmentEntity;
import com.cleannrooster.druids.entity.TotemEntity;
import com.cleannrooster.druids.items.Armors.Armors;
import com.cleannrooster.druids.items.DruidWeapons;
import com.cleannrooster.druids.items.ItemsInit;
import com.cleannrooster.druids.spells.SpellsV2;
import com.cleannrooster.druids.DruidsSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spell_engine.api.config.ConfigFile;
import net.spell_engine.api.render.CustomModels;
import net.tiny_config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.minecraft.registry.Registries.ENTITY_TYPE;

public class Druids implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("soulwarden");
	public static final String MODID = "soulwarden";

	public static EntityType<SoulFragmentEntity> SOUL_FRAGMENT;
	public static EntityType<BoundSoulEntity> BOUND_SOUL;
	public static EntityType<AncestralGuardianEntity> ANCESTRAL_GUARDIAN;
	public static EntityType<TotemEntity> SUMMON_TOTEM;
    public static ConfigManager<TweaksConfig> tweaksConfig = new ConfigManager<>
            ("tweaks", new TweaksConfig())
            .builder()
            .setDirectory(MODID)
            .sanitize(true)
            .build();

	public static ConfigManager<ConfigFile.Equipment> itemConfig = new ConfigManager<ConfigFile.Equipment>
			("equipment", Default.itemConfig)
			.builder()
			.setDirectory(MODID)
			.sanitize(true)
			.build();

	@Override
	public void onInitialize() {
		itemConfig.refresh();
        DruidWeapons.register(itemConfig.value.weapons);
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

		SOUL_FRAGMENT = Registry.register(
				ENTITY_TYPE,
				Identifier.of(MODID, "soul_fragment"),
				FabricEntityTypeBuilder.<SoulFragmentEntity>create(SpawnGroup.MISC, SoulFragmentEntity::new)
						.dimensions(EntityDimensions.fixed(0.5F, 0.5F))
						.trackRangeBlocks(64)
						.trackedUpdateRate(10)
						.build()
		);
		BOUND_SOUL = Registry.register(
				ENTITY_TYPE,
				Identifier.of(MODID, "bound_soul"),
				FabricEntityTypeBuilder.<BoundSoulEntity>create(SpawnGroup.MISC, BoundSoulEntity::new)
						.dimensions(EntityDimensions.fixed(0.6F, 1.8F))
						.trackRangeBlocks(64)
						.trackedUpdateRate(3)
						.build()
		);
		ANCESTRAL_GUARDIAN = Registry.register(
				ENTITY_TYPE,
				Identifier.of(MODID, "ancestral_guardian"),
				FabricEntityTypeBuilder.<AncestralGuardianEntity>create(SpawnGroup.MISC, AncestralGuardianEntity::new)
						.dimensions(EntityDimensions.fixed(0.6F, 1.8F))
						.trackRangeBlocks(64)
						.trackedUpdateRate(3)
						.build()
		);
		SUMMON_TOTEM = Registry.register(
				ENTITY_TYPE,
				Identifier.of(MODID, "summon_totem"),
				FabricEntityTypeBuilder.<TotemEntity>create(SpawnGroup.MISC, TotemEntity::new)
						.dimensions(EntityDimensions.fixed(0.8F, 1.8F))
						.trackRangeBlocks(64)
						.trackedUpdateRate(4)
						.build()
		);
		} catch (Exception ignore) {
			System.err.println("Error registering or syncing custom entities: ");
		}
		FabricDefaultAttributeRegistry.register(SOUL_FRAGMENT, SoulFragmentEntity.createMobAttributes());
		FabricDefaultAttributeRegistry.register(BOUND_SOUL, BoundSoulEntity.createMobAttributes());
		FabricDefaultAttributeRegistry.register(ANCESTRAL_GUARDIAN, AncestralGuardianEntity.createMobAttributes());
		FabricDefaultAttributeRegistry.register(SUMMON_TOTEM, TotemEntity.createAttributes());
        CustomModels.registerModelIds(List.of(Identifier.of(MODID,"spell_projectile/necrospirit")));
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		DruidsSounds.register();
		SpellsV2.registerDeliveries();
		itemConfig.save();
		//LOGGER.info("Hello Fabric world!");
	}
}