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
public class Tree extends HeinousEntity {

    private SpreadEaglesActivity parentActivity;
    private MoveXModifier ModifierBuffer;

    private boolean killed;

    public Tree(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, SpreadEaglesActivity pParent) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        this.parentActivity  = pParent;
        this.setWidth(SpreadEaglesActivity.TREE_WIDTH);
        this.setHeight(SpreadEaglesActivity.CAMERA_HEIGHT - SpreadEaglesActivity.TREE_BASE_HEIGHT);
        killed = false;
        this.setColor(.2f,1,.2f,1);
//        ModifierBuffer = new MoveXModifier(SpreadEaglesActivity.TREE_SPEED, this.getX(), -this.getWidth());
//        ModifierBuffer.addModifierListener(new IModifier.IModifierListener<IEntity>() {
//            @Override
//            public void onModifierStarted(IModifier<IEntity> iEntityIModifier, IEntity iEntity) {
//            }
//
//            @Override
//            public void onModifierFinished(IModifier<IEntity> iEntityIModifier, IEntity iEntity) {
//                killMe();
//            }
//        });
    }

    public void useTree(){
        this.setInUse(true);
        killed = false;
        this.setX(-this.getWidth());
        setVisible(true);
        parentActivity.attachEntityWithZ(this, SpreadEaglesActivity.TREE_Z_DEPTH);
        ModifierBuffer = new MoveXModifier(SpreadEaglesActivity.TREE_SPEED, this.getX(), SpreadEaglesActivity.CAMERA_WIDTH);
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
        Log.v("Tree-Used", "Tree" + address);
    }


    public void killMe(){
        if(!killed) {
            killed = true;
            parentActivity.addToRecycleList(this);
        }
    }

    @Override
    public void recycleMe() {
        Log.v("Tree-Recycle","Called");
        this.setVisible(false);
        Log.v("Tree-Recycle", "detached:" + this.detachSelf() + " and parent:" + this.getParent());
        this.setInUse(false);
    }
}