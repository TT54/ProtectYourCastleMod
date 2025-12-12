package fr.tt54.pycmod;

import net.minecraft.server.level.ServerPlayer;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.UUID;

public class PYCCuriosBridge {

    public static void clearCuriosItems(UUID playerUUID){
        ServerPlayer serverPlayer = PYCMod.server.getPlayerList().getPlayer(playerUUID);
        if(serverPlayer != null){
            ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory(serverPlayer).resolve().get();
            for(int i = 0; i < curiosInventory.getEquippedCurios().getSlots(); i++){
                curiosInventory.getEquippedCurios().extractItem(i, 64, false);
            }
        }
    }

}
