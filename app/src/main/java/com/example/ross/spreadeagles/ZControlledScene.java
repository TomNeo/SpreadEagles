package com.example.ross.spreadeagles;

import org.andengine.entity.IEntity;
import org.andengine.entity.scene.Scene;

/**
 * Created by Ross on 5/30/2015.
 */
public class ZControlledScene extends Scene {

    public ZControlledScene(){
        super();
    }

    public void attachChildWithZ(IEntity pEntity, int Zdepth){
        this.attachChild(pEntity);
        pEntity.setZIndex(Zdepth);
        this.sortChildren();
    }

}
