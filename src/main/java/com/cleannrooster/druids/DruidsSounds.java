package com.cleannrooster.druids;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class DruidsSounds {

    public static final SoundEvent MAGIC_BOLT = SoundEvent.of(Identifier.of(Druids.MODID, "magic"));
    public static final SoundEvent RITUAL = SoundEvent.of(Identifier.of(Druids.MODID, "ritual"));
    public static final SoundEvent OMINOUS = SoundEvent.of(Identifier.of(Druids.MODID, "ominous"));

    public static void register() {
        Registry.register(Registries.SOUND_EVENT, Identifier.of(Druids.MODID, "magic"), MAGIC_BOLT);
        Registry.register(Registries.SOUND_EVENT, Identifier.of(Druids.MODID, "ritual"), RITUAL);
        Registry.register(Registries.SOUND_EVENT, Identifier.of(Druids.MODID, "ominous"), OMINOUS);
    }
}
