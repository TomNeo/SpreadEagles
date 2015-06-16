package com.example.ross.spreadeagles;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Ross on 5/30/2015.
 */
public class Breakables extends HeinousEntity{

    private SpreadEaglesActivity parentActivity;
    private boolean hit;
    private boolean killed;

    public Breakables(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, SpreadEaglesActivity pParent) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        this.parentActivity  = pParent;
        this.setColor(1,0,0,1);
        hit = false;
        killed = false;
    }

    public void killMe(){
        if(!killed) {
            killed = true;
            parentActivity.addToList(this);
        }
    }

    @Override
    public void onDetached(){
            killMe();
    }

    public boolean isHit(){
        return hit;
    }

    public void hit(){
        hit = true;
        parentActivity.addHit();
        this.setColor(0,1,0,1);
    }

    @Override
    public void recycleMe() {
        detachSelf();
        dispose();
    }

}


