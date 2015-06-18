package com.example.ross.spreadeagles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;


public class SpreadEaglesActivity extends SimpleBaseGameActivity {

    /**
     * **        This is my control panel for basically everything that is tweakable in the game so far.
     * <p/>
     * I have a small list of considerations to keep in mind while setting these values:
     * <p/>
     * --IF (BUILDING_HEIGHT_OPTIONS - 1) * BUILDING_HEIGHT_INTERVALS + FOOTER_SPACE >= MAXIMUM_BUILDING_HEIGHT YOU COULD END UP WITH BAD BUILDINGS
     * --MINIMUM_BUILDING_WIDTH must be less than MAXIMUM_BUILDING_WIDTH
     * --EAGLE_COLLISION should be less than 1. anything >= 1 will result in ALL eagles killing themselves without ever checking for collisions.
     * <p/>
     * ***
     */
    public final static int MAX_NUMBER_EAGLES = 6;
    public final static int MAX_NUMBER_BUILDINGS = 4;
    public final static int MAX_NUMBER_BREAKABLES = 10;

    public final static int CAMERA_WIDTH = 748;
    public final static int CAMERA_HEIGHT = 480;
    public final static int GAME_LENGTH = 46;   //in seconds
    public final static float EAGLE_SPEED = .25f;  // Furthest path takes n seconds, all others adjusted to this speed
    public final static float EAGLE_COLLISION = .85f;   // percentage as a decimal. how far along path before collisions are checked must be less than 1 or else it will never check for collision.
    public final static float INTERVAL_BETWEEN_BUILDINGS = .8f; //counted in seconds
    public final static float BUILDING_SPEED = 2f;        //number of seconds it takes to move a building (and all it's breakables) across the screen.
    public final static int BUILDING_HEIGHT_INTERVALS = 60;  //Determines how much to decrease Building heights by
    public final static int BUILDING_HEIGHT_OPTIONS = 2;    //Maximum number of BUILDING_HEIGHT_INTERVALS to be subtracted from MAXIMUM_BUILDING_HEIGHT. Actual range is 0 -> (n - 1)
    public final static float MAXIMUM_BUILDING_HEIGHT = CAMERA_HEIGHT / 2;
    public final static float MINIMUM_BUILDING_WIDTH = 240;
    public final static float MAXIMUM_BUILDING_WIDTH = 280;
    public final static float FOOTER_SPACE = 120;           //distance from bottom of screen to bottom of buildings

    
    /*Higher numbers will appear on top of lower numbers*/

    public final static int CROSSHAIR_Z_DEPTH = 2;
    public final static int EAGLE_Z_DEPTH = 1;
    public final static int BREAKABLE_Z_DEPTH = 0;
    public final static int BUILDING_Z_DEPTH = -1;

    private int highestNumOfEagles;
    private int totalNumOfEagles;
    private int totalHits;
    private int totalBreakables;
    private float life;

    private Camera mCamera;
    private ZControlledScene mScene;
    private Background mBackground;
    private Crosshair mPlayer;
    private Eagle[] Eagles;
    private Building[] Buildings;
    private Breakable[] Breakables;
    private ArrayList<HeinousEntity> recycleBin;

    private ITexture BlockTexture, CrosshairTexture, EaglesTexture;
    private BitmapTextureAtlas mFontTexture;
    private Font mFont;

    private float gameLength;

    SharedPreferences myPrefs;
    private ITextureRegion BlockRegion, CrosshairRegion, EaglesRegion;
    private boolean stopped;


    public SpreadEaglesActivity() {
        gameLength = GAME_LENGTH;
        recycleBin = new ArrayList<HeinousEntity>(0);
        Eagles = new Eagle[MAX_NUMBER_EAGLES];
        Buildings = new Building[MAX_NUMBER_BUILDINGS];
        Breakables = new Breakable[MAX_NUMBER_BREAKABLES];
        highestNumOfEagles = 0;
        totalNumOfEagles = 0;
        totalHits = 0;
        totalBreakables = 0;
        life = 0;
        stopped = false;

        Log.v("TrashBin:", "INITIALIZED");
    }


