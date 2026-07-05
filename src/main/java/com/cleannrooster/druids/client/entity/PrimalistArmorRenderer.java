package com.cleannrooster.druids.client.entity;

import com.cleannrooster.druids.Druids;
import com.cleannrooster.druids.entity.AncestralGuardianEntity;
import com.cleannrooster.druids.items.Armors.PrimalistArmor;
import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.common.render.entity.AzEntityRenderer;
import mod.azure.azurelib.common.render.entity.AzEntityRendererConfig;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class PrimalistArmorRenderer extends AzArmorRenderer {

    public static final Identifier MODEL = Identifier.of(
            Druids.MODID, "geo/primalist.geo.json"
    );
    public static final Identifier MODEL_netherite = Identifier.of(
            Druids.MODID, "geo/primalist_netherite.geo.json"
    );
    public static final Identifier TEXTURE = Identifier.of(
            Druids.MODID, "textures/armor/primalist.png"
    );
    public static final Identifier TEXTURE_netherite = Identifier.of(
            Druids.MODID, "textures/armor/primalist_netherite.png"
    );

    public PrimalistArmorRenderer(Identifier tex, Identifier model) {
        super(  AzArmorRendererConfig.builder(model,  tex)
                .build());
    }
}
