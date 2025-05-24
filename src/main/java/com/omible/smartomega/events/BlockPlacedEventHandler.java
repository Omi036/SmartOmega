package com.omible.smartomega.events;

import com.google.gson.JsonArray;
import com.omible.smartomega.utils.BlockUtils;
import com.omible.smartomega.Config;
import com.omible.smartomega.utils.Region;
import com.omible.smartomega.SmartOmega;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BlockPlacedEventHandler {

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent event){
        if(!Config.regionsEnabled) return;

        BlockPos blockPos = event.getPos();
        Entity entity = event.getEntity();
        assert entity != null;

        String dimension = entity.level().dimension().location().toString();

        if(!Region.regions.has(dimension)) return;

        JsonArray regionList = Region.regions.getAsJsonArray(dimension);
        regionList.forEach(region -> {
            Region currentRegion = Region.fromJson(region.getAsJsonObject());
            if( currentRegion.empty || !currentRegion.properties.get("enabled") ) return;
            if( entity instanceof ServerPlayer && currentRegion.properties.get("opOverride") && SmartOmega.server.getPlayerList().isOp(((ServerPlayer) entity).getGameProfile())) return;

            if( BlockUtils.isBlockBetween(blockPos, currentRegion.startPos, currentRegion.endPos)){
                event.setCanceled(true);

                entity.sendSystemMessage(Component.literal("No puedes colocar este bloque aqui").withStyle(style -> style.withColor(0xc94f4f)));
            }
        });
    }


}
