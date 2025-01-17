package me.iron.WarpSpace.Mod;

import api.listener.fastevents.FastListenerCommon;
import api.listener.fastevents.ThrusterElementManagerListener;
import api.utils.game.SegmentControllerUtils;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.power.reactor.tree.ReactorElement;
import org.schema.game.common.controller.elements.thrust.ThrusterElementManager;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;

import javax.vecmath.Vector3f;

/**
 * Created by Jake on 12/7/2021.
 * <insert description here>
 */
public class WarpThrusterListener implements ThrusterElementManagerListener {
    public WarpThrusterListener(WarpMain warpMain) {
        FastListenerCommon.thrusterElementManagerListeners.add(this);
    }

    @Override
    public void instantiate(ThrusterElementManager thrusterElementManager) {

    }

    @Override
    public float getSingleThrust(ThrusterElementManager thrusterElementManager, float v) {
        return v;
    }

    @Override
    public float getSharedThrust(ThrusterElementManager thrusterElementManager, float v) {
        return v;
    }

    @Override
    public float getThrustMassRatio(ThrusterElementManager thrusterElementManager, float v) {
        return v;
    }
    @Override
    public float getMaxSpeed(ThrusterElementManager thrusterElementManager, float v) {
        SegmentController sc = thrusterElementManager.getSegmentController();
        if(WarpManager.IsInWarp(sc)){
            if(sc instanceof ManagedUsableSegmentController<?>){
                ManagedUsableSegmentController<?> musc = (ManagedUsableSegmentController<?>) sc;

                ElementInformation JUMP_DIST_1 = ElementKeyMap.getInfo(119);
                ElementInformation JUMP_DIST_2 = ElementKeyMap.getInfo(118);
                ElementInformation JUMP_DIST_3 = ElementKeyMap.getInfo(117);

                ReactorElement jd1Chamber = SegmentControllerUtils.getChamberFromElement(musc, JUMP_DIST_1);
                ReactorElement jd2Chamber = SegmentControllerUtils.getChamberFromElement(musc, JUMP_DIST_2);
                ReactorElement jd3Chamber = SegmentControllerUtils.getChamberFromElement(musc, JUMP_DIST_3);
                if(jd3Chamber != null && jd3Chamber.isAllValid()){
                    return v * 2;
                }
                if(jd2Chamber != null && jd2Chamber.isAllValid()){
                    return v * 1.6F;
                }
                if(jd1Chamber != null && jd1Chamber.isAllValid()){
                    return v * 1.3F;
                }
            }
        }
        return v;
    }

    @Override
    public float getMaxSpeedAbsolute(ThrusterElementManager thrusterElementManager, float v) {
        return v;
    }

    @Override
    public Vector3f getOrientationPower(ThrusterElementManager thrusterElementManager, Vector3f vector3f) {
        return vector3f;
    }

    @Override
    public void handle(ThrusterElementManager thrusterElementManager) {

    }

    @Override
    public double getPowerConsumptionResting(ThrusterElementManager thrusterElementManager, double v) {
        return v;
    }

    @Override
    public double getPowerConsumptionCharging(ThrusterElementManager thrusterElementManager, double v) {
        return v;
    }
}