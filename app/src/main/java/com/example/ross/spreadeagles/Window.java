package com.example.ross.spreadeagles;

import android.util.Log;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.IModifier;

/**
 * Created by Steve on 8/25/2015.
 */
public class Window extends Breakable {

    public boolean isWindowOne() {
        return isWindowOne;
    }

    private boolean isWindowOne;

    public Window(float pX, float pY, ITextureRegion pTextureRegion,
                  VertexBufferObjectManager pVertexBufferObjectManager, SpreadEaglesActivity pParent,
                  boolean mIsWindowOne) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager, pParent);
        this.setAlpha(1);
        isWindowOne = mIsWindowOne;
    }

    @Override
    public void useBreakable(float x, float y, float width, float height) {
        this.setInUse(true);
        killed = false;
        setPosition(x, y);
        setHeight(height);
        setWidth(width);
        this.setVisible(true);
        parentActivity.attachEntityWithZ(this, parentActivity.BREAKABLE_Z_DEPTH);
        Log.v("Br-Used", "Window address" + address);
    }

    @Override
    public void hit() {
        hit = true;
        parentActivity.addPoints(1);
        this.setAlpha(0);
//        this.setColor(1,0,0,1);
    }

    @Override
    public void recycleMe() {
        Log.v("Break-Recycle", "Called");
        this.setPosition(SpreadEaglesActivity.CAMERA_WIDTH / 2, SpreadEaglesActivity.CAMERA_HEIGHT);
        this.setVisible(false);
        this.setAlpha(1);
        hit = false;
        Log.v("Break-Recycle", "detached:" + this.detachSelf() + " and parent:" + this.getParent());
        this.setInUse(false);
    }

    @Override
    public void setMovements(MoveXModifier pIEntityModifier) {
        pIEntityModifier.addModifierListener(new IModifier.IModifierListener<IEntity>() {
            @Override
            public void onModifierStarted(IModifier<IEntity> iEntityIModifier, IEntity iEntity) {
            }

            @Override
            public void onModifierFinished(IModifier<IEntity> iEntityIModifier, IEntity iEntity) {
                killMe();
            }
        });
        this.registerEntityModifier(pIEntityModifier);
    }

}
