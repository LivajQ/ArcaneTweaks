package com.livajq.arcanetweaks;

import com.Polarice3.Goety.api.ritual.RitualType;
import com.livajq.arcanetweaks.bossbehavior.BossBehaviorRegistry;
import com.livajq.arcanetweaks.client.renderer.dimensioneffects.EndSkyboxEffects;
import com.livajq.arcanetweaks.compat.alexscaves.BiomeConfigLoader;
import com.livajq.arcanetweaks.compat.goety.ritualtype.AdeptNetherCustomRitualType;
import com.livajq.arcanetweaks.compat.goety.ritualtype.EndCustomRitualType;
import com.livajq.arcanetweaks.compat.goety.ritualtype.ExpertNetherCustomRitualType;
import com.livajq.arcanetweaks.handlers.PacketHandler;
import com.livajq.arcanetweaks.handlers.ResistanceHandler;
import com.livajq.arcanetweaks.handlers.ResourceReloadHandler;
import com.livajq.arcanetweaks.init.ArcaneBiomeSources;
import com.livajq.arcanetweaks.init.ArcaneEntities;
import com.livajq.arcanetweaks.init.ArcaneSounds;
import com.livajq.arcanetweaks.util.SpecialDeathMessages;
import com.livajq.arcanetweaks.world.level.storage.loot.predicates.ModLootConditions;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.betterx.betterend.client.render.BetterEndSkyEffect;
import org.slf4j.Logger;

@Mod(ArcaneTweaks.MODID)
public class ArcaneTweaks {
    public static final String MODID = "arcanetweaks";
    public static final String NAME = "ArcaneTweaks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ArcaneTweaks(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onRegister);
        forgeEventBus.addListener(this::reloadListener);
        
        ArcaneEntities.ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(ArcaneEntities::onRegisterAttributes);
        ArcaneSounds.SOUNDS.register(modEventBus);
        modEventBus.addListener(this::onRegister);
        
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC, MODID + "/arcanetweaks.toml");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            RitualType.addRitualType("end", new EndCustomRitualType());
            RitualType.addRitualType("adept_nether", new AdeptNetherCustomRitualType());
            RitualType.addRitualType("expert_nether", new ExpertNetherCustomRitualType());
            BossBehaviorRegistry.init();
            BiomeConfigLoader.init();
            PacketHandler.register();
            ResistanceHandler.init();
            //ReskillableGenerator.generateReskillableEntries();
            //ItemObliteratorGenerator.generateObliteratorBlacklist();
        });
    }
    
    private void reloadListener(AddReloadListenerEvent event) {
        event.addListener(new ResourceReloadHandler());
    }
    
    private void onRegister(RegisterEvent event) {
        event.register(Registries.BIOME_SOURCE, ArcaneBiomeSources::register);
        event.register(Registries.LOOT_CONDITION_TYPE, helper -> {helper.register(new ResourceLocation(ArcaneTweaks.MODID, "game_stage_per_player"), ModLootConditions.GAME_STAGE_PER_PLAYER);});
    }
    
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        
        @SubscribeEvent
        public static void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
            event.register(new ResourceLocation(MODID, "the_end_skybox"), new EndSkyboxEffects());
            event.register(new ResourceLocation("minecraft", "the_end"), new BetterEndSkyEffect());
        }
        
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                SpecialDeathMessages.init();
            });
        }
    }
}