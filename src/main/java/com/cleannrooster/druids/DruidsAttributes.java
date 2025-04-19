package com.cleannrooster.druids;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.entry.RegistryEntry;

public class DruidsAttributes {
    public static RegistryEntry<EntityAttribute> TOTEMHEALTH ;


    public static void register(){
        ((TOTEMHEALTH.value())).setTracked(true);


    }
}
