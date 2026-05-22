package io.github.awornl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class TutorialManager {
    public enum Step {
        CLICK_COOKIE,
        BUY_CURSOR,
        BUY_GRANDMA,
        PRESTIGE_INFO,
        GOLDEN_INFO,
        COMPLETE
    }

    private Step currentStep = Step.CLICK_COOKIE;
    private boolean active = true;
    private float timer = 0f;
    private Texture arrowTexture;

    public TutorialManager(Texture arrowTexture) {
        this.arrowTexture = arrowTexture;
    }

    public void update(float delta, GameState state, InputHandler input) {
        if (!active) return;
        timer += delta;

        if (timer > 30f) {
            nextStep();
            timer = 0;
        }

        switch (currentStep) {
            case CLICK_COOKIE:
                if (state.totalClicks > 0) nextStep();
                break;
            case BUY_CURSOR:
                if (state.buildings[0].count > 0) nextStep();
                break;
            case BUY_GRANDMA:
                if (state.buildings[1].count > 0) nextStep();
                break;
            case PRESTIGE_INFO:
                if (timer > 8f) nextStep();
                break;
            case GOLDEN_INFO:
                if (timer > 8f) nextStep();
                break;
            default:
                break;
        }
    }

    private void nextStep() {
        switch (currentStep) {
            case CLICK_COOKIE: currentStep = Step.BUY_CURSOR; timer = 0; break;
            case BUY_CURSOR:   currentStep = Step.BUY_GRANDMA; timer = 0; break;
            case BUY_GRANDMA:  currentStep = Step.PRESTIGE_INFO; timer = 0; break;
            case PRESTIGE_INFO:currentStep = Step.GOLDEN_INFO; timer = 0; break;
            case GOLDEN_INFO:  currentStep = Step.COMPLETE; timer = 0; active = false;
                Gdx.app.getPreferences("cookie_save").putBoolean("tutorialComplete", true).flush();
                break;
            default: active = false;
        }
    }

    public void render(SpriteBatch batch, Assets assets, InputHandler input, float worldW, float worldH) {
        if (!active) return;

        batch.setColor(1, 1, 1, 1f);
        if (assets.tutorialGradient != null) {
            batch.draw(assets.tutorialGradient, 0, 0, worldW, worldH);
        } else {
            batch.setColor(0, 0, 0, 0.6f);
            batch.draw(assets.whitePixel, 0, 0, worldW, worldH);
            batch.setColor(1, 1, 1, 1);
        }

        Rectangle target = null;
        String message = "";
        float arrowRotation = 90f;
        float arrowX = 0, arrowY = 0;

        switch (currentStep) {
            case CLICK_COOKIE:
                target = new Rectangle(input.getCookieX(), input.getCookieY(), input.getCookieSize(), input.getCookieSize());
                message = "Click the big cookie!";
                arrowX = target.x + target.width + 20;
                arrowY = target.y + target.height / 2f - 16;
                arrowRotation = 90f;
                break;
            case BUY_CURSOR:
                target = input.getBuyButtons()[0];
                message = "Buy a Cursor to bake more cookies!";
                arrowX = target.x - 50;
                arrowY = target.y + target.height / 2f - 16;
                arrowRotation = -90f;
                break;
            case BUY_GRANDMA:
                target = input.getBuyButtons()[1];
                message = "Now buy a Grandma for even more cookies per second!";
                arrowX = target.x - 50;
                arrowY = target.y + target.height / 2f - 16;
                arrowRotation = -90f;
                break;
            case PRESTIGE_INFO:
                message = "When you earn 1 billion total cookies, you can Prestige.\nResets progress but gives a permanent 25% bonus!";
                break;
            case GOLDEN_INFO:
                message = "A Golden Cookie will appear occasionally.\nClick it for a 7x Frenzy!";
                break;
            default:
                return;
        }

        if (target != null) {
            batch.setColor(1, 1, 0, 0.8f);
            float bw = 3;
            batch.draw(assets.whitePixel, target.x - bw, target.y - bw, target.width + bw*2, bw);
            batch.draw(assets.whitePixel, target.x - bw, target.y + target.height, target.width + bw*2, bw);
            batch.draw(assets.whitePixel, target.x - bw, target.y - bw, bw, target.height + bw*2);
            batch.draw(assets.whitePixel, target.x + target.width, target.y - bw, bw, target.height + bw*2);
            batch.setColor(1, 1, 1, 1);

            batch.draw(arrowTexture, arrowX, arrowY, 16, 16, 32, 32, 1, 1, arrowRotation, 0, 0, 32, 32, false, false);
        }

        assets.fontMedium.setColor(Color.GOLD);
        assets.fontMedium.draw(batch, message, worldW/2 - 250, 100);
        assets.fontMedium.setColor(Color.WHITE);
    }

    public boolean isComplete() { return !active; }
}
