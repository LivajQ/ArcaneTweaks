package com.livajq.arcanetweaks.mixin.betterend;

import com.google.common.base.Suppliers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.world.features.terrain.SulphurHillFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

@Mixin(SulphurHillFeature.class)
public class SulphurHillFeatureMixin {
    
    private static final Supplier<Block> customLiquid = Suppliers.memoize(() ->
            ForgeRegistries.BLOCKS.getValue(new ResourceLocation("immersivepetroleum:crudeoil_fluid_block"))
    );
    
    @Redirect(
            method = "makeCircle",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/betterx/bclib/util/BlocksHelper;setWithoutUpdate(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"            ),
            remap = false
    )
    private void redirectWaterState(LevelAccessor world, BlockPos pos, BlockState state) {
        if (state.is(Blocks.WATER)) {
            Block block = customLiquid.get();
            if (block != null) state = block.defaultBlockState();
        }
        BlocksHelper.setWithoutUpdate(world, pos, state);
    }
    
    @Redirect(
            method = "makeCircle",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/betterx/bclib/util/BlocksHelper;setWithoutUpdate(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;)V"            ),
            remap = false
    )
    private void redirectWaterBlock(LevelAccessor world, BlockPos pos, Block block) {
        if (block == Blocks.WATER) {
            Block block2 = customLiquid.get();
            if (block2 != null) block = block2;
        }
        BlocksHelper.setWithoutUpdate(world, pos, block);
    }
}
