package net.light.stalkermod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class EffectZoneEntityRenderer extends EntityRenderer<EffectZoneEntity> {

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
        String typeName = entity.type == 1 ? "§a[Рад]" : "§d[Пси]";
        Text customText = Text.literal(typeName + " Сила: " + entity.strength);

        matrices.push();
        matrices.translate(0.0D, 1.0D, 0.0D);
        super.renderLabelIfPresent(entity, customText, matrices, vertexConsumers, light, tickDelta);
        matrices.pop();
    }

    @Override
    public Identifier getTexture(EffectZoneEntity entity) {
        return null;
    }
}