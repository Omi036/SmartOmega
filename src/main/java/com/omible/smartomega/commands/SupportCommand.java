package com.omible.smartomega.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.omible.smartomega.Config;
import com.omible.smartomega.SmartOmega;
import com.omible.smartomega.utils.NTFYNotification;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import com.omible.smartomega.utils.NTFYNotification;

@SuppressWarnings("SameReturnValue")
public class SupportCommand {
    public static final String COMMAND_NAME = "soporte";
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){

        LiteralArgumentBuilder<CommandSourceStack> sourceStack = Commands.literal(COMMAND_NAME)
                .then(Commands.argument("mensaje", StringArgumentType.greedyString())
                        .executes(SupportCommand::execute));

        dispatcher.register( sourceStack );
    }

    public static int execute(CommandContext<CommandSourceStack> command) {
        ServerPlayer player = (ServerPlayer) command.getSource().getEntity();
        String prompt = StringArgumentType.getString(command, "mensaje");

        NTFYNotification.Notification notification = new NTFYNotification.Notification();
        notification.setTopic("###");
        notification.setTitle("🟠 Soporte Requerido");
        notification.setDescription(String.format("%s: %s", player.getDisplayName().getString(), prompt));
        notification.send();

        player.sendSystemMessage(Component.literal("§2Mensaje enviado a Omi, te responderá por Discord"));

        return Command.SINGLE_SUCCESS;
    }
}
