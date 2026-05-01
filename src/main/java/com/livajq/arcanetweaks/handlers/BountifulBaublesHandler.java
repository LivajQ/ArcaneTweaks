package com.livajq.arcanetweaks.handlers;

import com.jinqinxixi.bountifulbaubles.item.Baubles.BrokenHeartItem;
import com.jinqinxixi.bountifulbaubles.item.Baubles.PhylacteryCharmItem;
import com.livajq.arcanetweaks.ArcaneTweaks;
import com.livajq.arcanetweaks.mixin.bountifulbaubles.BrokenHeartItemAccessor;
import com.livajq.arcanetweaks.mixin.bountifulbaubles.PhylacteryCharmItemAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArcaneTweaks.MODID)
public class BountifulBaublesHandler {
    
    //mostly a broken heart/phylactery class copypaste but moved to LivingDamage LOWEST, so it actually uses armor and other reductions
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        if (!ModList.get().isLoaded("bountifulbaubles")) return;
        if (!ModList.get().isLoaded("firstaid")) {
            LivingEntity var2 = event.getEntity();
            if (var2 instanceof Player) {
                Player player = (Player)var2;
                if (!event.isCanceled() && !player.level().isClientSide) {
                    if (PhylacteryCharmItem.isEquipped(player)) handlePhylacteryDamageProtection(player, event);
                    if (event.isCanceled()) return;
                    if (BrokenHeartItem.isEquipped(player)) handleHeartDamageProtection(player, event);
                }
            }
        }
    }
    
    private static void handleHeartDamageProtection(Player player, LivingDamageEvent event) {
        float effectiveHealth = player.getHealth() + player.getAbsorptionAmount();
        float damageAmount = event.getAmount();
        
        if (damageAmount < effectiveHealth) return;
        
        float overflow = damageAmount - effectiveHealth;
        float adjusted = overflow <= 1.0F ? 1.0F : overflow;
        
        AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttr == null) return;
        
        if (maxHealthAttr.getValue() <= adjusted) return;

        event.setCanceled(true);
        player.setAbsorptionAmount(0);
        player.setHealth(1);
        
        CompoundTag data = player.getPersistentData();
        float totalLoss = data.getFloat("broken_heart_loss") + adjusted;
        data.putFloat("broken_heart_loss", totalLoss);
        
        BrokenHeartItemAccessor.invokeUpdateHealthModifier(player, maxHealthAttr, totalLoss);
        
        if (player instanceof ServerPlayer sp) {
            sp.level().playSound(null, sp.getX(), sp.getY(), sp.getZ(),
                    SoundEvents.IRON_GOLEM_HURT, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        
        BrokenHeartItemAccessor.invokeTriggerAttributeSync(player);
    }

    private static void handlePhylacteryDamageProtection(Player player, LivingDamageEvent event) {
        float effectiveHealth = player.getHealth() + player.getAbsorptionAmount();
        float damageAmount = event.getAmount();
        
        if (damageAmount < effectiveHealth) return;
        
        float overflow = damageAmount - effectiveHealth;
        float adjusted = overflow <= 1.0F ? 1.0F : overflow;
        
        AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttr == null) return;
        
        if (maxHealthAttr.getValue() <= adjusted) return;
        
        event.setCanceled(true);
        player.setAbsorptionAmount(0);
        player.setHealth(1);
        
        CompoundTag data = player.getPersistentData();
        float totalLoss = data.getFloat("phylactery_loss") + adjusted;
        data.putFloat("phylactery_loss", totalLoss);
        
        PhylacteryCharmItemAccessor.invokeUpdateHealthModifier(player, maxHealthAttr, totalLoss);
        
        if (player instanceof ServerPlayer sp) {
            PhylacteryCharmItem.performTeleport(sp);
        }
        
        PhylacteryCharmItemAccessor.invokeTriggerAttributeSync(player);
    }
}