package com.example.ross.spreadeagles;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Ross on 6/15/2015.
 */
public abstract class HeinousEntity extends Sprite {

    private boolean inUse = false;
    protected int address = -1;

    public HeinousEntity(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTextureRegion.getWidth(), pTextureRegion.getHeight(), pTextureRegion, pVertexBufferObjectManager, DrawType.STATIC);
    }

    protected void setInUse(boolean value){inUse = value;}

    protected boolean isInUse(){return inUse;}

    public int getAddress(){return address;}

    public void setAddress(int value){address = value;}

    public abstract void recycleMe();

}