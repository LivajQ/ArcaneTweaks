package com.livajq.arcanetweaks.mixin.betterend;

import com.livajq.arcanetweaks.init.ArcaneTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.betterx.betterend.blocks.HydrothermalVentBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HydrothermalVentBlock.class)
public class HydrothermalVentBlockMixin {
    
    @Inject(
            method = "canSurvive",
            at = @At("HEAD"),
            cancellable = true
    )
    private void allowAnyValidSurface(BlockState state, LevelReader world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState below = world.getBlockState(pos.below());
        cir.setReturnValue(below.is(ArcaneTags.HYDROTHERMAL_VENT_SURFACE));
    }
}