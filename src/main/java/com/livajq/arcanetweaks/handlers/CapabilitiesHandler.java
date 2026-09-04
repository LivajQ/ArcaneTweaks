package com.livajq.arcanetweaks.handlers;

import com.estie.arcanecontent.common.capability.bossminion.BossMinionData;
import com.estie.arcanecontent.common.capability.bossminion.BossMinionProvider;
import com.estie.arcanecontent.common.capability.foodbonuses.FoodBonusesData;
import com.estie.arcanecontent.common.capability.foodbonuses.FoodBonusesProvider;
import com.estie.arcanecontent.common.capability.parry.ParryData;
import com.estie.arcanecontent.common.capability.parry.ParryProvider;
import com.livajq.arcanetweaks.ArcaneTweaks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArcaneTweaks.MODID)
public class CapabilitiesHandler {
    
    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Mob mob) {
            BossMinionProvider provider = new BossMinionProvider();
            provider.getBackend().setLevel(mob.level());
            event.addCapability(new ResourceLocation(ArcaneTweaks.MODID, "boss_minion"), provider);
        }
        
        if (event.getObject() instanceof Player player) {
            event.addCapability(new ResourceLocation(ArcaneTweaks.MODID, "food_bonuses"), new FoodBonusesProvider());
            event.addCapability(new ResourceLocation(ArcaneTweaks.MODID, "parry"), new ParryProvider(player));
        }
    }
    
    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(BossMinionData.class);
        event.register(FoodBonusesData.class);
        event.register(ParryData.class);
    }
}

