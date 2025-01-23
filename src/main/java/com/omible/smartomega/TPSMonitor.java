package com.omible.smartomega;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TPSMonitor {

    private static final int TPS_GOAL = 20;
    private static long lastTime = System.nanoTime();
    public static double tps = TPS_GOAL;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            long currentTime = System.nanoTime();
            long timePassed = currentTime - lastTime;
            lastTime = currentTime;

            // Calcular TPS basado en el tiempo transcurrido entre ticks
            double tpsNow = 1_000_000_000.0 / (timePassed / (double) TPS_GOAL);
            tps = Math.min(TPS_GOAL, tpsNow);
        }
    }
}