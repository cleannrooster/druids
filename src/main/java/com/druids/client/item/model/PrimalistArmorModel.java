package com.druids.client.item.model;

import com.druids.Druids;
import com.druids.items.Armors.PrimalistArmor;
import mod.azure.azurelib.common.api.client.model.GeoModel;
import net.minecraft.util.Identifier;

public class PrimalistArmorModel extends GeoModel<PrimalistArmor> {


    @Override
    public Identifier getModelResource(PrimalistArmor animatable) {

        return Identifier.of(Druids.MODID,"geo/primalist.geo.json");
    }

    @Override
    public Identifier getTextureResource(PrimalistArmor animatable) {


        return Identifier.of(Druids.MODID,"textures/armor/primalist.png");
    }

    @Override
    public Identifier getAnimationResource(PrimalistArmor animatable) {
        return null;
    }
}
