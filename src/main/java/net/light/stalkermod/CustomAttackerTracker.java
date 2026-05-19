package net.light.stalkermod;

import net.minecraft.entity.Entity;

public interface CustomAttackerTracker {
    void stalkermod$setLastAttacker(Entity entity);
    Entity stalkermod$getLastAttacker();
}