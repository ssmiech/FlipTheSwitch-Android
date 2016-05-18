package com.github.michaelengland.fliptheswitch.gradle;

import java.util.HashMap;
import java.util.Map;

public class ProductFlavorConfig {
    private final String name;

    private Map<String, Boolean> overrides;

    public ProductFlavorConfig(final String name) {
        this.name = name;
        overrides = new HashMap<>();
    }

    public String name() {
        return name;
    }

    public Map<String, Boolean> overrides() {
        return overrides;
    }

    public void overrides(final Map<String, Boolean> overrides) {
        this.overrides = overrides;
    }
}