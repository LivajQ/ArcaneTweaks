package com.livajq.arcanetweaks.common.block;

import com.livajq.arcanetweaks.init.ArcaneDamageSources;
import com.livajq.arcanetweaks.init.ArcaneTags;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

public class FrenzyFireBlock extends BaseFireBlock {
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;
    
    private final float frenzyFireDamage;
    
    public FrenzyFireBlock(Properties properties, float frenzyFireDamage) {
        super(properties, frenzyFireDamage);
        this.frenzyFireDamage = frenzyFireDamage;
        
        registerDefaultState(
                stateDefinition.any()
                        .setValue(NORTH, false)
                        .setValue(EAST, false)
                        .setValue(SOUTH, false)
                        .setValue(WEST, false)
                        .setValue(UP, false)
        );
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP);
    }

    @Override
    protected boolean canBurn(BlockState state) {
        return true;
    }
    
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide) return;
        if (entity.getType().is(ArcaneTags.FRENZY_FIRE_IMMUNE)) return;
        if (entity instanceof LivingEntity livingEntity) applyFrenzy(level, livingEntity);
        entity.setSecondsOnFire(8);
    }
    
    private void applyFrenzy(Level level, LivingEntity entity) {
        if (entity.invulnerableTime > 11) return;
        entity.invulnerableTime = 0;
        MobEffect soulFracture = ModEffects.SOUL_FRACTURE.get();
        
        if (entity.hasEffect(soulFracture)) {
            int amplifier = entity.getEffect(soulFracture).getAmplifier();
            entity.removeEffect(soulFracture);
            entity.addEffect(new MobEffectInstance(soulFracture, 160, amplifier + 1));
        }
        else entity.addEffect(new MobEffectInstance(soulFracture, 160, 0));
        
        entity.hurt(ArcaneDamageSources.inFrenzyFire(level), this.frenzyFireDamage);
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.FAIL;
    }
    
    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        super.attack(state, level, pos, player);
        if (level.isClientSide) return;
        applyFrenzy(level, player);
    }
    
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState()
                .setValue(NORTH, true)
                .setValue(EAST, true)
                .setValue(SOUTH, true)
                .setValue(WEST, true)
                .setValue(UP, true);
    }
    
    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return 0.0F;
    }
    
    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return false;
    }
    
    @Override
    public boolean canBeReplaced(BlockState state, Fluid fluid) {
        return false;
    }
}