package com.livajq.arcanetweaks.mixin.eeeabsmobs;

import com.eeeab.eeeabsmobs.sever.item.util.EMArmorMaterial;
import net.minecraft.world.item.ArmorMaterial;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EMArmorMaterial.class)
public abstract class EMArmorMaterialMixin implements ArmorMaterial {
    
    @Shadow
    @Final
    private String name;
    
    @Overwrite
    public String getName() {
        return name;
    }
}