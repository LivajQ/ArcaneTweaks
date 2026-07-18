package com.livajq.arcanetweaks.common.capability.parry;

import com.livajq.arcanetweaks.Config;
import com.livajq.arcanetweaks.init.ArcaneSounds;
import com.oblivioussp.spartanweaponry.api.ModToolActions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class ParryImp implements ParryData {

    Player player;
    
    private long parryWindUpTicks = 0;
    private long parryWindUpTicksMax = Config.parryWindUp;
    private long parryWindowTicks = 0;
    private long parryWindowTicksMax = Config.parryWindow;
    private long parryCooldownTicks = 0;
    private long parryCooldownTicksFail = Config.parryCooldownFail;
    private long ParryCooldownTicksSuccess = Config.parryCooldownSuccess;
    
    private boolean parryWindowActive = false;
    
    public ParryImp(Player player) {
        this.player = player;
    }
    
    @Override
    public boolean isUsingParryItem() {
        if (player.isUsingItem()) {
            ItemStack stack = player.getUseItem();
            if (stack.isEmpty()) return false;
            return stack.getItem().canPerformAction(stack, ModToolActions.MELEE_BLOCK);
        }
        return false;
    }
    
    @Override
    public boolean isParryWindowActive() {
        return parryWindowActive;
    }
    
    @Override
    public boolean canParrySource(DamageSource source) {
        return source.getMsgId().equals("player")
                || source.getMsgId().equals("mob")
                && !source.is(DamageTypeTags.IS_EXPLOSION)
                && !source.is(DamageTypeTags.IS_FIRE)
                && !source.is(DamageTypeTags.IS_PROJECTILE)
                && !source.is(DamageTypeTags.BYPASSES_ARMOR);
    }
    
    @Override
    public void setParryWindowActive(boolean parryWindowActive) {
        this.parryWindowActive = parryWindowActive;
    }
    
    @Override
    public void tickParry() {
        if (parryCooldownTicks > 0) {
            parryCooldownTicks--;
            return;
        }
        
        if (!isUsingParryItem()) {
            if (parryWindUpTicks > 0 || parryWindowTicks > 0 || isParryWindowActive()) performParryFail();
            return;
        }
        
        if (!isParryWindowActive()) {
            if (parryWindUpTicks <= 0) parryWindUpTicks = parryWindUpTicksMax;
            else {
                parryWindUpTicks--;
                if (parryWindUpTicks <= 0) {
                    setParryWindowActive(true);
                    parryWindowTicks = parryWindowTicksMax;
                }
            }
        }
        else {
            parryWindowTicks--;
            if (parryWindowTicks <= 0) performParryFail();
        }
    }
    
    @Override
    public void performParryFail() {
        parryCooldownTicks = parryCooldownTicksFail;
        parryWindUpTicks = 0;
        parryWindowTicks = 0;
        setParryWindowActive(false);
    }
    
    @Override
    public void performParrySuccess(LivingEntity target) {
        parryCooldownTicks = ParryCooldownTicksSuccess;
        parryWindUpTicks = 0;
        parryWindowTicks = 0;
        setParryWindowActive(false);
        
        if (player.level() instanceof ServerLevel level) {
            
            Vec3 eyePos = player.getEyePosition();
            Vec3 look = player.getLookAngle();
            Vec3 sparkPos = eyePos.add(look.scale(0.6));
            
            int count = 30;
            for (int i = 0; i < count; i++) {
                double dx = (level.random.nextDouble() - 0.5) * 0.6;
                double dy = (level.random.nextDouble() - 0.5) * 0.6;
                double dz = (level.random.nextDouble() - 0.5) * 0.6;
                
                level.sendParticles(
                        ParticleTypes.FLAME,
                        sparkPos.x, sparkPos.y, sparkPos.z,
                        1,
                        dx, dy, dz,
                        0.3
                );
            }
            
            level.sendParticles(
                    ParticleTypes.FLASH,
                    sparkPos.x, sparkPos.y, sparkPos.z,
                    1, 0, 0, 0, 0
            );
            
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ArcaneSounds.MELEE_PARRY.get(), SoundSource.PLAYERS, 1f, 1f);
            
            MobEffect stun = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("eeeabsmobs", "stun_effect"));
            MobEffect vulnerable = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("apothecary", "vulnerable"));
            MobEffect disruption = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("spartanweaponry", "ender_distruption"));
            MobEffect inexhaustible = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("parcool", "inexhaustible"));
            MobEffect strength = MobEffects.DAMAGE_BOOST;
           
            if (stun != null) target.addEffect(new MobEffectInstance(stun, 40, 0));
            if (vulnerable != null) target.addEffect(new MobEffectInstance(vulnerable, 40, 1));
            if (disruption != null) target.addEffect(new MobEffectInstance(disruption, 600, 0));
            if (inexhaustible != null) player.addEffect(new MobEffectInstance(inexhaustible, 60, 0));
            player.addEffect(new MobEffectInstance(strength, 60, 1));
        }
    }
    
    @Override
    public void setParryWindUpTicksMax(int ticks) {
        parryWindUpTicksMax = ticks;
    }
    
    @Override
    public void setParryWindowTicksMax(int ticks) {
        parryWindowTicksMax = ticks;
    }
    
    @Override
    public void setParryCooldownTicksFail(int ticks) {
        parryCooldownTicksFail = ticks;
    }
    
    @Override
    public void setParryCooldownTicksSuccess(int ticks) {
        ParryCooldownTicksSuccess = ticks;
    }
    
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("ParryWindUPTicks", parryWindUpTicks);
        tag.putLong("ParryWindowTicks", parryWindowTicks);
        tag.putLong("ParryCooldownTicks", parryCooldownTicks);
        tag.putBoolean("ParryWindowActive", parryWindowActive);
        return tag;
    }
   
    public void deserializeNBT(CompoundTag tag) {
        parryWindUpTicks = tag.getLong("ParryWindUPTicks");
        parryWindowTicks = tag.getLong("ParryWindowTicks");
        parryCooldownTicks = tag.getLong("ParryCooldownTicks");
        parryWindowActive = tag.getBoolean("ParryWindowActive");
    }
}