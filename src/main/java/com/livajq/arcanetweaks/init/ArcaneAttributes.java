package com.livajq.arcanetweaks.init;

import com.livajq.arcanetweaks.ArcaneTweaks;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ArcaneAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, ArcaneTweaks.MODID);
    
    public static final RegistryObject<Attribute> SHIELD_BASH_STRENGTH =
            ATTRIBUTES.register("shield_bash_strength", () ->
                    new RangedAttribute("attribute." + ArcaneTweaks.MODID + ".shield_bash_strength",
                            0.0D,
                            0.0D,
                            1024.0D
                    ).setSyncable(true)
            );
}