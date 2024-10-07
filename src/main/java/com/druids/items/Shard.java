package com.druids.items;

import com.druids.client.item.renderer.ShardRenderer;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import mod.azure.azurelib.common.api.common.animatable.GeoItem;
import mod.azure.azurelib.common.internal.client.RenderProvider;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.Animation;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Identifier;
import net.spell_engine.api.item.weapon.StaffItem;
import net.spell_power.api.SpellSchool;
import net.spell_power.api.SpellSchools;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Shard extends Item implements GeoItem {

    public Shard(ToolMaterial material, Settings settings,SpellSchool school) {
        super( settings);
        this.school = school;

    }
    SpellSchool school = SpellSchools.getSchool(Identifier.of("spell_power", "physical_melee").toString());

    @Override
    public void createRenderer(Consumer<RenderProvider> consumer) {
        consumer.accept(new RenderProvider() {
            private ShardRenderer renderer;

            @Override
            public BuiltinModelItemRenderer getCustomRenderer() {
                if (renderer == null) return new ShardRenderer();
                return this.renderer;
            }
        });
    }

   public AnimatableInstanceCache factory = AzureLibUtil.createInstanceCache(this);



    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");

    public SpellSchool getSchool() {
        return school;
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<Shard>(this,"idle",
                0,animationState -> {
            animationState.getController().setAnimation(RawAnimation.begin().then(
                    "idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;})
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

}
