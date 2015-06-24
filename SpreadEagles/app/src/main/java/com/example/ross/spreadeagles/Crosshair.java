package com.example.ross.spreadeagles;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Ross on 5/30/2015.
 */

public class Crosshair extends HeinousEntity {

    private SpreadEaglesActivity parentActivity;
    private boolean score;

    public Crosshair(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, SpreadEaglesActivity activity) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        parentActivity = activity;
        setProperties();
        setMovements();
    }

    private void setProperties(){

        this.setWidth(SpreadEaglesActivity.CROSSHAIR_WIDTH);
        this.setHeight(SpreadEaglesActivity.CROSSHAIR_HEIGHT);
    }

    private void setMovements(){
    }

    private void killMe(){
        parentActivity.addToRecycleList(this);
    }

    @Override
    public void recycleMe() {

    }
}
