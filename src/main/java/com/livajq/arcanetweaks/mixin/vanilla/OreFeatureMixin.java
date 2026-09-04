package com.livajq.arcanetweaks.mixin.vanilla;

import com.estie.arcanecontent.init.ArcaneBlocks;
import com.livajq.arcanetweaks.Config;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(OreFeature.class)
public abstract class OreFeatureMixin {
    
    @Redirect(
            method = "doPlace",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;"
            )
    )
    private BlockState replaceWithSilly(LevelChunkSection section, int x, int y, int z, BlockState state, boolean lock) {
        if (Math.random() < Config.blockSillyChance / 100) {
            state = ArcaneBlocks.BLOCK_OF_SILLY.get().defaultBlockState();
        }
        return section.setBlockState(x, y, z, state, lock);
    }
}
