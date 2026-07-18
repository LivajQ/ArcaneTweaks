package com.livajq.arcanetweaks.handlers;

import com.livajq.arcanetweaks.ArcaneTweaks;
import com.livajq.arcanetweaks.init.ArcaneAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArcaneTweaks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AttributesHandler {
    
    @SubscribeEvent
    public static void onAttributeModification(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ArcaneAttributes.SHIELD_BASH_STRENGTH.get());
    }
}
