package io.github.awornl;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class Main extends ApplicationAdapter {
    public static final float V_WIDTH  = 1280f;
    public static final float V_HEIGHT = 720f;

    public static final int STATE_MENU    = 0;
    public static final int STATE_GAME    = 1;
    public static final int STATE_GUIDE   = 2;

    int   currentState = STATE_MENU;

    boolean guideCompleted = false;
    boolean paused = false;

    SpriteBatch     batch;
    ExtendViewport  viewport;
    GameState       gameState;
    Renderer        renderer;
    InputHandler    inputHandler;
    Assets          assets;
    MenuScreen      menuScreen;
    GuideScreen     guideScreen;
    PauseScreen     pauseScreen;

    @Override
    public void create() {
        batch    = new SpriteBatch();
        viewport = new ExtendViewport(V_WIDTH, V_HEIGHT);
        viewport.apply(true);

        assets = new Assets();
        assets.loadAll();

        Preferences prefs = Gdx.app.getPreferences("cookie_save");
        guideCompleted = prefs.getBoolean("guideCompleted", false);

        menuScreen = new MenuScreen(assets, viewport, this);
        Gdx.input.setInputProcessor(menuScreen);
    }

    public void startGame() {
        if (gameState == null) {
            gameState = new GameState();
            Building[] buildings = new Building[]{
                new Building("Cursor",  "Auto-clicks the cookie",     assets.iconCursor,   15L,     0.1),
                new Building("Grandma", "Bakes cookies with love",    assets.iconGrandma,  100L,    0.8),
                new Building("Mine",    "Mines cookie dough",         assets.iconMine,     500L,    4.0),
                new Building("Factory", "Mass produces cookies",      assets.iconFactory,  3000L,   15.0),
                new Building("Ship",    "Ships cookies across space", assets.iconShip,     20000L,  80.0),
                new Building("Portal",  "Opens cookie dimension",     assets.iconPortal,   200000L, 500.0),
            };
            gameState.setBuildings(buildings);

            gameState.load();

            renderer     = new Renderer(batch, assets, gameState, viewport);
            inputHandler = new InputHandler(gameState, assets, viewport, this);
            renderer.setInputHandler(inputHandler);
            pauseScreen  = new PauseScreen(assets, viewport, this);
        }

        if (!guideCompleted) {
            currentState = STATE_GUIDE;
            guideScreen  = new GuideScreen(assets, viewport, this);
            Gdx.input.setInputProcessor(guideScreen);
        } else {
            enterGame();
        }
    }

    public void enterGame() {
        currentState = STATE_GAME;
        paused = false;
        Gdx.input.setInputProcessor(inputHandler);
    }

    public void completeGuide() {
        guideCompleted = true;

        Preferences prefs = Gdx.app.getPreferences("cookie_save");
        prefs.putBoolean("guideCompleted", true);
        prefs.flush();

        enterGame();
    }

    public void goToMenu() {
        currentState = STATE_MENU;
        paused = false;
        Gdx.input.setInputProcessor(menuScreen);
    }

    public void setPaused(boolean p) {
        paused = p;
        if (!p) Gdx.input.setInputProcessor(inputHandler);
    }

    public boolean isPaused() {
        return paused;
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

        ScreenUtils.clear(0.05f, 0.04f, 0.12f, 1f);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        if (currentState == STATE_MENU) {
            menuScreen.render(delta);
        } else if (currentState == STATE_GUIDE) {
            guideScreen.render(delta);
        } else {
            renderer.updateWorldSize(worldW, worldH);
            inputHandler.updateWorldSize(worldW, worldH);

            if (!paused) {
                gameState.update(delta);

                gameState.autoSaveTimer += delta;

                if (gameState.autoSaveTimer >= 5f) {
                    gameState.autoSaveTimer = 0f;
                    gameState.save();
                }

                inputHandler.update(delta);
            }

            renderer.render(delta);

            if (paused) {
                pauseScreen.render(delta);
            }
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        float ww = viewport.getWorldWidth();
        float wh = viewport.getWorldHeight();
        if (menuScreen  != null) menuScreen.resize();
        if (guideScreen != null) guideScreen.resize();
        if (renderer    != null) renderer.updateWorldSize(ww, wh);
        if (inputHandler!= null) inputHandler.updateWorldSize(ww, wh);
        if (pauseScreen != null) pauseScreen.resize(ww, wh);
    }

    @Override
    public void dispose() {

        if (gameState != null) {
            gameState.save();
        }

        batch.dispose();
        assets.disposeAll();
    }
}
