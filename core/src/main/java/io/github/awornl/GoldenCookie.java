package io.github.awornl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

public class GoldenCookie {

    public float x, y;
    public float size = 80f;
    public boolean visible = false;
    public float lifeTimer = 0f;
    public float maxLife = 13f;
    public float spawnTimer = 0f;
    public float spawnInterval = 90f;
    public float pulseTimer = 0f;
    public float glowAlpha = 0f;
    public boolean wasClicked = false;

    public void update(float delta) {
        pulseTimer += delta;

        if (!visible) {
            spawnTimer += delta;
            if (spawnTimer >= spawnInterval) {
                spawnTimer = 0;
                spawn();
            }
        } else {
            lifeTimer -= delta;
            glowAlpha = (float)(Math.sin(pulseTimer * 5f) * 0.5f + 0.5f);
            if (lifeTimer <= 0) {
                visible = false;
                wasClicked = false;
            }
        }
    }

    public void spawn() {
        float margin = size;
        float panelWidth = 300f;
        x = MathUtils.random(margin + panelWidth, Gdx.graphics.getWidth() - panelWidth - margin);
        y = MathUtils.random(margin, Gdx.graphics.getHeight() - margin);
        visible = true;
        wasClicked = false;
        lifeTimer = maxLife;
    }

    public boolean contains(float tx, float ty) {
        float cx = x + size / 2f;
        float cy = y + size / 2f;
        float dist = (float)Math.sqrt((tx - cx) * (tx - cx) + (ty - cy) * (ty - cy));
        return dist < size / 2f;
    }

    public float getAlpha() {
        if (lifeTimer > maxLife - 1f) {
            return 1f - (lifeTimer - (maxLife - 1f));
        }
        if (lifeTimer < 2f) {
            return lifeTimer / 2f;
        }
        return 1f;
    }
}
