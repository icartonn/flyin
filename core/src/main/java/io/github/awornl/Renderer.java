package io.github.awornl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Renderer {

    SpriteBatch batch;
    Assets      assets;
    GameState   gameState;
    Viewport    viewport;
    InputHandler inputHandler;

    float worldW, worldH;
    static final float PANEL_W = 310f;

    TextureRegion starsRegion;
    float         starsScrollV = 0f;
    com.badlogic.gdx.graphics.Texture whitePx;

    float cookiePulseTimer = 0f;

    Color colGold   = new Color(0.7f, 0.6f,  0.07f, 1f);
    Color colSilver = new Color(0.55f, 0.55f, 0.62f, 1f);
    Color colFrenzy = new Color(1f,   0.3f,  0.05f, 1f);
    Color colGreen  = new Color(0.2f, 0.9f,  0.4f,  1f);
    Color colDimRed = new Color(0.8f, 0.3f,  0.3f,  1f);

    public Renderer(SpriteBatch batch, Assets assets, GameState gameState, Viewport viewport) {
        this.batch    = batch;
        this.assets   = assets;
        this.gameState= gameState;
        this.viewport = viewport;

        if (assets.bgStars != null) {
            assets.bgStars.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            starsRegion = new TextureRegion(assets.bgStars);
        }

        com.badlogic.gdx.graphics.Pixmap px = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        px.setColor(1, 1, 1, 1);
        px.fill();
        whitePx = new com.badlogic.gdx.graphics.Texture(px);
        px.dispose();
    }

    public void setInputHandler(InputHandler h) { this.inputHandler = h; }

    public void updateWorldSize(float w, float h) {
        worldW = w;
        worldH = h;
    }

    public void render(float delta) {
        cookiePulseTimer += delta;

        if (assets.bgStars != null) {
            int texH = assets.bgStars.getHeight();
            starsScrollV += (20f / texH) * delta;
            if (starsScrollV > 1f) starsScrollV -= 1f;
        }

        drawBackground();
        drawCookieArea();
        drawLeftPanel();
        drawRightPanel();
        drawParticles();
        drawFloatingTexts();
        drawGoldenCookie();
        drawMilestonePopup();
        drawAchievementPopup();
        drawFrenzyOverlay();
        drawPauseButton();
    }

    void drawBackground() {
        batch.setColor(1, 1, 1, 1);
        if (assets.bg != null) batch.draw(assets.bg, 0, 0, worldW, worldH);

        if (starsRegion != null) {
            float repeatX = worldW / assets.bgStars.getWidth();
            float repeatY = worldH / assets.bgStars.getHeight();
            starsRegion.setRegion(0f, starsScrollV, repeatX, starsScrollV + repeatY);
            batch.setColor(1, 1, 1, 0.9f);
            batch.draw(starsRegion, 0, 0, worldW, worldH);
        }
        batch.setColor(1, 1, 1, 1);
    }

    void drawCookieArea() {
        if (inputHandler == null) return;
        float cx = inputHandler.getCookieX();
        float cy = inputHandler.getCookieY();
        float size = inputHandler.getCookieSize();
        float midX = cx + size / 2f;
        float midY = cy + size / 2f;

        float glowPulse = (float)(Math.sin(cookiePulseTimer * 2f) * 0.07f + 0.93f);
        float gs = size * 1.55f * glowPulse;

        if (gameState.goldenFrenzy) batch.setColor(colFrenzy.r, colFrenzy.g, colFrenzy.b, 0.5f);
        else batch.setColor(colGold.r, colGold.g, colGold.b, 0.32f);

        if (assets.cookieGlow != null) {
            batch.draw(assets.cookieGlow, midX - gs / 2f, midY - gs / 2f, gs, gs);
        }

        float hover = inputHandler.cookieHovered ? 1.04f : 1f;
        float dw = size * inputHandler.cookieSquish * hover;
        float dh = size * (2f - inputHandler.cookieSquish) * hover;
        batch.setColor(1, 1, 1, 1);
        if (assets.cookie != null) {
            batch.draw(assets.cookie, midX - dw / 2f, midY - dh / 2f, dw, dh);
        }
    }

    void drawLeftPanel() {
        batch.setColor(0.05f, 0.04f, 0.12f, 0.93f);
        batch.draw(assets.panelLeft != null ? assets.panelLeft : whitePx, 0, 0, PANEL_W, worldH);
        batch.setColor(1, 1, 1, 1);

        float px = 16f;
        float topY = worldH - 22f;

        assets.fontTitle.setColor(colGold);
        assets.fontTitle.draw(batch, "flyin", px, topY);

        assets.fontMedium.setColor(Color.WHITE);
        assets.fontMedium.draw(batch, gameState.formatCookies(gameState.cookies) + " cookies", px, topY - 48f);

        assets.fontSmall.setColor(colSilver);
        assets.fontSmall.draw(batch, "per sec:   " + gameState.formatCps(gameState.cookiesPerSecond), px, topY - 78f);
        assets.fontSmall.draw(batch, "per click: " + gameState.formatCookies((double)gameState.cookiesPerClick), px, topY - 100f);

        float ey = topY - 135f;
        assets.fontSmall.setColor(colGold);
        assets.fontSmall.draw(batch, "Total earned:", px, ey);
        assets.fontSmall.setColor(Color.WHITE);
        assets.fontSmall.draw(batch, gameState.formatCookies(gameState.totalCookiesEarned), px, ey - 22f);

        if (gameState.prestigeLevel > 0) {
            assets.fontSmall.setColor(new Color(0.85f, 0.55f, 1f, 1f));
            assets.fontSmall.draw(batch, "Prestige x" + gameState.prestigeLevel + " (+" + (int)((gameState.prestigeBonus - 1f) * 100) + "%)", px, ey - 60f);
        }

        drawMilestoneList(px, ey - 105f);
    }

    void drawMilestoneList(float x, float startY) {
        assets.fontSmall.setColor(colGold);
        assets.fontSmall.draw(batch, "Milestones:", x, startY);
        float y = startY - 26f;
        for (Milestone m : gameState.milestones) {
            if (m.unlocked) {
                batch.setColor(1, 1, 1, 1);
                if (assets.milestoneIcon != null) batch.draw(assets.milestoneIcon, x, y - 16f, 18f, 18f);
                assets.fontSmall.setColor(colGreen);
                assets.fontSmall.draw(batch, m.title, x + 24f, y);
            } else {
                assets.fontSmall.setColor(0.38f, 0.38f, 0.48f, 1f);
                assets.fontSmall.draw(batch, "? ? ?", x + 24f, y);
            }
            y -= 27f;
        }
        batch.setColor(1, 1, 1, 1);
    }

    void drawRightPanel() {
        float panelX = worldW - PANEL_W;
        batch.setColor(0.05f, 0.04f, 0.12f, 0.93f);
        batch.draw(assets.panelRight != null ? assets.panelRight : whitePx, panelX, 0, PANEL_W, worldH);
        batch.setColor(1, 1, 1, 1);

        if (inputHandler == null) return;

        drawPrestigeButton(inputHandler.getPrestigeButton());

        assets.fontTitle.setColor(colGold);
        assets.fontTitle.draw(batch, "UPGRADES", panelX + 16f, worldH - 22f);

        float clipTop = worldH - 90f;
        float clipBottom = 20f;

        Rectangle[] btns = inputHandler.getBuyButtons();
        for (int i = 0; i < gameState.buildings.length; i++) {
            Rectangle r = btns[i];
            if (r.y + r.height > clipBottom && r.y < clipTop) {
                drawBuildingButton(r, gameState.buildings[i], i);
            }
        }
    }

    void drawBuildingButton(Rectangle r, Building b, int idx) {
        boolean canAfford = gameState.cookies >= b.currentCost;

        if (inputHandler.buttonHoverTimers[idx] > 0) batch.setColor(1f, 1f, 0.45f, 1f);
        else if (canAfford) batch.setColor(0.14f, 0.52f, 0.24f, 1f);
        else batch.setColor(0.24f, 0.14f, 0.19f, 1f);

        Texture btnTex = canAfford ? assets.buttonNormal : assets.buttonLocked;
        batch.draw(btnTex != null ? btnTex : whitePx, r.x, r.y, r.width, r.height);

        batch.setColor(1, 1, 1, 1);
        float iconSize = 52f;
        Texture iconTex = b.icon != null ? b.icon : assets.whitePixel;
        batch.draw(iconTex != null ? iconTex : whitePx, r.x + 8f, r.y + (r.height - iconSize) / 2f, iconSize, iconSize);

        assets.fontMedium.setColor(Color.WHITE);
        assets.fontMedium.draw(batch, b.name + " (" + b.count + ")", r.x + 68f, r.y + r.height - 14f);

        assets.fontSmall.setColor(canAfford ? colGold : colDimRed);
        assets.fontSmall.draw(batch, gameState.formatCookies((double)b.currentCost), r.x + 68f, r.y + r.height - 36f);

        assets.fontSmall.setColor(colSilver);
        String cpsStr = b.baseCps >= 1 ? gameState.formatCps(b.baseCps) : String.format("%.2f/s", b.baseCps);
        assets.fontSmall.draw(batch, cpsStr + (b.baseCps >= 1 ? "/s each" : " each"), r.x + 68f, r.y + r.height - 56f);
    }

    void drawPrestigeButton(Rectangle r) {
        if (r == null) return;
        boolean can = gameState.totalCookiesEarned >= gameState.getPrestigeCost();

        batch.setColor(can ? new Color(0.58f, 0.1f, 0.78f, 1f) : new Color(0.18f, 0.1f, 0.28f, 0.85f));
        Texture btnTex = can ? assets.buttonNormal : assets.buttonLocked;
        batch.draw(btnTex != null ? btnTex : whitePx, r.x, r.y, r.width, r.height);

        batch.setColor(1, 1, 1, 1);
        if (assets.prestige != null) batch.draw(assets.prestige, r.x + 6f, r.y + 8f, 30f, 30f);

        assets.fontSmall.setColor(can ? new Color(0.9f, 0.5f, 1f, 1f) : colSilver);
        assets.fontSmall.draw(batch, "PRESTIGE", r.x + 42f, r.y + 33f);
    }

    void drawParticles() {
        for (Particle p : gameState.particles) {
            batch.setColor(p.color);
            if (assets.sparkles != null && p.texIndex < assets.sparkles.length) {
                Texture t = assets.sparkles[p.texIndex];
                if (t != null) batch.draw(t, p.x - p.size/2f, p.y - p.size/2f, p.size, p.size);
            }
        }
        batch.setColor(1, 1, 1, 1);
    }

    void drawFloatingTexts() {
        for (FloatingText ft : gameState.floatingTexts) {
            assets.fontBig.setColor(ft.color.r, ft.color.g, ft.color.b, ft.alpha);
            assets.fontBig.draw(batch, ft.text, ft.x, ft.y);
        }
        assets.fontBig.setColor(Color.WHITE);
    }

    void drawGoldenCookie() {
        GoldenCookie gc = gameState.goldenCookie;
        if (gc == null || !gc.visible) return;
        batch.setColor(1, 1, 1, gc.getAlpha());
        if (assets.golden != null) batch.draw(assets.golden, gc.x, gc.y, gc.size, gc.size);
        batch.setColor(1, 1, 1, 1);
    }

    void drawMilestonePopup() {
        Milestone m = gameState.activeMilestone;
        if (m == null) return;
        float alpha = Math.min(1f, m.showTimer);
        float bx = worldW/2f - 190f;
        float by = worldH - 105f;

        batch.setColor(0.08f, 0.06f, 0.18f, alpha * 0.95f);
        batch.draw(assets.buttonNormal != null ? assets.buttonNormal : whitePx, bx, by, 380f, 72f);

        batch.setColor(1, 1, 1, alpha);
        if (assets.milestoneIcon != null) batch.draw(assets.milestoneIcon, bx + 12f, by + 12f, 48f, 48f);

        assets.fontMedium.setColor(colGold.r, colGold.g, colGold.b, alpha);
        assets.fontMedium.draw(batch, m.title, bx + 75f, by + 52f);
        assets.fontSmall.setColor(1, 1, 1, alpha * 0.8f);
        assets.fontSmall.draw(batch, "Milestone Unlocked!", bx + 75f, by + 24f);
        batch.setColor(1, 1, 1, 1);
    }

    void drawAchievementPopup() {
        Achievement a = gameState.activeAchievement;
        if (a == null) return;
        float alpha = Math.min(1f, a.showTimer);
        float bx = worldW/2f - 190f;
        float by = worldH - 185f;

        batch.setColor(0.05f, 0.15f, 0.1f, alpha * 0.95f);
        batch.draw(assets.buttonNormal != null ? assets.buttonNormal : whitePx, bx, by, 380f, 72f);

        batch.setColor(1, 1, 1, alpha);
        Texture icon = assets.milestoneIcon;
        if (a.type == 2 && a.buildingIndex >= 0 && a.buildingIndex < gameState.buildings.length) {
            icon = gameState.buildings[a.buildingIndex].icon;
        } else if (a.type == 1) icon = assets.iconCursor;
        else if (a.type == 3) icon = assets.prestige;

        if (icon != null) batch.draw(icon, bx + 12f, by + 12f, 48f, 48f);

        assets.fontMedium.setColor(colGreen.r, colGreen.g, colGreen.b, alpha);
        assets.fontMedium.draw(batch, a.title, bx + 75f, by + 52f);
        assets.fontSmall.setColor(1, 1, 1, alpha * 0.8f);
        assets.fontSmall.draw(batch, "Achievement Unlocked!", bx + 75f, by + 24f);
        batch.setColor(1, 1, 1, 1);
    }

    void drawFrenzyOverlay() {
        if (!gameState.goldenFrenzy) return;
        assets.fontBig.setColor(colFrenzy);
        assets.fontBig.draw(batch, "FRENZY x7!", worldW/2f - 80f, worldH - 28f);
        assets.fontBig.setColor(Color.WHITE);
    }

    void drawPauseButton() {
        if (inputHandler == null) return;
        Rectangle r = inputHandler.getPauseButton();
        if (r == null) return;
        batch.setColor(0.1f, 0.08f, 0.22f, 0.85f);
        batch.draw(whitePx, r.x, r.y, r.width, r.height);

        batch.setColor(1, 1, 1, 0.9f);
        float bw = r.width * 0.18f;
        float bh = r.height * 0.45f;
        float space = r.width * 0.12f;
        float x1 = r.x + (r.width - (bw * 2 + space)) / 2f;
        float y = r.y + (r.height - bh) / 2f;
        batch.draw(whitePx, x1, y, bw, bh);
        batch.draw(whitePx, x1 + bw + space, y, bw, bh);
        batch.setColor(1, 1, 1, 1);
    }
}
