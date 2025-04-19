package com.cleannrooster.druids.entity;

import net.minecraft.entity.mob.MobEntity;
import net.spell_power.api.SpellPower;

public interface TotemSummon<T extends MobEntity> {
    public TotemEntity<T> getTotem();
    public void  setTotem(TotemEntity totem);
    public SpellPower.Result getSpellPower() ;

}
