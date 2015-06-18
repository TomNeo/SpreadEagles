package com.example.ross.spreadeagles;

import android.util.Log;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.IModifier;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Ross on 5/30/2015.
 */

public class Building extends HeinousEntity {

    private SpreadEaglesActivity parentActivity;
    private ArrayList<Breakable> breakableList;
    private boolean killed;
    Breakable bufferBreakable;

    public Building(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, SpreadEaglesActivity activity) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        parentActivity = activity;
        killed = false;
        breakableList = new ArrayList<Breakable>(0);
    }

    public Breakable addBreakable(float pX,float pY, float width, float height) {
        Log.v("Breakable", pX + ":" + pY);
        bufferBreakable = parentActivity.getUnusedBreakable();
        bufferBreakable.useBreakable(pX, pY, width, height);
        breakableList.add(bufferBreakable);
        return bufferBreakable;
    }

    public void useBuilding(float x, float y){
        this.setInUse(true);
        killed = false;
        this.setPosition(x, y);
        setVisible(true);
        Log.v("Bu-Used", "Building address" + address);
        setProperties();
        setMovements();
        parentActivity.attachEntityWithZ(this, SpreadEaglesActivity.BUILDING_Z_DEPTH);
    }

    private void setProperties(){

        Random rand = new Random();

        this.setWidth(rand.nextInt(Math.round(SpreadEaglesActivity.MAXIMUM_BUILDING_WIDTH - SpreadEaglesActivity.MINIMUM_BUILDING_WIDTH)) + SpreadEaglesActivity.MINIMUM_BUILDING_WIDTH);
        this.setHeight((SpreadEaglesActivity.CAMERA_HEIGHT - this.getY()) - SpreadEaglesActivity.FOOTER_SPACE);
        this.setColor(0, 1, 1, 1);

        float bufferWidth = this.getWidth()/4;
        float bufferHeight = this.getHeight()/4;


        Breakable door = this.addBreakable(this.getX() + rand.nextInt((int)(this.getWidth() - bufferWidth)),this.getY() + this.getHeight()/2 + rand.nextInt((int)(this.getHeight()/2 - bufferHeight)) ,bufferWidth,bufferHeight);
        Breakable window = this.addBreakable(this.getX() + rand.nextInt((int)(this.getWidth() - bufferWidth)),this.getY() + rand.nextInt((int)(this.getHeight()/2 - bufferHeight)),bufferWidth,bufferHeight);

    }

    private void setMovements(){
        MoveXModifier ModifierBuffer = new MoveXModifier(SpreadEaglesActivity.BUILDING_SPEED, this.getX(), this.getX() - SpreadEaglesActivity.CAMERA_WIDTH - this.getWidth());
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
            ModifierBuffer = new MoveXModifier(SpreadEaglesActivity.BUILDING_SPEED,getBreakables().get(i).getX(),getBreakables().get(i).getX() - SpreadEaglesActivity.CAMERA_WIDTH - this.getWidth());
            getBreakables().get(i).setMovements(ModifierBuffer);
        }

    }

    public ArrayList<Breakable> getBreakables(){
        breakableList.trimToSize();
        return breakableList;
    }

    public void killMe(){
        if(!killed){
            killed = true;
            parentActivity.addToRecycleList(this);
        }
    }

    @Override
    public void recycleMe() {
        this.setPosition(SpreadEaglesActivity.CAMERA_WIDTH / 2, SpreadEaglesActivity.CAMERA_HEIGHT);
        this.setVisible(false);
        breakableList.clear();
        breakableList.trimToSize();
        Log.v("Build-Recycle", "detached:" + this.detachSelf() + " and parent:" + this.getParent() + " and breakableList: " + breakableList.size());
        this.setInUse(false);
    }
}
