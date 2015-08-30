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
    private int CAMERA_HEIGHT = 576;

    private Camera mCamera;
    private Scene mScene;

    private ITextureRegion introRegion;
    private ITexture introTexture;

    private SharedPreferences myPrefs;

    @Override
    protected void onCreateResources() {

        myPrefs = getSharedPreferences("levelCompletion", MODE_PRIVATE);

        try {
            this.introTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
                @Override
                public InputStream open() throws IOException {
                    return getAssets().open("gfx/intro.png");
                }
            });
            this.introTexture.load();
            this.introRegion = TextureRegionFactory.extractFromTexture(this.introTexture);
        } catch (IOException e) {
            Debug.e(e);
        }

    }

    @Override
    protected Scene onCreateScene() {
        mScene = new Scene();

        mScene.setColor(0, 0, 0);

        final Sprite introScreen = new Sprite(CAMERA_WIDTH/2 - introTexture.getWidth()/2, CAMERA_HEIGHT/2 - introTexture.getHeight()/2, introRegion, getVertexBufferObjectManager());

        mScene.registerTouchArea(introScreen);

        mScene.attachChild(introScreen);

        mScene.setOnAreaTouchListener(new IOnAreaTouchListener() {
            @Override
            public boolean onAreaTouched(TouchEvent touchEvent, ITouchArea iTouchArea, float v, float v2) {

                if (touchEvent.isActionUp()) {
                    Intent intent = new Intent(Main.this, SpreadEaglesActivity.class);
                    startActivity(intent);
                    finish();
                }
                return false;
            }
        });

        return mScene;
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        mCamera = new Camera(0.0F, 0.0F, CAMERA_WIDTH, CAMERA_HEIGHT);
        EngineOptions localEngineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
                new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
        localEngineOptions.getAudioOptions().setNeedsSound(true);
        localEngineOptions.getAudioOptions().setNeedsMusic(true);
        return localEngineOptions;
    }
}
