package com.cleannrooster.druids;

import com.cleannrooster.druids.client.entity.SpectralGoatRenderer;
import com.cleannrooster.druids.client.entity.SpiderLightningRenderer;
import com.cleannrooster.druids.client.entity.TotemRenderer;
import com.cleannrooster.druids.client.entity.WarriorRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class DruidsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.register(Druids.TOTEM    , TotemRenderer::new);

        EntityRendererRegistry.register(Druids.TOTEM_TRAIL, SpiderLightningRenderer::new);
        EntityRendererRegistry.register(Druids.ANCESTRAL_WARRIOR, WarriorRenderer::new);
        EntityRendererRegistry.register(Druids.SPECTRAL_GOAT, SpectralGoatRenderer::new);


    }
}
