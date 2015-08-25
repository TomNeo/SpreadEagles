package com.example.ross.spreadeagles;

import android.util.Log;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.IModifier;

/**
 * Created by Ross on 5/30/2015.
 */
public class Eagle extends HeinousEntity {

    private SpreadEaglesActivity parentActivity;
    private final float DURATION;
    private final float MAX_DISTANCE;
    private float life = 0;
    private float distance;
    private MoveModifier ModifierBuffer;

    private boolean killed;

    public Eagle(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, SpreadEaglesActivity pParent) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        this.parentActivity  = pParent;
        DURATION = SpreadEaglesActivity.EAGLE_SPEED;
        MAX_DISTANCE = (float)Math.sqrt(Math.pow(SpreadEaglesActivity.CAMERA_HEIGHT, 2) + Math.pow(SpreadEaglesActivity.CAMERA_WIDTH/2, 2));
        this.setWidth(SpreadEaglesActivity.EAGLE_WIDTH);
        this.setHeight(SpreadEaglesActivity.EAGLE_HEIGHT);
        killed = false;
    }

    public void useEagle(float destinationX, float destinationY){
        this.setInUse(true);
        if (destinationX <= SpreadEaglesActivity.CAMERA_WIDTH / 2) {
            this.setFlipped(true, false);
        } else {
            this.setFlipped(false, false);
        }
        killed = false;
        setVisible(true);
        life = 0;
        distance = (float)Math.sqrt(Math.pow(Math.abs(SpreadEaglesActivity.CAMERA_WIDTH/2 - destinationX), 2) + Math.pow((SpreadEaglesActivity.CAMERA_WIDTH - destinationY), 2));
        parentActivity.attachEntityWithZ(this, SpreadEaglesActivity.EAGLE_Z_DEPTH);
        setMovements(destinationX, destinationY);
        Log.v("Ea-Used", "Eagle address" + address);
    }

    private void setMovements(float pX, float pY){
            ModifierBuffer = new MoveModifier(distance / MAX_DISTANCE * DURATION, Eagle.this.getX(), pX, Eagle.this.getY(), pY);
            ModifierBuffer.addModifierListener(new IModifier.IModifierListener<IEntity>() {
                @Override
                public void onModifierStarted(IModifier<IEntity> iEntityIModifier, IEntity iEntity) {
                }
                @Override
                public void onModifierFinished(IModifier<IEntity> iEntityIModifier, IEntity iEntity) {
                    Log.v("eagle killed", "end of path thing");
                    killMe();
                }
            });
            this.registerEntityModifier(ModifierBuffer);
        }

    @Override
    protected void onManagedUpdate(float pSecondsElapsed){
        super.onManagedUpdate(pSecondsElapsed);
        life = life + pSecondsElapsed;
        if (life/((distance/MAX_DISTANCE)*DURATION) >= SpreadEaglesActivity.EAGLE_COLLISION) {
            Log.v("InEagle", "Ripe: " + this.toString() + " " + address);
            if(parentActivity.checkEagleCollidesWithBreakables(this)){
                Log.v("InEagle", "Collided: " + this.toString() + " " + address);
                killMe();
                clearEntityModifiers();
            }
        }
    }

    public void killMe(){
        if(!killed) {
            killed = true;
            parentActivity.addToRecycleList(this);
        }
    }

    @Override
    public void recycleMe() {
        Log.v("Eagle-Recycle","Called");
        this.setPosition(SpreadEaglesActivity.CAMERA_WIDTH / 2, SpreadEaglesActivity.CAMERA_HEIGHT);
        this.setVisible(false);
        Log.v("Eagle-Recycle", "detached:" + this.detachSelf() + " and parent:" + this.getParent());
        this.setInUse(false);
    }
}