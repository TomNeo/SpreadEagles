package com.example.ross.spreadeagles;

import android.util.Log;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.IModifier;

/**
 * Created by Ross on 5/30/2015.
 */
public abstract class Breakable extends HeinousEntity {

    public abstract void recycleMe();
    public abstract void useBreakable(float x, float y, float width, float height);
    public abstract void hit();
    public abstract void setMovements(MoveXModifier pIEntityModifier);

    public SpreadEaglesActivity parentActivity;
    public boolean hit;
    public boolean killed;

    public Breakable(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, SpreadEaglesActivity pParent) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        this.parentActivity = pParent;
        hit = false;
        killed = false;
    }

    public void killMe(){
        if(!killed) {
            killed = true;
            parentActivity.addToRecycleList(this);
        }
    }

    public boolean isHit(){
        return hit;
    }

}


