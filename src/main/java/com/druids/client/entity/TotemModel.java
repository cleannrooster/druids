package com.druids.client.entity;

import com.druids.Druids;
import com.druids.entity.TotemEntity;
import mod.azure.azurelib.common.api.client.model.GeoModel;
import net.minecraft.util.Identifier;

public class TotemModel<T extends TotemEntity> extends GeoModel<TotemEntity> {

    @Override
    public Identifier getModelResource(TotemEntity reaver) {

        return Identifier.of(Druids.MODID,"geo/totem.geo.json");
    }
    @Override
    public Identifier getTextureResource(TotemEntity reaver) {

        return Identifier.of(Druids.MODID, "textures/mob/totem.png");

    }

    @Override
    public Identifier getAnimationResource(TotemEntity reaver) {
        return null;
    }

}
