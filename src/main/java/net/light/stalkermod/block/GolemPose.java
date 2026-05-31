package net.light.stalkermod.block;

import net.minecraft.util.StringIdentifiable;

public enum GolemPose implements StringIdentifiable {
    STANDING("standing", 1),
    SITTING("sitting", 2),
    RUNNING("running", 3),
    STAR("star", 4);

    private final String name;
    private final int power;

    GolemPose(String name, int power) {
        this.name = name;
        this.power = power;
    }

    @Override
    public String asString() { return this.name; }

    public int getPower() { return this.power; }

    public GolemPose next() {
        return switch (this) {
            case STANDING -> SITTING;
            case SITTING -> RUNNING;
            case RUNNING -> STAR;
            case STAR -> STANDING;
        };
    }
}