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




}