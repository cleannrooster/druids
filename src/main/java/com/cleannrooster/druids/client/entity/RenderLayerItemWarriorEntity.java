package com.cleannrooster.druids.client.entity;

import com.cleannrooster.druids.entity.AncestralWarrior;
import mod.azure.azurelib.common.api.client.renderer.layer.BlockAndItemGeoLayer;
import mod.azure.azurelib.common.internal.client.renderer.GeoRenderer;
import mod.azure.azurelib.common.internal.common.cache.object.GeoBone;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class RenderLayerItemWarriorEntity<T extends AncestralWarrior> extends BlockAndItemGeoLayer<AncestralWarrior> {
    public RenderLayerItemWarriorEntity(GeoRenderer<AncestralWarrior> entityRendererIn) {
        super(entityRendererIn);
    }
    private static final String LEFT_HAND = "leftItem";
    private static final String RIGHT_HAND = "itemBone";
    @Override
    protected ItemStack getStackForBone(GeoBone bone, AncestralWarrior animatable) {
        // Retrieve the items in the entity's hands for the relevant bone

        return switch (bone.getName()) {
            case RIGHT_HAND -> Items.IRON_AXE.getDefaultStack();
            default -> null;
        };
    }

    @Override
    protected ModelTransformationMode getTransformTypeForStack(GeoBone bone, ItemStack stack, AncestralWarrior animatable) {
        // Apply the camera transform for the given hand
        return switch (bone.getName()) {
            case RIGHT_HAND -> ModelTransformationMode.THIRD_PERSON_RIGHT_HAND;
            default -> ModelTransformationMode.NONE;
        };
    }

    // Do some quick render modifications depending on what the item is
    @Override
    protected void renderStackForBone(MatrixStack poseStack, GeoBone bone, ItemStack stack, AncestralWarrior animatable,
                                      VertexConsumerProvider bufferSource, float partialTick, int packedLight, int packedOverlay) {
            poseStack.translate(0,3/16F,-3/16F);
            poseStack.scale(2F,2F,2F);
       /* if (stack == animatable.getMainHandStack()) {
            poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));

            if (stack.getItem() instanceof ShieldItem)
                poseStack.translate(0, 0.125, -0.25);
        }
        else if (stack == animatable.getOffHandStack()) {
            poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));

            if (stack.getItem() instanceof ShieldItem) {
                poseStack.translate(0, 0.125, 0.25);
                poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            }
        }
*/
        super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
    }

}
