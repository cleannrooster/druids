package com.druids.client.item.model;

import com.druids.Druids;
import com.druids.items.Shard;
import mod.azure.azurelib.common.api.client.model.GeoModel;
import net.minecraft.util.Identifier;
import net.spell_power.api.SpellSchools;

public class  ShardModel extends GeoModel<Shard> {


    @Override
    public Identifier getModelResource(Shard orb) {
        return Identifier.of(Druids.MODID,"geo/shard.geo.json");
    }

    @Override
    public Identifier getTextureResource(Shard orb) {
        if(orb.getSchool() == SpellSchools.FIRE) {
            return Identifier.of(Druids.MODID, "textures/item/shard_fire.png");
        }
        if(orb.getSchool() == SpellSchools.FROST) {
            return Identifier.of(Druids.MODID, "textures/item/shard_frost.png");
        }
        if(orb.getSchool() == SpellSchools.LIGHTNING) {
            return Identifier.of(Druids.MODID, "textures/item/shard_lightning.png");
        }
        return Identifier.of(Druids.MODID, "textures/item/shard_arcane.png");
    }

    @Override
    public Identifier getAnimationResource(Shard orb) {
        return Identifier.of(Druids.MODID,"animations/shard.animations.json");
    }
}