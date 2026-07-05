package com.cleannrooster.druids.client.entity;

import com.cleannrooster.druids.Druids;
import com.cleannrooster.druids.entity.AncestralGuardianEntity;
import mod.azure.azurelib.common.render.entity.AzEntityRenderer;
import mod.azure.azurelib.common.render.entity.AzEntityRendererConfig;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class AncestralGuardianAzRenderer extends AzEntityRenderer<AncestralGuardianEntity> {
    protected AncestralGuardianAzRenderer(AzEntityRendererConfig config, EntityRendererFactory.Context context) {
        super(config, context);
    }
    public static final Identifier MODEL = Identifier.of(
            Druids.MODID, "geo/ancestral_guardian.geo.json"
    );
    public static final Identifier TEXTURE = Identifier.of(
            Druids.MODID, "textures/mob/ancestral_guardian.png"
    );
    public AncestralGuardianAzRenderer(EntityRendererFactory.Context context, Identifier model, Identifier texture) {
        super(
                AzEntityRendererConfig.<AncestralGuardianEntity>builder(model  ,texture)
                        .setAnimatorProvider(AncestralGuardianAnimationProvider::new)
                        .setDeathMaxRotation(180F) // Custom death rotation
                        .setShadowRadius(1.0F) // Sets a shadow radius
                        .setShadowRadius(exampleEntity -> 1.0F) // Sets a shadow radius with context

                        .setRenderType(RenderLayer.getEntityTranslucent(texture)) // Sets RenderType
                        .setRenderType(exampleEntity -> RenderLayer.getEntityTranslucent(texture)) // Sets RenderType with context

                        //  .setModelRenderer(ExampleCustomEntityModelRenderer::new) // Sets the Model Renderer of your render to the ExampleCustomEntityModelRenderer
                        //.setPipelineContext(ExampleEntityRendererPipelineContext::new) // Sets the Pipeline Context to the ExampleEntityRendererPipelineContext
                        .setPrerenderEntry(context2 -> {
                            // Insert code you want to run here

                            return context2;
                        }) // Pre-render hook
                        .setRenderEntry(context3 -> {


                            // Insert code you want to run here
                            return context3;
                        }) // Render hook
                        .setPostRenderEntry(context2 -> {
                            // Insert code you want to run here

                            return context2;
                        }) // Post-render hook
                        .setAlpha(exampleEntity -> 1.0F) // Alpha with context
                        .setAlpha(1.0F) // Alpha with just a value
                        .setScale(1.0F, 1.0F) // Scale for width and height
                        .setScale(1.0F) // Scale for width and height being the same
                        .setScale(exampleEntity -> 1.0F) // Scale for width and height being the same with context
                        .setScale(exampleEntity -> 1.0F, exampleEntity -> 1.0F) // Scale for width and height with context
                        .build(),
                context
        );
    }
}
