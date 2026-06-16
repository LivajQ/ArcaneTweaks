package com.livajq.arcanetweaks.init;

import com.livajq.arcanetweaks.ArcaneTweaks;
import com.livajq.arcanetweaks.common.item.BlockItemOfSilly;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ArcaneItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ArcaneTweaks.MODID);
    public static final RegistryObject<BlockItemOfSilly> BLOCK_OF_SILLY = ITEMS.register("block_of_silly", () -> new BlockItemOfSilly(ArcaneBlocks.BLOCK_OF_SILLY.get(), new Item.Properties()));
}