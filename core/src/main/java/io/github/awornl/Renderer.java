package io.github.awornl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Renderer {

    SpriteBatch  batch;
    Assets       assets;
    GameState    gameState;
    InputHandler inputHandler;
    Viewport     viewport;

    static final float PANEL_W = 310f;

    float worldW = Main.V_WIDTH;
    float worldH = Main.V_HEIGHT;

    TextureRegion starsRegion;
    float         starsScrollV = 0f;

    float cookiePulseTimer = 0f;

    Color colGold   = new Color(1f,   0.85f, 0.1f,  1f);
    Color colSilver = new Color(0.8f, 0.8f,  0.9f,  1f);
    Color colFrenzy = new Color(1f,   0.3f,  0.05f, 1f);
    Color colGreen  = new Color(0.2f, 0.9f,  0.4f,  1f);
    Color colDimRed = new Color(0.8f, 0.3f,  0.3f,  1f);

    public Renderer(SpriteBatch batch, Assets assets, GameState gameState, Viewport viewport) {
        this.batch    = batch;
        this.assets   = assets;
        this.gameState= gameState;
        this.viewport = viewport;

        assets.bgStars.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        starsRegion = new TextureRegion(assets.bgStars);
    }

    public void setInputHandler(InputHandler h) { this.inputHandler = h; }

    public void updateWorldSize(float w, float h) {
        worldW = w;
        worldH = h;
    }

    public void render(float delta) {
        cookiePulseTimer += delta;

        int texH = assets.bgStars.getHeight();
        starsScrollV += (20f / texH) * delta;
        if (starsScrollV > 1f) starsScrollV -= 1f;

        drawBackground();
        drawCookieArea();
        drawLeftPanel();
        drawRightPanel();
        drawParticles();
        drawFloatingTexts();
        drawGoldenCookie();
        drawMilestonePopup();
        drawFrenzyOverlay();
    }

    void drawBackground() {
        batch.setColor(1, 1, 1, 1);
        batch.draw(assets.bg, 0, 0, worldW, worldH);

        int texW = assets.bgStars.getWidth();
        int texH = assets.bgStars.getHeight();

        float repeatX = worldW / texW;
        float repeatY = worldH / texH;

        starsRegion.setRegion(
            0f,
            starsScrollV,
            repeatX,
            starsScrollV + repeatY
        );

        batch.setColor(1, 1, 1, 0.9f);
        batch.draw(starsRegion, 0, 0, worldW, worldH);
        batch.setColor(1, 1, 1, 1);
    }

    void drawCookieArea() {
        if (inputHandler == null) return;

        float cx   = inputHandler.getCookieX();
        float cy   = inputHandler.getCookieY();
        float size = inputHandler.getCookieSize();
        float midX = cx + size / 2f;
        float midY = cy + size / 2f;

        float glowSize  = size * 1.55f;
        float glowPulse = (float)(Math.sin(cookiePulseTimer * 2f) * 0.07f + 0.93f);
        float gs        = glowSize * glowPulse;

        if (gameState.goldenFrenzy)
            batch.setColor(colFrenzy.r, colFrenzy.g, colFrenzy.b, 0.5f);
        else
            batch.setColor(colGold.r, colGold.g, colGold.b, 0.32f);

        batch.draw(assets.cookieGlow, midX - gs / 2f, midY - gs / 2f, gs, gs);

        float squish = inputHandler.cookieSquish;
        float hover  = inputHandler.cookieHovered ? 1.04f : 1f;
        float drawW  = size * squish * hover;
        float drawH  = size * (2f - squish) * hover;

        batch.setColor(1, 1, 1, 1);
        batch.draw(assets.cookie, midX - drawW / 2f, midY - drawH / 2f, drawW, drawH);
    }

    void drawLeftPanel() {
        batch.setColor(0.05f, 0.04f, 0.12f, 0.93f);
        batch.draw(assets.panelLeft, 0, 0, PANEL_W, worldH);
        batch.setColor(1, 1, 1, 1);

        float px   = 16f;
        float topY = worldH - 22f;

        assets.fontTitle.setColor(colGold);
        assets.fontTitle.draw(batch, "flyin", px, topY);

        assets.fontMedium.setColor(Color.WHITE);
        assets.fontMedium.draw(batch, gameState.formatCookies(gameState.cookies) + " cookies", px, topY - 48f);

        assets.fontSmall.setColor(colSilver);
        assets.fontSmall.draw(batch, "per sec:   " + gameState.formatCps(gameState.cookiesPerSecond),    px, topY - 78f);
        assets.fontSmall.draw(batch, "per click: " + gameState.formatCookies(gameState.cookiesPerClick), px, topY - 100f);

        float ey = topY - 135f;
        assets.fontSmall.setColor(colGold);
        assets.fontSmall.draw(batch, "Total earned:", px, ey);
        assets.fontSmall.setColor(Color.WHITE);
        assets.fontSmall.draw(batch, gameState.formatCookies(gameState.totalCookiesEarned), px, ey - 22f);

        if (gameState.prestigeLevel > 0) {
            assets.fontSmall.setColor(new Color(0.85f, 0.55f, 1f, 1f));
            assets.fontSmall.draw(batch,
                "Prestige x" + gameState.prestigeLevel +
                    "  (+" + (int)((gameState.prestigeBonus - 1f) * 100) + "%)",
                px, ey - 60f);
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
                batch.draw(assets.milestoneIcon, x, y - 16f, 18f, 18f);
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
        batch.draw(assets.panelRight, panelX, 0, PANEL_W, worldH);
        batch.setColor(1, 1, 1, 1);

        assets.fontTitle.setColor(colGold);
        assets.fontTitle.draw(batch, "UPGRADES", panelX + 16f, worldH - 22f);

        if (inputHandler == null) return;

        Rectangle[] btns = inputHandler.getBuyButtons();
        for (int i = 0; i < gameState.buildings.length; i++) {
            drawBuildingButton(btns[i], gameState.buildings[i], i);
        }
        drawPrestigeButton(inputHandler.getPrestigeButton());
    }

    void drawBuildingButton(Rectangle r, Building b, int idx) {
        boolean canAfford  = gameState.cookies >= b.currentCost;
        boolean justBought = inputHandler.buttonHoverTimers[idx] > 0;

        if (justBought)
            batch.setColor(1f, 1f, 0.45f, 1f);
        else if (canAfford)
            batch.setColor(0.14f, 0.52f, 0.24f, 1f);
        else
            batch.setColor(0.24f, 0.14f, 0.19f, 1f);

        batch.draw(canAfford ? assets.buttonNormal : assets.buttonLocked,
            r.x, r.y, r.width, r.height);

        batch.setColor(1, 1, 1, 1);
        float iconSize = 52f;
        batch.draw(b.icon, r.x + 8f, r.y + (r.height - iconSize) / 2f, iconSize, iconSize);

        assets.fontMedium.setColor(Color.WHITE);
        assets.fontMedium.draw(batch, b.name + " (" + b.count + ")", r.x + 68f, r.y + r.height - 14f);

        assets.fontSmall.setColor(canAfford ? colGold : colDimRed);
        assets.fontSmall.draw(batch, gameState.formatCookies(b.currentCost), r.x + 68f, r.y + r.height - 36f);

        assets.fontSmall.setColor(colSilver);
        String cpsStr = b.baseCps >= 1
            ? gameState.formatCps(b.baseCps) + "/s"
            : String.format("%.2f/s", b.baseCps);
        assets.fontSmall.draw(batch, cpsStr + " each", r.x + 68f, r.y + r.height - 56f);

        drawProgressBar(r.x + 8f, r.y + 4f, r.width - 16f, 5f, b);
    }

    void drawProgressBar(float x, float y, float w, float h, Building b) {
        long prevCost = b.count == 0
            ? b.baseCost
            : (long)(b.baseCost * Math.pow(1.15, b.count - 1));
        float progress = (b.currentCost > prevCost)
            ? MathUtils.clamp(
            (float)(gameState.cookies - prevCost) / (float)(b.currentCost - prevCost),
            0f, 1f)
            : 1f;

        batch.setColor(0.14f, 0.1f, 0.2f, 1f);
        batch.draw(assets.progressBarBg, x, y, w, h);
        batch.setColor(colGold);
        batch.draw(assets.progressBarFill, x, y, w * progress, h);
        batch.setColor(1, 1, 1, 1);
    }

    void drawPrestigeButton(Rectangle r) {
        boolean can = gameState.totalCookiesEarned >= gameState.getPrestigeCost();

        batch.setColor(can
            ? new Color(0.58f, 0.1f, 0.78f, 1f)
            : new Color(0.18f, 0.1f, 0.28f, 0.85f));
        batch.draw(can ? assets.buttonNormal : assets.buttonLocked, r.x, r.y, r.width, r.height);

        batch.setColor(1, 1, 1, 1);
        batch.draw(assets.prestige, r.x + 8f, r.y + 8f, 34f, 34f);

        assets.fontSmall.setColor(can ? new Color(0.9f, 0.5f, 1f, 1f) : new Color(0.5f, 0.38f, 0.6f, 1f));
        assets.fontSmall.draw(batch,
            "PRESTIGE  (need " + gameState.formatCookies(gameState.getPrestigeCost()) + ")",
            r.x + 48f, r.y + 33f);
        assets.fontSmall.setColor(colSilver);
        assets.fontSmall.draw(batch, "Resets game  +25% permanent bonus", r.x + 48f, r.y + 14f);
    }

    void drawParticles() {
        for (Particle p : gameState.particles) {
            batch.setColor(p.color);
            Texture t = assets.sparkles[p.texIndex];
            batch.draw(t,
                p.x - p.size / 2f, p.y - p.size / 2f,
                p.size / 2f,       p.size / 2f,
                p.size,            p.size,
                1f, 1f,            p.rotation,
                0, 0,              t.getWidth(), t.getHeight(),
                false, false);
        }
        batch.setColor(1, 1, 1, 1);
    }

    void drawFloatingTexts() {
        for (FloatingText ft : gameState.floatingTexts) {
            assets.fontBig.getData().setScale(ft.size);
            assets.fontBig.setColor(ft.color.r, ft.color.g, ft.color.b, ft.alpha);
            assets.fontBig.draw(batch, ft.text, ft.x, ft.y);
        }
        assets.fontBig.getData().setScale(1f);
        assets.fontBig.setColor(Color.WHITE);
    }

    void drawGoldenCookie() {
        GoldenCookie gc = gameState.goldenCookie;
        if (!gc.visible) return;

        float gs = gc.size * 1.8f;
        batch.setColor(colGold.r, colGold.g, colGold.b, gc.glowAlpha * gc.getAlpha() * 0.7f);
        batch.draw(assets.goldenGlow,
            gc.x + gc.size / 2f - gs / 2f,
            gc.y + gc.size / 2f - gs / 2f,
            gs, gs);

        batch.setColor(1, 1, 1, gc.getAlpha());
        batch.draw(assets.golden, gc.x, gc.y, gc.size, gc.size);
        batch.setColor(1, 1, 1, 1);

        assets.fontSmall.setColor(colGold.r, colGold.g, colGold.b, gc.getAlpha());
        assets.fontSmall.draw(batch, String.format("%.0fs", gc.lifeTimer),
            gc.x + gc.size / 2f - 10f, gc.y - 5f);
        assets.fontSmall.setColor(Color.WHITE);
    }

    void drawMilestonePopup() {
        Milestone m = gameState.activeMilestone;
        if (m == null) return;

        float popW = 380f;
        float popH = 72f;
        float popX = worldW / 2f - popW / 2f;
        float popY = worldH - 105f;
        float alpha = Math.min(1f, m.showTimer);

        batch.setColor(0.08f, 0.06f, 0.18f, alpha * 0.95f);
        batch.draw(assets.buttonNormal, popX, popY, popW, popH);
        batch.setColor(1, 1, 1, alpha);
        batch.draw(assets.milestoneIcon, popX + 12f, popY + popH / 2f - 18f, 36f, 36f);

        assets.fontMedium.setColor(colGold.r, colGold.g, colGold.b, alpha);
        assets.fontMedium.draw(batch, m.title, popX + 58f, popY + popH - 12f);
        assets.fontSmall.setColor(1f, 1f, 1f, alpha * 0.8f);
        assets.fontSmall.draw(batch, m.description, popX + 58f, popY + popH - 36f);

        batch.setColor(1, 1, 1, 1);
        assets.fontMedium.setColor(Color.WHITE);
        assets.fontSmall.setColor(Color.WHITE);
    }

    void drawFrenzyOverlay() {
        if (!gameState.goldenFrenzy) return;

        float pulse = (float)(Math.sin(cookiePulseTimer * 10f) * 0.04f + 0.07f);
        batch.setColor(colFrenzy.r, colFrenzy.g, colFrenzy.b, pulse);
        batch.draw(assets.bg, 0, 0, worldW, worldH);
        batch.setColor(1, 1, 1, 1);

        assets.fontBig.getData().setScale(1.5f);
        assets.fontBig.setColor(colFrenzy);
        String msg = "FRENZY x7!  " + String.format("%.0fs", gameState.goldenFrenzyTimer);
        assets.fontBig.draw(batch, msg, worldW / 2f - 130f, worldH - 28f);
        assets.fontBig.getData().setScale(1f);
        assets.fontBig.setColor(Color.WHITE);
    }
}
