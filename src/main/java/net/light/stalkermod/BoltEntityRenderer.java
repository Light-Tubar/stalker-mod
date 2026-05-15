package net.light.stalkermod;

import net.light.stalkermod.entity.BoltEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class BoltEntityRenderer extends EntityRenderer<BoltEntity> {
    private final ItemRenderer itemRenderer;

    public BoltEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(BoltEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        
        matrices.push();

        
        matrices.translate(0.0, 0.125, 0.0);

        
        float currentPitch = net.minecraft.util.math.MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch());
        float currentYaw = net.minecraft.util.math.MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw());

        
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(currentYaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(currentPitch));

        
        matrices.scale(0.25f, 0.25f, 0.25f);

        
        this.itemRenderer.renderItem(entity.getStack(), ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), entity.getId());

        
        matrices.pop();

        
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(BoltEntity entity) {
        return net.minecraft.screen.PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
    }
}