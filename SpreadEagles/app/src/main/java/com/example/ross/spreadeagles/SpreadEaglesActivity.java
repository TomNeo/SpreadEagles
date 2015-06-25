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
     *
     * I have a small list of considerations to keep in mind while setting these values:
     *
     * --EAGLE_COLLISION should be less than 1. anything >= 1 will result in ALL eagles killing themselves without ever checking for collisions.
     * --INTERVAL_BETWEEN_BUILDINGS gets offset during runtime to control even spacing between different random building widths
     *
     * ***
     */
    public final static int MAX_NUMBER_EAGLES = 6;
    public final static int MAX_NUMBER_BUILDINGS = 4;
    public final static int MAX_NUMBER_BREAKABLES = 16;

    public final static int CAMERA_WIDTH = 748;
    public final static int CAMERA_HEIGHT = 480;
    public final static int GAME_LENGTH = 46;   //in seconds
    public final static float EAGLE_HEIGHT = 30f;
    public final static float EAGLE_WIDTH = 30f;
    public final static float EAGLE_SPEED = .25f;  // Furthest path takes n seconds, all others adjusted to this speed
    public final static float EAGLE_COLLISION = .9f;   // percentage as a decimal. how far along path before collisions are checked must be less than 1 or else it will never check for collision.
    public final static float CROSSHAIR_HEIGHT = 30f;
    public final static float CROSSHAIR_WIDTH = 30f;
    public final static float INTERVAL_BETWEEN_BUILDINGS = 1.2f; //counted in seconds
    public final static float BUILDING_SPEED = 2.5f;        //number of seconds it takes for the BUILDING_WIDTH_STANDARD (and all it's breakables) across the screen.
    public final static float BUILDING_WIDTH_STANDARD = 250; //Used on the switch case default for building types, and sets the base speed for all buildings
    public final static float FOOTER_SPACE = 120;//distance from bottom of screen to bottom of buildings
    public final static float BUILDING_VELOCITY = (CAMERA_WIDTH + BUILDING_WIDTH_STANDARD)/BUILDING_SPEED; //calculated so we can adjust other settings and not have to redo the algebra
    public final static float TREE_BASE_HEIGHT = 70;//distance from bottom of screen to bottom of trees
    public final static float TREE_SPEED = BUILDING_SPEED * .7f;
    public final static float TREE_WIDTH = 44;
    /*Higher numbers will appear on top of lower numbers*/

    public final static int CROSSHAIR_Z_DEPTH = 2;
    public final static int EAGLE_Z_DEPTH = 1;
    public final static int TREE_Z_DEPTH = 0;
    public final static int BREAKABLE_Z_DEPTH = -1;
    public final static int BUILDING_Z_DEPTH = -2;

    private int highestNumOfEagles;
    private int totalNumOfEagles;
    private int totalHits;
    private int totalBreakables;
    private float life;

    private Camera mCamera;
    private ZControlledScene mScene;
    private Background mBackground;
    private Crosshair mPlayer;
    private Tree mTree;
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
       // mTree = new Tree(CAMERA_WIDTH, 0, BlockRegion, getVertexBufferObjectManager(), this);
        Log.v("TrashBin:", "INITIALIZED");
    }


    //Ran at onCreateResources(). Fills array Eagles with all the eagles the game will need
    private void populateEagles(int count) {
        for (int i = 0; i < Eagles.length; i++) {
            Eagles[i] = new Eagle(CAMERA_WIDTH / 2, CAMERA_HEIGHT, EaglesRegion, getVertexBufferObjectManager(), SpreadEaglesActivity.this);
            Eagles[i].setAddress(i);
            Log.v("Gen-Ea", "Eagle:" + Eagles[i].getAddress());
        }
    }

    //Ran at onCreateResources(). Fills array Buildings with all the Buildings the game will need
    private void populateBuildings(int count) {
        for (int i = 0; i < Buildings.length; i++) {
            Buildings[i] = new Building(CAMERA_WIDTH / 2, CAMERA_HEIGHT, BlockRegion, getVertexBufferObjectManager(), SpreadEaglesActivity.this);
            Buildings[i].setAddress(i);
            Log.v("Gen-Bu", "Building:" + Buildings[i].getAddress());
        }
    }

    //Ran at onCreateResources(). Fills array Breakables with all the Breakables the game will need
    private void populateBreakables(int count) {
        for (int i = 0; i < Breakables.length; i++) {
            Breakables[i] = new Breakable(CAMERA_WIDTH / 2, CAMERA_HEIGHT, BlockRegion, getVertexBufferObjectManager(), SpreadEaglesActivity.this);
            Breakables[i].setAddress(i);
            Log.v("Gen-Br", "Breakable:" + Breakables[i].getAddress());
        }
    }

    /*Used to return the first eagle in Eagles that is currently not 'Used'
      NOTE: This method does NOT make that Eagle used */
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

    /*Used to return the first Building in Buildings that is currently not 'Used'
      NOTE: This method does NOT make that Building used */
    private Building getUnusedBuilding() {
        for (int i = 0; i < Buildings.length; i++) {
            if (!Buildings[i].isInUse()) {
                return Buildings[i];
            }
        }
        Log.v("Not found", "no building found");
        return null;
    }

    /*Used to return the first Breakable in Breakables that is currently not 'Used'
      NOTE: This method does NOT make that Breakable used */
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

    //Currently only being used to track total hits being posted in Log.v "FINAL"
    public void addHit() {
        totalHits++;
    }

    /*This runs at the end of every managedUpdate cycle. Used to recycle all 'killed' objects at a single time for synchronizing purposes.
        recycleBin was made a list of HeinousEntity's so I could call the same 'recycleMe()' */
    private void cleanUp() {
        Log.v("TrashBin:", "Size " + recycleBin.size());
        recycleBin.trimToSize();
        for (int i = 0; i < recycleBin.size(); i++) {
            recycleBin.get(i).recycleMe();
        }
        recycleBin.clear();
        recycleBin.trimToSize();
    }

    //Called after game 'life' has reached the GAME_LENGTH
    private void finalCleanUp() {
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

    //Called inside the HeinousEntity's killMe() to get recycled at the next cleanUp()
    public void addToRecycleList(HeinousEntity entity) {
        recycleBin.add(entity);
        Log.v("Added Trash", "object:" + entity.toString() + " " + entity.address);
    }

    //Called inside the Entity's 'Use' (or constructor I suppose) to set the Z_DEPTH of that object. Higher zDepths appear on top of lower Entities
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
        mTree = new Tree(CAMERA_WIDTH, 0, BlockRegion, getVertexBufferObjectManager(), this);
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
            float BuildingCount = 0;
            float TreeCount = 0;
            int cycles = 0;
            float treeOffset = .2f;
            float offset = 0;//this offset is required since wider buildings take more time to cross the width of the screen...(see below)
            Random rand = new Random();

            @Override
            public void onUpdate(float time) {
                if (!stopped) {
                    life = life + time;
                    BuildingCount = BuildingCount + time;
                    TreeCount = TreeCount + time;
                    cycles++;

                    if (BuildingCount >= INTERVAL_BETWEEN_BUILDINGS + offset) { // <------------ (where offset is applied)
                        Log.v("FPS", " FPS: " + (cycles / BuildingCount));
                        BuildingCount = 0;
                        cycles = 0;
                        Building BuildingBuffer = getUnusedBuilding();
                        offset = BuildingBuffer.useBuilding(CAMERA_WIDTH, CAMERA_HEIGHT - FOOTER_SPACE,rand.nextInt(3));//...so when the building knows how long it'll take, we offset the release of the next building
                    }

                    if (TreeCount >=  TREE_SPEED + treeOffset) {

                        treeOffset = rand.nextInt(12)/7;
                        TreeCount = 0;
                        cycles = 0;
                        if (!mTree.isInUse()) {
                            mTree.useTree();
                        }
                    }

                    //If game is out of time, do ending stuff
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
                    cleanUp();//<--------- Should be last thing in parent update loop. Should never be called anywhere else, for asynchronous reasons.
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

    /*This is used by an Eagle object (using itself as a parameter) so that THIS object and check its private list Breakables for collisions.
        if it is a collision on a currently in use, NOT hit breakable, the breakable becomes hit and the 'return true;' tells the eagle to
        stop all modifiers and 'kill' itself  */
    public boolean checkEagleCollidesWithBreakables(HeinousEntity pEntity) {

        //if eagle is hitting tree, kill eagle, no need to check further
        if (pEntity.collidesWith(mTree)){
            return true;
        }

        for(int i = 0; i < Breakables.length; i++){
            if(Breakables[i].isInUse() && Breakables[i].collidesWith(pEntity) && !Breakables[i].isHit()){
                Breakables[i].hit();
                return true;
            }
        }
        return false;
    }

}