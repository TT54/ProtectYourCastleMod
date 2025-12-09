package fr.tt54.pycmod;

import de.maxhenkel.voicechat.api.*;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@ForgeVoicechatPlugin
public class VoiceChatPlugin implements VoicechatPlugin {

    public static VoicechatServerApi voicechatApi;
    private static final List<CompletableFuture<VoicechatApi>> waitingEnabling = new ArrayList<>();

    @Override
    public String getPluginId() {
        return "pycmod";
    }

    @Override
    public void initialize(VoicechatApi api) {
        PYCMod.LOGGER.info("API instanceof server api : " + (api instanceof VoicechatServerApi));
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
    }

    public void onServerStarted(VoicechatServerStartedEvent event) {
        voicechatApi = event.getVoicechat();
        PYCMod.LOGGER.info("Voicechat started !");
        for(CompletableFuture<VoicechatApi> future : waitingEnabling){
            future.complete(voicechatApi);
        }
    }

    public static CompletableFuture<VoicechatApi> waitForEnabling(){
        CompletableFuture<VoicechatApi> future = new CompletableFuture<>();
        waitingEnabling.add(future);
        return future;
    }

    public static boolean isVoiceChatReady(){
        return voicechatApi != null;
    }

    public static UUID createOpenGroup(String name, boolean persistent){
        if(voicechatApi == null) return null;
        return voicechatApi.groupBuilder()
                .setName(name)
                .setPersistent(persistent)
                .setType(Group.Type.OPEN)
                .build().getId();
    }

    public static boolean joinGroup(UUID groupUUID, UUID playerUUID){
        if(voicechatApi == null) return false;
        Group group = voicechatApi.getGroup(groupUUID);
        VoicechatConnection connection = voicechatApi.getConnectionOf(playerUUID);
        if(group != null && connection != null){
            connection.setGroup(group);
            return true;
        }
        return false;
    }
}
