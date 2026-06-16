package com.livajq.arcanetweaks.common.block;

import com.livajq.arcanetweaks.ArcaneTweaks;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class BlockOfSilly extends Block {
    
    public BlockOfSilly(Properties properties) {
        super(properties);
    }
    
    /*
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (!level.isClientSide) return;
        if (random.nextFloat() > 0.005F) return;
        AABB radius = AABB.ofSize(pos.getCenter(), 20.0D, 20.0D, 20.0D);
        
        level.getEntitiesOfClass(LocalPlayer.class, radius).stream().findFirst().ifPresent(localPlayer -> RenderHandler.flashTicks = 2);
    }
     */
    
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (level.isClientSide && RenderHandler.fadingFlashTicks <= 0) RenderHandler.fadingFlashTicks = 40;
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }
    
    @Mod.EventBusSubscriber(modid = ArcaneTweaks.MODID, value = Dist.CLIENT)
    public static class RenderHandler {
        private static int flashTicks = 0;
        private static int fadingFlashTicks = 0;
        private static int fadingFlashTicksMaxAlpha = 40;
        private static final ResourceLocation SILLY_TEXTURE = new ResourceLocation(ArcaneTweaks.MODID, "textures/block/block_of_silly.png");
        
        @SubscribeEvent
        public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
            if (event.getOverlay() != VanillaGuiOverlay.HOTBAR.type()) return;
            
            final float alpha;
            if (fadingFlashTicks > 0) alpha = (float)fadingFlashTicks / (float)fadingFlashTicksMaxAlpha;
            else if (flashTicks > 0) alpha = 0.15F;
            else return;
        
            Minecraft mc = Minecraft.getInstance();
            GuiGraphics g = event.getGuiGraphics();
            
            int w = mc.getWindow().getGuiScaledWidth();
            int h = mc.getWindow().getGuiScaledHeight();
            
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            
            g.setColor(1F, 1F, 1F, alpha);
            g.blit(SILLY_TEXTURE, 0, 0, 0, 0, w, h, w, h);
            g.setColor(1F, 1F, 1F, 1F);
           
            RenderSystem.disableBlend();
        }
        
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                if (flashTicks > 0) flashTicks--;
                if (fadingFlashTicks > 0) fadingFlashTicks--;
                
                Minecraft mc = Minecraft.getInstance();
                LocalPlayer player = mc.player;
                Level level = mc.level;
                
                if (player == null || level == null) return;
                if (level.random.nextInt(200) != 0) return;
                
                int radius = 20;
                BlockPos playerPos = player.blockPosition();
                
                for (BlockPos bp : BlockPos.betweenClosed(playerPos.offset(-radius, -radius, -radius), playerPos.offset(radius, radius, radius))) {
                    if (level.getBlockState(bp).getBlock() instanceof BlockOfSilly) {
                        RenderHandler.flashTicks = 1;
                        break;
                    }
                }
            }
        }
    }
}