package io.github.awornl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PauseScreen {

    Assets   assets;
    Viewport viewport;
    Main     main;

    float worldW;
    float worldH;

    Rectangle resumeButton;
    Rectangle mainMenuButton;

    Rectangle soundSliderRect;
    Rectangle musicSliderRect;

    Rectangle clickSound0;
    Rectangle clickSound1;
    Rectangle clickSound2;

    boolean draggingSound = false;
    boolean draggingMusic = false;

    Color colGold    = new Color(1f,   0.85f, 0.1f,  1f);
    Color colSilver  = new Color(0.8f, 0.8f,  0.9f,  1f);
    Color colOverlay = new Color(0f, 0f, 0f, 0.65f);

    public PauseScreen(Assets assets, Viewport viewport, Main main) {
        this.assets   = assets;
        this.viewport = viewport;
        this.main     = main;
        worldW = viewport.getWorldWidth();
        worldH = viewport.getWorldHeight();
        if (worldW < 1f) worldW = Main.V_WIDTH;
        if (worldH < 1f) worldH = Main.V_HEIGHT;
        buildLayout();
    }

    public void resize(float w, float h) {
        worldW = w;
        worldH = h;
        buildLayout();
    }

    void buildLayout() {
        float cx = worldW / 2f;
        float cy = worldH / 2f;

        resumeButton    = new Rectangle(cx - 140f, cy + 90f,  280f, 58f);
        mainMenuButton  = new Rectangle(cx - 140f, cy + 20f,  280f, 58f);

        float sliderW = 300f;
        float sliderX = cx - sliderW / 2f;
        soundSliderRect = new Rectangle(sliderX, cy - 60f,  sliderW, 16f);
        musicSliderRect = new Rectangle(sliderX, cy - 130f, sliderW, 16f);

        float bw = 130f;
        float bh = 46f;
        float bx = cx - (bw * 1.5f + 10f);
        clickSound0 = new Rectangle(bx,              cy - 202f, bw, bh);
        clickSound1 = new Rectangle(bx + bw + 10f,   cy - 202f, bw, bh);
        clickSound2 = new Rectangle(bx + bw * 2 + 20f, cy - 202f, bw, bh);
    }

    public void render(float delta) {
        worldW = viewport.getWorldWidth();
        worldH = viewport.getWorldHeight();
        batch().setColor(colOverlay);
        batch().draw(assets.bg, 0, 0, worldW, worldH);
        batch().setColor(1, 1, 1, 1);

        float cx = worldW / 2f;
        float cy = worldH / 2f;

        assets.fontTitle.setColor(colGold);
        assets.fontTitle.draw(batch(), "PAUSED", cx - 75f, cy + 200f);

        batch().setColor(colGold.r, colGold.g, colGold.b, 0.4f);
        batch().draw(assets.progressBarFill, cx - 200f, cy + 162f, 400f, 1.5f);
        batch().setColor(1, 1, 1, 1);

        drawBtn(resumeButton,   "RESUME",    new Color(0.14f, 0.52f, 0.24f, 1f));
        drawBtn(mainMenuButton, "MAIN MENU", new Color(0.14f, 0.12f, 0.28f, 0.95f));

        assets.fontSmall.setColor(Color.WHITE);
        assets.fontSmall.draw(batch(), "Sound volume:", soundSliderRect.x, soundSliderRect.y + 28f);
        drawSlider(soundSliderRect, assets.soundVolume);

        assets.fontSmall.draw(batch(), "Music volume:", musicSliderRect.x, musicSliderRect.y + 28f);
        drawSlider(musicSliderRect, assets.musicVolume);

        assets.fontSmall.setColor(Color.WHITE);
        assets.fontSmall.draw(batch(), "Click sound:", clickSound0.x, clickSound0.y + 60f);

        drawClickSoundBtn(clickSound0, "Classic", 0);
        drawClickSoundBtn(clickSound1, "Crunch",     1);
        drawClickSoundBtn(clickSound2, "Pop",    2);
    }

    void drawBtn(Rectangle r, String text, Color color) {
        batch().setColor(color);
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

    public boolean touchDown(float tx, float ty) {
        if (resumeButton.contains(tx, ty)) {
            assets.playClickSound();
            main.setPaused(false);
            return true;
        }
        if (mainMenuButton.contains(tx, ty)) {
            assets.playClickSound();
            main.setPaused(false);
            main.goToMenu();
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
        return false;
    }

    public boolean touchDragged(float tx, float ty) {
        if (draggingSound) { updateSoundSlider(tx); return true; }
        if (draggingMusic) { updateMusicSlider(tx); return true; }
        return false;
    }

    public void touchUp() {
        draggingSound = false;
        draggingMusic = false;
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
