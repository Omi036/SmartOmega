package com.omible.smartomega.events;

import com.google.gson.JsonArray;
import com.omible.smartomega.BlockUtils;
import com.omible.smartomega.Config;
import com.omible.smartomega.Region;
import com.omible.smartomega.SmartOmega;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BlockBreakEventHandler {

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if(!Config.regionsEnabled) return;

        BlockPos blockPos = event.getPos();
        ServerPlayer player = (ServerPlayer) event.getPlayer();
        String dimension = player.level().dimension().location().toString();


        if(Region.regions.has(dimension)){
            JsonArray regionList = Region.regions.getAsJsonArray(dimension);
            regionList.forEach(region -> {
                Region currentRegion = Region.fromJson(region.getAsJsonObject());
                if( currentRegion.empty || !currentRegion.properties.get("enabled") ) return;
                if( currentRegion.properties.get("opOverride") && SmartOmega.server.getPlayerList().isOp(player.getGameProfile())) return;

                if(BlockUtils.isBlockBetween(blockPos, BlockUtils.BlockPosFromVec3(currentRegion.startPos), BlockUtils.BlockPosFromVec3(currentRegion.endPos))){
                    event.setCanceled(true);

                    player.sendSystemMessage(Component.literal("No puedes romper este bloque").withStyle(style -> style.withColor(0xc94f4f)));
                }
            });
        }
    }


}
