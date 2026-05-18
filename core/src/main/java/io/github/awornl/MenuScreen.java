package io.github.awornl;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuScreen extends InputAdapter {

    Assets   assets;
    Viewport viewport;
    Main     main;

    boolean showSettings = false;

    float worldW;
    float worldH;

    Rectangle playButton;
    Rectangle settingsButton;
    Rectangle backButton;

    Rectangle soundSliderRect;
    Rectangle musicSliderRect;

    Rectangle clickSound0;
    Rectangle clickSound1;
    Rectangle clickSound2;

    boolean draggingSound = false;
    boolean draggingMusic = false;

    Vector3 touchVec = new Vector3();

    Color colGold = new Color(1f, 0.85f, 0.1f, 1f);
    Color colSilver = new Color(0.8f, 0.8f, 0.9f, 1f);

    float animTimer = 0f;

    public MenuScreen(Assets assets, Viewport viewport, Main main) {
        this.assets   = assets;
        this.viewport = viewport;
        this.main     = main;
        worldW = viewport.getWorldWidth();
        worldH = viewport.getWorldHeight();
        if (worldW < 1f) worldW = Main.V_WIDTH;
        if (worldH < 1f) worldH = Main.V_HEIGHT;
        buildLayout();
    }

    public void resize() {
        worldW = viewport.getWorldWidth();
        worldH = viewport.getWorldHeight();
        buildLayout();
    }

    void buildLayout() {
        float cx = worldW / 2f;
        float cy = worldH / 2f;

        playButton     = new Rectangle(cx - 150f, cy + 20f,  300f, 70f);
        settingsButton = new Rectangle(cx - 150f, cy - 70f,  300f, 70f);
        backButton     = new Rectangle(cx - 100f, 30f,       200f, 50f);

        float sliderW = 300f;
        float sliderX = cx - sliderW / 2f;
        soundSliderRect = new Rectangle(sliderX, cy + 60f, sliderW, 16f);
        musicSliderRect = new Rectangle(sliderX, cy - 30f, sliderW, 16f);

        float btnW = 130f;
        float btnH = 50f;
        float bx = cx - (btnW * 1.5f + 10f);
        clickSound0 = new Rectangle(bx,              cy - 110f, btnW, btnH);
        clickSound1 = new Rectangle(bx + btnW + 10f, cy - 110f, btnW, btnH);
        clickSound2 = new Rectangle(bx + btnW * 2f + 20f, cy - 110f, btnW, btnH);
    }

    public void render(float delta) {
        worldW = viewport.getWorldWidth();
        worldH = viewport.getWorldHeight();
        animTimer += delta;

        batch().draw(assets.bg, 0, 0, worldW, worldH);

        float alpha = (float)(Math.sin(animTimer * 1.5f) * 0.06f + 0.94f);
        batch().setColor(1, 1, 1, alpha);

        assets.fontTitle.setColor(colGold);
        String title = "flyin";
        assets.fontTitle.draw(batch(), title, worldW / 2f - 60f, worldH - 100f);
        batch().setColor(1, 1, 1, 1);

        assets.fontMedium.setColor(colSilver);
        assets.fontMedium.draw(batch(), "Cookie Clicker", worldW / 2f - 100f, worldH - 155f);
        assets.fontMedium.setColor(Color.WHITE);

        if (!showSettings) {
            drawMenuMain();
        } else {
            drawMenuSettings();
        }
    }

    void drawMenuMain() {
        drawBtn(playButton, "PLAY");
        drawBtn(settingsButton, "SETTINGS");
    }

    void drawMenuSettings() {
        float cx = worldW / 2f;
        float cy = worldH / 2f;

        assets.fontMedium.setColor(colGold);
        assets.fontMedium.draw(batch(), "SETTINGS", cx - 70f, cy + 130f);
        assets.fontMedium.setColor(Color.WHITE);

        assets.fontSmall.setColor(Color.WHITE);
        assets.fontSmall.draw(batch(), "Sound volume:", soundSliderRect.x, soundSliderRect.y + 30f);
        drawSlider(soundSliderRect, assets.soundVolume);

        assets.fontSmall.draw(batch(), "Music volume:", musicSliderRect.x, musicSliderRect.y + 30f);
        drawSlider(musicSliderRect, assets.musicVolume);

        assets.fontSmall.draw(batch(), "Click sound:", clickSound0.x, clickSound0.y + 70f);

        drawClickSoundBtn(clickSound0, "Classic", 0);
        drawClickSoundBtn(clickSound1, "Crunch",     1);
        drawClickSoundBtn(clickSound2, "Pop",    2);

        drawBtn(backButton, "BACK");
    }

    void drawBtn(Rectangle r, String text) {
        batch().setColor(0.14f, 0.12f, 0.28f, 0.95f);
        batch().draw(assets.buttonNormal, r.x, r.y, r.width, r.height);
        batch().setColor(1, 1, 1, 1);
        assets.fontMedium.setColor(colGold);
        assets.fontMedium.draw(batch(), text, r.x + r.width / 2f - text.length() * 5f, r.y + r.height / 2f + 10f);
        assets.fontMedium.setColor(Color.WHITE);
    }

    void drawSlider(Rectangle r, float value) {
        batch().setColor(0.2f, 0.18f, 0.35f, 1f);
        batch().draw(assets.sliderBg, r.x, r.y, r.width, r.height);

        float filledW = r.width * value;
        batch().setColor(colGold);
        batch().draw(assets.progressBarFill, r.x, r.y, filledW, r.height);

        float knobX = r.x + filledW - 12f;
        float knobY = r.y + r.height / 2f - 14f;
        batch().setColor(1, 1, 1, 1);
        batch().draw(assets.sliderKnob, knobX, knobY, 28f, 28f);
    }

    void drawClickSoundBtn(Rectangle r, String label, int idx) {
        boolean selected = assets.clickSoundChoice == idx;
        if (selected) batch().setColor(0.3f, 0.6f, 0.3f, 1f);
        else          batch().setColor(0.14f, 0.12f, 0.28f, 0.95f);
        batch().draw(assets.buttonNormal, r.x, r.y, r.width, r.height);
        batch().setColor(1, 1, 1, 1);
        assets.fontSmall.setColor(selected ? Color.WHITE : colSilver);
        assets.fontSmall.draw(batch(), label, r.x + 20f, r.y + r.height / 2f + 8f);
        assets.fontSmall.setColor(Color.WHITE);
    }

    com.badlogic.gdx.graphics.g2d.SpriteBatch batch() {
        return main.batch;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchVec.set(screenX, screenY, 0);
        viewport.unproject(touchVec);
        float tx = touchVec.x;
        float ty = touchVec.y;

        if (!showSettings) {
            if (playButton.contains(tx, ty)) {
                assets.playClickSound();
                main.startGame();
                return true;
            }
            if (settingsButton.contains(tx, ty)) {
                assets.playClickSound();
                showSettings = true;
                return true;
            }
        } else {
            if (backButton.contains(tx, ty)) {
                assets.playClickSound();
                showSettings = false;
                return true;
            }
            if (clickSound0.contains(tx, ty)) {
                assets.clickSoundChoice = 0;
                assets.playClickSound();
                return true;
            }
            if (clickSound1.contains(tx, ty)) {
                assets.clickSoundChoice = 1;
                assets.playClickSound();
                return true;
            }
            if (clickSound2.contains(tx, ty)) {
                assets.clickSoundChoice = 2;
                assets.playClickSound();
                return true;
            }
            if (soundSliderRect.contains(tx, ty)) {
                draggingSound = true;
                updateSoundSlider(tx);
                return true;
            }
            if (musicSliderRect.contains(tx, ty)) {
                draggingMusic = true;
                updateMusicSlider(tx);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        touchVec.set(screenX, screenY, 0);
        viewport.unproject(touchVec);
        if (draggingSound) updateSoundSlider(touchVec.x);
        if (draggingMusic) updateMusicSlider(touchVec.x);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        draggingSound = false;
        draggingMusic = false;
        return false;
    }

    void updateSoundSlider(float tx) {
        float raw = (tx - soundSliderRect.x) / soundSliderRect.width;
        assets.soundVolume = Math.max(0f, Math.min(1f, raw));
    }

    void updateMusicSlider(float tx) {
        float raw = (tx - musicSliderRect.x) / musicSliderRect.width;
        assets.musicVolume = Math.max(0f, Math.min(1f, raw));
        assets.applyMusicVolume();
    }
}
