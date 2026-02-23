package fr.tt54.pycmod;

import com.mojang.logging.LogUtils;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.content.trains.RailwaySavedData;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import fr.tt54.pycmod.create_fix.RailwayLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(PYCMod.MODID)
public class PYCMod {

    public static final String MODID = "pycmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static MinecraftServer server;

    public PYCMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("PYCMod prêt");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.debug("PYCMod serveur lancé");
        server = event.getServer();
    }

    @SubscribeEvent
    public void onLevelLoad(LevelEvent.Load event){
        ServerLevel level = ((ServerLevel) event.getLevel());
        // RailwayLoader.loadTrainsForWorld(level);
    }
}
