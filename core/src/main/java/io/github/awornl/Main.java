package io.github.awornl;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class Main extends ApplicationAdapter {

    public static final float V_WIDTH  = 1280f;
    public static final float V_HEIGHT = 720f;

    SpriteBatch     batch;
    ExtendViewport  viewport;
    GameState       gameState;
    Renderer        renderer;
    InputHandler    inputHandler;
    Assets          assets;

    @Override
    public void create() {
        batch    = new SpriteBatch();
        viewport = new ExtendViewport(V_WIDTH, V_HEIGHT);
        viewport.apply(true);

        assets = new Assets();
        assets.loadAll();

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

        renderer     = new Renderer(batch, assets, gameState, viewport);
        inputHandler = new InputHandler(gameState, assets, viewport);
        renderer.setInputHandler(inputHandler);
        Gdx.input.setInputProcessor(inputHandler);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();
        renderer.updateWorldSize(worldW, worldH);
        inputHandler.updateWorldSize(worldW, worldH);

        gameState.update(delta);
        inputHandler.update(delta);

        ScreenUtils.clear(0.05f, 0.04f, 0.12f, 1f);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        renderer.render(delta);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        assets.disposeAll();
    }
}