    private void populateEagles(int count) {
        for (int i = 0; i < Eagles.length; i++) {
            Eagles[i] = new Eagle(CAMERA_WIDTH / 2, CAMERA_HEIGHT, EaglesRegion, getVertexBufferObjectManager(), SpreadEaglesActivity.this);
            Eagles[i].setAddress(i);
            Log.v("Gen-Ea", "Eagle:" + Eagles[i].getAddress());
        }
    }

    private void populateBuildings(int count) {
        for (int i = 0; i < Buildings.length; i++) {
            Buildings[i] = new Building(CAMERA_WIDTH / 2, CAMERA_HEIGHT, BlockRegion, getVertexBufferObjectManager(), SpreadEaglesActivity.this);
            Buildings[i].setAddress(i);
            Log.v("Gen-Bu", "Building:" + Buildings[i].getAddress());
        }
    }

    private void populateBreakables(int count) {
        for (int i = 0; i < Breakables.length; i++) {
            Breakables[i] = new Breakable(CAMERA_WIDTH / 2, CAMERA_HEIGHT, BlockRegion, getVertexBufferObjectManager(), SpreadEaglesActivity.this);
            Breakables[i].setAddress(i);
            Log.v("Gen-Br", "Breakable:" + Breakables[i].getAddress());
        }
    }

    private Eagle getUnusedEagle() {
        for (int i = 0; i < Eagles.length; i++) {
            if (!Eagles[i].isInUse()) {
                totalNumOfEagles++;
                highestNumOfEagles = i + 1 > highestNumOfEagles ? i + 1 : highestNumOfEagles;
                return Eagles[i];
            }
        }
        Log.v("Not found", "no eagle found");
        return null;
    }

    private Building getUnusedBuilding() {
        for (int i = 0; i < Buildings.length; i++) {
            if (!Buildings[i].isInUse()) {
                return Buildings[i];
            }
        }
        Log.v("Not found", "no building found");
        return null;
    }

    public Breakable getUnusedBreakable() {
        for (int i = 0; i < Breakables.length; i++) {
            if (!Breakables[i].isInUse()) {
                totalBreakables++;
                return Breakables[i];
            }
        }
        Log.v("Not found", "no building found");
        return null;
    }

    public void addHit() {
        totalHits++;
    }

    private void cleanUp() {
        Log.v("TrashBin:", "Size " + recycleBin.size());
        recycleBin.trimToSize();
        for (int i = 0; i < recycleBin.size(); i++) {
            Log.v("specific loop", "start " + i);
            recycleBin.get(i).recycleMe();
            Log.v("specific loop", "passed " + i);
        }
        recycleBin.clear();
        recycleBin.trimToSize();
    }

    private void finalCleanUp() {
        Log.v("eagleList", "FINAL CALLED");
        Log.v("BuildingList", "FINAL CALLED");
        for (int i = 0; i < Eagles.length; i++) {
            Eagles[i].dispose();
        }
        for (int i = 0; i < Buildings.length; i++) {
            Buildings[i].dispose();
        }
        for (int i = 0; i < Breakables.length; i++) {
            Breakables[i].dispose();
        }
        Log.v("TrashBin:", "FINAL CALLED");

        cleanUp();
    }

    public void addToRecycleList(HeinousEntity entity) {
        recycleBin.add(entity);
        Log.v("Added Trash", "object:" + entity.toString() + " " + entity.address);
    }

    public void attachEntityWithZ(IEntity pEntity, int zDepth) {
        mScene.attachChildWithZ(pEntity, zDepth);
    }

