package com.livajq.arcanetweaks.mixin.bountifulbaubles;

import com.jinqinxixi.bountifulbaubles.event.listener.ModEvents;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ModEvents.class)
public interface ModEventsAccessor {
    
    @Invoker(value = "handleCriticalHit")
    static void invokeHandleCriticalHit(Player player) {}
}