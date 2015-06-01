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

    private final float duration = .3f;

    private SpreadEaglesActivity parentActivity;

    public Eagle(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, SpreadEaglesActivity pParent) {
        super(pX, pY, pTextureRegion.getWidth(), pTextureRegion.getHeight(), pTextureRegion, pVertexBufferObjectManager, DrawType.STATIC);
        this.parentActivity  = pParent;
        this.setWidth(50);
        this.setHeight(50);
    }

    public void setMovements(float pX,float pY){
        //MoveXModifier ModifierBuffer = new MoveXModifier(2,this.getX(),this.getX() - parentActivity.getCAMERA_WIDTH() - this.getWidth());
        MoveModifier ModifierBuffer = new MoveModifier(duration,Eagle.this.getX(),pX,Eagle.this.getY(),pY);
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
        Log.v("InEagle", "Eagle update:" +this.toString());
        for (int i=0;i < parentActivity.getActiveBuildings().size();i++) {
            Log.v("Counting Building loops","Count:" + i);
            if (this.collidesWith(parentActivity.getActiveBuildings().get(i))) {
                for(int m = 0; m < parentActivity.getActiveBuildings().get(i).getBreakables().size();m++) {
                    if(this.collidesWith(parentActivity.getActiveBuildings().get(i).getBreakables().get(m))){
                        parentActivity.getActiveBuildings().get(i).getBreakables().get(m).hit();
                        clearEntityModifiers();
                        killMe();
                    }
                }
                Log.v("CollidedWithBuilding","hit building:"+parentActivity.getActiveBuildings().get(i).toString() );
            }
        }
    }

    private void killMe(){
        parentActivity.addToList(this);
    }

}
