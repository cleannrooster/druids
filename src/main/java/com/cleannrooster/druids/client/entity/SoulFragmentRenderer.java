package com.cleannrooster.druids.client.entity;

import com.cleannrooster.druids.entity.SoulFragmentEntity;
import com.cleannrooster.druids.items.ItemsInit;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class SoulFragmentRenderer extends EntityRenderer<SoulFragmentEntity> {

    private static final Identifier TEXTURE = Identifier.of("soulwarden", "textures/entity/soul_fragment.png");
    private final ItemRenderer itemRenderer;

    public SoulFragmentRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(SoulFragmentEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        // Bob up and down
        float age = entity.age + tickDelta;
        float bob = MathHelper.sin(age * 0.1f) * 0.1f;
        matrices.translate(0.0, 0.25 + bob, 0.0);

        // Slow spin
        float rotation = age * 2.0f;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));

        // Slight scale down
        matrices.scale(0.65f, 0.65f, 0.65f);

        ItemStack stack = new ItemStack(ItemsInit.SOUL_FRAGMENT_ITEM);
        BakedModel model = this.itemRenderer.getModel(stack, entity.getWorld(), null, entity.getId());
        this.itemRenderer.renderItem(
                stack,
                ModelTransformationMode.GROUND,
                false,
                matrices,
                vertexConsumers,
                15728880, // Full brightness
                OverlayTexture.DEFAULT_UV,
                model
        );

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(SoulFragmentEntity entity) {
        return TEXTURE;
    }
}
