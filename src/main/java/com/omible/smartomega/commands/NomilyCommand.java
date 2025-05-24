package com.omible.smartomega.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.omible.smartomega.utils.GenAI;
import com.omible.smartomega.SmartOmega;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("SameReturnValue")
public class NomilyCommand {
    public static final String COMMAND_NAME = "nomily";
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){

        LiteralArgumentBuilder<CommandSourceStack> sourceStack = Commands.literal(COMMAND_NAME)
                .then(Commands.argument("texto", StringArgumentType.greedyString())
                .executes(NomilyCommand::execute));

        dispatcher.register( sourceStack );
    }

    public static int execute(CommandContext<CommandSourceStack> command) {
        ServerPlayer player = (ServerPlayer) command.getSource().getEntity();
        String prompt = StringArgumentType.getString(command, "texto");

        // Execute async to AI doesnt block us
        CompletableFuture.runAsync(() -> {
            if(SmartOmega.server.getPlayerList().isOp(player.getGameProfile())){

                Component message = Component.Serializer.fromJson(String.format("[{\"text\":\"\\uE921\"},{\"text\":\" %s \", \"color\":\"#9253a9\", \"bold\":true}, {\"text\":\"a @Nomily >> \", \"color\":\"#555555\"}, {\"text\":\"%s\"}]", player.getDisplayName().getString(), prompt));
                SmartOmega.server.getPlayerList().getPlayers().forEach(_player -> {
                    _player.sendSystemMessage(message);
                });

                String response = GenAI.promptText(prompt, player.getDisplayName().getString());
                Component res = Component.Serializer.fromJson(String.format("[{\"text\":\"\\uE925\"},{\"text\":\" Nomily \", \"color\":\"#6261d7\", \"bold\":true}, {\"text\":\"a @%s >> \", \"color\":\"#555555\"}, {\"text\":\"%s\"}]", player.getDisplayName().getString(), response));

                SmartOmega.server.getPlayerList().getPlayers().forEach(_player -> {
                    _player.sendSystemMessage(res);
                });


            } else {

                Component message = Component.Serializer.fromJson(String.format("[{\"text\":\"\\uE922\"},{\"text\":\" %s\", \"color\":\"#dddddd\", \"bold\":true}, {\"text\":\" a @Nomily: \", \"color\":\"#555555\"}, {\"text\":\"%s\"}]", player.getDisplayName().getString(), prompt));
                SmartOmega.server.getPlayerList().getPlayers().forEach(_player -> {
                    _player.sendSystemMessage(message);
                });

                String response = GenAI.promptText(prompt, player.getDisplayName().getString());
                Component res = Component.Serializer.fromJson(String.format("[{\"text\":\"\\uE925\"},{\"text\":\" Nomily \", \"color\":\"#6261d7\", \"bold\":true}, {\"text\":\"a @%s >> \", \"color\":\"#555555\"}, {\"text\":\"%s\"}]", player.getDisplayName().getString(), response));
                SmartOmega.server.getPlayerList().getPlayers().forEach(_player -> {
                    _player.sendSystemMessage(res);
                });

            }
        });

        return Command.SINGLE_SUCCESS;
    }
}
