package com.omible.smartomega.events;

import com.google.gson.JsonArray;
import com.omible.smartomega.utils.BlockUtils;
import com.omible.smartomega.Config;
import com.omible.smartomega.utils.Region;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class DetonateEventHandler {

    @SubscribeEvent
    public void onDetonate(ExplosionEvent.Detonate event){
        if(!Config.regionsEnabled) return;

        String dimension = event.getLevel().dimension().location().toString();
        List<BlockPos> affectedBlocks = event.getAffectedBlocks();

        JsonArray regionList = Region.regions.getAsJsonArray(dimension);
        regionList.forEach(region -> {
            Region currentRegion = Region.fromJson(region.getAsJsonObject());
            if( currentRegion.empty || !currentRegion.properties.get("enabled") ) return;

            affectedBlocks.removeIf(blockPos -> BlockUtils.isBlockBetween(blockPos, currentRegion.startPos, currentRegion.endPos));
        });
    }


}
