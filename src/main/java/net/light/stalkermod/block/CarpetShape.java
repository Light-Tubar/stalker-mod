package net.light.stalkermod.block;

import net.minecraft.util.StringIdentifiable;

public enum CarpetShape implements StringIdentifiable {
    NONE("none"),
    LOW("low");

    private final String name;

    CarpetShape(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}