package com.cleannrooster.druids.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.spell_engine.internals.target.EntityRelations;
import net.spell_engine.internals.target.SpellTarget;
import org.jetbrains.annotations.Nullable;

/**
 * Shared targeting goals for all player-owned minions.
 * Both goals are "fire-and-forget" target selectors: canStart() finds a valid
 * target, start() applies it, and shouldContinue() returns false so the goal
 * immediately steps back and lets the melee/navigation goals take over.
 */
public final class MinionGoals {

    private MinionGoals() {}

    // -------------------------------------------------------------------------
    // Friendly-fire check
    // Returns true if the candidate is a minion owned by the same owner.
    // Used both in the target goals and in each entity's damage() override so
    // that RevengeGoal is also neutralised (no damage → no attacker recorded).
    // -------------------------------------------------------------------------
    public static boolean isFriendlyMinion(@Nullable Entity candidate, @Nullable Entity owner) {
        if (candidate == null || owner == null) return false;
        if (!(candidate instanceof Ownable ownable)) return false;
        return owner.equals(ownable.getOwner());
    }

    // -------------------------------------------------------------------------
    // DefendOwnerGoal
    // Targets whatever entity has most recently attacked the owner.
    // MC clears LivingEntity.getAttacker() after 100 ticks (5 s) automatically,
    // so no extra staleness check is needed here.
    // -------------------------------------------------------------------------
    public static class DefendOwnerGoal extends Goal {

        private final PathAwareEntity minion;
        @Nullable private LivingEntity targetToSet;

        public DefendOwnerGoal(PathAwareEntity minion) {
            this.minion = minion;
        }

        @Override
        public boolean canStart() {
            targetToSet = null;
            if (!(minion instanceof Ownable ownable)) return false;
            Entity owner = ownable.getOwner();
            if (!(owner instanceof LivingEntity livingOwner)) return false;

            LivingEntity attacker = livingOwner.getAttacker();
            if (attacker == null
                    || attacker == minion                        // don't target ourselves
                    || attacker == owner                         // don't target the owner
                    || attacker.isDead()
                    || !EntityRelations.actionAllowed(SpellTarget.FocusMode.AREA, SpellTarget.Intent.HARMFUL,livingOwner,attacker)

                    || isFriendlyMinion(attacker, owner)) return false; // don't target sister minions

            targetToSet = attacker;
            return true;
        }

        @Override
        public void start() {
            minion.setTarget(targetToSet);
            targetToSet = null;
        }

        @Override
        public boolean shouldContinue() { return false; }
    }

    // -------------------------------------------------------------------------
    // CopyOwnerTargetGoal
    // Mirrors whatever the owner is currently attacking so minions assist
    // the owner's chosen target rather than choosing their own fights.
    // LivingEntity.getAttacking() tracks who the owner most recently hit.
    // -------------------------------------------------------------------------
    public static class CopyOwnerTargetGoal extends Goal {

        private final PathAwareEntity minion;
        @Nullable private LivingEntity targetToSet;

        public CopyOwnerTargetGoal(PathAwareEntity minion) {
            this.minion = minion;
        }

        @Override
        public boolean canStart() {
            targetToSet = null;
            if (!(minion instanceof Ownable ownable)) return false;
            Entity owner = ownable.getOwner();
            if (!(owner instanceof PlayerEntity livingOwner)) return false;

            LivingEntity ownerTarget = livingOwner.getAttacking();
            if (ownerTarget == null
                    || ownerTarget == minion
                    || ownerTarget.isDead()
                    || isFriendlyMinion(ownerTarget, owner)
            || !EntityRelations.actionAllowed(SpellTarget.FocusMode.AREA, SpellTarget.Intent.HARMFUL,livingOwner,ownerTarget)
            ) return false;

            // Don't bother switching if already on the same target
            if (ownerTarget == minion.getTarget()) return false;

            targetToSet = ownerTarget;
            return true;
        }

        @Override
        public void start() {
            minion.setTarget(targetToSet);
            targetToSet = null;
        }

        @Override
        public boolean shouldContinue() { return false; }
    }
}
