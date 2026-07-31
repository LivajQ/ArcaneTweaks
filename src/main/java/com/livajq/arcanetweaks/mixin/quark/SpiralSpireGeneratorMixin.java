package com.livajq.arcanetweaks.mixin.quark;

import com.livajq.arcanetweaks.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.violetmoon.quark.content.world.gen.SpiralSpireGenerator;

@Mixin(SpiralSpireGenerator.class)
public abstract class SpiralSpireGeneratorMixin {
    
    @Redirect(
            method = "getSourcesInChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/BlockPos;closerThan(Lnet/minecraft/core/Vec3i;D)Z"
            )
    )
    private boolean removeCenterDistanceRestriction(BlockPos chunkCorner, Vec3i vec, double distance) {
        return false;
    }
    
    @Redirect(
            method = "generateChunkPart",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;"
            )
    )
    private Block changeTargetAnchorBlock(BlockState state) {
        Block actual = state.getBlock();
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(actual);
        boolean isValid = id != null && Config.spiralSpireBlocksSet.contains(id.toString());
        return isValid ? Blocks.END_STONE : actual;
    }
    
    @Redirect(
            method = "makeSpike",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z"
            )
    )
    private boolean bypassPreScanCheck(BlockState state) {
        return true;
    }
}