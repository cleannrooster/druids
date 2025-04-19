package com.cleannrooster.druids.client.item.model;

import com.cleannrooster.druids.items.Armors.NetheritePrimalistArmor;
import com.cleannrooster.druids.Druids;
import mod.azure.azurelib.common.api.client.model.GeoModel;
import net.minecraft.util.Identifier;

public class PrimalistArmorNetheriteModel extends GeoModel<NetheritePrimalistArmor> {


    @Override
    public Identifier getModelResource(NetheritePrimalistArmor animatable) {

        return Identifier.of(Druids.MODID,"geo/primalist_netherite.geo.json");
    }

    @Override
    public Identifier getTextureResource(NetheritePrimalistArmor animatable) {


        return Identifier.of(Druids.MODID,"textures/armor/primalist_netherite.png");
    }

    @Override
    public Identifier getAnimationResource(NetheritePrimalistArmor animatable) {
        return null;
    }
}
