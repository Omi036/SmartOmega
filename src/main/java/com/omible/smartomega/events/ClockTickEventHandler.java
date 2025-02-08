package com.omible.smartomega.events;

import com.omible.smartomega.SmartOmega;
import static com.omible.smartomega.TabList.updateTabList;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClockTickEventHandler {

    @SubscribeEvent
    public void onClockTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            SmartOmega.clock++;

            if (SmartOmega.clock % 20*5 == 0) {
                updateTabList(event.getServer());
            }
        }
    }

}
