package io.github.awornl;

import com.badlogic.gdx.graphics.Color;

public class FloatingText {

    public String text;
    public float x, y;
    public float alpha;
    public float vy;
    public float vx;
    public float size;
    public Color color;
    public boolean isCrit;

    public FloatingText(String text, float x, float y, boolean isCrit) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.alpha = 1f;
        this.isCrit = isCrit;

        if (isCrit) {
            this.vy = 120f;
            this.vx = (float)(Math.random() * 60 - 30);
            this.size = 2.2f;
            this.color = new Color(1f, 0.85f, 0.1f, 1f);
        } else {
            this.vy = 75f;
            this.vx = (float)(Math.random() * 40 - 20);
            this.size = 1.4f;
            this.color = new Color(1f, 1f, 1f, 1f);
        }
    }

    public void update(float delta) {
        y += vy * delta;
        x += vx * delta;
        alpha -= delta * (isCrit ? 1.2f : 1.8f);
        if (alpha < 0) alpha = 0;
    }

    public boolean isDead() {
        return alpha <= 0;
    }
}
