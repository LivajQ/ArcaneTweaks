package com.livajq.arcanetweaks.init;

import com.livajq.arcanetweaks.ArcaneTweaks;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class ArcaneDamageSources {
    
    public static final ResourceKey<DamageType> VAPORIZED = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(ArcaneTweaks.MODID, "vaporized"));
    public static final ResourceKey<DamageType> IN_FRENZY_FIRE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(ArcaneTweaks.MODID, "in_frenzy_fire"));
    
    public static DamageSource vaporized(Level level) {
        Holder<DamageType> type = level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(VAPORIZED);
        
        return new DamageSource(type) {
            
            @Override
            public Component getLocalizedDeathMessage(LivingEntity entity) {
                int i = entity.getRandom().nextInt(4);
               
                return Component.translatable(
                        "death.attack.vaporized." + i,
                        entity.getDisplayName()
                );
            }
        };
    }
    
    public static DamageSource inFrenziedFire(Level level) {
        Holder<DamageType> type = level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(IN_FRENZY_FIRE);
        
        return new DamageSource(type) {
            
            @Override
            public Component getLocalizedDeathMessage(LivingEntity entity) {
                int i = entity.getRandom().nextInt(3);
                
                return Component.translatable(
                        "death.attack.in_frenzy_fire." + i,
                        entity.getDisplayName()
                );
            }
        };
    }
}