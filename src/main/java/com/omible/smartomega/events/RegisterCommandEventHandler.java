package com.omible.smartomega.events;

import com.omible.smartomega.Config;
import com.omible.smartomega.commands.*;
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
        if(Config.geminiEnabled) NomilyCommand.register(event.getDispatcher());
    }

}
