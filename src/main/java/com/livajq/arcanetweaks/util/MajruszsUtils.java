package com.livajq.arcanetweaks.util;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
public class MajruszsUtils {
    
    public static boolean isLoaded() {
        return ModList.get().isLoaded("majruszsdifficulty");
    }
    
    public static GameStage getGameStage(Player player) {
        return player != null && GameStageHelper.isPerPlayerDifficultyEnabled() ? GameStageHelper.getGameStage(player) : GameStageHelper.getGlobalGameStage();
    }
    
    public static boolean isMasterMode(Player player) {
        return getGameStage(player).getId().equals(GameStage.MASTER_ID);
    }
}