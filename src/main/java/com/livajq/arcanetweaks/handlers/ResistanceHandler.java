package com.livajq.arcanetweaks.handlers;

import com.livajq.arcanetweaks.ArcaneTweaks;
import com.livajq.arcanetweaks.Config;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.saksolm.monsterexpansion.entity.util.DamageTypes;
import net.saksolm.monsterexpansion.util.ICombatData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ArcaneTweaks.MODID)
public class ResistanceHandler {
    private static final UUID FIRE_MAGIC_RES_UUID = UUID.fromString("d4e8f2d0-6c4a-4b6e-9f1a-2b7a6e3c1a11");
    private static final UUID ICE_MAGIC_RES_UUID = UUID.fromString("d4e8f2d0-6c4a-4b6e-9f1a-2b7a6e3c1a12");
    private static final UUID LIGHTNING_MAGIC_RES_UUID = UUID.fromString("d4e8f2d0-6c4a-4b6e-9f1a-2b7a6e3c1a13");
    
    private static final Map<MobEffect, MagicResData> RESISTANCES = new HashMap<>();
    
    public static void init() {
        RESISTANCES.put(MobEffects.FIRE_RESISTANCE, new MagicResData(MobEffects.FIRE_RESISTANCE, AttributeRegistry.FIRE_MAGIC_RESIST.get(), FIRE_MAGIC_RES_UUID, Config.fireResistanceAmount));
        
        MobEffect ice = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("trinketsandbaubles", "ice_resistance"));
        if (ice != null) RESISTANCES.put(ice, new MagicResData(ice, AttributeRegistry.ICE_MAGIC_RESIST.get(), ICE_MAGIC_RES_UUID, Config.iceResistanceAmount));
        
        MobEffect lightning = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("trinketsandbaubles", "lightning_resistance"));
        if (lightning != null) RESISTANCES.put(lightning, new MagicResData(lightning, AttributeRegistry.LIGHTNING_MAGIC_RESIST.get(), LIGHTNING_MAGIC_RES_UUID, Config.lightningResistanceAmount));
    }
    
    @SubscribeEvent
    public static void customResistanceCalc(LivingDamageEvent event) {
        DamageSource source = event.getSource();
        if (source.is(DamageTypeTags.BYPASSES_RESISTANCE) || source.is(DamageTypeTags.BYPASSES_EFFECTS)) return;
        LivingEntity target = event.getEntity();
        MobEffectInstance resistance = target.getEffect(MobEffects.DAMAGE_RESISTANCE);
        if (resistance == null) return;
        
        float resAmount = (float) ((resistance.getAmplifier() + 1) * Config.resistanceAmount);
        event.setAmount(event.getAmount() * Math.max(1 - resAmount, 0));
    }
    
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void customFireResistanceCalc(LivingDamageEvent event) {
        DamageSource source = event.getSource();
        LivingEntity target = event.getEntity();
        
        if (!source.is(DamageTypeTags.IS_FIRE) || !target.hasEffect(MobEffects.FIRE_RESISTANCE)) return;
        MobEffectInstance fireResistance = target.getEffect(MobEffects.FIRE_RESISTANCE);
        if (fireResistance == null) return;
        
        float fireResAmount = (float) ((fireResistance.getAmplifier() + 1) * Config.fireResistanceAmount);
        if (fireResAmount >= 1.0F) target.setRemainingFireTicks(0);
        event.setAmount(event.getAmount() * Math.max(1 - fireResAmount, 0));
    }
    
    @SubscribeEvent
    public static void addMagicResBonus(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance inst = event.getEffectInstance();
        if (entity == null || inst == null) return;
        
        MagicResData data = RESISTANCES.get(inst.getEffect());
        if (data == null) return;
        
        AttributeInstance attr = entity.getAttribute(data.attribute());
        if (attr == null) return;
        
        double value = (inst.getAmplifier() + 1) * data.multiplier();
        
        attr.removeModifier(data.uuid());
        attr.addPermanentModifier(
                new AttributeModifier(data.uuid(), "Elemental magic res bonus", value, AttributeModifier.Operation.MULTIPLY_TOTAL)
        );
    }
    
    @SubscribeEvent
    public static void removeMagicResBonus(MobEffectEvent.Remove event) {
        LivingEntity entity = event.getEntity();
        if (entity == null) return;
        
        MagicResData data = RESISTANCES.get(event.getEffect());
        if (data == null) return;
        
        AttributeInstance attr = entity.getAttribute(data.attribute());
        if (attr != null) attr.removeModifier(data.uuid());
    }
    
    private record MagicResData(MobEffect effect, Attribute attribute, UUID uuid, double multiplier) {}
    
    @SubscribeEvent
    public static void onSpellDamage(SpellDamageEvent event) {
        ICombatData combatData = (ICombatData) event.getSpellDamageSource();
        AbstractSpell spell = event.getSpellDamageSource().spell();
        
        if (spell.getSchoolType() == SchoolRegistry.FIRE.get()) combatData.setElementType(DamageTypes.ElementType.FIRE);
        else if (spell.getSchoolType() == SchoolRegistry.ICE.get()) combatData.setElementType(DamageTypes.ElementType.ICE);
        else return;
        combatData.setElementalDamage(event.getAmount());
    }
}