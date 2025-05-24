package com.omible.smartomega;

import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SkullUtils {
    public static ItemStack createPlayerHead(ServerPlayer victim, ServerPlayer killer) {
        ItemStack head = new ItemStack(Items.PLAYER_HEAD);

        // Setear SkullOwner (para que use la skin correcta)
        GameProfile profile = new GameProfile(victim.getUUID(), victim.getGameProfile().getName());
        CompoundTag skullOwnerTag = NbtUtils.writeGameProfile(new CompoundTag(), profile);
        head.getOrCreateTag().put("SkullOwner", skullOwnerTag);

        // Nombre del ítem en dorado
        head.setHoverName(Component.literal("Cabeza de " + victim.getName().getString())
                .withStyle(ChatFormatting.LIGHT_PURPLE));

        // Crear el lore (líneas de descripción)
        CompoundTag display = head.getOrCreateTagElement("display");
        ListTag loreList = new ListTag();

        // Linea de lore en gris
        String loreText = Component.Serializer.toJson(
                Component.literal("Asesinado por: " + killer.getName().getString())
                        .withStyle(ChatFormatting.DARK_PURPLE)
        );

        loreList.add(StringTag.valueOf(loreText));

        display.put("Lore", loreList);

        return head;
    }
}
