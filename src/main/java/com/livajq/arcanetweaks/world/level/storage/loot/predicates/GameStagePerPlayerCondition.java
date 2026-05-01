package com.livajq.arcanetweaks.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.livajq.arcanetweaks.util.MajruszsUtils;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class GameStagePerPlayerCondition implements LootItemCondition {
    
    private final String stage;
    
    public GameStagePerPlayerCondition(String stage) {
        this.stage = stage;
    }
    
    @Override
    public boolean test(LootContext ctx) {
        if (!MajruszsUtils.isLoaded()) return false;
        
        Entity breaker = ctx.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (breaker instanceof Player player) {
            GameStage playerStage = GameStageHelper.getGameStage(player);
            return playerStage.is(stage);
        }

        Entity killer = ctx.getParamOrNull(LootContextParams.KILLER_ENTITY);
        if (killer instanceof Player player) {
            GameStage playerStage = GameStageHelper.getGameStage(player);
            return playerStage.is(stage);
        }
        
        return false;
    }
    
    @Override
    public LootItemConditionType getType() {
        return ModLootConditions.GAME_STAGE_PER_PLAYER;
    }
    
    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<GameStagePerPlayerCondition> {
        
        @Override
        public void serialize(JsonObject json, GameStagePerPlayerCondition value, JsonSerializationContext ctx) {
            json.addProperty("stage", value.stage);
        }
        
        @Override
        public GameStagePerPlayerCondition deserialize(JsonObject json, JsonDeserializationContext ctx) {
            return new GameStagePerPlayerCondition(json.get("stage").getAsString());
        }
    }
}