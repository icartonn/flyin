package io.github.awornl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;

public class AssetScaler {

    static final int VW = (int) Main.V_WIDTH;
    static final int VH = (int) Main.V_HEIGHT;

    static final int BG_W       = VW;
    static final int BG_H       = VH;
    static final int COOKIE     = VH / 3;
    static final int GLOW       = VH / 2;
    static final int PANEL_W    = VW / 4;
    static final int PANEL_H    = VH;
    static final int BTN_W      = (VW / 4) - 20;
    static final int BTN_H      = VH / 9;
    static final int ICON       = 64;
    static final int SMALL_ICON = 36;
    static final int SPARKLE    = 32;
    static final int PARTICLE   = 16;
    static final int GOLDEN     = 80;
    static final int BAR_W      = BTN_W;
    static final int BAR_H      = 8;
    static final int STARS_MIN  = 128;
    static final int SLIDER_W   = 300;
    static final int SLIDER_H   = 16;
    static final int KNOB       = 32;

    public static void scaleAll() {
        scale("bg.png",            BG_W,    BG_H);
        scaleMini("bg_stars.png",  STARS_MIN);

        scale("cookie.png",        COOKIE,  COOKIE);
        scale("cookie_glow.png",   GLOW,    GLOW);
        scale("golden_cookie.png", GOLDEN,  GOLDEN);
        scale("golden_glow.png",   GLOW/2,  GLOW/2);

        scale("panel_left.png",    PANEL_W, PANEL_H);
        scale("panel_right.png",   PANEL_W, PANEL_H);

        scale("button_normal.png", BTN_W,   BTN_H);
        scale("button_hover.png",  BTN_W,   BTN_H);
        scale("button_locked.png", BTN_W,   BTN_H);

        scale("bar_bg.png",        BAR_W,   BAR_H);
        scale("bar_fill.png",      BAR_W,   BAR_H);

        scale("icon_cursor.png",   ICON,    ICON);
        scale("icon_grandma.png",  ICON,    ICON);
        scale("icon_factory.png",  ICON,    ICON);
        scale("icon_mine.png",     ICON,    ICON);
        scale("icon_ship.png",     ICON,    ICON);
        scale("icon_portal.png",   ICON,    ICON);

        scale("coin.png",          SMALL_ICON, SMALL_ICON);
        scale("crit.png",          SMALL_ICON, SMALL_ICON);
        scale("milestone.png",     SMALL_ICON, SMALL_ICON);
        scale("prestige.png",      SMALL_ICON, SMALL_ICON);
        scale("particle.png",      PARTICLE,   PARTICLE);

        scale("sparkle1.png",      SPARKLE, SPARKLE);
        scale("sparkle2.png",      SPARKLE, SPARKLE);
        scale("sparkle3.png",      SPARKLE, SPARKLE);
        scale("sparkle4.png",      SPARKLE, SPARKLE);

        scale("slider_bg.png",     SLIDER_W, SLIDER_H);
        scale("slider_knob.png",   KNOB,     KNOB);
    }

    static void scale(String name, int targetW, int targetH) {
        FileHandle file = resolveReadable(name);
        if (file == null) {
            Gdx.app.log("AssetScaler", "MISSING: " + name);
            return;
        }

        Pixmap src = new Pixmap(file);
        if (src.getWidth() == targetW && src.getHeight() == targetH) {
            src.dispose();
            return;
        }

        Gdx.app.log("AssetScaler", name + ": " + src.getWidth() + "x" + src.getHeight()
            + " -> " + targetW + "x" + targetH);

        Pixmap dst = new Pixmap(targetW, targetH, src.getFormat());
        dst.setFilter(Pixmap.Filter.BiLinear);
        dst.drawPixmap(src, 0, 0, src.getWidth(), src.getHeight(), 0, 0, targetW, targetH);

        PixmapIO.writePNG(Gdx.files.local(name), dst);
        src.dispose();
        dst.dispose();
    }

    static void scaleMini(String name, int minSize) {
        FileHandle file = resolveReadable(name);
        if (file == null) {
            Gdx.app.log("AssetScaler", "MISSING: " + name);
            return;
        }

        Pixmap src = new Pixmap(file);
        int w = src.getWidth();
        int h = src.getHeight();

        if (w >= minSize && h >= minSize) {
            src.dispose();
            return;
        }

        int newW = Math.max(w, minSize);
        int newH = Math.max(h, minSize);
        Gdx.app.log("AssetScaler", name + " too small: " + w + "x" + h + " -> " + newW + "x" + newH);

        Pixmap dst = new Pixmap(newW, newH, src.getFormat());
        dst.setFilter(Pixmap.Filter.BiLinear);
        dst.drawPixmap(src, 0, 0, w, h, 0, 0, newW, newH);

        PixmapIO.writePNG(Gdx.files.local(name), dst);
        src.dispose();
        dst.dispose();
    }

    static FileHandle resolveReadable(String name) {
        FileHandle local = Gdx.files.local(name);
        if (local.exists()) return local;
        FileHandle internal = Gdx.files.internal(name);
        if (internal.exists()) return internal;
        return null;
    }
}
