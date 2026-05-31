package net.light.stalkermod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

public class EffectZoneEntityRenderer extends EntityRenderer<EffectZoneEntity> {

    private static final Identifier FORCEFIELD_TEXTURE = Identifier.of("minecraft", "textures/misc/forcefield.png");

    public EffectZoneEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    protected boolean hasLabel(EffectZoneEntity entity) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        return player != null && player.isCreative() && (player.getMainHandStack().isOf(StalkerMod.ANOMALY_SPAWNER) || player.getOffHandStack().isOf(StalkerMod.ANOMALY_SPAWNER));
    }

    @Override
    protected void renderLabelIfPresent(EffectZoneEntity entity, Text ignoredText, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float tickDelta) {
        String typeName = entity.type == 1 ? "§a[Рад]" : entity.type == 2 ? "§d[Пси]" : "§b[Укрытие]";
        Text customText = Text.literal(typeName + (entity.type == 3 ? "" : " Сила: " + entity.strength));

        matrices.push();
        matrices.translate(0.0D, 1.0D, 0.0D);
        super.renderLabelIfPresent(entity, customText, matrices, vertexConsumers, light, tickDelta);
        matrices.pop();
    }

    @Override
    public void render(EffectZoneEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);

        if (!hasLabel(entity)) return;

        Box box = entity.getZoneBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
        matrices.push();

        float time = (entity.age + tickDelta) * 0.015f;

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(FORCEFIELD_TEXTURE));

        int r = entity.type == 1 ? 50 : entity.type == 2 ? 180 : 50;
        int g = entity.type == 1 ? 255 : entity.type == 2 ? 80 : 200;
        int b = entity.type == 1 ? 50 : entity.type == 2 ? 255 : 255;
        int a = 100;

        MatrixStack.Entry entry = matrices.peek();
        drawBoxFaces(entry, buffer, box, r, g, b, a, time);

        matrices.pop();
    }

    private void drawBoxFaces(MatrixStack.Entry entry, VertexConsumer buffer, Box b, int r, int g, int blue, int a, float time) {
        float minX = (float)b.minX; float minY = (float)b.minY; float minZ = (float)b.minZ;
        float maxX = (float)b.maxX; float maxY = (float)b.maxY; float maxZ = (float)b.maxZ;
        int l = LightmapTextureManager.MAX_LIGHT_COORDINATE;
        int o = OverlayTexture.DEFAULT_UV;

        float v0 = 0.0f - time;
        float v1 = (maxY - minY) - time;
        float uX = (maxX - minX);
        float uZ = (maxZ - minZ);

        buffer.vertex(entry, minX, maxY, minZ).color(r, g, blue, a).texture(0, v0).overlay(o).light(l).normal(entry, 0, 0, -1);
        buffer.vertex(entry, maxX, maxY, minZ).color(r, g, blue, a).texture(uX, v0).overlay(o).light(l).normal(entry, 0, 0, -1);
        buffer.vertex(entry, maxX, minY, minZ).color(r, g, blue, a).texture(uX, v1).overlay(o).light(l).normal(entry, 0, 0, -1);
        buffer.vertex(entry, minX, minY, minZ).color(r, g, blue, a).texture(0, v1).overlay(o).light(l).normal(entry, 0, 0, -1);

        buffer.vertex(entry, minX, minY, maxZ).color(r, g, blue, a).texture(0, v1).overlay(o).light(l).normal(entry, 0, 0, 1);
        buffer.vertex(entry, maxX, minY, maxZ).color(r, g, blue, a).texture(uX, v1).overlay(o).light(l).normal(entry, 0, 0, 1);
        buffer.vertex(entry, maxX, maxY, maxZ).color(r, g, blue, a).texture(uX, v0).overlay(o).light(l).normal(entry, 0, 0, 1);
        buffer.vertex(entry, minX, maxY, maxZ).color(r, g, blue, a).texture(0, v0).overlay(o).light(l).normal(entry, 0, 0, 1);

        buffer.vertex(entry, minX, minY, minZ).color(r, g, blue, a).texture(0, v1).overlay(o).light(l).normal(entry, -1, 0, 0);
        buffer.vertex(entry, minX, minY, maxZ).color(r, g, blue, a).texture(uZ, v1).overlay(o).light(l).normal(entry, -1, 0, 0);
        buffer.vertex(entry, minX, maxY, maxZ).color(r, g, blue, a).texture(uZ, v0).overlay(o).light(l).normal(entry, -1, 0, 0);
        buffer.vertex(entry, minX, maxY, minZ).color(r, g, blue, a).texture(0, v0).overlay(o).light(l).normal(entry, -1, 0, 0);

        buffer.vertex(entry, maxX, maxY, minZ).color(r, g, blue, a).texture(0, v0).overlay(o).light(l).normal(entry, 1, 0, 0);
        buffer.vertex(entry, maxX, maxY, maxZ).color(r, g, blue, a).texture(uZ, v0).overlay(o).light(l).normal(entry, 1, 0, 0);
        buffer.vertex(entry, maxX, minY, maxZ).color(r, g, blue, a).texture(uZ, v1).overlay(o).light(l).normal(entry, 1, 0, 0);
        buffer.vertex(entry, maxX, minY, minZ).color(r, g, blue, a).texture(0, v1).overlay(o).light(l).normal(entry, 1, 0, 0);

        buffer.vertex(entry, minX, maxY, minZ).color(r, g, blue, a).texture(0, 0).overlay(o).light(l).normal(entry, 0, 1, 0);
        buffer.vertex(entry, minX, maxY, maxZ).color(r, g, blue, a).texture(0, uZ).overlay(o).light(l).normal(entry, 0, 1, 0);
        buffer.vertex(entry, maxX, maxY, maxZ).color(r, g, blue, a).texture(uX, uZ).overlay(o).light(l).normal(entry, 0, 1, 0);
        buffer.vertex(entry, maxX, maxY, minZ).color(r, g, blue, a).texture(uX, 0).overlay(o).light(l).normal(entry, 0, 1, 0);

        buffer.vertex(entry, minX, minY, minZ).color(r, g, blue, a).texture(0, 0).overlay(o).light(l).normal(entry, 0, -1, 0);
        buffer.vertex(entry, maxX, minY, minZ).color(r, g, blue, a).texture(uX, 0).overlay(o).light(l).normal(entry, 0, -1, 0);
        buffer.vertex(entry, maxX, minY, maxZ).color(r, g, blue, a).texture(uX, uZ).overlay(o).light(l).normal(entry, 0, -1, 0);
        buffer.vertex(entry, minX, minY, maxZ).color(r, g, blue, a).texture(0, uZ).overlay(o).light(l).normal(entry, 0, -1, 0);
    }

    @Override
    public Identifier getTexture(EffectZoneEntity entity) {
        return FORCEFIELD_TEXTURE;
    }
}