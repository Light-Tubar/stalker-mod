package net.light.stalkermod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Box;

public class GeigerSoundManager {
    private static int tickDelay = 0;
    private static final SoundEvent[] CLICKS = {
            StalkerMod.GEIGER_1,
            StalkerMod.GEIGER_2,
            StalkerMod.GEIGER_3,
            StalkerMod.GEIGER_4};
    public static void tick(MinecraftClient client) {
        if (client.player == null || client.world == null || client.isPaused()) return;
        tickDelay++;
        if (tickDelay >= 2) {
            tickDelay = 0;
            float maxRadInfluence = 0.0f;
            Box searchBox = client.player.getBoundingBox().expand(15.0);
            for (Entity entity : client.world.getOtherEntities(client.player, searchBox, e ->
                    (e instanceof EffectZoneEntity && ((EffectZoneEntity) e).type == 1))) {
                EffectZoneEntity zone = (EffectZoneEntity) entity;
                float strength = zone.strength;
                if (zone.getZoneBox().contains(client.player.getPos())) {
                    if (strength > maxRadInfluence) {
                        maxRadInfluence = strength;}} else {
                    double dist = Math.sqrt(client.player.squaredDistanceTo(zone.getPos()));
                    if (dist < 15.0) {
                        float influence = strength * (float) Math.pow(1.0 - (dist / 15.0), 2);
                        if (influence > maxRadInfluence) {
                            maxRadInfluence = influence;}}}}
            float invRad = StalkerMod.getInventoryRadiation(client.player);
            float totalRad = maxRadInfluence + invRad;
            if (totalRad > 0.05f) {
                double clickProbability = Math.min(0.85, totalRad * 0.06);
                if (client.world.random.nextDouble() < clickProbability) {
                    SoundEvent randomSound = CLICKS[client.world.random.nextInt(CLICKS.length)];
                    float pitch = 0.9f + Math.min(0.2f, totalRad * 0.03f) + client.world.random.nextFloat() * 0.1f;
                    float volume = 0.1f + Math.min(0.5f, totalRad * 0.06f);
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