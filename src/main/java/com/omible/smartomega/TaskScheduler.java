package com.omible.smartomega;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber
public class TaskScheduler {

    private static final List<ScheduledTask> tasks = new ArrayList<>();

    // Método para añadir tareas al scheduler
    public static void scheduleTask(Runnable task, int delayTicks) {
        tasks.add(new ScheduledTask(task, delayTicks));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Iterator<ScheduledTask> iterator = tasks.iterator();

            while (iterator.hasNext()) {
                ScheduledTask task = iterator.next();
                task.decrementDelay();

                // Ejecuta la tarea si el retraso ha terminado
                if (task.isReady()) {
                    task.run();
                    iterator.remove();
                }
            }
        }
    }

    // Clase interna para representar una tarea programada
    private static class ScheduledTask {
        private final Runnable task;
        private int delay;

        public ScheduledTask(Runnable task, int delay) {
            this.task = task;
            this.delay = delay;
        }

        public void decrementDelay() {
            delay--;
        }

        public boolean isReady() {
            return delay <= 0;
        }

        public void run() {
            task.run();
        }
    }
}
