package com.cleannrooster.druids.client.entity;

import com.cleannrooster.druids.Druids;
import com.cleannrooster.druids.entity.AncestralGuardianEntity;
import com.cleannrooster.druids.entity.TotemEntity;
import mod.azure.azurelib.common.animation.AzAnimatorConfig;
import mod.azure.azurelib.common.animation.controller.AzAnimationController;
import mod.azure.azurelib.common.animation.controller.AzAnimationControllerBuilder;
import mod.azure.azurelib.common.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.common.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.common.animation.easing.AzEasingTypes;
import mod.azure.azurelib.common.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.common.animation.play_behavior.AzPlayBehaviors;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TotemAnimationProvider extends AzEntityAnimator<TotemEntity> {
    private static final Identifier ANIMATIONS = Identifier.of(Druids.MODID,
            "animations/totem.animation.json"
    );

    public TotemAnimationProvider() {
        super(AzAnimatorConfig.defaultConfig());
        builders = List.of(
                AzAnimationController.builder(this, "base_controller").setTransitionLength(5),
                AzAnimationController.builder(this, "dash").setTransitionLength(2).setEasingType(AzEasingTypes.BEZIER),

                AzAnimationController.builder(this, "attacks").setTransitionLength(2).setEasingType(AzEasingTypes.BEZIER));

    }

    public List<AzAnimationControllerBuilder> builders;

    @Override
    public void registerControllers(AzAnimationControllerContainer<TotemEntity> animationControllerContainer) {
        for (AzAnimationControllerBuilder controller : builders) {
            animationControllerContainer.add(
                    controller.build()
            );
        }
    }

    @Override
    public @NotNull Identifier getAnimationLocation(TotemEntity animatable) {
        return ANIMATIONS;
    }

    public record MinibossAnimationDispatcher(TotemEntity entity) {


        public static final AzCommand IDLE = AzCommand.create(
                "base_controller",
                "animation.totem.idle",
                AzPlayBehaviors.PLAY_ONCE
        );
        public static final AzCommand CHARGE = AzCommand.create(
                "dash",
                "animation.totem.charge",
                AzPlayBehaviors.PLAY_ONCE
        );
        public static final AzCommand SMASH = AzCommand.create(
                "attacks",
                "animation.totem.smash",
                AzPlayBehaviors.PLAY_ONCE
        );
        public static final AzCommand RISE = AzCommand.create(
                "dash",
                "animation.totem.rise",
                AzPlayBehaviors.PLAY_ONCE
        );
        public void rise(TotemEntity animatable) {
            RISE.sendForEntity(animatable);
        }
        public void charge(TotemEntity animatable) {
            CHARGE.sendForEntity(animatable);
        }
        public void smash(TotemEntity animatable) {
            SMASH.sendForEntity(animatable);
        }
        public void idle(TotemEntity animatable) {
            IDLE.sendForEntity(animatable);
        }
    }
}