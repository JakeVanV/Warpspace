package Mod;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 28.10.2020
 * TIME: 15:29
 */

import api.DebugFile;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;

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
     * the offset of warpspace to the realspace sector on the y axis. Use a number outside of the galaxy -> empty space
     */
    public static int offset = 150; //offset in sectors

    /**
     * check if an objects positon is in warpspace
     * @param object
     * @return
     */
    public static boolean IsInWarp(SegmentController object) {
        DebugFile.log("checkInWarp is an empty method :(");
        return true;
    }

    /**
     * Calculate the Warpspace position from a realworld position
     * @param RealSpacePos sector in realspace
     * @return correlating sector in warpspace
     */
    public static Vector3i GetWarpSpacePos(Vector3i RealSpacePos) {
        Vector3i warpPos;
        Vector3f realPosF = RealSpacePos.toVector3f();
        realPosF.x = Math.round(realPosF.x / scale);
        realPosF.y = Math.round(realPosF.y / scale);
        realPosF.z = Math.round(realPosF.z / scale);
        realPosF.y += offset * 2; //offset sectors to up (y axis)
        warpPos = new Vector3i(realPosF.x,realPosF.y,realPosF.z);

        return warpPos;
    }

    /**
     * Calculate the realspace position from a warpspace position
     * @param WarpSpacePos sector in warpspace
     * @return correlating sector in realspace
     */
    public static Vector3i GetRealSpacePos(Vector3i WarpSpacePos) {
        Vector3i warpPos;
        Vector3f realPosF = WarpSpacePos.toVector3f();
        realPosF.y -= offset * 2; //offset sectors to up (y axis)
        realPosF.x = Math.round(realPosF.x * scale);
        realPosF.y = Math.round(realPosF.y * scale);
        realPosF.z = Math.round(realPosF.z * scale);
        warpPos = new Vector3i(realPosF.x,realPosF.y,realPosF.z);

        return warpPos;
    }
}