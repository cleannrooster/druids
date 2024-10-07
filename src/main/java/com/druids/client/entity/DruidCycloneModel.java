package com.druids.client.entity;

import com.druids.Druids;
import com.druids.entity.DruidCyclone;
import mod.azure.azurelib.common.api.client.model.GeoModel;
import net.minecraft.util.Identifier;

public class DruidCycloneModel<T extends DruidCyclone> extends GeoModel<DruidCyclone> {

    @Override
    public Identifier getModelResource(DruidCyclone reaver) {

        return Identifier.of(Druids.MODID,"geo/cyclone.json");
    }
    @Override
    public Identifier getTextureResource(DruidCyclone reaver) {
        if(reaver.getColor() == 0) {
            return Identifier.of(Druids.MODID, "textures/mob/soulwhirlwind.png");
        }
        else if (reaver.getColor() == 1 || reaver.getColor() == 5){
            return Identifier.of(Druids.MODID, "textures/mob/tempest.png");
        }
        else if (reaver.getColor() == 2){
            return Identifier.of(Druids.MODID, "textures/mob/inferno.png");
        }

        return Identifier.of(Druids.MODID, "textures/mob/whirlwind.png");

    }

    @Override
    public Identifier getAnimationResource(DruidCyclone reaver) {
        return Identifier.of(Druids.MODID,"animations/cyclone.animation.json");
    }

}
