package com.example.ross.spreadeagles;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import java.util.Random;

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

        Random rand = new Random();

        this.setWidth(50);
        this.setHeight(50);
    }


/*    @Override
    protected void onManagedUpdate(float pSecondsElapsed){
        super.onManagedUpdate(pSecondsElapsed);
        if(this.collidesWith(parentActivity.getPlayer())){
            parentActivity.getPlayer().countScore(score);
            //clearEntityModifiers();
            //killMe();
        }
    }
*/
    private void setMovements(){
    }

    private void killMe(){
        parentActivity.addToList(this);
    }

    @Override
    public void recycleMe() {

    }
}
