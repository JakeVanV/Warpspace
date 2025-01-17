package me.iron.WarpSpace.Mod;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 28.10.2020
 * TIME: 15:29
 */

import api.DebugFile;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.view.gamemap.GameMapDrawer;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.server.data.Galaxy;
import org.schema.game.server.data.GameServerState;

import javax.vecmath.Vector3f;

/**
 * defines mechanics in warp, hold settings of the warp like its position.
 */
public class WarpManager {
    /**
     *  the scale of realspace to warpspace in sectors.
     */
    public static int scale = 10; //scale warpspace distance to realspace distance

    /**
     * Galaxy size * System size * 64 + Galaxy size * System size * 64 / scale + System size * 2
     * 64 galaxies realspace + 64 galaxies warpspace + 2 systems buffer.
     */
    public static int universeSize = Galaxy.size * 16 * 64;

    /**
     * the offset of warpspace to the realspace sector on the y axis. Use a number outside of the galaxy: empty space
     */
    public static int offset = (int)(universeSize * (1 + 1f / scale)) + 16 * 2; //offset in sectors

    /**
     *  minimum speed required to stay in warp
     */
    public static int minimumSpeed = 50;

    /**
     * check if an objects positon is in warpspace
     * @param object segmentcontroller to check
     * @return boolean, true if segmentcontrollers position is in warp
     */
    public static boolean IsInWarp(SegmentController object) {
        if (object == null) {
            DebugFile.log("isInWarp called with null object");
            return false;
        }
        if (object.getSector(new Vector3i()) == null) {
            DebugFile.log("isInWarp object has no sector:"+object.getName());
            return false;
        }
        return IsInWarp(object.getSector(new Vector3i()));
    }

    /**
     * check if an objects positon is in warpspace
     * @param pos  position to check
     * @return boolean, true if position is in warp
     */
    public static boolean IsInWarp(Vector3i pos) {
        if (pos != null && pos.y >= offset - (universeSize / scale) && pos.y <= offset + (universeSize / scale)) {
            return true;
        }
        return false;
    }

    /**
     * Calculate the Warpspace position from a realworld position, will round to closest point on scale: -5->0<-+4 at scale = 10
     * @param RealSpacePos sector in realspace
     * @return correlating sector in warpspace
     */
    public static Vector3i getWarpSpacePos(Vector3i RealSpacePos) {
        Vector3i warpPos;
        Vector3f realPosF = RealSpacePos.toVector3f();
        realPosF.x = Math.round(realPosF.x / scale);
        realPosF.y = Math.round(realPosF.y / scale);
        realPosF.z = Math.round(realPosF.z / scale);
        realPosF.y += offset; //offset sectors to up (y axis)
        warpPos = new Vector3i(realPosF.x,realPosF.y,realPosF.z);

        return warpPos;
    }

    /**
     * Calculate the realspace position from a warpspace position
     * @param WarpSpacePos sector in warpspace
     * @return correlating sector in realspace
     */
    public static Vector3i getRealSpacePos(Vector3i WarpSpacePos) {
        Vector3i realPos;
        Vector3f warpPosF = WarpSpacePos.toVector3f();
        warpPosF.y -= offset; //offset sectors to up (y axis)
        warpPosF.x = Math.round(warpPosF.x * scale);
        warpPosF.y = Math.round(warpPosF.y * scale);
        warpPosF.z = Math.round(warpPosF.z * scale);
        realPos = new Vector3i(warpPosF.x,warpPosF.y,warpPosF.z);

        return realPos;
    }
}
