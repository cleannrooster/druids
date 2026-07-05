package com.cleannrooster.druids.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.entity.SpellProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Gives spirit_bolts projectiles a wispy, arcing flight path:
 *   - gentle downward gravity
 *   - sinusoidal lateral drift perpendicular to the direction of travel
 *
 * Two overlapping sine waves (slow drift + fast flutter) with a per-entity
 * phase offset create an organic, unpredictable look without pure randomness.
 * The existing sculk_soul travel particles naturally form a curved ghost trail.
 */
@Mixin(value = SpellProjectile.class)
public class SpellProjectileMixin {

    private static final Identifier SPIRIT_BOLTS_ID = Identifier.of("soulwarden", "spirit_bolts");

    /** Injected at the very end of SpellProjectile.tick() so spell-engine's
     *  own logic (target-following, collision, etc.) runs first. */
    @Inject(method = "tick", at = @At("TAIL"))
    private void spiritBolts_wispyArc(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;

        // Position updates are server-authoritative; skip on the render side
        if (self.getWorld().isClient()) return;

        // Filter — only touch spirit_bolts projectiles
        SpellProjectile proj = (SpellProjectile) (Object) this;
        RegistryEntry<Spell> entry = proj.getSpellEntry();
        if (entry == null) return;
        var key = entry.getKey();
        if (key.isEmpty() || !key.get().getValue().equals(SPIRIT_BOLTS_ID)) return;

        Vec3d vel = self.getVelocity();

        // ── Gravity ──────────────────────────────────────────────────────────
        // 0.006 blocks/tick² — subtle arc (vanilla arrows use ~0.05/tick²)
        final double GRAVITY = 0.025;

        // ── Lateral wobble ────────────────────────────────────────────────────
        // Phase uniquely offsets each projectile using the golden-ratio spread,
        // so a burst of bolts fans out instead of oscillating in lockstep.
        double phase = (self.getId() * 1.6180339) % (Math.PI * 2.0);
        int    age   = self.age;

        // Unit vector perpendicular to horizontal travel — rotate XZ 90°
        double perpX  = -vel.z;
        double perpZ  =  vel.x;
        double perpLen = Math.sqrt(perpX * perpX + perpZ * perpZ);
        if (perpLen > 1e-6) {
            perpX /= perpLen;
            perpZ /= perpLen;
        }

        // Primary: slow, sweeping drift (0.18 rad/tick ≈ full oscillation ~35t)
        // Secondary: faster flutter layered on top (0.43 rad/tick ≈ ~15t)
        double wobble = Math.sin(age * 0.18 + phase)          * 0.022
                      + Math.sin(age * 0.43 + phase * 1.4142) * 0.010;

        self.setVelocity(
                vel.x + 2*perpX * wobble,
                vel.y - GRAVITY,
                vel.z + 2*perpZ * wobble
        );
    }
}
