package fr.tt54.pycmod.create_fix;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.RailwaySavedData;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class RailwayLoader {

    public static void loadTrainsForWorld(ServerLevel level){
        System.out.println("Loading railway information for world " + level.dimension().location());
        RailwaySavedData data = loadFromWorld(level);

        for(Train train : new ArrayList<>(Create.RAILWAYS.trains.values())){
            if(data.getTrains().containsKey(train.id)){
                Create.RAILWAYS.removeTrain(train.id);
            }
        }

        for(TrackGraph trackGraph : new ArrayList<>(Create.RAILWAYS.trackNetworks.values())){
            if(data.getTrackNetworks().containsKey(trackGraph.id)){
                Create.RAILWAYS.removeGraphAndGroup(trackGraph);
            }
        }

        for(SignalEdgeGroup signalEdgeGroup : new ArrayList<>(Create.RAILWAYS.signalEdgeGroups.values())){
            if(data.getSignalBlocks().containsKey(signalEdgeGroup.id)){
                Create.RAILWAYS.signalEdgeGroups.remove(signalEdgeGroup.id);
            }
        }



        for(TrackGraph trackGraph : data.getTrackNetworks().values()){
            Create.RAILWAYS.putGraph(trackGraph);
        }
        Create.RAILWAYS.signalEdgeGroups.putAll(data.getSignalBlocks());

        for(Train train : data.getTrains().values()){
            Create.RAILWAYS.addTrain(train);
        }
        System.out.println("Finished loading railway information for world " + level.dimension().location());
    }

    private static RailwaySavedData loadFromWorld(ServerLevel level){
        return level.getDataStorage().computeIfAbsent(RailwayLoader::load, () -> {
            try {
                Constructor<RailwaySavedData> railwaySavedDataConstructor = RailwaySavedData.class.getDeclaredConstructor();
                railwaySavedDataConstructor.setAccessible(true);
                return railwaySavedDataConstructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }, "create_tracks");
    }

    private static RailwaySavedData load(CompoundTag nbt) {
        try {
            Constructor<RailwaySavedData> railwaySavedDataConstructor = RailwaySavedData.class.getDeclaredConstructor();
            railwaySavedDataConstructor.setAccessible(true);
            RailwaySavedData sd = railwaySavedDataConstructor.newInstance();

            Field trackNetworkdsField = RailwaySavedData.class.getDeclaredField("trackNetworks");
            trackNetworkdsField.setAccessible(true);
            trackNetworkdsField.set(sd, new HashMap<UUID, TrackGraph>());

            Field signalEdgeGroupsField = RailwaySavedData.class.getDeclaredField("signalEdgeGroups");
            signalEdgeGroupsField.setAccessible(true);
            signalEdgeGroupsField.set(sd, new HashMap<UUID, SignalEdgeGroup>());

            Field trainsField = RailwaySavedData.class.getDeclaredField("trains");
            trainsField.setAccessible(true);
            trainsField.set(sd, new HashMap<UUID, Train>());
//		Create.LOGGER.info("Loading Railway Information...");

            DimensionPalette dimensions = DimensionPalette.read(nbt);
            NBTHelper.iterateCompoundList(nbt.getList("RailGraphs", Tag.TAG_COMPOUND), c -> {
                TrackGraph graph = TrackGraph.read(c, dimensions);
                sd.getTrackNetworks().put(graph.id, graph);
            });
            NBTHelper.iterateCompoundList(nbt.getList("SignalBlocks", Tag.TAG_COMPOUND), c -> {
                SignalEdgeGroup group = SignalEdgeGroup.read(c);
                sd.getSignalBlocks().put(group.id, group);
            });
            NBTHelper.iterateCompoundList(nbt.getList("Trains", Tag.TAG_COMPOUND), c -> {
                Train train = Train.read(c, sd.getTrackNetworks(), dimensions);
                sd.getTrains().put(train.id, train);
            });

            for (TrackGraph graph : sd.getTrackNetworks().values()) {
                for (SignalBoundary signal : graph.getPoints(EdgePointType.SIGNAL)) {
                    UUID groupId = signal.groups.getFirst();
                    UUID otherGroupId = signal.groups.getSecond();
                    if (groupId == null || otherGroupId == null)
                        continue;
                    SignalEdgeGroup group = sd.getSignalBlocks().get(groupId);
                    SignalEdgeGroup otherGroup = sd.getSignalBlocks().get(otherGroupId);
                    if (group == null || otherGroup == null)
                        continue;
                    group.putAdjacent(otherGroupId);
                    otherGroup.putAdjacent(groupId);
                }
            }

            return sd;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchFieldException e){
            e.printStackTrace();
            return null;
        }
    }
}
