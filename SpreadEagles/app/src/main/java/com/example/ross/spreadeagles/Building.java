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
    private boolean killed;

    public Building(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, SpreadEaglesActivity activity) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        parentActivity = activity;
        blockTexture = pTextureRegion;
        killed = false;
        breakableList = new ArrayList<Breakables>(0);
        setProperties();
        setMovements();
    }

    public Breakables addBreakable(float pX,float pY, float width, float height, ITextureRegion img) {
        Log.v("Breakable", pX + ":" + pY);
        Breakables bufferBreakable = new Breakables(pX,pY,img,parentActivity.getVertexBufferObjectManager(),parentActivity);
        bufferBreakable.setHeight(height);
        bufferBreakable.setWidth(width);
        breakableList.add(bufferBreakable);
        return bufferBreakable;
    }

    private void setProperties(){

        Random rand = new Random();

        this.setWidth(rand.nextInt(Math.round(parentActivity.getMAXIMUM_BUILDING_WIDTH() - parentActivity.getMINIMUM_BUILDING_WIDTH())) + parentActivity.getMINIMUM_BUILDING_WIDTH());
        this.setHeight((parentActivity.getCAMERA_HEIGHT() - this.getY()) - parentActivity.getFOOTER_SPACE());
        this.setColor(0, 1, 1, 1);

        float bufferWidth = this.getWidth()/4;
        float bufferHeight = this.getHeight()/4;


        Breakables door = this.addBreakable(this.getX() + rand.nextInt((int)(this.getWidth() - bufferWidth)),this.getY() + this.getHeight()/2 + rand.nextInt((int)(this.getHeight()/2 - bufferHeight)) ,bufferWidth,bufferHeight,blockTexture);
        Breakables window = this.addBreakable(this.getX() + rand.nextInt((int)(this.getWidth() - bufferWidth)),this.getY() + rand.nextInt((int)(this.getHeight()/2 - bufferHeight)),bufferWidth,bufferHeight,blockTexture);

    }

    private void setMovements(){
        MoveXModifier ModifierBuffer = new MoveXModifier(parentActivity.getBUILDING_SPEED(),this.getX(),this.getX() - parentActivity.getCAMERA_WIDTH() - this.getWidth());
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
            ModifierBuffer = new MoveXModifier(parentActivity.getBUILDING_SPEED(),getBreakables().get(i).getX(),getBreakables().get(i).getX() - parentActivity.getCAMERA_WIDTH() - this.getWidth());
            ModifierBuffer.addModifierListener(new IModifier.IModifierListener<IEntity>() {
                @Override
                public void onModifierStarted(IModifier<IEntity> iEntityIModifier, IEntity iEntity) {
                }
                @Override
                public void onModifierFinished(IModifier<IEntity> iEntityIModifier, IEntity iEntity) {
                    iEntity.onDetached();
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
        if(!killed){
            killed = true;
            parentActivity.addToList(this);
            Log.v("Remove", "" + parentActivity.removeMe(this));
        }
    }
}
