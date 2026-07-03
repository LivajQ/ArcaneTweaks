package com.livajq.arcanetweaks.mixin.betterend;

import com.google.common.base.Suppliers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.ForgeRegistries;
import org.betterx.bclib.sdf.primitive.SDFPrimitive;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.world.features.terrain.GeyserFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;
import java.util.function.Supplier;

//TODO find out what else is missing to fully replace water everywhere. I'm getting tired of this shitass mod
@Mixin(GeyserFeature.class)
public class GeyserFeatureMixin {
    
    private static final Supplier<Block> customLiquid = Suppliers.memoize(() ->
            ForgeRegistries.BLOCKS.getValue(new ResourceLocation("immersivepetroleum:crudeoil_fluid_block"))
    );
    
    @Shadow
    @Final
    @Mutable
    private static Function<BlockState, Boolean> IGNORE;
    
    @Inject(method = "place", at = @At("HEAD"))
    private void place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig, CallbackInfoReturnable<Boolean> cir) {
        IGNORE = (state) -> state.is(customLiquid.get()) || state.is(Blocks.WATER) || state.is(Blocks.CAVE_AIR) || state.is(EndBlocks.SULPHURIC_ROCK.stone) || state.is(EndBlocks.BRIMSTONE);
    }
    
    @Redirect(method = "place", at = @At(value = "INVOKE",
            target = "Lorg/betterx/bclib/util/BlocksHelper;setWithoutUpdate(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private void redirectWaterState(LevelAccessor world, BlockPos pos, BlockState state) {
        if (state.is(Blocks.WATER)) state = customLiquid.get().defaultBlockState();
        BlocksHelper.setWithoutUpdate(world, pos, state);
    }
    
    @Redirect(method = "place", at = @At(value = "INVOKE",
            target = "Lorg/betterx/bclib/util/BlocksHelper;setWithoutUpdate(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;)V"))
    private void redirectWaterBlock(LevelAccessor world, BlockPos pos, Block block) {
        if (block == Blocks.WATER) block = customLiquid.get();
        BlocksHelper.setWithoutUpdate(world, pos, block);
    }
    
    @Redirect(method = "place", at = @At(value = "INVOKE",
            target = "Lorg/betterx/bclib/sdf/primitive/SDFPrimitive;setBlock(Lnet/minecraft/world/level/block/state/BlockState;)Lorg/betterx/bclib/sdf/primitive/SDFPrimitive;"))
    private SDFPrimitive redirectSDFBlockState(SDFPrimitive sdf, BlockState state) {
        if (state.is(Blocks.WATER)) state = customLiquid.get().defaultBlockState();
        return sdf.setBlock(state);
    }
    
    @Redirect(method = "place", at = @At(value = "INVOKE",
            target = "Lorg/betterx/bclib/sdf/primitive/SDFPrimitive;setBlock(Lnet/minecraft/world/level/block/Block;)Lorg/betterx/bclib/sdf/primitive/SDFPrimitive;"))
    private SDFPrimitive redirectSDFBlock(SDFPrimitive sdf, Block block) {
        if (block == Blocks.WATER) block = customLiquid.get();
        return sdf.setBlock(block);
    }
    
}