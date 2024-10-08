package com.druids.spells;

import com.druids.Druids;
import com.druids.api.ChainList;
import com.druids.entity.ChainLightning;
import com.druids.entity.DruidLightning;
import com.druids.entity.SoulBombEntity;
import com.druids.entity.TotemEntity;
import it.unimi.dsi.fastutil.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import net.spell_engine.api.render.CustomModels;
import net.spell_engine.api.spell.CustomSpellHandler;
import net.spell_engine.api.spell.SpellInfo;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellPowerMechanics;
import net.spell_power.api.SpellSchool;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.List;

public class Spells {
    public static final ArrayList<Spell> entries = new ArrayList<>();
    private static Spell entry(String namespace, String name, Function<CustomSpellHandler.Data, Boolean> handler ) {
        var entry = new Spell(namespace, name, handler);
            entries.add(entry);

        return entry;
    }
    public static class Spell {
        private final String namespace;
        private final String name;

        private final Function<CustomSpellHandler.Data, Boolean> handler;
        public Spell(String namespace, String name, Function<CustomSpellHandler.Data, Boolean> handler) {
            this.namespace = namespace;
            this.name = name;
            this.handler = handler;
        }
    };
    public static Spell spiritBolts(String namespace, String name, Function<CustomSpellHandler.Data,Boolean> handler){
        return entry(namespace,name,handler);
    }
    public static final Spell SPIRITBOLTS = spiritBolts(Druids.MODID,"spiritbolts", (data) ->{
        CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
        double total = 0;
        if(data1.caster().getAttributeValue((SpellSchools.SOUL).getAttributeEntry())+
                data1.caster().getAttributeValue((SpellSchools.FIRE).getAttributeEntry())+
                        data1.caster().getAttributeValue((SpellSchools.FROST).getAttributeEntry()) > 0){
            total += data1.caster().getAttributeValue((SpellSchools.SOUL).getAttributeEntry());
            total += data1.caster().getAttributeValue((SpellSchools.FIRE).getAttributeEntry());
            total += data1.caster().getAttributeValue((SpellSchools.FROST).getAttributeEntry());
        }
        double seed = data1.caster().getRandom().nextDouble()*total;
        if( seed < data1.caster().getAttributeValue((SpellSchools.SOUL).getAttributeEntry())){
            fireBoltOfType((SpellSchools.SOUL), data1);

            return false;
        }
        if(seed >= data1.caster().getAttributeValue((SpellSchools.SOUL).getAttributeEntry())
        && seed < data1.caster().getAttributeValue((SpellSchools.SOUL).getAttributeEntry()) + data1.caster().getAttributeValue((SpellSchools.FIRE).getAttributeEntry())){
            fireBoltOfType(SpellSchools.FIRE, data1);

            return false;
        }
        if(seed  >=  data1.caster().getAttributeValue((SpellSchools.SOUL).getAttributeEntry()) + data1.caster().getAttributeValue((SpellSchools.FIRE).getAttributeEntry())){
            fireBoltOfType((SpellSchools.FROST), data1);
            return false;
        }
        return false;
    });
    public static void fireBoltOfType(SpellSchool school, CustomSpellHandler.Data data  ){
        net.spell_engine.api.spell.Spell  spell = null;
        SpellInfo spellInfo = null;


        if(school == SpellSchools.FIRE){
           spell = SpellRegistry.getSpell(Identifier.of(Druids.MODID,"firebolts"));
            spellInfo = new SpellInfo(spell,Identifier.of(Druids.MODID,"firebolts"));

        }
        if(school == SpellSchools.FROST){
            spell = SpellRegistry.getSpell(Identifier.of(Druids.MODID,"coldbolts"));
            spellInfo = new SpellInfo(spell,Identifier.of(Druids.MODID,"coldbolts"));

        }
        if(school == SpellSchools.SOUL){
            spell = SpellRegistry.getSpell(Identifier.of(Druids.MODID,"necrobolts"));

            spellInfo = new SpellInfo(spell,Identifier.of(Druids.MODID,"necrobolts"));
        }

       if(!data.targets().isEmpty() && spell != null){

           for(Entity target : data.targets()) {
               if(data.targets().size() <= 4  || (data.caster().getRandom().nextFloat() < (float) 4 /(float)data.targets().size())) {
                   SpellHelper.ImpactContext context = data.impactContext();

                   SpellHelper.shootProjectile(data.caster().getWorld(), data.caster(), target, spellInfo, data.impactContext().position(data.caster().getPos().add(0,data.caster().getHeight(),0)));
               }
           }

       }

    }
    public static Spell maelstromtotem(String namespace, String name, Function<CustomSpellHandler.Data,Boolean> handler){
        return entry(namespace,name,handler);
    }
    public static final Spell MAELSTROMTOTEM = maelstromtotem(Druids.MODID, "maelstromtotem",(data) ->{
        CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
        BlockHitResult result = data1.caster().getWorld().raycast(new RaycastContext(data1.caster().getEyePos(),data1.caster().getEyePos().add(data1.caster().getRotationVector().multiply(SpellRegistry.getSpell(Identifier.of(Druids.MODID,"maelstromtotem")).range)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE,data1.caster()));
        if(result.getType().equals(HitResult.Type.BLOCK)){
            TotemEntity totem = new TotemEntity(Druids.TOTEM,data1.caster().getWorld());
            totem.setOwner(data1.caster());
            totem.context = data1.impactContext();
            totem.setPosition(result.getPos());
            data1.caster().getWorld().spawnEntity(totem);

            return true;
        }
        return false;
    });
    public static Spell siphonSouls(String namespace, String name, Function<CustomSpellHandler.Data,Boolean> handler){
        return entry(namespace,name,handler);
    }
    public static final Spell SIPHONSOULS = siphonSouls(Druids.MODID, "siphonsouls",(data) ->{
        return true;
    });
    public static Spell soulFireBastion(String namespace, String name, Function<CustomSpellHandler.Data,Boolean> handler){
        return entry(namespace,name,handler);
    }
    public static final Spell SOULFIREBASTION = soulFireBastion(Druids.MODID,"soulfirebastion", (data) ->{
        CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;

        return true;
    });
    public static Spell lightningBlast(String namespace, String name, Function<CustomSpellHandler.Data,Boolean> handler){
        return entry(namespace,name,handler);
    }
    public static final Spell LIGHTNINGBLAST = lightningBlast(Druids.MODID,"lightningblast", (data) ->{
        CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
        if(!data1.targets().isEmpty()) {
            DruidLightning lightning = new DruidLightning(Druids.DRUIDLIGHTNING, data1.caster().getWorld());

            net.spell_engine.api.spell.Spell.Release.Target.Area area =  new net.spell_engine.api.spell.Spell.Release.Target.Area();
            area.angle_degrees = 360;
            if(data1.targets().get(0) instanceof CreeperEntity creeper && creeper.getWorld() instanceof ServerWorld world && Druids.config.charge_creepers){
                creeper.onStruckByLightning(world,new LightningEntity(EntityType.LIGHTNING_BOLT,creeper.getWorld()));
            }
            List<Entity> entities = TargetHelper.targetsFromArea(data1.targets().get(0),data1.targets().get(0).getPos(),4,area,target -> TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL,data1.caster(), target));
            net.spell_engine.api.spell.Spell spell = SpellRegistry.getSpell(Identifier.of(Druids.MODID, "lightningblast"));
            SpellInfo info = new SpellInfo(spell, Identifier.of(Druids.MODID,"lightningblast"));

            SpellHelper.performImpacts(data1.caster().getWorld(), data1.caster(), data1.targets().get(0), data1.caster(), info, data1.impactContext());
            lightning.setOwner(data1.caster());
            lightning.setPosition(data1.targets().get(0).getPos());
            lightning.setColor(0);
            data1.caster().getWorld().spawnEntity(lightning);

            for(Entity entity : entities) {
                DruidLightning lightning1 = new DruidLightning(Druids.DRUIDLIGHTNING, data1.caster().getWorld());

                lightning1.setOwner(data1.caster());
                lightning1.setPosition(entity.getPos());
                lightning1.setColor(0);
                data1.caster().getWorld().spawnEntity(lightning1);
                if(entity instanceof CreeperEntity creeper && creeper.getWorld() instanceof ServerWorld world && Druids.config.charge_creepers){
                    creeper.onStruckByLightning(world,new LightningEntity(EntityType.LIGHTNING_BOLT,creeper.getWorld()));
                }

                SpellHelper.performImpacts(data1.caster().getWorld(), data1.caster(), entity, data1.caster(), info, data1.impactContext());

            }
            return true;

        }
        return false;
    });
    public static Spell SoulFlare(String namespace, String name, Function<CustomSpellHandler.Data,Boolean> handler){
        return entry(namespace,name,handler);
    }
    public static final Spell SOULFLARE = SoulFlare(Druids.MODID,"soulflare", (data) ->{
        CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;

        SoulBombEntity amethyst = new SoulBombEntity(Druids.SoulFlare, data1.caster().getWorld(), data1.caster());
        amethyst.setPosition(data1.caster().getEyePos().add(data1.caster().getRotationVec(1).normalize()));
        amethyst.setVelocity(data1.caster().getRotationVec(1).multiply(1, 1, 1));
        amethyst.setOwner(data1.caster());
        amethyst.spell = SpellRegistry.getSpell(Identifier.of(Druids.MODID,"soulflare"));
        amethyst.context = data1.impactContext();
        SpellPower.Vulnerability vulnerability = SpellPower.Vulnerability.none;


        SpellPower.Result power = SpellPower.getSpellPower(SpellSchools.SOUL, data1.caster());
        amethyst.power = power;
        data1.caster().getWorld().spawnEntity(amethyst);

        return true;
    });
    public static Spell tendrils(String namespace, String name, Function<CustomSpellHandler.Data,Boolean> handler){
        return entry(namespace,name,handler);
    }
    public static final Spell TENDRILS = SoulFlare(Druids.MODID,"chain", (data) ->{

        CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
        if(!data1.targets().isEmpty()) {

            ChainLightning amethyst = new ChainLightning(Druids.ChainLightning, data1.caster().getWorld(), new ChainList(new ArrayList<>(List.of())));
            amethyst.setPosition(data1.caster().getEyePos().add(data1.caster().getRotationVec(1).normalize()));
            amethyst.setVelocity(data1.caster().getRotationVec(1).multiply(1, 1, 1));
            amethyst.setOwner(data1.caster());
            amethyst.remainingchains = 9;
            if (!data1.targets().isEmpty()) {
                amethyst.target = ((CustomSpellHandler.Data) data).targets().get(0);
                amethyst.list.list.add(data1.targets().get(0));

            }

            amethyst.spell = SpellRegistry.getSpell(Identifier.of(Druids.MODID, "chain"));
            amethyst.context = data1.impactContext();
            SpellPower.Vulnerability vulnerability = SpellPower.Vulnerability.none;


            SpellPower.Result power = SpellPower.getSpellPower(SpellSchools.LIGHTNING, data1.caster());
            amethyst.power = power;
            data1.caster().getWorld().spawnEntity(amethyst);

            return true;
        }
        else{
            return false;
        }
    });
    public static Spell voltaic(String namespace, String name, Function<CustomSpellHandler.Data,Boolean> handler){
        return entry(namespace,name,handler);
    }
    public static final Spell voltaic = SoulFlare(Druids.MODID,"voltaic_burst", (data) ->{
        CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;


        return true;
    });
    public static Spell stormcall(String namespace, String name, Function<CustomSpellHandler.Data,Boolean> handler){
        return entry(namespace,name,handler);
    }

    public static final Spell STORMCALL = stormcall(Druids.MODID, "stormcall",(data) ->{
        CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
        DruidLightning lightning = new DruidLightning(Druids.DRUIDLIGHTNING, data1.caster().getWorld());
        lightning.setOwner(data1.caster());
        lightning.setPosition(data1.caster().getPos().add(0, data1.caster().getHeight() / 2, 0));
        lightning.setColor(1);
        data1.caster().getWorld().spawnEntity(lightning);

        return true;
    });
    public static void initializeSpells(){
        CustomModels.registerModelIds(List.of(
                Identifier.of(Druids.MODID, "projectile/coldspirit")
        ));
        CustomModels.registerModelIds(List.of(
                Identifier.of(Druids.MODID, "projectile/firespirit")
        ));
        CustomModels.registerModelIds(List.of(
                Identifier.of(Druids.MODID, "projectile/necrospirit")
        ));
        for(Spell entry : entries){
            CustomSpellHandler.register(Identifier.of(entry.namespace,entry.name),entry.handler);
        }
    }
}
