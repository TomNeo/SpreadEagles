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

    public Eagle(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, SpreadEaglesActivity pParent,float destinationX, float destinationY) {
        super(pX, pY,pTextureRegion, pVertexBufferObjectManager);
        this.parentActivity  = pParent;
        DURATION = parentActivity.getEAGLE_SPEED();
        MAX_DISTANCE = (float)Math.sqrt(Math.pow(parentActivity.getCAMERA_HEIGHT(),2) + Math.pow(parentActivity.getCAMERA_WIDTH()/2,2));
        parentActivity.addEagleToList(this);
        distance = (float)Math.sqrt(Math.pow(Math.abs(parentActivity.getCAMERA_WIDTH()/2 - destinationX),2) + Math.pow((parentActivity.getCAMERA_WIDTH()- destinationY),2));
        setMovements(destinationX,destinationY);
        this.setWidth(50);
        this.setHeight(50);
        killed = false;
    }

    public Eagle(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, SpreadEaglesActivity pParent) {
        super(pX, pY,pTextureRegion, pVertexBufferObjectManager);
        this.parentActivity  = pParent;
        DURATION = parentActivity.getEAGLE_SPEED();
        MAX_DISTANCE = (float)Math.sqrt(Math.pow(parentActivity.getCAMERA_HEIGHT(),2) + Math.pow(parentActivity.getCAMERA_WIDTH()/2,2));
        //parentActivity.addEagleToList(this);
        //distance = (float)Math.sqrt(Math.pow(Math.abs(parentActivity.getCAMERA_WIDTH()/2 - destinationX),2) + Math.pow((parentActivity.getCAMERA_WIDTH()- destinationY),2));
        //setMovements(destinationX,destinationY);
        this.setWidth(50);
        this.setHeight(50);
        killed = false;
    }

    public void useEagle(float destinationX, float destinationY){
        this.setInUse(true);
        setVisible(true);
        life = 0;
        parentActivity.addEagleToList(this);
        distance = (float)Math.sqrt(Math.pow(Math.abs(parentActivity.getCAMERA_WIDTH()/2 - destinationX),2) + Math.pow((parentActivity.getCAMERA_WIDTH()- destinationY),2));
        setMovements(destinationX, destinationY);
        Log.v("Ea-Used","Eagle address" + address);
    }

    private void setMovements(float pX,float pY){
            ModifierBuffer = new MoveModifier(distance / MAX_DISTANCE * DURATION, Eagle.this.getX(), pX, Eagle.this.getY(), pY);
            ModifierBuffer.addModifierListener(new IModifier.IModifierListener<IEntity>() {
                @Override
                public void onModifierStarted(IModifier<IEntity> iEntityIModifier, IEntity iEntity) {

                }

                @Override
                public void onModifierFinished(IModifier<IEntity> iEntityIModifier, IEntity iEntity) {
                    Log.v("eagle killed","end of path thing");
                    killMe();
                }
            });
            this.registerEntityModifier(ModifierBuffer);
        }



    @Override
    protected void onManagedUpdate(float pSecondsElapsed){
        super.onManagedUpdate(pSecondsElapsed);
        life = life + pSecondsElapsed;
        Log.v("InEagle", "Eagle update:" + this.toString());
        if (life/((distance/MAX_DISTANCE)*DURATION) >= parentActivity.getEAGLE_COLLISION() ) {
            building: for (int i = 0; i < parentActivity.getActiveBuildings().size(); i++) {
                Log.v("Counting Building loops", "Count:" + i);
                if (this.collidesWith(parentActivity.getActiveBuildings().get(i))) {
                    for (int m = 0; m < parentActivity.getActiveBuildings().get(i).getBreakables().size(); m++) {
                        if (this.collidesWith(parentActivity.getActiveBuildings().get(i).getBreakables().get(m))) {
                            if (!parentActivity.getActiveBuildings().get(i).getBreakables().get(m).isHit()) {
                                if(!killed) {
                                    parentActivity.getActiveBuildings().get(i).getBreakables().get(m).hit();
                                    Log.v("eagle killed", "hit new thing");
                                    killMe();
                                    clearEntityModifiers();
                                }
                                break building;
                            }
                        }
                    }
                    Log.v("CollidedWithBuilding", "hit building:" + parentActivity.getActiveBuildings().get(i).toString());
                }
            }
        }
    }

    @Override
    public void onDetached(){
        Log.v("Detach Eagle", "Exactly");
        parentActivity.subtractEagle();
    }

    public void killMe(){
        if(!killed) {
            killed = true;
            parentActivity.addToList(this);
            Log.v("Remove", " Eagle - " + parentActivity.removeMe(this));
        }
    }

    @Override
    public void recycleMe() {
        Log.v("Eagle-Recycle","Called");
        this.setPosition(parentActivity.getCAMERA_WIDTH() / 2, parentActivity.getCAMERA_HEIGHT());
        this.setVisible(false);
        killed = false;
        Log.v("Eagle-Recycle", "detached:" + this.detachSelf() + " and parent:" + this.getParent());
        this.setInUse(false);
    }
}