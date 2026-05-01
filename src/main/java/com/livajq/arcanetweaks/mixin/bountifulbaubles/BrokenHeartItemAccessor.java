package com.livajq.arcanetweaks.mixin.bountifulbaubles;

import com.jinqinxixi.bountifulbaubles.item.Baubles.BrokenHeartItem;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BrokenHeartItem.class)
public interface BrokenHeartItemAccessor {
    
    @Invoker(value = "updateHealthModifier")
    static void invokeUpdateHealthModifier(Player player, AttributeInstance attr, float loss) {}
    
    @Invoker(value = "triggerAttributeSync")
    static void invokeTriggerAttributeSync(Player player) {}
}