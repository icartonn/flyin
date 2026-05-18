package io.github.awornl;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GuideScreen extends InputAdapter {

    Assets   assets;
    Viewport viewport;
    Main     main;

    float worldW;
    float worldH;

    int   currentPage = 0;
    int   totalPages  = 4;

    Rectangle nextButton;
    Rectangle prevButton;
    Rectangle startButton;
    Rectangle skipButton;

    Vector3 touchVec = new Vector3();

    Color colGold   = new Color(1f,   0.85f, 0.1f,  1f);
    Color colSilver = new Color(0.8f, 0.8f,  0.9f,  1f);
    Color colGreen  = new Color(0.2f, 0.9f,  0.4f,  1f);
    Color colOverlay = new Color(0.05f, 0.04f, 0.15f, 0.72f);

    float animTimer = 0f;

    String[] pageTitles = {
        "Welcome to the Cookie Clicker!",
        "Buying Upgrades",
        "Prestige System",
        "Golden Cookie & Frenzy"
    };

    String[][] pageLines = {
        {
            "The BIG COOKIE is in the center of the screen.",
            "Click it to earn cookies!",
            "",
            "The LEFT PANEL shows your stats:",
            "  - cookies you have right now",
            "  - cookies per second (auto-income)",
            "  - cookies per click",
            "  - total cookies earned",
            "  - milestones you unlocked"
        },
        {
            "The RIGHT PANEL has UPGRADES (buildings).",
            "",
            "Each building earns cookies per second automatically.",
            "  Cursor   - auto-clicks. Also boosts your click power!",
            "  Grandma  - bakes cookies slowly but steadily",
            "  Mine     - digs up dough from underground",
            "  Factory  - mass produces cookies",
            "  Ship     - ships cookies across the galaxy",
            "  Portal   - opens a cookie dimension!",
            "",
            "Green button = you can afford it!",
            "Red button   = save more cookies first."
        },
        {
            "When you have 1 BILLION cookies total earned,",
            "the PRESTIGE button appears at the bottom right.",
            "",
            "Prestige RESETS your cookies and buildings,",
            "but gives you a PERMANENT +25% cookie bonus!",
            "",
            "Every prestige level stacks the bonus further.",
            "It gets easier each time you prestige."
        },
        {
            "Sometimes a GOLDEN COOKIE appears on screen.",
            "Click it fast before it disappears (13 seconds)!",
            "",
            "Golden Cookie gives you FRENZY mode:",
            "  x7 cookies per second for 30 seconds!",
            "  Your click power also multiplies by 7!",
            "",
            "The screen turns red during frenzy - click like crazy!",
            "",
            "Good luck, Cookie Master!"
        }
    };

    public GuideScreen(Assets assets, Viewport viewport, Main main) {
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
        float bottomY = 55f;
        float btnH = 50f;
        float btnW = 140f;

        prevButton  = new Rectangle(cx - 180f, bottomY, btnW, btnH);
        nextButton  = new Rectangle(cx + 40f,  bottomY, btnW, btnH);
        startButton = new Rectangle(cx - 70f,  bottomY, 140f, btnH);
        skipButton  = new Rectangle(worldW - 130f, worldH - 50f, 110f, 36f);
    }

    public void render(float delta) {
        worldW = viewport.getWorldWidth();
        worldH = viewport.getWorldHeight();
        animTimer += delta;

        batch().draw(assets.bg, 0, 0, worldW, worldH);

        batch().setColor(colOverlay);
        batch().draw(assets.bg, 0, 0, worldW, worldH);
        batch().setColor(1, 1, 1, 1);

        float cx = worldW / 2f;
        float divY = worldH - 90f;

        assets.fontTitle.setColor(colGold);
        assets.fontTitle.draw(batch(), "HOW TO PLAY", cx - 130f, divY + 48f);

        batch().setColor(colGold.r, colGold.g, colGold.b, 0.4f);
        batch().draw(assets.progressBarFill, cx - 280f, divY, 560f, 1.5f);
        batch().setColor(1, 1, 1, 1);

        assets.fontMedium.setColor(colGold);
        assets.fontMedium.draw(batch(), pageTitles[currentPage], cx - 280f, divY - 22f);

        float lineY = divY - 58f;
        for (String line : pageLines[currentPage]) {
            if (line.equals("")) {
                lineY -= 16f;
                continue;
            }
            boolean isIndented = line.startsWith(" ");
            assets.fontSmall.setColor(isIndented ? colSilver : Color.WHITE);
            assets.fontSmall.draw(batch(), line, cx - 280f, lineY);
            lineY -= 26f;
        }

        drawSmallBtn(skipButton, "SKIP");

        if (currentPage > 0) {
            drawSmallBtn(prevButton, "< PREV");
        }

        if (currentPage < totalPages - 1) {
            drawSmallBtn(nextButton, "NEXT >");
        } else {
            batch().setColor(0.18f, 0.6f, 0.22f, 0.9f);
            batch().draw(assets.buttonNormal, startButton.x, startButton.y, startButton.width, startButton.height);
            batch().setColor(1, 1, 1, 1);
            assets.fontMedium.setColor(colGold);
            assets.fontMedium.draw(batch(), "START!", startButton.x + 22f, startButton.y + 33f);
            assets.fontMedium.setColor(Color.WHITE);
        }
    }

    void drawSmallBtn(Rectangle r, String text) {
        batch().setColor(0.1f, 0.08f, 0.22f, 0.88f);
        batch().draw(assets.buttonNormal, r.x, r.y, r.width, r.height);
        batch().setColor(1, 1, 1, 1);
        assets.fontSmall.setColor(colSilver);
        assets.fontSmall.draw(batch(), text, r.x + 16f, r.y + r.height / 2f + 8f);
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

        if (skipButton.contains(tx, ty)) {
            assets.playClickSound();
            main.completeGuide();
            return true;
        }
        if (nextButton.contains(tx, ty) && currentPage < totalPages - 1) {
            assets.playClickSound();
            currentPage++;
            return true;
        }
        if (prevButton.contains(tx, ty) && currentPage > 0) {
            assets.playClickSound();
            currentPage--;
            return true;
        }
        if (currentPage == totalPages - 1 && startButton.contains(tx, ty)) {
            assets.playClickSound();
            main.completeGuide();
            return true;
        }

        return false;
    }
}
