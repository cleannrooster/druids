package com.cleannrooster.druids.client.entity;

import com.cleannrooster.druids.entity.SpectralGoat;
import com.cleannrooster.druids.Druids;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SpectralGoatRenderer extends MobEntityRenderer<SpectralGoat, SpectralGoatModel<SpectralGoat>> {
    private static final Identifier TEXTURE = Identifier.of(Druids.MODID, "textures/mob/goat.png");;

    public SpectralGoatRenderer(EntityRendererFactory.Context context) {

        super(context, new SpectralGoatModel(context.getPart(EntityModelLayers.GOAT)), 1.6F);
    }

    @Override
    protected void scale(SpectralGoat entity, MatrixStack matrices, float amount) {
        matrices.scale(3,3,3);
        super.scale(entity, matrices, amount);
    }

    public Identifier getTexture(SpectralGoat goatEntity) {
        return TEXTURE;
    }
}
