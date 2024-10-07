package com.druids.client.entity;

import com.druids.entity.TotemEntity;
import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

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

}