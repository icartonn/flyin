package io.github.awornl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Assets {

    public Texture arrow;
    public Texture bg;
    public Texture bgStars;
    public Texture cookie;
    public Texture cookieGlow;
    public Texture cookieParticle;
    public Texture panelLeft;
    public Texture panelRight;
    public Texture buttonNormal;
    public Texture buttonHover;
    public Texture buttonLocked;
    public Texture progressBarBg;
    public Texture progressBarFill;
    public Texture iconCursor;
    public Texture iconGrandma;
    public Texture iconFactory;
    public Texture iconMine;
    public Texture iconShip;
    public Texture iconPortal;
    public Texture iconTime;
    public Texture iconAntimatter;
    public Texture iconPrism;
    public Texture iconChance;
    public Texture iconFractal;
    public Texture iconJs;
    public Texture coinIcon;
    public Texture critIcon;
    public Texture milestoneIcon;
    public Texture prestige;
    public Texture golden;
    public Texture goldenGlow;
    public Texture sliderBg;
    public Texture sliderKnob;
    public Texture[] sparkles;
    public Texture whitePixel;
    public Texture tutorialGradient;

    public BitmapFont fontSmall;
    public BitmapFont fontMedium;
    public BitmapFont fontBig;
    public BitmapFont fontTitle;

    public Sound clickNormal;
    public Sound clickPop;
    public Sound clickDing;
    public Sound clickCrit;
    public Sound buySound;
    public Sound milestoneSound;
    public Sound goldenSound;
    public Music bgMusic;

    public float soundVolume = 0.7f;
    public float musicVolume = 0.35f;
    public int   clickSoundChoice = 0;

    public void loadAll() {
        loadSettings();
        AssetScaler.scaleAll();

        bg            = tex("bg.png");
        bgStars       = tex("bg_stars.png");
        cookie        = tex("cookie.png");
        cookieGlow    = tex("cookie_glow.png");
        cookieParticle= tex("particle.png");
        panelLeft     = tex("panel_left.png");
        panelRight    = tex("panel_right.png");
        buttonNormal  = tex("button_normal.png");
        buttonHover   = tex("button_hover.png");
        buttonLocked  = tex("button_locked.png");
        progressBarBg   = tex("bar_bg.png");
        progressBarFill = tex("bar_fill.png");
        iconCursor    = tex("icon_cursor.png");
        iconGrandma   = tex("icon_grandma.png");
        iconFactory   = tex("icon_factory.png");
        iconMine      = tex("icon_mine.png");
        iconShip      = tex("icon_ship.png");
        iconPortal    = tex("icon_portal.png");
        iconTime      = tex("icon_time.png");
        iconAntimatter= tex("icon_antimatter.png");
        iconPrism     = tex("icon_prism.png");
        iconChance    = tex("icon_chance.png");
        iconFractal   = tex("icon_fractal.png");
        iconJs        = tex("icon_js.png");
        coinIcon      = tex("coin.png");
        critIcon      = tex("crit.png");
        milestoneIcon = tex("milestone.png");
        prestige      = tex("prestige.png");
        golden        = tex("golden_cookie.png");
        goldenGlow    = tex("golden_glow.png");
        sliderBg      = tex("slider_bg.png");
        sliderKnob    = tex("slider_knob.png");

        sparkles = new Texture[4];
        sparkles[0] = tex("sparkle1.png");
        sparkles[1] = tex("sparkle2.png");
        sparkles[2] = tex("sparkle3.png");
        sparkles[3] = tex("sparkle4.png");

        fontSmall  = new BitmapFont(Gdx.files.internal("font_small.fnt"));
        fontMedium = new BitmapFont(Gdx.files.internal("font_medium.fnt"));
        fontBig    = new BitmapFont(Gdx.files.internal("font_big.fnt"));
        fontTitle  = new BitmapFont(Gdx.files.internal("font_title.fnt"));

        fontSmall.getData().setScale(1f);
        fontMedium.getData().setScale(1f);
        fontBig.getData().setScale(1f);
        fontTitle.getData().setScale(1f);

        clickNormal   = Gdx.audio.newSound(Gdx.files.internal("click.wav"));
        clickPop      = Gdx.audio.newSound(Gdx.files.internal("click2.wav"));
        clickDing     = Gdx.audio.newSound(Gdx.files.internal("click3.wav"));
        clickCrit     = Gdx.audio.newSound(Gdx.files.internal("click_crit.wav"));
        buySound      = Gdx.audio.newSound(Gdx.files.internal("buy.wav"));
        milestoneSound= Gdx.audio.newSound(Gdx.files.internal("milestone.wav"));
        goldenSound   = Gdx.audio.newSound(Gdx.files.internal("golden.wav"));

        arrow = tex("arrow.png");
        if (arrow == null) {
            arrow = createArrowTexture();
        }

        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        bgMusic.setLooping(true);
        bgMusic.setVolume(musicVolume);
        bgMusic.play();

        Pixmap px = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        px.setColor(1, 1, 1, 1);
        px.fill();
        whitePixel = new Texture(px);
        px.dispose();

        createTutorialGradient();
    }

    private void createTutorialGradient() {
        int h = 256;
        Pixmap px = new Pixmap(1, h, Pixmap.Format.RGBA8888);
        for (int i = 0; i < h; i++) {
            float alpha = (float) i / (h - 1) * 0.85f;
            px.setColor(0, 0, 0, alpha);
            px.drawPixel(0, i);
        }
        tutorialGradient = new Texture(px);
        px.dispose();
    }

    private Texture createArrowTexture() {
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fillTriangle(16, 4, 4, 28, 28, 28);
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }

    public void playClickSound() {
        if (clickSoundChoice == 0) clickNormal.play(soundVolume);
        else if (clickSoundChoice == 1) clickPop.play(soundVolume);
        else clickDing.play(soundVolume);
    }

    public void applyMusicVolume() {
        if (bgMusic != null) bgMusic.setVolume(musicVolume);
    }

    public void saveSettings() {
        Preferences prefs = Gdx.app.getPreferences("settings");
        prefs.putFloat("soundVolume", soundVolume);
        prefs.putFloat("musicVolume", musicVolume);
        prefs.putInteger("clickSoundChoice", clickSoundChoice);
        prefs.flush();
    }

    public void loadSettings() {
        Preferences prefs = Gdx.app.getPreferences("settings");
        soundVolume = prefs.getFloat("soundVolume", 0.7f);
        musicVolume = prefs.getFloat("musicVolume", 0.35f);
        clickSoundChoice = prefs.getInteger("clickSoundChoice", 0);
    }

    Texture tex(String name) {
        FileHandle local = Gdx.files.local(name);
        if (local.exists()) {
            return new Texture(local);
        }
        FileHandle internal = Gdx.files.internal(name);
        if (internal.exists()) return new Texture(internal);
        return null;
    }

    public void disposeAll() {
        saveSettings();
        if (bg != null) bg.dispose();
        if (bgStars != null) bgStars.dispose();
        if (cookie != null) cookie.dispose();
        if (cookieGlow != null) cookieGlow.dispose();
        if (cookieParticle != null) cookieParticle.dispose();
        if (panelLeft != null) panelLeft.dispose();
        if (panelRight != null) panelRight.dispose();
        if (buttonNormal != null) buttonNormal.dispose();
        if (buttonHover != null) buttonHover.dispose();
        if (buttonLocked != null) buttonLocked.dispose();
        if (progressBarBg != null) progressBarBg.dispose();
        if (progressBarFill != null) progressBarFill.dispose();
        if (iconCursor != null) iconCursor.dispose();
        if (iconGrandma != null) iconGrandma.dispose();
        if (iconFactory != null) iconFactory.dispose();
        if (iconMine != null) iconMine.dispose();
        if (iconShip != null) iconShip.dispose();
        if (iconPortal != null) iconPortal.dispose();
        if (iconTime != null) iconTime.dispose();
        if (iconAntimatter != null) iconAntimatter.dispose();
        if (iconPrism != null) iconPrism.dispose();
        if (iconChance != null) iconChance.dispose();
        if (iconFractal != null) iconFractal.dispose();
        if (iconJs != null) iconJs.dispose();
        if (coinIcon != null) coinIcon.dispose();
        if (critIcon != null) critIcon.dispose();
        if (milestoneIcon != null) milestoneIcon.dispose();
        if (prestige != null) prestige.dispose();
        if (golden != null) golden.dispose();
        if (goldenGlow != null) goldenGlow.dispose();
        if (sliderBg != null) sliderBg.dispose();
        if (sliderKnob != null) sliderKnob.dispose();
        if (whitePixel != null) whitePixel.dispose();
        if (tutorialGradient != null) tutorialGradient.dispose();
        if (sparkles != null) for (Texture t : sparkles) if (t != null) t.dispose();
        if (fontSmall != null) fontSmall.dispose();
        if (fontMedium != null) fontMedium.dispose();
        if (fontBig != null) fontBig.dispose();
        if (fontTitle != null) fontTitle.dispose();
        if (clickNormal != null) clickNormal.dispose();
        if (clickPop != null) clickPop.dispose();
        if (clickDing != null) clickDing.dispose();
        if (clickCrit != null) clickCrit.dispose();
        if (buySound != null) buySound.dispose();
        if (milestoneSound != null) milestoneSound.dispose();
        if (goldenSound != null) goldenSound.dispose();
        if (bgMusic != null) bgMusic.dispose();
        if (arrow != null) arrow.dispose();
    }
}
