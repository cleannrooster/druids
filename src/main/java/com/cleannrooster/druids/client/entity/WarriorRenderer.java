package com.cleannrooster.druids.client.entity;

import com.cleannrooster.druids.entity.AncestralWarrior;
import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import mod.azure.azurelib.common.internal.common.cache.object.BakedGeoModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

public class WarriorRenderer<T extends AncestralWarrior> extends GeoEntityRenderer<AncestralWarrior> {

    public WarriorRenderer(EntityRendererFactory.Context context) {
        super(context, new WarriorModel<>());
        addRenderLayer(new RenderLayerItemWarriorEntity<>(this));

    }


    @Override
    protected int getBlockLight(AncestralWarrior entity, BlockPos pos) {
        return 15;
    }

    @Override
    public void actuallyRender(MatrixStack poseStack, AncestralWarrior animatable, BakedGeoModel model, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        colour = (int)(colour * Math.min(1,(animatable.getTick(animatable)+partialTick)/20));
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
    @Override
    protected int getSkyLight(AncestralWarrior entity, BlockPos pos) {
        return 15;
    }
}