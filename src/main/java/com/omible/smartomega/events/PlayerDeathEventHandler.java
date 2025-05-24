package com.omible.smartomega.events;

import com.omible.smartomega.Config;
import com.omible.smartomega.DiscordWebhook;
import com.omible.smartomega.DiscordWebhook.EmbedObject;
import com.omible.smartomega.SkullUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

        if (!(event.getSource().getEntity() instanceof ServerPlayer attacker)) return;

        ItemStack weapon = attacker.getMainHandItem();
        if (!weapon.hasTag()) return;

        // Verifica si contiene el NBT "smartiumsword" con valor 1
        CompoundTag tag = weapon.getTag();
        if (tag != null && tag.contains("smartiumsword") && tag.getInt("smartiumsword") == 1) {
            player.spawnAtLocation(SkullUtils.createPlayerHead((ServerPlayer) player, attacker), 1.0F);
        }
    }
}