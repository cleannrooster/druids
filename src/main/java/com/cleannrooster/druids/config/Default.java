package com.cleannrooster.druids.config;


import java.util.*;
import java.util.stream.Collectors;

public class Default {
    public final static DruidsConfig itemConfig;

    static {
        itemConfig = new DruidsConfig();
        var largeWeight = 0;
        var smallWeight = 5;

    }

    @SafeVarargs
    private static <T> List<T> joinLists(List<T>... lists) {
        return Arrays.stream(lists).flatMap(Collection::stream).collect(Collectors.toList());
    }
}