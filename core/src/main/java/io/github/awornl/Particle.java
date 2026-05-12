package io.github.awornl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

public class Particle {

    public float x, y;
    public float vx, vy;
    public float life;
    public float maxLife;
    public float size;
    public float rotation;
    public float rotationSpeed;
    public Color color;
    public int texIndex;

    public Particle(float x, float y, boolean isCrit) {
        this.x = x;
        this.y = y;

        float angle = MathUtils.random(0f, 360f);
        float speed = MathUtils.random(isCrit ? 200f : 80f, isCrit ? 450f : 220f);
        this.vx = MathUtils.cosDeg(angle) * speed;
        this.vy = MathUtils.sinDeg(angle) * speed;
        this.maxLife = MathUtils.random(0.3f, isCrit ? 0.9f : 0.6f);
        this.life = maxLife;
        this.size = MathUtils.random(6f, isCrit ? 20f : 14f);
        this.rotation = MathUtils.random(0f, 360f);
        this.rotationSpeed = MathUtils.random(-300f, 300f);
        this.texIndex = MathUtils.random(0, 3);

        if (isCrit) {
            this.color = new Color(MathUtils.random(0.9f, 1f), MathUtils.random(0.7f, 1f), MathUtils.random(0f, 0.3f), 1f);
        } else {
            float r = MathUtils.random(0.6f, 1f);
            float g = MathUtils.random(0.3f, 0.7f);
            this.color = new Color(r, g, 0.1f, 1f);
        }
    }

    public void update(float delta) {
        x += vx * delta;
        y += vy * delta;
        vy -= 400f * delta;
        rotation += rotationSpeed * delta;
        life -= delta;
        color.a = life / maxLife;
    }

    public boolean isDead() {
        return life <= 0;
    }
}
