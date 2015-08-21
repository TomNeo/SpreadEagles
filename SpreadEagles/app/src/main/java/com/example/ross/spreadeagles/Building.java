package com.example.ross.spreadeagles;

import android.util.Log;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.IModifier;

import java.util.Random;

/**
 * Created by Ross on 5/30/2015.
 */

public class Building extends HeinousEntity {

    private SpreadEaglesActivity parentActivity;
    private boolean killed;
    Breakable bufferBreakable;

    public Building(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, SpreadEaglesActivity activity) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        parentActivity = activity;
        killed = false;
    }

    //Currently only used in Building.setProperties(int type) Calling this
    public Breakable grabBreakable(float pX, float pY, float width, float height) {
        Log.v("Breakable", pX + ":" + pY);
        bufferBreakable = parentActivity.getUnusedBreakable();
        bufferBreakable.useBreakable(pX, pY, width, height);
        return bufferBreakable;
    }


    //Returns the amount of time to offset the release of the next building
    public float useBuilding(float x, float y, int type){
        this.setInUse(true);
        killed = false;
        this.setPosition(x, y);
        setVisible(true);
        Log.v("Bu-Used", "Building address" + address);
        setProperties(type);
        parentActivity.attachEntityWithZ(this, SpreadEaglesActivity.BUILDING_Z_DEPTH);
        return ((SpreadEaglesActivity.CAMERA_WIDTH + this.getWidth())/SpreadEaglesActivity.BUILDING_VELOCITY) - SpreadEaglesActivity.BUILDING_SPEED;//Math to determine the difference (in seconds) needed to keep the pixel spacing between buildings consistent
    }

    private void setProperties(int type){

        Random rand = new Random();
        float bufferWidth;
        float bufferHeight;
        MoveXModifier ModifierBuffer;

        this.setColor(0, 1, 1, 1);

        switch (type) {
            case 1:
                this.setY(40);
                this.setWidth(240);
                this.setX(this.getX()-this.getWidth());
                this.setHeight(SpreadEaglesActivity.CAMERA_HEIGHT - SpreadEaglesActivity.FOOTER_SPACE - this.getY());

                ModifierBuffer = new MoveXModifier(SpreadEaglesActivity.BUILDING_SPEED, this.getX(), SpreadEaglesActivity.CAMERA_WIDTH);
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

                //set attributes for door breakable
                bufferWidth = this.getWidth() / 6;
                bufferHeight = this.getHeight() / 4;
                bufferBreakable = this.grabBreakable((this.getX() + this.getWidth()/2 - bufferWidth/2), (this.getY() + this.getHeight() - bufferHeight), bufferWidth, bufferHeight);
                ModifierBuffer = new MoveXModifier((SpreadEaglesActivity.CAMERA_WIDTH + this.getWidth())/SpreadEaglesActivity.BUILDING_VELOCITY, bufferBreakable.getX(), bufferBreakable.getX() + SpreadEaglesActivity.CAMERA_WIDTH - this.getX());
                bufferBreakable.setMovements(ModifierBuffer); //I have the individual breakables attach the Modifiers so the 'onModifierFinished(...)' can call that objects 'killMe()'

                //set attributes for Left Window breakable
                bufferWidth = this.getWidth() / 10;
                bufferHeight = this.getHeight() / 6;
                bufferBreakable = this.grabBreakable((this.getX() + bufferWidth), (this.getY() + this.getHeight()/2 - bufferHeight/2), bufferWidth, bufferHeight);
                ModifierBuffer = new MoveXModifier((SpreadEaglesActivity.CAMERA_WIDTH + this.getWidth())/SpreadEaglesActivity.BUILDING_VELOCITY, bufferBreakable.getX(), bufferBreakable.getX() + SpreadEaglesActivity.CAMERA_WIDTH - this.getX());
                bufferBreakable.setMovements(ModifierBuffer);//I have the individual breakables attach the Modifiers so the 'onModifierFinished(...)' can call that objects 'killMe()'

                //set attributes for Right Window breakable (same size as one above)
                bufferBreakable = this.grabBreakable((this.getX() + this.getWidth() - bufferWidth*2), (this.getY() + this.getHeight()/2 - bufferHeight/2), bufferWidth, bufferHeight);
                ModifierBuffer = new MoveXModifier((SpreadEaglesActivity.CAMERA_WIDTH + this.getWidth())/SpreadEaglesActivity.BUILDING_VELOCITY, bufferBreakable.getX(), bufferBreakable.getX() + SpreadEaglesActivity.CAMERA_WIDTH - this.getX());
                bufferBreakable.setMovements(ModifierBuffer);//I have the individual breakables attach the Modifiers so the 'onModifierFinished(...)' can call that objects 'killMe()'

                break;

            case 2:
                this.setY(80);
                this.setWidth(400);
                this.setX(this.getX() - this.getWidth());
                this.setHeight(SpreadEaglesActivity.CAMERA_HEIGHT - SpreadEaglesActivity.FOOTER_SPACE - this.getY());
                ModifierBuffer = new MoveXModifier((SpreadEaglesActivity.CAMERA_WIDTH + this.getWidth())/SpreadEaglesActivity.BUILDING_VELOCITY, this.getX(), SpreadEaglesActivity.CAMERA_WIDTH);
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

                //set attributes for "door" breakable"
                bufferWidth = this.getWidth() / 10;
                bufferHeight = this.getHeight() / 3;
                bufferBreakable = this.grabBreakable((this.getX() + this.getWidth()/2 - bufferWidth/2), (this.getY() + this.getHeight() - bufferHeight), bufferWidth, bufferHeight);
                ModifierBuffer = new MoveXModifier((SpreadEaglesActivity.CAMERA_WIDTH + this.getWidth())/SpreadEaglesActivity.BUILDING_VELOCITY, bufferBreakable.getX(), bufferBreakable.getX() + SpreadEaglesActivity.CAMERA_WIDTH - this.getX());
                bufferBreakable.setMovements(ModifierBuffer);//I have the individual breakables attach the Modifiers so the 'onModifierFinished(...)' can call that objects 'killMe()'

                //set attributes for Left Most Window breakable
                bufferWidth = this.getWidth() / 10;
                bufferHeight = this.getWidth() / 10;
                bufferBreakable = this.grabBreakable((this.getX() + this.getWidth()/8 - bufferWidth/2), (this.getY() + this.getHeight()/3 - bufferHeight/2), bufferWidth, bufferHeight);
                ModifierBuffer = new MoveXModifier((SpreadEaglesActivity.CAMERA_WIDTH + this.getWidth())/SpreadEaglesActivity.BUILDING_VELOCITY, bufferBreakable.getX(), bufferBreakable.getX() + SpreadEaglesActivity.CAMERA_WIDTH - this.getX());
                bufferBreakable.setMovements(ModifierBuffer);//I have the individual breakables attach the Modifiers so the 'onModifierFinished(...)' can call that objects 'killMe()'

                //set attributes for Center Left Window breakable (same size as first window)
                bufferBreakable = this.grabBreakable((this.getX() + (3 * this.getWidth()/8) - bufferWidth/2), (this.getY() + this.getHeight()/3 - bufferHeight/2), bufferWidth, bufferHeight);
                ModifierBuffer = new MoveXModifier((SpreadEaglesActivity.CAMERA_WIDTH + this.getWidth())/SpreadEaglesActivity.BUILDING_VELOCITY, bufferBreakable.getX(), bufferBreakable.getX() + SpreadEaglesActivity.CAMERA_WIDTH - this.getX());
                bufferBreakable.setMovements(ModifierBuffer);//I have the individual breakables attach the Modifiers so the 'onModifierFinished(...)' can call that objects 'killMe()'

                //set attributes for Center Right Window breakable (same size as first window)
                bufferBreakable = this.grabBreakable((this.getX() + (5 * this.getWidth()/8) - bufferWidth/2), (this.getY() + this.getHeight()/3 - bufferHeight/2), bufferWidth, bufferHeight);
                ModifierBuffer = new MoveXModifier((SpreadEaglesActivity.CAMERA_WIDTH + this.getWidth())/SpreadEaglesActivity.BUILDING_VELOCITY, bufferBreakable.getX(), bufferBreakable.getX() + SpreadEaglesActivity.CAMERA_WIDTH - this.getX());
                bufferBreakable.setMovements(ModifierBuffer);//I have the individual breakables attach the Modifiers so the 'onModifierFinished(...)' can call that objects 'killMe()'

                //set attributes for Right Most Window breakable (same size as first window)
                bufferBreakable = this.grabBreakable((this.getX() + (7 * this.getWidth()/8) - bufferWidth/2), (this.getY() + this.getHeight()/3 - bufferHeight/2), bufferWidth, bufferHeight);
                ModifierBuffer = new MoveXModifier((SpreadEaglesActivity.CAMERA_WIDTH + this.getWidth())/SpreadEaglesActivity.BUILDING_VELOCITY, bufferBreakable.getX(), bufferBreakable.getX() + SpreadEaglesActivity.CAMERA_WIDTH - this.getX());
                bufferBreakable.setMovements(ModifierBuffer);//I have the individual breakables attach the Modifiers so the 'onModifierFinished(...)' can call that objects 'killMe()'

                break;

            default:
                this.setY(40);
                this.setWidth(SpreadEaglesActivity.BUILDING_WIDTH_STANDARD);
                this.setX(this.getX() - this.getWidth());
                this.setHeight((SpreadEaglesActivity.CAMERA_HEIGHT - this.getY()) - SpreadEaglesActivity.FOOTER_SPACE);
                ModifierBuffer = new MoveXModifier((SpreadEaglesActivity.CAMERA_WIDTH + this.getWidth())/SpreadEaglesActivity.BUILDING_VELOCITY, this.getX(), SpreadEaglesActivity.CAMERA_WIDTH);
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

                bufferWidth = this.getWidth() / 4;
                bufferHeight = this.getHeight() / 4;

                //Sets bottom breakable somewhere contained in the bottom half of this building
                bufferBreakable = this.grabBreakable(this.getX() + rand.nextInt((int) (this.getWidth() - bufferWidth)), this.getY() + this.getHeight() / 2 + rand.nextInt((int) (this.getHeight() / 2 - bufferHeight)), bufferWidth, bufferHeight);
                ModifierBuffer = new MoveXModifier((SpreadEaglesActivity.CAMERA_WIDTH + this.getWidth())/SpreadEaglesActivity.BUILDING_VELOCITY, bufferBreakable.getX(), bufferBreakable.getX() + SpreadEaglesActivity.CAMERA_WIDTH - this.getX());
                bufferBreakable.setMovements(ModifierBuffer);//I have the individual breakables attach the Modifiers so the 'onModifierFinished(...)' can call that objects 'killMe()'

                //sets top breakable somewhere contained in the top half of building
                bufferBreakable = this.grabBreakable(this.getX() + rand.nextInt((int) (this.getWidth() - bufferWidth)), this.getY() + rand.nextInt((int) (this.getHeight() / 2 - bufferHeight)), bufferWidth, bufferHeight);
                ModifierBuffer = new MoveXModifier((SpreadEaglesActivity.CAMERA_WIDTH + this.getWidth())/SpreadEaglesActivity.BUILDING_VELOCITY, bufferBreakable.getX(), bufferBreakable.getX() + SpreadEaglesActivity.CAMERA_WIDTH - this.getX());
                bufferBreakable.setMovements(ModifierBuffer);//I have the individual breakables attach the Modifiers so the 'onModifierFinished(...)' can call that objects 'killMe()'

                break;
        }
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
        Log.v("Build-Recycle", "detached:" + this.detachSelf() + " and parent:" + this.getParent());
        this.setInUse(false);
    }
}
