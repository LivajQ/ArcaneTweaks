package com.livajq.arcanetweaks.mixin.betterend;

import com.google.common.base.Suppliers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.ForgeRegistries;
import org.betterx.betterend.world.features.terrain.SulphuricCaveFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(SulphuricCaveFeature.class)
public class SulphuricCaveFeatureMixin {
    @Shadow @Final @Mutable
    private static BlockState WATER;
    
    private static final Supplier<Block> customLiquid = Suppliers.memoize(() ->
            ForgeRegistries.BLOCKS.getValue(new ResourceLocation("immersivepetroleum:crudeoil_fluid_block"))
    );
    
    private static boolean liquidReplaced = false;
    
    @Inject(method = "place", at = @At("HEAD"))
    private void replaceLiquid(FeaturePlaceContext<NoneFeatureConfiguration> ctx, CallbackInfoReturnable<Boolean> cir) {
        if (!liquidReplaced) {
            Block block = customLiquid.get();
            if (block != null) {
                WATER = block.defaultBlockState();
                liquidReplaced = true;
            }
        }
    }
}