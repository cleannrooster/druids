package com.cleannrooster.druids.client.entity;

import com.cleannrooster.druids.entity.TotemEntity;
import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

public class TotemRenderer<T extends TotemEntity> extends GeoEntityRenderer<TotemEntity> {

    public TotemRenderer(EntityRendererFactory.Context context) {
        super(context, new TotemModel<>());

    }

    @Override
    public void render(TotemEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        if(entity.age <= 5){
            poseStack.translate(0, (float) (-2 * (5 - entity.age-partialTick)) /5,0);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);


    }

    @Override
    protected int getBlockLight(TotemEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    protected int getSkyLight(TotemEntity entity, BlockPos pos) {
        return 15;
    }
}