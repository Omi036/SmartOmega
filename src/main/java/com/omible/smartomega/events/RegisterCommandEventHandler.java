package com.omible.smartomega.events;

import com.omible.smartomega.commands.EnableEventMode;
import com.omible.smartomega.commands.OOPCommand;
import com.omible.smartomega.commands.RegionCommand;
import com.omible.smartomega.commands.RunCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RegisterCommandEventHandler {

    // Registering Commands
    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event){
        OOPCommand.register(event.getDispatcher());
        RunCommand.register(event.getDispatcher());
        RegionCommand.register(event.getDispatcher());
        EnableEventMode.register(event.getDispatcher());
    }

}
