package com.cleannrooster.druids.client.entity;

import com.cleannrooster.druids.entity.TotemTrail;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SpiderEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class SpiderLightningRenderer<T extends TotemTrail<?>> extends MobEntityRenderer<T, SpiderEntityModel<T>> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/spider/spider.png");

    public SpiderLightningRenderer(EntityRendererFactory.Context context) {
        this(context, EntityModelLayers.SPIDER);
    }

    public SpiderLightningRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer) {
        super(ctx, new SpiderEntityModel(ctx.getPart(layer)), 0.8F);
        this.addFeature(new SpiderEyesFeatureRenderer(this));
    }

    @Override
    public void render(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
    }

    protected float getLyingAngle(T spiderEntity) {
        return 180.0F;
    }

    public Identifier getTexture(T spiderEntity) {
        return TEXTURE;
    }

}
