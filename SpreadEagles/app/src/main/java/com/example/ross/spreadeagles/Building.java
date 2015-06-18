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
    private boolean score;
    private ArrayList<Breakable> breakableList;
    private boolean killed;
    Breakable bufferBreakable;

    public Building(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, SpreadEaglesActivity activity) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        parentActivity = activity;
        killed = false;
        breakableList = new ArrayList<Breakable>(0);
        //setProperties();
        //setMovements();
    }

    public Breakable addBreakable(float pX,float pY, float width, float height) {
        Log.v("Breakable", pX + ":" + pY);
        bufferBreakable = parentActivity.getUnusedBreakable(); //new Breakable(pX,pY,img,parentActivity.getVertexBufferObjectManager(),parentActivity); //parentActivity.getUnusedBreakable();
        bufferBreakable.useBreakable(pX,pY,width,height);
        breakableList.add(bufferBreakable);
        return bufferBreakable;
    }

    public void useBuilding(float x, float y){
        this.setInUse(true);
        this.setPosition(x, y);
        setVisible(true);
        parentActivity.addBuilding(this);
        Log.v("Bu-Used", "Building address" + address);
        setProperties();
        setMovements();
        parentActivity.attachEntityWithZ(this,parentActivity.BUILDING_Z_DEPTH);
    }
/*
    public void useBuilding(float x, float y, float width, float height){
        this.setInUse(true);
        setVisible(true);
        parentActivity.addBuilding(this);
        this.setPosition(x,y);
        this.setWidth(width);
        this.setHeight(height);
    }

    public void useBuilding(int type){

    }
*/
    private void setProperties(){

        Random rand = new Random();

        this.setWidth(rand.nextInt(Math.round(parentActivity.getMAXIMUM_BUILDING_WIDTH() - parentActivity.getMINIMUM_BUILDING_WIDTH())) + parentActivity.getMINIMUM_BUILDING_WIDTH());
        this.setHeight((parentActivity.getCAMERA_HEIGHT() - this.getY()) - parentActivity.getFOOTER_SPACE());
        this.setColor(0, 1, 1, 1);

        float bufferWidth = this.getWidth()/4;
        float bufferHeight = this.getHeight()/4;


        Breakable door = this.addBreakable(this.getX() + rand.nextInt((int)(this.getWidth() - bufferWidth)),this.getY() + this.getHeight()/2 + rand.nextInt((int)(this.getHeight()/2 - bufferHeight)) ,bufferWidth,bufferHeight);
        Breakable window = this.addBreakable(this.getX() + rand.nextInt((int)(this.getWidth() - bufferWidth)),this.getY() + rand.nextInt((int)(this.getHeight()/2 - bufferHeight)),bufferWidth,bufferHeight);

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
            getBreakables().get(i).setMovements(ModifierBuffer);
        }

    }

    public ArrayList<Breakable> getBreakables(){
        breakableList.trimToSize();
        return breakableList;
    }

    public void killBreakables(){
        for(int i = 0; i < breakableList.size(); i++){
            breakableList.get(i).killMe();
        }
    }

    public void killMe(){
        if(!killed){
            killed = true;
            parentActivity.addToRecycleList(this);
            Log.v("Remove", "Building - " + parentActivity.removeMe(this));
        }
    }

    @Override
    public void recycleMe() {
        Log.v("Build-Recycle","Called");
        this.setPosition(parentActivity.getCAMERA_WIDTH() / 2, parentActivity.getCAMERA_HEIGHT());
        this.setVisible(false);
        breakableList.clear();
        breakableList.trimToSize();
        killed = false;
        Log.v("Build-Recycle", "detached:" + this.detachSelf() + " and parent:" + this.getParent() + " and breakableList: " + breakableList.size());
        this.setInUse(false);
    }
}
