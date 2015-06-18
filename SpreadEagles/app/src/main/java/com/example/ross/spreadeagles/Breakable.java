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
public class Breakable extends HeinousEntity{

    private SpreadEaglesActivity parentActivity;
    private boolean hit;
    private boolean killed;

    public Breakable(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, SpreadEaglesActivity pParent) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        this.parentActivity  = pParent;
        this.setColor(1,0,0,1);
        hit = false;
        killed = false;
    }

    public void useBreakable(float x, float y, float width, float height){
        this.setInUse(true);
        setPosition(x, y);
        setHeight(height);
        setWidth(width);
        this.setVisible(true);
        parentActivity.attachEntityWithZ(this,parentActivity.BREAKABLE_Z_DEPTH);
        Log.v("Br-Used", "Breakable address" + address);
    }

    public void setMovements(MoveXModifier pIEntityModifier){
        pIEntityModifier.addModifierListener(new IModifier.IModifierListener<IEntity>() {
            @Override
            public void onModifierStarted(IModifier<IEntity> iEntityIModifier, IEntity iEntity) {
            }

            @Override
            public void onModifierFinished(IModifier<IEntity> iEntityIModifier, IEntity iEntity) {
                killMe();
            }
        });
        this.registerEntityModifier(pIEntityModifier);
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

    public void hit(){
        hit = true;
        parentActivity.addHit();
        this.setColor(0,1,0,1);
    }

    @Override
    public void recycleMe() {
        Log.v("Break-Recycle", "Called");
        this.setPosition(parentActivity.getCAMERA_WIDTH() / 2, parentActivity.getCAMERA_HEIGHT());
        this.setVisible(false);
        this.setColor(1,0,0,1);
        hit = false;
        killed = false;
        Log.v("Break-Recycle", "detached:" + this.detachSelf() + " and parent:" + this.getParent());
        this.setInUse(false);
    }

}


