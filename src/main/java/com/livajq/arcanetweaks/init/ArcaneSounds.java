package com.livajq.arcanetweaks.init;

import com.livajq.arcanetweaks.ArcaneTweaks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ArcaneSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ArcaneTweaks.MODID);
    
    public static final RegistryObject<SoundEvent> DRAGON_NUKE_CHARGE_FIRE =
            SOUNDS.register("dragon_nuke_charge_fire",
                    () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ArcaneTweaks.MODID, "dragon_nuke_charge_fire")));
    
    public static final RegistryObject<SoundEvent> DRAGON_NUKE_CHARGE_ICE =
            SOUNDS.register("dragon_nuke_charge_ice",
                    () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ArcaneTweaks.MODID, "dragon_nuke_charge_ice")));
    
    public static final RegistryObject<SoundEvent> DRAGON_NUKE_CHARGE_LIGHTNING =
            SOUNDS.register("dragon_nuke_charge_lightning",
                    () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ArcaneTweaks.MODID, "dragon_nuke_charge_lightning")));
    
    public static final RegistryObject<SoundEvent> DRAGON_NUKE_EXPLODE =
            SOUNDS.register("dragon_nuke_explode",
                    () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ArcaneTweaks.MODID, "dragon_nuke_explode")));
    
    public static final RegistryObject<SoundEvent> MRKRABS_SADGE =
            SOUNDS.register("mrkrabs_sadge",
                    () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ArcaneTweaks.MODID, "mrkrabs_sadge")));
    
    public static final RegistryObject<SoundEvent> MELEE_BLOCK_BREAK =
            SOUNDS.register("melee_block_break",
                    () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ArcaneTweaks.MODID, "melee_block_break")));
    
    public static final RegistryObject<SoundEvent> MELEE_PARRY =
            SOUNDS.register("melee_parry",
                    () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ArcaneTweaks.MODID, "melee_parry")));
    
    public static final RegistryObject<SoundEvent> FUNNEHSOUND =
            SOUNDS.register("funnehsound",
                    () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ArcaneTweaks.MODID, "funnehsound")));
    
    public static final RegistryObject<SoundEvent> BLOCK_OF_SILLY_MINE =
            SOUNDS.register("block_of_silly_mine",
                    () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ArcaneTweaks.MODID, "block_of_silly_mine")));
    
    public static final Supplier<SoundType> BLOCK_OF_SILLY = () -> new SoundType(1.0f, 1.3f, BLOCK_OF_SILLY_MINE.get(), SoundEvents.STONE_STEP, SoundEvents.STONE_PLACE, SoundEvents.STONE_HIT, SoundEvents.STONE_FALL);
    
    public static final RegistryObject<SoundEvent> WITHER_BOSS_PHASE_1 =
            SOUNDS.register("wither_boss_phase_1",
                    () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ArcaneTweaks.MODID, "wither_boss_phase_1")));
    
    public static final RegistryObject<SoundEvent> WITHER_BOSS_PHASE_2 =
            SOUNDS.register("wither_boss_phase_2",
                    () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ArcaneTweaks.MODID, "wither_boss_phase_2")));
    
    public static final RegistryObject<SoundEvent> WITHER_BOSS_PHASE_3 =
            SOUNDS.register("wither_boss_phase_3",
                    () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ArcaneTweaks.MODID, "wither_boss_phase_3")));
}