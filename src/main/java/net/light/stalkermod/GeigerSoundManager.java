package net.light.stalkermod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Box;

public class GeigerSoundManager {
    private static int tickDelay = 0;
    private static double lastClosestDistSq = Double.MAX_VALUE;

    
    private static final SoundEvent[] CLICKS = {
            StalkerMod.GEIGER_1,
            StalkerMod.GEIGER_2,
            StalkerMod.GEIGER_3,
            StalkerMod.GEIGER_4
    };

    public static void tick(MinecraftClient client) {
        if (client.player == null || client.world == null || client.isPaused()) return;

        tickDelay++;
        if (tickDelay >= 2) {
            tickDelay = 0;
            lastClosestDistSq = Double.MAX_VALUE;

            Box searchBox = client.player.getBoundingBox().expand(12.0);

            for (Entity entity : client.world.getOtherEntities(client.player, searchBox, e ->
                    (e instanceof EffectZoneEntity && ((EffectZoneEntity) e).type == 1))) {

                double dist = client.player.squaredDistanceTo(entity.getPos());
                if (dist < lastClosestDistSq) {
                    lastClosestDistSq = dist;
                }
            }

            if (lastClosestDistSq <= 144.0) {
                double distance = Math.max(1.0, Math.sqrt(lastClosestDistSq));

                
                
                double clickProbability = Math.pow(1.0 - (distance / 12.0), 2);

                if (client.world.random.nextDouble() < clickProbability) {
                    
                    SoundEvent randomSound = CLICKS[client.world.random.nextInt(CLICKS.length)];

                    
                    float pitch = 0.95f + client.world.random.nextFloat() * 0.1f;

                    
                    float volume = 0.15f + (float) (1.0 - (distance / 12.0)) * 0.5f;

                    client.world.playSound(
                            client.player.getX(), client.player.getY(), client.player.getZ(),
                            randomSound,
                            net.minecraft.sound.SoundCategory.AMBIENT,
                            volume, pitch, false
                    );
                }
            }
        }
    }
}