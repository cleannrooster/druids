package com.druids.entity;

import com.druids.Druids;
import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.block.Oxidizable;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.SpellInfo;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.utils.TargetHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class DruidLightning extends Entity implements Ownable {
    private static final int field_30062 = 2;
    private static final double field_33906 = 3.0;
    private static final double field_33907 = 15.0;
    private int ambientTick;
    public long seed;
    private int remainingActions;
    private boolean cosmetic;
    private int ownerUuid;
    private Entity owner;
    @Nullable
    private ServerPlayerEntity channeler;
    private final Set<Entity> struckEntities = Sets.newHashSet();
    private int blocksSetOnFire;
    public static final TrackedData<Integer> COLOR;
    public static final TrackedData<Integer> OWNER;

    static {
        COLOR = DataTracker.registerData(DruidLightning.class, TrackedDataHandlerRegistry.INTEGER);
        OWNER = DataTracker.registerData(DruidLightning.class, TrackedDataHandlerRegistry.INTEGER);

    }


    protected void initDataTracker(DataTracker.Builder builder) {

        builder.add(COLOR,1);
        builder.add(OWNER,-1);

    }
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("Color", (Integer) this.dataTracker.get(COLOR));
        nbt.putInt("Owner", this.ownerUuid);


    }
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("Color")) {
            this.dataTracker.set(COLOR, nbt.getInt("Color"));
        }

        if (nbt.containsUuid("Owner")) {
            this.ownerUuid = nbt.getInt("Owner");
            this.owner = null;
        }

    }
    public void setOwner(@Nullable Entity entity) {
        if (entity != null) {
            this.ownerUuid = entity.getId();
            this.owner = entity;
            this.dataTracker.set(OWNER,entity.getId());
        }
    }
    @Nullable
    public Entity getOwner() {
        if(this.dataTracker.get(OWNER) != -1) {
            return this.getWorld().getEntityById(this.dataTracker.get(OWNER));
        }else{
            return null;
        }
    }
    public int getColor(){
        return this.dataTracker.get(COLOR);
    }
    public void setColor(int color){
        this.dataTracker.set(COLOR,color);
    }

    public DruidLightning(EntityType<? extends DruidLightning> entityType, World world) {
        super(entityType, world);
        this.ignoreCameraFrustum = true;
        this.ambientTick = 2;
        this.seed = this.random.nextLong();
        this.remainingActions = this.random.nextInt(4) + 7;
    }

    public void setCosmetic(boolean bl) {
        this.cosmetic = bl;
    }

    public SoundCategory getSoundCategory() {
        return SoundCategory.WEATHER;
    }

    @Nullable
    public ServerPlayerEntity getChanneler() {
        return this.channeler;
    }

    public void setChanneler(@Nullable ServerPlayerEntity serverPlayerEntity) {
        this.channeler = serverPlayerEntity;
    }


    public void tick() {
        super.tick();
        if (this.ambientTick == 2 || (this.getColor() == 1 && this.age % 40 == 0)) {
            if (this.getWorld().isClient()) {
                this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F, false);
                this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F, false);
            }
        }

        --this.ambientTick;
        if(this.getColor() == 0) {
            Iterator var2;
            List list;
            if (this.remainingActions == 0) {
                this.discard();
            } else if (0 == this.random.nextInt(2)) {
                --this.remainingActions;
                this.ambientTick = 1;
                this.seed = this.random.nextLong();

            }
        }
        if(this.getColor() == 1 && this.age % 3 == 0){
            this.seed = this.random.nextLong();
        }
        if(this.getColor() == 1 && this.getOwner() instanceof LivingEntity && this.age % 5 == 0 && !this.getWorld().isClient()){
            for(Entity living : TargetHelper.targetsFromArea(this,SpellRegistry.getSpell(Identifier.of(Druids.MODID,
                    "stormcall")).range, SpellRegistry.getSpell(Identifier.of(Druids.MODID,
                    "stormcall")).release.target.area,(target) -> TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL,(LivingEntity)this.getOwner(),
                    target))){
                if(living instanceof CreeperEntity creeper && creeper.getWorld() instanceof ServerWorld world && Druids.config.charge_creepers){
                    creeper.onStruckByLightning(world,new LightningEntity(EntityType.LIGHTNING_BOLT,creeper.getWorld()));
                }
                Spell spell = SpellRegistry.getSpell(Identifier.of(Druids.MODID, "stormcall"));
                SpellInfo info = new SpellInfo(spell, Identifier.of(Druids.MODID,"stormcall"));

                SpellHelper.performImpacts(this.getWorld(), (LivingEntity) this.getOwner(), living, this.getOwner(),
                        info, new SpellHelper.ImpactContext());
            }
        }
        if(this.getWorld().isClient  && this.getColor() == 1) {
            int NUM_POINTS = 100;
            double radius = SpellRegistry.getSpell(Identifier.of(Druids.MODID,
                    "stormcall")).range;
            for (int i = 0; i < NUM_POINTS; ++i) {
                final double angle = Math.toRadians(((double) i / NUM_POINTS) * 360d);

                double x = Math.cos(angle) * radius;
                double y = Math.sin(angle) * radius;
                if (this.getWorld().getRandom().nextBoolean()) {
                    this.getWorld().addParticle(ParticleTypes.SCULK_SOUL, true, this.getX() + x, this.getY(), this.getZ() + y, 0, 0, 0);
                }
                if (this.getWorld().getRandom().nextBoolean()) {

                    this.getWorld().addParticle(ParticleTypes.ELECTRIC_SPARK, true, this.getX() + x, this.getY(), this.getZ() + y, 0, 0, 0);
                }
            }
        }
        if(this.age > 16*20 && !this.getWorld().isClient()){
            this.discard();
        }

    }



    private static void cleanOxidation(World world, BlockPos blockPos) {
        BlockState blockState = world.getBlockState(blockPos);
        BlockPos blockPos2;
        BlockState blockState2;
        if (blockState.isOf(Blocks.LIGHTNING_ROD)) {
            blockPos2 = blockPos.offset(((Direction)blockState.get(LightningRodBlock.FACING)).getOpposite());
            blockState2 = world.getBlockState(blockPos2);
        } else {
            blockPos2 = blockPos;
            blockState2 = blockState;
        }

        if (blockState2.getBlock() instanceof Oxidizable) {
            world.setBlockState(blockPos2, Oxidizable.getUnaffectedOxidationState(world.getBlockState(blockPos2)));
            BlockPos.Mutable mutable = blockPos.mutableCopy();
            int i = world.random.nextInt(3) + 3;

            for(int j = 0; j < i; ++j) {
                int k = world.random.nextInt(8) + 1;
                cleanOxidationAround(world, blockPos2, mutable, k);
            }

        }
    }

    private static void cleanOxidationAround(World world, BlockPos blockPos, BlockPos.Mutable mutable, int i) {
        mutable.set(blockPos);

        for(int j = 0; j < i; ++j) {
            Optional<BlockPos> optional = cleanOxidationAround(world, mutable);
            if (!optional.isPresent()) {
                break;
            }

            mutable.set((Vec3i)optional.get());
        }

    }

    private static Optional<BlockPos> cleanOxidationAround(World world, BlockPos blockPos) {
        Iterator var2 = BlockPos.iterateRandomly(world.random, 10, blockPos, 1).iterator();

        BlockPos blockPos2;
        BlockState blockState;
        do {
            if (!var2.hasNext()) {
                return Optional.empty();
            }

            blockPos2 = (BlockPos)var2.next();
            blockState = world.getBlockState(blockPos2);
        } while(!(blockState.getBlock() instanceof Oxidizable));


        world.syncWorldEvent(3002, blockPos2, -1);
        return Optional.of(blockPos2);
    }

    public boolean shouldRender(double d) {
        double e = 64.0 * getRenderDistanceMultiplier();
        return d < e * e;
    }




    public int getBlocksSetOnFire() {
        return this.blocksSetOnFire;
    }

    public Stream<Entity> getStruckEntities() {
        return this.struckEntities.stream().filter(Entity::isAlive);
    }
}
