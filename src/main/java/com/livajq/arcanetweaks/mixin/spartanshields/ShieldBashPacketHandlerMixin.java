package com.livajq.arcanetweaks.mixin.spartanshields;

import com.livajq.arcanetweaks.Config;
import com.livajq.arcanetweaks.init.ArcaneAttributes;
import com.oblivioussp.spartanshields.network.ShieldBashPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ShieldBashPacket.Handler.class)
public class ShieldBashPacketHandlerMixin {
    
    @ModifyVariable(
            method = "bashEntity",
            at = @At(value = "STORE", ordinal = 0),
            name = "bashDamage",
            remap = false
    )
    private static float modifyBashDamage(float original, Entity targetEntity, Player player, ItemStack shieldStack, int knockbackLevel, InteractionHand hand) {
        if (player.getAttributes().hasAttribute(ArcaneAttributes.SHIELD_BASH_STRENGTH.get())) original += (float) player.getAttributeValue(ArcaneAttributes.SHIELD_BASH_STRENGTH.get());
      
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(shieldStack.getItem());
        if (Config.shieldBashProperties.containsKey(id)) {
            Config.ShieldBashProperty property = Config.shieldBashProperties.get(id);
            original += property.damageBonus();
            
            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(property.effectId());
            if (effect != null && targetEntity instanceof LivingEntity livingEntity) livingEntity.addEffect(new MobEffectInstance(effect, property.duration(), property.amplifier()));
        }
        
        return original;
    }
}
