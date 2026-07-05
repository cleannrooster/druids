package com.cleannrooster.druids.config;


import net.spell_engine.api.config.ConfigFile;

import java.util.*;
import java.util.stream.Collectors;

public class Default {
    public final static ConfigFile.Equipment itemConfig;

    static {
        itemConfig =  new ConfigFile.Equipment();
        var largeWeight = 0;
        var smallWeight = 5;

    }

    @SafeVarargs
    private static <T> List<T> joinLists(List<T>... lists) {
        return Arrays.stream(lists).flatMap(Collection::stream).collect(Collectors.toList());
    }
}