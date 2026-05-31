package net.light.stalkermod.client.render;

import net.light.stalkermod.block.ShelfBlock;
import net.light.stalkermod.block.entity.ShelfBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class ShelfBlockEntityRenderer implements BlockEntityRenderer<ShelfBlockEntity> {
    private final ItemRenderer itemRenderer;

    public ShelfBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }

    @Override
    public void render(ShelfBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Direction facing = entity.getCachedState().get(ShelfBlock.FACING);

        for (int i = 0; i < 3; i++) {
            ItemStack stack = entity.getStack(i);
            if (stack.isEmpty()) continue;

            matrices.push();

            matrices.translate(0.5, 0.5, 0.5);

            float rot = facing.asRotation();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-rot));

            double xOffset = (i - 1) * 0.3;
            matrices.translate(xOffset, 0.0, -0.3);

            matrices.scale(0.4f, 0.4f, 0.4f);

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));

            this.itemRenderer.renderItem(stack, ModelTransformationMode.FIXED, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
            matrices.pop();
        }
    }
}