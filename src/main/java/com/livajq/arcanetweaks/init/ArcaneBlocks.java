package com.livajq.arcanetweaks.init;

import com.livajq.arcanetweaks.ArcaneTweaks;
import com.livajq.arcanetweaks.common.block.BlockOfSilly;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ArcaneBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ArcaneTweaks.MODID);
    
    public static final RegistryObject<BlockOfSilly> BLOCK_OF_SILLY = BLOCKS.register("block_of_silly",
            () -> new BlockOfSilly(BlockBehaviour.Properties.copy(Blocks.STONE).sound(ArcaneSounds.BLOCK_OF_SILLY.get())));
}