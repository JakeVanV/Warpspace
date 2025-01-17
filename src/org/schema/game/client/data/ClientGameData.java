//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.schema.game.client.data;

import api.DebugFile;
import me.iron.WarpSpace.Mod.WarpManager;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.element.Element;
import org.schema.game.common.data.world.RemoteSector;
import org.schema.schine.common.language.Lng;

/**
 * decompiled version edited by ironsight
 */
public class ClientGameData {
    private final GameClientState state;
    private Vector3i waypoint;
    private Vector3i nearestToWayPoint;
    private Vector3i tmp = new Vector3i();
    private Vector3i ltmp = new Vector3i();
    private Vector3i playerPosTmp = new Vector3i();
    private Vector3i warpWP = new Vector3i();

    public ClientGameData(GameClientState var1) {
        this.state = var1;
    }

    public Vector3i getNearestToWayPoint() {
        return this.nearestToWayPoint;
    }

    public Vector3i getWaypoint() {
        playerPosTmp = GameClientState.instance.getPlayer().getCurrentSector();

        //if player is sitting in his waypoint, in warp -> dont return true wp -> dont delete waypoint.
        if (waypoint != null && playerPosTmp != null && playerPosTmp.equals(warpWP)) {
            GameClientState.instance.message(Lng.astr("dropout point reached."),3);
            return null;
        }

        //return warp pos if player is in warp
        if (waypoint != null && WarpManager.IsInWarp(playerPosTmp)) {
            return warpWP;
        }

        return this.waypoint;
    }

    public void setWaypoint(Vector3i newWaypoint) {

        //never allow setting direction in warp. always use RSP pos.
        //false
        if (newWaypoint != null && WarpManager.IsInWarp(newWaypoint)) {
            newWaypoint = WarpManager.getRealSpacePos(newWaypoint);
        }

        this.waypoint = newWaypoint;    //set to null
        if (waypoint != null) { //false
            this.warpWP = WarpManager.getWarpSpacePos(waypoint);
        }
        this.nearestToWayPoint = null;
        this.updateNearest(this.state.getCurrentSectorId());
    }

    public void updateNearest(int currentSectorID) {
        Vector3i wpOut = this.getWaypoint();
        if (wpOut  != null) {
            RemoteSector remoteSector;
            if ((remoteSector = (RemoteSector)this.state.getLocalAndRemoteObjectContainer().getLocalObjects().get(currentSectorID)) == null) {
                this.state.getController().flagWaypointUpdate = true;
                return;
            }

            Vector3i clientPos = remoteSector.clientPos();
            if (this.nearestToWayPoint == null) {
                this.nearestToWayPoint = new Vector3i();
            }

            //set waypoint to null bc player reached it
            if (clientPos.equals(this.getWaypoint())) {
                this.setWaypoint((Vector3i)null);
                return;
            }

            this.ltmp.set(0, 0, 0);

            for(int var2 = 0; var2 < Element.DIRECTIONSi.length; ++var2) {
                this.tmp.add(clientPos, Element.DIRECTIONSi[var2]);
                if (this.tmp.equals(this.getWaypoint())) {
                    this.nearestToWayPoint.add(clientPos, Element.DIRECTIONSi[var2]);
                    break;
                }

                this.tmp.sub(this.getWaypoint());
                if (this.ltmp.length() != 0.0F && this.tmp.length() >= this.ltmp.length()) {
                    System.err.println("NOT TAKING: " + this.tmp.length() + " / " + this.ltmp.length());
                } else {
                    this.nearestToWayPoint.add(clientPos, Element.DIRECTIONSi[var2]);
                    this.ltmp.set(this.tmp);
                }
            }

            System.err.println("NEAREST WAYPOINT " + this.nearestToWayPoint);
        }

    }
}
