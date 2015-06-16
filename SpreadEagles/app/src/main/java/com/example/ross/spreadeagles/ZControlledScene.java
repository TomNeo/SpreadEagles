package com.example.ross.spreadeagles;

import android.util.Log;

import org.andengine.entity.scene.Scene;

/**
 * Created by Ross on 5/30/2015.
 */
public class ZControlledScene extends Scene {

    public ZControlledScene(){
        super();
    }

    public void attachChildWithZ(HeinousEntity pEntity, int Zdepth){
        Log.v("pre attached","" + pEntity.toString() + pEntity.address);
        this.attachChild(pEntity);
        Log.v("post attached", "" + pEntity.toString());
        pEntity.setZIndex(Zdepth);
        this.sortChildren();
    }

}
