package com.cleannrooster.druids.client.entity;

import com.cleannrooster.druids.entity.BoundSoulEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class BoundSoulRenderer extends MobEntityRenderer<BoundSoulEntity, BipedEntityModel<BoundSoulEntity>> {

    // Vanilla zombie-family textures — texture packs replace these automatically.
    // Index must stay in sync with the range passed to nextInt() in SpellsV2.
    private static final Identifier[] ZOMBIE_TEXTURES = {
        Identifier.ofVanilla("textures/entity/zombie/zombie.png"),
        Identifier.ofVanilla("textures/entity/zombie/husk.png"),
        Identifier.ofVanilla("textures/entity/zombie/drowned.png"),
    };

    public BoundSoulRenderer(EntityRendererFactory.Context context) {
        super(context, new BipedEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE)), 0.5f);
    }

    // Wrap the vertex consumer provider so every vertex alpha is multiplied by
    // ~55%, giving a translucent ghost appearance without requiring a custom texture.
    @Override
    public void render(BoundSoulEntity entity, float yaw, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        VertexConsumerProvider wrapped =
                layer -> new AlphaModifyingVertexConsumer(vertexConsumers.getBuffer(layer), 0.55f);
        super.render(entity, yaw, tickDelta, matrices, wrapped, light);
    }

    // Emissive translucent layer — glows even in the dark and is see-through.
    @Override
    protected RenderLayer getRenderLayer(BoundSoulEntity entity, boolean showBody,
                                         boolean translucent, boolean glowing) {
        return RenderLayer.getEntityTranslucentEmissive(getTexture(entity));
    }

    @Override
    protected int getBlockLight(BoundSoulEntity entity, BlockPos pos) { return 10; }

    @Override
    protected int getSkyLight(BoundSoulEntity entity, BlockPos pos) { return 15; }

    @Override
    public Identifier getTexture(BoundSoulEntity entity) {
        int variant = entity.getTextureVariant();
        return ZOMBIE_TEXTURES[variant % ZOMBIE_TEXTURES.length];
    }

    // -------------------------------------------------------------------------
    // Thin VertexConsumer wrapper that scales alpha so the soul is translucent.
    // Records automatically satisfy the interface — just delegate every method.
    // -------------------------------------------------------------------------
    private record AlphaModifyingVertexConsumer(VertexConsumer inner, float alpha)
            implements VertexConsumer {

        @Override
        public VertexConsumer vertex(float x, float y, float z) {
            inner.vertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer color(int r, int g, int b, int a) {
            inner.color(r, g, b, (int)(a * alpha));
            return this;
        }

        @Override
        public VertexConsumer texture(float u, float v) {
            inner.texture(u, v);
            return this;
        }

        @Override
        public VertexConsumer overlay(int u, int v) {
            inner.overlay(u, v);
            return this;
        }

        @Override
        public VertexConsumer light(int u, int v) {
            inner.light(u, v);
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            inner.normal(x, y, z);
            return this;
        }
    }
}
