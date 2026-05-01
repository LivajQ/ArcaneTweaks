package com.livajq.arcanetweaks.mixin.bountifulbaubles;

import com.jinqinxixi.bountifulbaubles.item.Baubles.PhylacteryCharmItem;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PhylacteryCharmItem.class)
public interface PhylacteryCharmItemAccessor {
    
    @Invoker(value = "updateHealthModifier")
    static void invokeUpdateHealthModifier(Player player, AttributeInstance attr, float loss) {}
    
    @Invoker(value = "triggerAttributeSync")
    static void invokeTriggerAttributeSync(Player player) {}
}