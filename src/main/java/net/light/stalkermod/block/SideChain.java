package net.light.stalkermod.block;

import net.minecraft.util.StringIdentifiable;

public enum SideChain implements StringIdentifiable {
    UNCONNECTED("unconnected"),
    LEFT("left"),
    CENTER("center"),
    RIGHT("right");

    private final String name;

    SideChain(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}