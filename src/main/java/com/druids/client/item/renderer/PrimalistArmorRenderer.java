package com.druids.client.item.renderer;

import com.druids.client.item.model.PrimalistArmorModel;
import com.druids.items.Armors.PrimalistArmor;
import mod.azure.azurelib.common.api.client.renderer.GeoArmorRenderer;

public class PrimalistArmorRenderer extends GeoArmorRenderer<PrimalistArmor> {

    public PrimalistArmorRenderer() {
        super(new PrimalistArmorModel());

    }
}
