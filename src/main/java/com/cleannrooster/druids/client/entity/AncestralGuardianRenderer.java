package com.cleannrooster.druids.client.entity;

import com.cleannrooster.druids.entity.AncestralGuardianEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class AncestralGuardianRenderer extends MobEntityRenderer<AncestralGuardianEntity, BipedEntityModel<AncestralGuardianEntity>> {

    private static final Identifier TEXTURE = Identifier.of("soulwarden", "textures/entity/ancestral_guardian.png");

    public AncestralGuardianRenderer(EntityRendererFactory.Context context) {
        super(context, new BipedEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE)), 0.5f);
    }

    @Override
    protected RenderLayer getRenderLayer(AncestralGuardianEntity entity, boolean showBody, boolean translucent, boolean glowing) {
        return RenderLayer.getEntityTranslucentEmissive(getTexture(entity));
    }

    @Override
    protected int getBlockLight(AncestralGuardianEntity entity, BlockPos pos) {
        return 10;
    }

    @Override
    protected int getSkyLight(AncestralGuardianEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    public Identifier getTexture(AncestralGuardianEntity entity) {
        return TEXTURE;
    }
}
