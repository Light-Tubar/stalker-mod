package net.light.stalkermod.client.render;

import net.light.stalkermod.block.CopperGolemStatueBlock;
import net.light.stalkermod.block.entity.CopperGolemStatueBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.util.math.Direction;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CopperGolemStatueRenderer extends GeoBlockRenderer<CopperGolemStatueBlockEntity> {
    public CopperGolemStatueRenderer(BlockEntityRendererFactory.Context context) {
        super(new CopperGolemStatueModel());
    }

    @Override
    protected void rotateBlock(Direction facing, net.minecraft.client.util.math.MatrixStack poseStack) {
        float rot = facing.asRotation();
        poseStack.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(-rot));
    }
}