package com.livajq.arcanetweaks.mixin.betterend;

import com.google.common.base.Suppliers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.world.features.terrain.SulphuricLakeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

@Mixin(SulphuricLakeFeature.class)
public class SulphuricLakeFeatureMixin {
    
    private static final Supplier<Block> customLiquid = Suppliers.memoize(() ->
            ForgeRegistries.BLOCKS.getValue(new ResourceLocation("immersivepetroleum:crudeoil_fluid_block"))
    );
    
    @Redirect(
            method = "place",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/betterx/bclib/util/BlocksHelper;setWithoutUpdate(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;)V"
            )
    )
    private void redirectWaterState(LevelAccessor world, BlockPos pos, Block block) {
        if (block == Blocks.WATER) block = customLiquid.get();
        BlocksHelper.setWithoutUpdate(world, pos, block);
    }
}
