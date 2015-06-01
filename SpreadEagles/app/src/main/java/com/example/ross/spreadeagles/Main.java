package com.example.ross.spreadeagles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by shanus on 1/9/15.
 */
public class Main extends SimpleBaseGameActivity {

    private int CAMERA_WIDTH = 768;
    private int CAMERA_HEIGHT = 1024;

    private Camera mCamera;
    private Scene mScene;

    private ITextureRegion leftEmptyRegion, leftFilledRegion, rightEmptyRegion, rightFilledRegion;
    private ITexture leftEmptyTexture, leftFilledTexture, rightEmptyTexture, rightFilledTexture;

    private SharedPreferences myPrefs;

    @Override
    protected void onCreateResources() {

        myPrefs = getSharedPreferences("levelCompletion", MODE_PRIVATE);

        try {
            this.leftEmptyTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
                @Override
                public InputStream open() throws IOException {
                    return getAssets().open("gfx/left_half_empty.png");
                }
            });
            this.leftEmptyTexture.load();
            this.leftEmptyRegion = TextureRegionFactory.extractFromTexture(this.leftEmptyTexture);
        } catch (IOException e) {
            Debug.e(e);
        }

        try {
            this.rightEmptyTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
                @Override
                public InputStream open() throws IOException {
                    return getAssets().open("gfx/right_half_empty.png");
                }
            });
            this.rightEmptyTexture.load();
            this.rightEmptyRegion = TextureRegionFactory.extractFromTexture(this.rightEmptyTexture);
        } catch (IOException e) {
            Debug.e(e);
        }

        try {
            this.leftFilledTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
                @Override
                public InputStream open() throws IOException {
                    return getAssets().open("gfx/left_half_filled.png");
                }
            });
            this.leftFilledTexture.load();
            this.leftFilledRegion = TextureRegionFactory.extractFromTexture(this.leftFilledTexture);
        } catch (IOException e) {
            Debug.e(e);
        }

        try {
            this.rightFilledTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
                @Override
                public InputStream open() throws IOException {
                    return getAssets().open("gfx/right_half_filled.png");
                }
            });
            this.rightFilledTexture.load();
            this.rightFilledRegion = TextureRegionFactory.extractFromTexture(this.rightFilledTexture);
        } catch (IOException e) {
            Debug.e(e);
        }

    }

    @Override
    protected Scene onCreateScene() {
        mScene = new Scene();

        mScene.setBackground(new Background(1, 1, 1, 0));

        final Sprite leftEmpty = new Sprite(CAMERA_WIDTH/2 - 497, CAMERA_HEIGHT/2 - leftEmptyTexture.getHeight()/2, leftEmptyRegion, getVertexBufferObjectManager());
        final Sprite rightEmpty = new Sprite(CAMERA_WIDTH/2, CAMERA_HEIGHT/2 - rightEmptyTexture.getHeight()/2, rightEmptyRegion, getVertexBufferObjectManager());
        final Sprite leftFilled = new Sprite(CAMERA_WIDTH/2 - 497, CAMERA_HEIGHT/2 - leftFilledTexture.getHeight()/2, leftFilledRegion, getVertexBufferObjectManager());
        final Sprite rightFilled = new Sprite(CAMERA_WIDTH/2, CAMERA_HEIGHT/2 - rightFilledTexture.getHeight()/2, rightFilledRegion, getVertexBufferObjectManager());

        mScene.registerTouchArea(leftEmpty);
        mScene.registerTouchArea(rightEmpty);

        // check shared prefs
        // if pref doesnt exist or is false for left game, attach left empty
        // else, attach left filled
        if (!myPrefs.getBoolean("leftFinished", false)) {
            mScene.attachChild(leftEmpty);
        } else {
            mScene.attachChild(leftFilled);
        }

        // check shared prefs for right just like left
        if (!myPrefs.getBoolean("rightFinished", false)) {
            mScene.attachChild(rightEmpty);
        } else {
            mScene.attachChild(rightFilled);
        }

        mScene.setOnAreaTouchListener(new IOnAreaTouchListener() {
            @Override
            public boolean onAreaTouched(TouchEvent touchEvent, ITouchArea iTouchArea, float v, float v2) {

                Intent intent = null;

                if (iTouchArea.equals(leftEmpty) && touchEvent.isActionUp()) {
                    Log.v("leftEmptyTouched", "true");
                    intent = new Intent(Main.this, SpreadEaglesActivity.class);
                    startActivity(intent);
                    finish();
//                    mScene.attachChild(leftFilled);
                } else if (iTouchArea.equals(rightEmpty) && touchEvent.isActionUp()) {
                    Log.v("rightEmptyTouched", "true");
                    intent = new Intent(Main.this, SpreadEaglesActivity.class);
                    startActivity(intent);
                    finish();
//                    mScene.attachChild(rightFilled);
                }
                return false;
            }
        });

        return mScene;
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        mCamera = new Camera(0.0F, 0.0F, CAMERA_WIDTH, CAMERA_HEIGHT);
        EngineOptions localEngineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
        localEngineOptions.getAudioOptions().setNeedsSound(true);
        localEngineOptions.getAudioOptions().setNeedsMusic(true);
        return localEngineOptions;
    }
}
