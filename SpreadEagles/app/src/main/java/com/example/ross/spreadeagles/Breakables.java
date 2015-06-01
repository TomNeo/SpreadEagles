package com.example.ross.spreadeagles;

import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Ross on 5/30/2015.
 */
public class Breakables extends Sprite implements IEntity{

    private SpreadEaglesActivity parentActivity;

    public Breakables(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, SpreadEaglesActivity pParent) {
        super(pX, pY, pTextureRegion.getWidth(), pTextureRegion.getHeight(), pTextureRegion, pVertexBufferObjectManager, DrawType.STATIC);
        this.parentActivity  = pParent;
        this.setColor(1,0,0,1);
    }

    public void killMe(){
        parentActivity.addToList(this);
    }

    public void hit(){
        this.setColor(0,1,0,1);
    }

}


