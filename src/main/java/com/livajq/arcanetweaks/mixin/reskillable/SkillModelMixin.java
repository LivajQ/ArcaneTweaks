package com.livajq.arcanetweaks.mixin.reskillable;

import com.livajq.arcanetweaks.util.MajruszsUtils;
import com.livajq.arcanetweaks.util.ReskillableUtils;
import net.bandit.reskillable.common.capabilities.SkillModel;
import net.bandit.reskillable.common.commands.skills.Skill;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkillModel.class)
public abstract class SkillModelMixin {
    
    @Inject(method = "increaseSkillLevel", at = @At("HEAD"), cancellable = true, remap = false)
    private void checkGamestageLimit(Skill skill, Player player, CallbackInfo ci) {
        if (player.isCreative()) return;
        if (!MajruszsUtils.isLoaded()) return;
        
        SkillModel model = (SkillModel)(Object)this;
        int level = model.getSkillLevel(skill);
        int maxAllowed = ReskillableUtils.getMaxLevelForGamestage(player);
        
        if (level >= maxAllowed) {
            player.closeContainer();
            player.displayClientMessage(Component.literal("Maximum level for this mode is " + maxAllowed).withStyle(ChatFormatting.RED), true);
            ci.cancel();
        }
    }
}