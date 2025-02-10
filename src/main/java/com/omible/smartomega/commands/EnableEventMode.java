package com.omible.smartomega.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.omible.smartomega.Config;
import com.omible.smartomega.SmartOmega;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

@SuppressWarnings("SameReturnValue")
public class EnableEventMode {
    public static final String COMMAND_NAME = "eventmode";
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){

        LiteralArgumentBuilder<CommandSourceStack> sourceStack = Commands.literal(COMMAND_NAME)
                .requires(source -> source.hasPermission(4) && Config.regionsEnabled)
                .then(Commands.argument("state", BoolArgumentType.bool())
                .executes(EnableEventMode::execute));

        dispatcher.register( sourceStack );
    }

    public static int execute(CommandContext<CommandSourceStack> command) {
        ServerPlayer player = (ServerPlayer) command.getSource().getEntity();
        SmartOmega.eventModeEnabled = BoolArgumentType.getBool(command, "state");
        player.sendSystemMessage(Component.literal("§2Event mode changed."));

        return Command.SINGLE_SUCCESS;
    }
}
