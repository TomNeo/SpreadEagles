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
import org.andengine.util.adt.list.SmartList;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;


public class SpreadEaglesActivity extends SimpleBaseGameActivity {

    /*****        This is my control panel for basically everything that is tweakable in the game so far.

        I have a small list of considerations to keep in mind while setting these values:

        --IF (BUILDING_HEIGHT_OPTIONS - 1) * BUILDING_HEIGHT_INTERVALS + FOOTER_SPACE >= MAXIMUM_BUILDING_HEIGHT YOU COULD END UP WITH BAD BUILDINGS
        --MINIMUM_BUILDING_WIDTH must be less than MAXIMUM_BUILDING_WIDTH
        --EAGLE_COLLISION should be less than 1. anything >= 1 will result in ALL eagles killing themselves without ever checking for collisions.

     *****/

    private final static int CAMERA_WIDTH = 748;
    private final static int CAMERA_HEIGHT = 480;
    private final static int GAME_LENGTH = 2800;   //In update cycles
    private final static float EAGLE_SPEED = .25f;  // Furthest path takes n seconds, all others adjusted to this speed
    private final static float EAGLE_COLLISION = .85f;   // percentage as a decimal. how far along path before collisions are checked must be less than 1 or else it will never check for collision.
    private final static int INTERVAL_BETWEEN_BUILDINGS = 60; //counted in update cycles
    private final static float BUILDING_SPEED = 2f;        //number of seconds it takes to move a building (and all it's breakables) across the screen.
    private final static int BUILDING_HEIGHT_INTERVALS = 80;  //Determines how much to decrease Building heights by
    private final static int BUILDING_HEIGHT_OPTIONS = 2;    //Maximum number of BUILDING_HEIGHT_INTERVALS to be subtracted from MAXIMUM_BUILDING_HEIGHT. Actual range is 0 -> (n - 1)
    private final static float MAXIMUM_BUILDING_HEIGHT = CAMERA_HEIGHT * 5f/8f;
    private final static float MINIMUM_BUILDING_WIDTH = 350;
    private final static float MAXIMUM_BUILDING_WIDTH = 351;
    private final static float FOOTER_SPACE = 80;           //distance from bottom of screen to bottom of buildings

    private int currentNumOfEagles;
    private int highestNumOfEagles;

    private Camera mCamera;
    private ZControlledScene mScene;
    private Background mBackground;
    private Crosshair mPlayer;
    private ArrayList<IEntity> trashBin;
    private SmartList<Building> buildingList;

    private ITexture BlockTexture, CrosshairTexture, EaglesTexture;
    private BitmapTextureAtlas mFontTexture;
    private Font mFont;

    private float gameLength;

    SharedPreferences myPrefs;
    private ITextureRegion BlockRegion, CrosshairRegion, EaglesRegion;

    public SpreadEaglesActivity() {
        gameLength = GAME_LENGTH;
        trashBin = new ArrayList<IEntity>(0);
        buildingList = new SmartList<Building>(0);
        currentNumOfEagles = 0;
        highestNumOfEagles = 0;
    }

    public int getCAMERA_WIDTH(){
        return CAMERA_WIDTH;
    }

    public int getCAMERA_HEIGHT() {
        return CAMERA_HEIGHT;
    }

    public float getEAGLE_SPEED(){return EAGLE_SPEED;}

    public float getEAGLE_COLLISION(){return EAGLE_COLLISION;}

    public float getMINIMUM_BUILDING_WIDTH(){return MINIMUM_BUILDING_WIDTH;}

    public float getMAXIMUM_BUILDING_WIDTH(){return MAXIMUM_BUILDING_WIDTH;}

    public float getFOOTER_SPACE(){return FOOTER_SPACE;}

    public float getBUILDING_SPEED(){return BUILDING_SPEED;}

    public Crosshair getPlayer(){
        return mPlayer;
    }

    public void cleanUp(){
        Log.v("TrashBin:", "Size " + trashBin.size());
        trashBin.trimToSize();
        for(int i = 0; i < trashBin.size(); i++){
            mScene.detachChild(trashBin.get(i));
            trashBin.get(i).dispose();
        }
        trashBin.clear();
        trashBin.trimToSize();
        Log.v("TrashBin:", "Size " + trashBin.size());
    }

