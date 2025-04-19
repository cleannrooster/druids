package com.cleannrooster.druids.client.entity;

import com.cleannrooster.druids.entity.AncestralWarrior;
import com.cleannrooster.druids.Druids;
import mod.azure.azurelib.common.api.client.model.GeoModel;
import net.minecraft.util.Identifier;

public class WarriorModel<T extends AncestralWarrior> extends GeoModel<AncestralWarrior> {

    @Override
    public Identifier getModelResource(AncestralWarrior reaver) {

        return Identifier.of(Druids.MODID,"geo/ancestral_spirit.geo.json");
    }
    @Override
    public Identifier getTextureResource(AncestralWarrior reaver) {

        return Identifier.of(Druids.MODID, "textures/mob/ancestral_spirit.png");

    }

    @Override
    public Identifier getAnimationResource(AncestralWarrior reaver) {
        return Identifier.of(Druids.MODID,"animations/mobs.animations.json");
    }

}
