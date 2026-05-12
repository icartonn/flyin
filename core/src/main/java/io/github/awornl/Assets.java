package io.github.awornl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Assets {

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
    public Texture coinIcon;
    public Texture critIcon;
    public Texture milestoneIcon;
    public Texture prestige;
    public Texture golden;
    public Texture goldenGlow;
    public Texture[] sparkles;

    public BitmapFont fontSmall;
    public BitmapFont fontMedium;
    public BitmapFont fontBig;
    public BitmapFont fontTitle;

    public Sound clickNormal;
    public Sound clickCrit;
    public Sound buySound;
    public Sound milestoneSound;
    public Sound goldenSound;
    public Music bgMusic;

    public void loadAll() {
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
        coinIcon      = tex("coin.png");
        critIcon      = tex("crit.png");
        milestoneIcon = tex("milestone.png");
        prestige      = tex("prestige.png");
        golden        = tex("golden_cookie.png");
        goldenGlow    = tex("golden_glow.png");

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
        clickCrit     = Gdx.audio.newSound(Gdx.files.internal("click_crit.wav"));
        buySound      = Gdx.audio.newSound(Gdx.files.internal("buy.wav"));
        milestoneSound= Gdx.audio.newSound(Gdx.files.internal("milestone.wav"));
        goldenSound   = Gdx.audio.newSound(Gdx.files.internal("golden.wav"));

        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        bgMusic.setLooping(true);
        bgMusic.setVolume(0.35f);
        bgMusic.play();
    }

    Texture tex(String name) {
        FileHandle local = Gdx.files.local(name);
        if (local.exists()) {
            return new Texture(local);
        }
        return new Texture(Gdx.files.internal(name));
    }

    public void disposeAll() {
        bg.dispose(); bgStars.dispose();
        cookie.dispose(); cookieGlow.dispose(); cookieParticle.dispose();
        panelLeft.dispose(); panelRight.dispose();
        buttonNormal.dispose(); buttonHover.dispose(); buttonLocked.dispose();
        progressBarBg.dispose(); progressBarFill.dispose();
        iconCursor.dispose(); iconGrandma.dispose(); iconFactory.dispose();
        iconMine.dispose(); iconShip.dispose(); iconPortal.dispose();
        coinIcon.dispose(); critIcon.dispose(); milestoneIcon.dispose();
        prestige.dispose(); golden.dispose(); goldenGlow.dispose();
        for (Texture t : sparkles) t.dispose();
        fontSmall.dispose(); fontMedium.dispose(); fontBig.dispose(); fontTitle.dispose();
        clickNormal.dispose(); clickCrit.dispose(); buySound.dispose();
        milestoneSound.dispose(); goldenSound.dispose();
        bgMusic.dispose();
    }
}
