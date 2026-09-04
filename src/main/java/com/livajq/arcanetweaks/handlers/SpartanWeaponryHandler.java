package com.livajq.arcanetweaks.handlers;

import com.estie.arcanecontent.common.capability.ArcaneCapabilities;
import com.estie.arcanecontent.common.capability.parry.ParryData;
import com.estie.arcanecontent.init.ArcaneSounds;
import com.estie.arcanecontent.init.ArcaneTags;
import com.livajq.arcanetweaks.ArcaneTweaks;
import com.livajq.arcanetweaks.Config;
import com.livajq.arcanetweaks.packet.UseParcoolStaminaServerPacket;
import com.oblivioussp.spartanweaponry.api.ModToolActions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = ArcaneTweaks.MODID)
public class SpartanWeaponryHandler {
    
    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!player.isUsingItem()) return;
        ItemStack stack = player.getUseItem();
        if (stack.isEmpty()) return;
        
        if (!stack.getItem().canPerformAction(stack, ModToolActions.MELEE_BLOCK)) return;
        
        player.getCapability(ArcaneCapabilities.PARRY).ifPresent(cap -> {
            if (cap.isUsingParryItem()) {
                Entity attackerEntity = event.getSource().getEntity();
                if (!(attackerEntity instanceof LivingEntity attacker)) return;
                
                if (cap.isParryWindowActive() && cap.canParrySource(event.getSource())) {
                    cap.performParrySuccess(attacker);
                    event.setCanceled(true);
                    return;
                }
                else cap.performParryFail();
                
                if (attacker.getType().is(ArcaneTags.DISABLES_MELEE_BLOCK)) {
                    player.stopUsingItem();
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                            ArcaneSounds.MELEE_BLOCK_BREAK.get(), SoundSource.PLAYERS, 1f, 1f);
                    
                    int cooldown = Config.blockBreakCooldownBase + (int)(Config.blockBreakCooldownExtra * event.getAmount());
                    cooldown = Math.min(Config.blockBreakCooldownMax, cooldown);
                    
                    player.getCooldowns().addCooldown(stack.getItem(), cooldown);
                }
                
                if (ModList.get().isLoaded("parcool")) {
                    int staminaCost = Config.blockStaminaConsumeBase + (int)(Config.blockStaminaConsumeExtra * event.getAmount());
                    staminaCost = Math.min(Config.blockStaminaConsumeMax, staminaCost);
                    
                    PacketHandler.INSTANCE.send(
                            PacketDistributor.PLAYER.with(() -> player),
                            new UseParcoolStaminaServerPacket(staminaCost)
                    );
                }
            }
        });
    }
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;
        if (event.side.isClient()) return;
        
        Player player = event.player;
        player.getCapability(ArcaneCapabilities.PARRY).ifPresent(ParryData::tickParry);
    }
}