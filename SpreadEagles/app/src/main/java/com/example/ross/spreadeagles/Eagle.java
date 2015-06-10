package com.example.ross.spreadeagles;

import android.util.Log;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.*;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.IModifier;

/**
 * Created by Ross on 5/30/2015.
 */
public class Eagle extends Sprite {

    private SpreadEaglesActivity parentActivity;
    private final float DURATION;
    private final float MAX_DISTANCE;
    private float life = 0;
    private float distance;

    private boolean killed;

    public Eagle(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, SpreadEaglesActivity pParent,float destinationX, float destinationY) {
        super(pX, pY, pTextureRegion.getWidth(), pTextureRegion.getHeight(), pTextureRegion, pVertexBufferObjectManager, DrawType.STATIC);
        this.parentActivity  = pParent;
        DURATION = parentActivity.getEAGLE_SPEED();
        MAX_DISTANCE = (float)Math.sqrt(Math.pow(parentActivity.getCAMERA_HEIGHT(),2) + Math.pow(parentActivity.getCAMERA_WIDTH()/2,2));
        parentActivity.addEagle();
        distance = (float)Math.sqrt(Math.pow(Math.abs(parentActivity.getCAMERA_WIDTH()/2 - destinationX),2) + Math.pow((parentActivity.getCAMERA_WIDTH()- destinationY),2));
        setMovements(destinationX,destinationY);
        this.setWidth(50);
        this.setHeight(50);
        killed = false;
    }

    public void setMovements(float pX,float pY){
        MoveModifier ModifierBuffer = new MoveModifier(distance/MAX_DISTANCE*DURATION,Eagle.this.getX(),pX,Eagle.this.getY(),pY);
        ModifierBuffer.addModifierListener(new IModifier.IModifierListener<IEntity>() {
            @Override
            public void onModifierStarted(IModifier<IEntity> iEntityIModifier, IEntity iEntity) {

            }

            @Override
            public void onModifierFinished(IModifier<IEntity> iEntityIModifier, IEntity iEntity) {
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
            for (int i = 0; i < parentActivity.getActiveBuildings().size(); i++) {
                Log.v("Counting Building loops", "Count:" + i);
                if (this.collidesWith(parentActivity.getActiveBuildings().get(i))) {
                    for (int m = 0; m < parentActivity.getActiveBuildings().get(i).getBreakables().size(); m++) {
                        if (this.collidesWith(parentActivity.getActiveBuildings().get(i).getBreakables().get(m))) {
                            if (!parentActivity.getActiveBuildings().get(i).getBreakables().get(m).isHit()) {
                                if(!killed) {
                                    parentActivity.getActiveBuildings().get(i).getBreakables().get(m).hit();
                                    clearEntityModifiers();
                                    killMe();
                                }
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

    private void killMe(){
        if(!killed) {
            killed = true;
            parentActivity.addToList(this);
        }
    }

}
