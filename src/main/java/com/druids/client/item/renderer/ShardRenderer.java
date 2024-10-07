package com.druids.client.item.renderer;

import com.druids.client.item.model.ShardModel;
import com.druids.items.Shard;
import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

public class ShardRenderer extends GeoItemRenderer<Shard> {


    public ShardRenderer() {
        super(new ShardModel());

    }
}
