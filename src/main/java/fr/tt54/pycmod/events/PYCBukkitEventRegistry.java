package fr.tt54.pycmod.events;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PYCBukkitEventRegistry {

    private static Consumer<PlayerDamagedByPlayerEvent> playerDamagedByPlayerEventConsumer;

    public static void registerPlayerDamagedByPlayerEvent(Consumer<PlayerDamagedByPlayerEvent> consumer){
        playerDamagedByPlayerEventConsumer = consumer;
    }

    public static void callPlayerDamagedByPlayerEvent(PlayerDamagedByPlayerEvent event){
        if(playerDamagedByPlayerEventConsumer != null){
            playerDamagedByPlayerEventConsumer.accept(event);
        }
    }
}
