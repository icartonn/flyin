package io.github.awornl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class InputHandler extends InputAdapter {

    GameState gameState;
    Assets    assets;
    Viewport  viewport;
    Main      main;

    static final float PANEL_W   = 310f;
    static final float BTN_W     = 290f;
    static final float BTN_H     = 80f;
    static final float BTN_GAP   = 8f;
    static final float PAUSE_BTN = 44f;

    Circle    cookieCircle;
    float     cookieX, cookieY, cookieSize = 220f;

    Rectangle[] buyButtons;
    Rectangle   prestigeButton;
    Rectangle   pauseButton;

    public float[]  buttonHoverTimers;
    public boolean  cookieHovered = false;
    public float    cookieSquishTimer = 0f;
    public float    cookieSquish      = 1f;

    float worldW, worldH;

    Vector3 touchVec = new Vector3();

    public InputHandler(GameState gameState, Assets assets, Viewport viewport, Main main) {
        this.gameState = gameState;
        this.assets    = assets;
        this.viewport  = viewport;
        this.main      = main;

        int buildingCount = 6;
        buyButtons        = new Rectangle[buildingCount];
        buttonHoverTimers = new float[buildingCount];

        worldW = Main.V_WIDTH;
        worldH = Main.V_HEIGHT;
        rebuildLayout();
    }

    public void updateWorldSize(float newW, float newH) {
        if (Math.abs(newW - worldW) > 0.5f || Math.abs(newH - worldH) > 0.5f) {
            worldW = newW;
            worldH = newH;
            rebuildLayout();
        }
    }

    void rebuildLayout() {
        float panelRightX = worldW - PANEL_W;

        float areaLeft  = PANEL_W;
        float areaRight = panelRightX;
        float cx = (areaLeft + areaRight) / 2f;
        float cy = worldH / 2f;
        cookieX = cx - cookieSize / 2f;
        cookieY = cy - cookieSize / 2f;
        cookieCircle = new Circle(cx, cy, cookieSize / 2f);

        float startY = worldH - 170f;
        for (int i = 0; i < buyButtons.length; i++) {
            buyButtons[i] = new Rectangle(
                panelRightX + 10f,
                startY - i * (BTN_H + BTN_GAP),
                BTN_W, BTN_H
            );
        }
        prestigeButton = new Rectangle(panelRightX + 10f, 20f, BTN_W, 50f);
        pauseButton    = new Rectangle(PANEL_W + 8f, worldH - PAUSE_BTN - 8f, PAUSE_BTN, PAUSE_BTN);
    }

    public void update(float delta) {
        if (cookieSquishTimer > 0) {
            cookieSquishTimer -= delta;
            float p = 1f - (cookieSquishTimer / 0.12f);
            cookieSquish = 0.87f + p * 0.13f;
            if (cookieSquishTimer <= 0) cookieSquish = 1f;
        }

        for (int i = 0; i < buttonHoverTimers.length; i++) {
            if (buttonHoverTimers[i] > 0) buttonHoverTimers[i] -= delta;
        }

        touchVec.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(touchVec);
        cookieHovered = cookieCircle != null && cookieCircle.contains(touchVec.x, touchVec.y);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchVec.set(screenX, screenY, 0);
        viewport.unproject(touchVec);
        float tx = touchVec.x;
        float ty = touchVec.y;

        if (main.isPaused()) {
            main.pauseScreen.touchDown(tx, ty);
            return true;
        }

        if (pauseButton != null && pauseButton.contains(tx, ty)) {
            assets.playClickSound();
            main.setPaused(true);
            return true;
        }

        if (cookieCircle != null && cookieCircle.contains(tx, ty)) {
            gameState.clickCookie(tx, ty);
            cookieSquishTimer = 0.12f;
            assets.playClickSound();
            return true;
        }

        if (gameState.goldenCookie.visible && gameState.goldenCookie.contains(tx, ty)) {
            gameState.clickGoldenCookie();
            assets.goldenSound.play(assets.soundVolume);
            return true;
        }

        for (int i = 0; i < buyButtons.length; i++) {
            if (buyButtons[i] != null && buyButtons[i].contains(tx, ty)) {
                if (gameState.tryBuy(i)) {
                    assets.buySound.play(assets.soundVolume);
                    buttonHoverTimers[i] = 0.15f;
                }
                return true;
            }
        }

        if (prestigeButton != null && prestigeButton.contains(tx, ty)) {
            if (gameState.tryPrestige()) {
                assets.milestoneSound.play(assets.soundVolume);
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (main.isPaused()) {
            touchVec.set(screenX, screenY, 0);
            viewport.unproject(touchVec);
            main.pauseScreen.touchDragged(touchVec.x, touchVec.y);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (main.isPaused()) {
            main.pauseScreen.touchUp();
        }
        return false;
    }

    public float     getCookieX()           { return cookieX; }
    public float     getCookieY()           { return cookieY; }
    public float     getCookieSize()        { return cookieSize; }
    public float     getWorldW()            { return worldW; }
    public float     getWorldH()            { return worldH; }
    public Rectangle[] getBuyButtons()      { return buyButtons; }
    public Rectangle   getPrestigeButton()  { return prestigeButton; }
    public Rectangle   getPauseButton()     { return pauseButton; }
}
