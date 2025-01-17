package me.iron.WarpSpace.Mod;

import api.config.BlockConfig;
import api.listener.events.controller.ClientInitializeEvent;
import api.listener.events.controller.ServerInitializeEvent;
import api.mod.StarMod;
import api.network.packets.PacketUtil;
import api.utils.registry.UniversalRegistry;
import glossar.GlossarCategory;
import glossar.GlossarEntry;
import glossar.GlossarInit;

import me.iron.WarpSpace.Mod.client.*;
import me.iron.WarpSpace.Mod.client.map.DropPointMapDrawer;
import me.iron.WarpSpace.Mod.Interdiction.InterdictionHUDUpdateLoop;
import me.iron.WarpSpace.Mod.beacon.BeaconManager;
import me.iron.WarpSpace.Mod.beacon.BeaconUpdatePacket;
import me.iron.WarpSpace.Mod.beacon.WarpBeaconAddon;
import me.iron.WarpSpace.Mod.client.sounds.WarpSounds;
import me.iron.WarpSpace.Mod.network.PacketHUDUpdate;
import me.iron.WarpSpace.Mod.server.WarpCheckLoop;
import me.iron.WarpSpace.Mod.server.WarpJumpListener;
import me.iron.WarpSpace.Mod.taswin.WarpSpaceMap;
import me.iron.WarpSpace.Mod.visuals.BackgroundEventListener;
import org.schema.schine.resource.ResourceLoader;


/**
 * the me.iron.WarpSpace.Mod.testing.main class where the mod is run from by starloader.
 */
public class WarpMain extends StarMod {

    public static void main(String[] args) {
        System.out.println("hello space!");
    }
    public static WarpMain instance;
    public BeaconManager beaconManagerServer;
    public BeaconManager beaconManagerClient;
    public DropPointMapDrawer dropPointMapDrawer;
    public WarpThrusterListener warpThrusterListener;

    @Override
    public void onEnable() {
        super.onEnable();
        BackgroundEventListener.AddListener(); //add background color listener
        instance = this;

        PacketUtil.registerPacket(PacketHUDUpdate.class);
        PacketUtil.registerPacket(BeaconUpdatePacket.class);

        WarpSpaceMap.enable(instance);
        WarpBeaconAddon.registerAddonAddEventListener();

        dropPointMapDrawer = new DropPointMapDrawer(this);
        warpThrusterListener = new WarpThrusterListener(this);

    }
    
    @Override
    public void onDisable() {
        WarpSpaceMap.disable();
    }
    
    @Override
    public void onServerCreated(ServerInitializeEvent event) {
        super.onServerCreated(event);
        WarpJumpListener.createListener();
    //TODO thrust    ThrustEventhandler.createListener();
        WarpCheckLoop.loop(25);
        InterdictionHUDUpdateLoop.CreateServerLoop();
        beaconManagerServer = BeaconManager.getSavedOrNew(this.getSkeleton());
        beaconManagerServer.onInit();
    //    DebugChatEvent.addDebugChatListener();
    }

    @Override
    public void onClientCreated(ClientInitializeEvent event) {
        super.onClientCreated(event);
        WarpProcessController.initMap(); //build situation map for warp processes
        SpriteList.init();
        HUD_core.initList();
        GUIeventhandler.addHUDDrawListener();
        HUD_core.HUDLoop();
        beaconManagerClient = new BeaconManager();
        beaconManagerClient.onInit();
        dropPointMapDrawer.activate();
        DebugChatEvent.addDebugChatListener();
        GlossarInit.initGlossar(this);
        GlossarInit.addCategory(getWiki());
        new WarpSounds();
    }

    @Override
    public void onResourceLoad(ResourceLoader loader) {
        super.onResourceLoad(loader);
        dropPointMapDrawer.loadSprite();
    }

    @Override
    public void onBlockConfigLoad(BlockConfig blockConfig) {
        super.onBlockConfigLoad(blockConfig);
        WarpBeaconAddon.registerChamberBlock();
    }

    @Override
    public void onUniversalRegistryLoad() {
        super.onUniversalRegistryLoad();
        UniversalRegistry.registerURV(UniversalRegistry.RegistryType.PLAYER_USABLE_ID,this.getSkeleton(), WarpBeaconAddon.UIDName);
    }

    private GlossarCategory getWiki() {
        GlossarCategory cat = new GlossarCategory("WarpSpace");

        cat.addEntry(new GlossarEntry("Introduction","WarpSpace changes the jumping mechanic. Instead of being teleported to your waypoint or in the direction of the waypoint, instead you enter a parallel dimension that is a scaled down version of realspace: the warp. Here you can travel, just as in realspace, but distances are ten times shorter. \nThis means that you can follow others/be followed when you are travelling faster than light. The core feature is, that fast-travel becomes predictable.\n This renders raid-attacks, that rely on jumping away to hide, useless and greatly improves the ability to defend your territory.\nSince the warp is shared by everyone, its not unlikely to meet other players in it.\n "));
        cat.addEntry(new GlossarEntry("Jumping","Set your desired destination as your navigation waypoint ('N'). Then activate your jumpdrive. After a couple seconds you will switch dimensions and enter the Warp. " +
                "The Warp is a parallel dimension where distances are 10 times shorter. Follow your waypoint while you are in warp, until you reach it. You will see a notification 'droppoint' reached. Activate your jumpdrive again or slow down below 50 m/s for more than 10 seconds, to drop out of warp. You will re-enter realspace at the corresponding droppoint. This is your waypoint rounded to 10. After dropping, fly the remaining distance to your waypoint in realspace."));

        cat.addEntry(new GlossarEntry("Warpbeacon","The natural droppoints can be shifted to a more desirable position, by using a spacestation that deploys a beacon chamber. The chamber does not use energy, but 50% of the reactors chamber capacity. Once the beacon addon is activated in the reactor menu, every ship will drop out at the stations sector instead of the natural droppoint. The chamber stays active across loading/unloading the sector and server restarts.\n Beacons can only be deployed on non-homebase stations with undamaged chambers. It is recommended to reboot the station with 'y' before activating the chamber."));

        cat.addEntry(new GlossarEntry("Map","While in warp, a scaled down version of the universe is visible on the map. \nIn realspace, the droppoints are marked with small, blue spirals. Droppoints, that were shifted through the use of warpbeacons, are marked by the same symbol inside a box."));

        cat.addEntry(new GlossarEntry("Inhibition","Inhibition has been reworked and enhanced. Your HUD will tell you if an inhibitor is impacting you. It will show red symbols for inhibitors denying you leaving your current dimension or entering the other one. An active inhibitor will prevent ships from entering your sector and leaving your sector by using the jumpdrive. Note that slowing down and 'speeddropping' in warp pierces the inhibitor. Ships/Stations that field an inhibitor can deny warping to any ships, that have up to 3 times bigger reactors in a 3 sector radius. Inhibitors only work while being loaded."));

        cat.addEntry(new GlossarEntry("HUD","In your bottom right corner of the screen, you will see a white HUD element. The HUD will give you information about:\n " +
                "- the dimension you are in:\n" +
                "    - hollow spiral = realspace\n" +
                "    - filled spiral spinning fast = warp\n" +
                "- if you are currently jumping or dropping: small yellow arrows blinking\n" +
                "- if you are being inhibited:\n" +
                "    - red spiral: your sector is inhibited.\n" +
                "    - red arrows: the sector in the other dimension is inhibited."));
        return cat;
    }
}
