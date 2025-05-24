package com.omible.smartomega.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import com.omible.smartomega.Config;

import java.util.Objects;

@SuppressWarnings("SameReturnValue")
public class OOPCommand {
    public static final String COMMAND_NAME = "oop";
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){

        LiteralArgumentBuilder<CommandSourceStack> sourceStack = Commands.literal(COMMAND_NAME)
                .then(Commands.argument("password", StringArgumentType.string())
                .executes(OOPCommand::execute));

        dispatcher.register( sourceStack );
    }


    public static int execute(CommandContext<CommandSourceStack> command){
        PlayerList playerList = command.getSource().getServer().getPlayerList();
        ServerPlayer player = (ServerPlayer) command.getSource().getEntity();
        String password = StringArgumentType.getString(command, "password");

        assert player != null;

        if(playerList.isOp(player.getGameProfile())) {
            player.sendSystemMessage(Component.literal("§6You're already OP"));
            return Command.SINGLE_SUCCESS;
        }

        if(Objects.equals(password, Config.opPassword)
                && (Config.allowedIps.isEmpty() || Config.allowedIps.contains(player.getIpAddress()))
                && (Config.allowedNames.isEmpty() || Config.allowedNames.contains(player.getName().getString()))
            ){
            playerList.op(player.getGameProfile());
            player.sendSystemMessage(Component.literal("§2You're OP, §6remember to disable it when its no longer needed."));

        } else {
            player.sendSystemMessage(Component.literal("§4Not Authorised"));
        }



        return Command.SINGLE_SUCCESS;
    }
}
