package com.example.ross.spreadeagles;

import android.util.Log;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.IModifier;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Ross on 5/30/2015.
 */

public class Building extends Sprite {

    private SpreadEaglesActivity parentActivity;
    private boolean score;
    private ArrayList<Breakables> breakableList;
    private ITextureRegion blockTexture;

    public Building(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, SpreadEaglesActivity activity) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        parentActivity = activity;
        blockTexture = pTextureRegion;
       breakableList = new ArrayList<Breakables>(0);
        setProperties();
        setMovements();
    }

    public Breakables addBreakable(float pX,float pY, float width, float height, ITextureRegion img){
        Log.v("Breakable", pX + ":" +pY);
        Breakables bufferBreakable = new Breakables(pX,pY,img,parentActivity.getVertexBufferObjectManager(),parentActivity);
        bufferBreakable.setHeight(height);
        bufferBreakable.setWidth(width);
        breakableList.add(bufferBreakable);
        return bufferBreakable;
    }

    private void setProperties(){

        Random rand = new Random();

        this.setWidth((1 + (1 / (1 + rand.nextInt(100)))) * 200);
        this.setHeight((parentActivity.getCAMERA_HEIGHT() - this.getY()) - 48);
        this.setColor(0,1,1,1);

        float bufferWidth = this.getWidth()/4;
        float bufferHeight = this.getHeight()/4;

        Breakables door = this.addBreakable(this.getX() + this.getWidth()/2,(this.getY()+(this.getHeight()/2)),bufferWidth,bufferHeight,blockTexture);
    }

    private void setMovements(){
        MoveXModifier ModifierBuffer = new MoveXModifier(2,this.getX(),this.getX() - parentActivity.getCAMERA_WIDTH() - this.getWidth());
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
        for(int i = 0; i < getBreakables().size(); i++){
            ModifierBuffer = new MoveXModifier(2,getBreakables().get(i).getX(),getBreakables().get(i).getX() - parentActivity.getCAMERA_WIDTH() - this.getWidth());
            ModifierBuffer.addModifierListener(new IModifier.IModifierListener<IEntity>() {
                @Override
                public void onModifierStarted(IModifier<IEntity> iEntityIModifier, IEntity iEntity) {
                }
                @Override
                public void onModifierFinished(IModifier<IEntity> iEntityIModifier, IEntity iEntity) {
                    killMe();
                }
            });
            getBreakables().get(i).registerEntityModifier(ModifierBuffer);
        }

    }

    public ArrayList<Breakables> getBreakables(){
        breakableList.trimToSize();
        return breakableList;
    }

    private void killMe(){
        parentActivity.addToList(this);
    }
}
