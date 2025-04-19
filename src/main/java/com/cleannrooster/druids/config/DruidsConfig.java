package com.cleannrooster.druids.config;

import net.spell_engine.api.config.ArmorSetConfig;
import net.spell_engine.api.config.WeaponConfig;

import java.util.HashMap;
import java.util.Map;

public class DruidsConfig {
    public DruidsConfig() {}
    public Map<String, WeaponConfig> melee_weapons = new HashMap();
    public Map<String, ArmorSetConfig> armor_sets = new HashMap();
}
