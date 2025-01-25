package com.omible.smartomega.events;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import com.omible.smartomega.Config;
import com.omible.smartomega.SmartOmega;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

public class PlayerLeftEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public void onPlayerLeft(PlayerEvent.PlayerLoggedOutEvent event){
        ServerPlayer player = (ServerPlayer) event.getEntity();
        GameProfile profile = player.getGameProfile();
        PlayerList list = SmartOmega.server.getPlayerList();

        if(Config.deopOnLeave && list.isOp(profile)){
            list.deop(profile);
            LOGGER.info("Player {} was deoped because he left.", player.getName());
        }
    }
}