    public void checkEagleMax(int current){
        if (highestNumOfEagles < current)
            highestNumOfEagles = current;
    }

    public void addEagle(){
        currentNumOfEagles++;
    }

    public void subtractEagle(){
        currentNumOfEagles--;
    }

    public boolean removeMe(Building deadBuilding){
        boolean completed = false;
        int index = buildingList.indexOf(deadBuilding);
        if(index > -1){
            completed = (deadBuilding == buildingList.remove(index));
        }
        Log.v("BuildingList","size: " + buildingList.size());
        return completed;
    }

    public void addToList(IEntity entity){
        trashBin.add(entity);
    }

    public ArrayList<Building> getActiveBuildings(){
        buildingList.trimToSize();
        return buildingList;
    }

    public void addBuilding(Building childBuilding){
        buildingList.add(childBuilding);
    }

    public void attachBreakablesWithZ(ArrayList<Breakables> listObj,int zDepth){
        for(int i = 0; i < listObj.size(); i++){
            mScene.attachChildWithZ(listObj.get(i), zDepth);
        }
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

        this.mFontTexture = new BitmapTextureAtlas(mEngine.getTextureManager(),256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        this.mFont = new Font(this.getFontManager(),this.mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.YELLOW);

        this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
        this.getFontManager().loadFont(this.mFont);
    }

      @Override
    protected Scene onCreateScene() {

        mScene = new ZControlledScene();

        mBackground = new Background(0, 0, 0, 1);

        mScene.setBackground(mBackground);

        mPlayer = new Crosshair((CAMERA_WIDTH/4-32),(CAMERA_HEIGHT/1.5f-32),CrosshairRegion,getVertexBufferObjectManager(),this);

        mScene.attachChildWithZ(mPlayer, 2);

        mScene.setOnSceneTouchListener(new IOnSceneTouchListener() {
            @Override
            public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
                Eagle EagleBuffer;
                if (touchEvent.isActionDown()) {
                    mPlayer.setPosition(touchEvent.getX(), touchEvent.getY());
                    EagleBuffer = new Eagle(CAMERA_WIDTH/2,CAMERA_HEIGHT,EaglesRegion,getVertexBufferObjectManager(),SpreadEaglesActivity.this,touchEvent.getX(),touchEvent.getY());
                    mScene.attachChildWithZ(EagleBuffer,1);
                }
                return true;
            }
        });




        mScene.registerUpdateHandler(new IUpdateHandler() {
            int count = 0;
            Random rand = new Random();

            @Override
            public void onUpdate(float time){
                gameLength--;
                count++;

                if(count == INTERVAL_BETWEEN_BUILDINGS){
                    count = 0;
                    Building BuildingBuffer = new Building(CAMERA_WIDTH, (CAMERA_HEIGHT - MAXIMUM_BUILDING_HEIGHT + (rand.nextInt(BUILDING_HEIGHT_OPTIONS)*BUILDING_HEIGHT_INTERVALS)),BlockRegion,getVertexBufferObjectManager(),SpreadEaglesActivity.this);
                    addBuilding(BuildingBuffer);
                    mScene.attachChildWithZ(BuildingBuffer, -1);
                    attachBreakablesWithZ(BuildingBuffer.getBreakables(), 0);
                }

                if(gameLength < 0) {
                    //mPlayer.finalCount();
                    if (true)
                        myPrefs.edit().putBoolean("leftFinished", true).commit();
                    else
                        myPrefs.edit().putBoolean("leftFinished", false).commit();
                    gameLength = GAME_LENGTH;
                    Intent intent = new Intent(SpreadEaglesActivity.this, Main.class);
                    Log.v("Final max Eagles","" + highestNumOfEagles);
                    startActivity(intent);
                    finish();
                }
                checkEagleMax(currentNumOfEagles);
                cleanUp();
            }

            @Override
            public void reset(){}

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


}
