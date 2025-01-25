package com.omible.smartomega.events;

import com.omible.smartomega.Config;
import com.omible.smartomega.DiscordWebhook;
import com.omible.smartomega.DiscordWebhook.EmbedObject;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.world.entity.player.Player;

import java.awt.*;
import java.time.Instant;

public class PlayerDeathEventHandler {

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        // Send discord webhook
        if(Config.webhooksEnabled && Config.webhooksDeathEnabled){
            DiscordWebhook webhook = new DiscordWebhook(Config.webhookUrl);
            EmbedObject embed = new DiscordWebhook.EmbedObject()
                    .setTitle(String.format("⚫ PlayerDeath %s", player.getDisplayName().getString()))
                    .setDescription(String.format("- Name: `%s`\\n",player.getDisplayName().getString())
                            + String.format("- Position: `%03d %03d %03d`\\n", (int) player.position().x, (int) player.position().y, (int) player.position().z)
                            + String.format("- Dimension: `%s`\\n", player.level().dimension().location()))

                    .setColor(new Color(0x494949))
                    .setFooter( "ID: " + Instant.now().getEpochSecond(), "");

            webhook.addEmbed(embed);
            webhook.execute();
        }
    }
}