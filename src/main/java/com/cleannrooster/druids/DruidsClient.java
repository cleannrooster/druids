package com.cleannrooster.druids;

import com.cleannrooster.druids.client.entity.*;
import com.cleannrooster.druids.items.Armors.Armors;
import com.cleannrooster.druids.items.Armors.NetheritePrimalistArmor;
import com.cleannrooster.druids.items.Armors.PrimalistArmor;
import mod.azure.azurelib.common.render.armor.AzArmorRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.spell_engine.rpg_series.item.Armor;

public class DruidsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

       EntityRendererRegistry.register(Druids.SOUL_FRAGMENT, SoulFragmentRenderer::new);
       EntityRendererRegistry.register(Druids.BOUND_SOUL, BoundSoulRenderer::new);
       EntityRendererRegistry.register(Druids.ANCESTRAL_GUARDIAN, (ctx -> new AncestralGuardianAzRenderer(ctx,AncestralGuardianAzRenderer.MODEL,AncestralGuardianAzRenderer.TEXTURE)));
       EntityRendererRegistry.register(Druids.SUMMON_TOTEM, (ctx -> new TotemRenderer(ctx,TotemRenderer.MODEL,TotemRenderer.TEXTURE)));
        for(Armor.Entry entry: Armors.entries) {
            for(Object set : entry.armorSet().pieces()){
                if(set instanceof PrimalistArmor peasant) {
                    AzArmorRendererRegistry.register(peasant, () -> new PrimalistArmorRenderer(PrimalistArmorRenderer.TEXTURE,PrimalistArmorRenderer.MODEL));
                }
                if(set instanceof NetheritePrimalistArmor armor) {
                    AzArmorRendererRegistry.register(armor,() ->  new PrimalistArmorRenderer(PrimalistArmorRenderer.TEXTURE_netherite,PrimalistArmorRenderer.MODEL_netherite));
                }
            }
        }

    }
}