    @Override
    protected void onCreateResources() {
        myPrefs = getSharedPreferences("levelCompletion", MODE_PRIVATE);

        try {
            this.BlockTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
                @Override
                public InputStream open() throws IOException {
                    return getAssets().open("gfx/Block.png");
                }
            });
            this.BlockTexture.load();
            this.BlockRegion = TextureRegionFactory.extractFromTexture(this.BlockTexture);
        } catch (IOException e) {
            Debug.e(e);
        }

        try {
            this.CrosshairTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
                @Override
                public InputStream open() throws IOException {
                    return getAssets().open("gfx/Crosshair.png");
                }
            });
            this.CrosshairTexture.load();
            this.CrosshairRegion = TextureRegionFactory.extractFromTexture(this.CrosshairTexture);
        } catch (IOException e) {
            Debug.e(e);
        }

        try {
            this.EaglesTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
                @Override
                public InputStream open() throws IOException {
                    return getAssets().open("gfx/Eagless.png");
                }
            });
            this.EaglesTexture.load();
            this.EaglesRegion = TextureRegionFactory.extractFromTexture(this.EaglesTexture);
        } catch (IOException e) {
            Debug.e(e);
        }

        this.mFontTexture = new BitmapTextureAtlas(mEngine.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        this.mFont = new Font(this.getFontManager(), this.mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.YELLOW);

        this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
        this.getFontManager().loadFont(this.mFont);
        populateEagles(MAX_NUMBER_EAGLES);
        populateBuildings(MAX_NUMBER_BUILDINGS);
        populateBreakables(MAX_NUMBER_BREAKABLES);
    }

    @Override
    protected Scene onCreateScene() {

        mScene = new ZControlledScene();

        mBackground = new Background(0, 0, 0, 1);

        mScene.setBackground(mBackground);

        mPlayer = new Crosshair((CAMERA_WIDTH / 4 - 32), (CAMERA_HEIGHT / 1.5f - 32), CrosshairRegion, getVertexBufferObjectManager(), this);

        attachEntityWithZ(mPlayer, CROSSHAIR_Z_DEPTH);

        mScene.setOnSceneTouchListener(new IOnSceneTouchListener() {
            @Override
            public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
                if (touchEvent.isActionDown()) {
                    mPlayer.setPosition(touchEvent.getX(), touchEvent.getY());
                    Log.v("get eagle", "Before");
                    Eagle eagleBuffer = getUnusedEagle();
                    eagleBuffer.useEagle(touchEvent.getX(), touchEvent.getY());
                    Log.v("get eagle", "after address:" + eagleBuffer.getAddress());
                }
                return true;
            }
        });

        mScene.registerUpdateHandler(new IUpdateHandler() {
            float count = 0;
            int cycles = 0;
            Random rand = new Random();

            @Override
            public void onUpdate(float time) {
                if (!stopped) {
                    life = life + time;
                    count = count + time;
                    cycles++;
                    if (count >= INTERVAL_BETWEEN_BUILDINGS) {
                        Log.v("FPS", " FPS: " + (cycles / count));
                        count = 0;
                        cycles = 0;
                        Building BuildingBuffer = getUnusedBuilding();
                        BuildingBuffer.useBuilding(CAMERA_WIDTH, (CAMERA_HEIGHT - MAXIMUM_BUILDING_HEIGHT + (rand.nextInt(BUILDING_HEIGHT_OPTIONS) * BUILDING_HEIGHT_INTERVALS)));
                    }

                    if (gameLength - life <= 0) {
                        if (true)
                            myPrefs.edit().putBoolean("leftFinished", true).commit();
                        else
                            myPrefs.edit().putBoolean("leftFinished", false).commit();
                        gameLength = GAME_LENGTH;
                        Intent intent = new Intent(SpreadEaglesActivity.this, Main.class);
                        Log.v("Final", " max Eagles: " + highestNumOfEagles);
                        Log.v("Final", " Total Eagles: " + totalNumOfEagles);
                        Log.v("Final", " Total Breakables: " + totalBreakables);
                        Log.v("Final", " Total Hits: " + totalHits);
                        finalCleanUp();
                        startActivity(intent);
                        finish();
                        stopped = true;
                    }
                    Log.v("Time", " Total Time: " + life);
                    cleanUp();
                } else {
                    Log.v("Stopped", "Stopped");
                }
            }

            @Override
            public void reset() {
            }

        });
        return mScene;
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        mCamera = new Camera(0.0F, 0.0F, CAMERA_WIDTH, CAMERA_HEIGHT);
        EngineOptions localEngineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
        localEngineOptions.getAudioOptions().setNeedsSound(true);
        localEngineOptions.getAudioOptions().setNeedsMusic(true);
        return localEngineOptions;
    }

    public boolean checkEagleCollidesWithBreakables(HeinousEntity pEntity) {
        for(int i = 0; i < Breakables.length; i++){
            if(Breakables[i].isInUse() && Breakables[i].collidesWith(pEntity) && !Breakables[i].isHit()){
                Breakables[i].hit();
                return true;
            }
        }
        return false;
    }

}