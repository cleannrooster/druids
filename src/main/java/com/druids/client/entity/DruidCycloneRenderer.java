package com.druids.client.entity;

import com.druids.entity.DruidCyclone;
import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import mod.azure.azurelib.common.internal.common.cache.object.BakedGeoModel;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class DruidCycloneRenderer<T extends DruidCyclone> extends GeoEntityRenderer<DruidCyclone> {

    public DruidCycloneRenderer(EntityRendererFactory.Context context) {
        super(context, new DruidCycloneModel<>());

    }

    @Override
    public void preRender(MatrixStack poseStack, DruidCyclone animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

                this.scaleWidth = 2 * 1.75F;
                this.scaleHeight = 2 * 1.5F;





        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

}