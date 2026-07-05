package com.cleannrooster.druids.client.entity;

import com.cleannrooster.druids.Druids;
import com.cleannrooster.druids.entity.AncestralGuardianEntity;
import com.cleannrooster.druids.entity.TotemEntity;
import mod.azure.azurelib.common.render.entity.AzEntityRenderer;
import mod.azure.azurelib.common.render.entity.AzEntityRendererConfig;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Placeholder renderer — replace with your AzureLib renderer once the model
 * and animations are ready.  This version renders only the entity shadow so
 * the game doesn't crash on registration.
 *
 * Expose {@link TotemEntity#getPhase()} to drive your animation states:
 *   PHASE_SPAWNING (0) → spawn animation
 *   PHASE_CHARGING (1) → charge-up animation
 *   PHASE_WINDDOWN (2) → wind-down animation
 */
public class TotemRenderer extends AzEntityRenderer<TotemEntity> {
    public static final Identifier MODEL = Identifier.of(
            Druids.MODID, "geo/ancestral_totem.geo.json"
    );
    public static final Identifier TEXTURE = Identifier.of(
            Druids.MODID, "textures/mob/ancestral_totem.png"
    );

    @Override
    public void render(@NotNull TotemEntity entity, float entityYaw, float partialTick, @NotNull MatrixStack poseStack, @NotNull VertexConsumerProvider bufferSource, int packedLight) {
        if(entity.age <= 2){
            poseStack.translate(0,-4,0);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    public TotemRenderer(EntityRendererFactory.Context context, Identifier model, Identifier texture) {
        super(
                AzEntityRendererConfig.<TotemEntity>builder(model  ,texture)
                        .setAnimatorProvider(TotemAnimationProvider::new)
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