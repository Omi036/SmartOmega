package com.omible.smartomega.events;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import com.omible.smartomega.Config;
import com.omible.smartomega.utils.DiscordWebhook;
import com.omible.smartomega.SmartOmega;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

import java.awt.*;
import java.time.Instant;

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

        // Send discord webhook
        if(Config.webhooksEnabled && Config.webhooksClientEnabled){
            DiscordWebhook webhook = new DiscordWebhook(Config.webhookUrl);
            DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                    .setTitle(String.format("🔴 PlayerLeft %s", player.getDisplayName().getString()))
                    .setDescription("Player is now offline on the server\\n"
                            + String.format("- Name: `%s`\\n",player.getDisplayName().getString())
                            + String.format("- Position: `%03d %03d %03d`\\n", (int) player.position().x, (int) player.position().y, (int) player.position().z)
                            + String.format("- Dimension: `%s`\\n", player.level().dimension().location()))

                    .setColor(new Color(0xd9745e))
                    .setFooter( "ID: " + Instant.now().getEpochSecond(), "");

            webhook.addEmbed(embed);
            webhook.execute();
        }
    }
}
