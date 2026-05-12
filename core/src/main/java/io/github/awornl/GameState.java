package io.github.awornl;

import com.badlogic.gdx.utils.Array;

public class GameState {

    public long cookies = 0;
    public long totalCookiesEarned = 0;
    public long cookiesPerClick = 1;
    public double cookiesPerSecond = 0;

    public int prestigeLevel = 0;
    public float prestigeBonus = 1f;

    public boolean goldenFrenzy = false;
    public float goldenFrenzyTimer = 0f;
    public float goldenFrenzyMultiplier = 7f;

    public Building[] buildings;
    public Milestone[] milestones;

    public GoldenCookie goldenCookie;
    public Array<FloatingText> floatingTexts = new Array<>();
    public Array<Particle> particles = new Array<>();

    public float autoSaveTimer = 0f;
    public float cpsTimer = 0f;

    public long lastMilestoneShown = -1;
    public Milestone activeMilestone = null;

    public GameState() {
        goldenCookie = new GoldenCookie();
        initBuildings();
        initMilestones();
    }

    void initBuildings() {
        buildings = new Building[6];
    }

    public void setBuildings(Building[] b) {
        buildings = b;
    }

    void initMilestones() {
        milestones = new Milestone[]{
            new Milestone("First Bite",       "Earn 100 cookies",          100L),
            new Milestone("Cookie Hoarder",   "Earn 10,000 cookies",       10000L),
            new Milestone("Bakery Tycoon",    "Earn 1,000,000 cookies",    1000000L),
            new Milestone("Dough God",        "Earn 100,000,000 cookies",  100000000L),
            new Milestone("Beyond Infinity",  "Earn 10,000,000,000 cookies",10000000000L),
        };
    }

    public void update(float delta) {
        cpsTimer += delta;
        if (cpsTimer >= 0.1f) {
            cpsTimer -= 0.1f;
            double cps = cookiesPerSecond * prestigeBonus;
            if (goldenFrenzy) cps *= goldenFrenzyMultiplier;
            long earned = (long)(cps * 0.1);
            cookies += earned;
            totalCookiesEarned += earned;
        }

        if (goldenFrenzy) {
            goldenFrenzyTimer -= delta;
            if (goldenFrenzyTimer <= 0) {
                goldenFrenzy = false;
            }
        }

        goldenCookie.update(delta);

        for (int i = floatingTexts.size - 1; i >= 0; i--) {
            floatingTexts.get(i).update(delta);
            if (floatingTexts.get(i).isDead()) floatingTexts.removeIndex(i);
        }

        for (int i = particles.size - 1; i >= 0; i--) {
            particles.get(i).update(delta);
            if (particles.get(i).isDead()) particles.removeIndex(i);
        }

        checkMilestones(delta);

        updateClickPower();
        updateCps();
    }

    void checkMilestones(float delta) {
        if (activeMilestone != null) {
            activeMilestone.showTimer -= delta;
            if (activeMilestone.showTimer <= 0) activeMilestone = null;
        }

        for (Milestone m : milestones) {
            if (m.check(totalCookiesEarned) && activeMilestone == null) {
                activeMilestone = m;
            }
        }
    }

    void updateClickPower() {
        long base = 1 + buildings[0].count;
        cookiesPerClick = (long)(base * prestigeBonus);
        if (goldenFrenzy) cookiesPerClick *= (long)goldenFrenzyMultiplier;
    }

    void updateCps() {
        double total = 0;
        for (Building b : buildings) {
            total += b.getTotalCps();
        }
        cookiesPerSecond = total;
    }

    public void clickCookie(float x, float y) {
        boolean isCrit = Math.random() < 0.05 + prestigeLevel * 0.01;
        long gained = cookiesPerClick * (isCrit ? 5 : 1);
        cookies += gained;
        totalCookiesEarned += gained;

        String text = (isCrit ? "CRIT! +" : "+") + formatCookies(gained);
        floatingTexts.add(new FloatingText(text, x, y, isCrit));

        int particleCount = isCrit ? 28 : 10;
        for (int i = 0; i < particleCount; i++) {
            particles.add(new Particle(x, y, isCrit));
        }
    }

    public void clickGoldenCookie() {
        goldenFrenzy = true;
        goldenFrenzyTimer = 30f;
        long bonus = (long)(cookiesPerSecond * 60 * prestigeBonus);
        if (bonus < 1000) bonus = 1000;
        cookies += bonus;
        totalCookiesEarned += bonus;
        goldenCookie.visible = false;
        floatingTexts.add(new FloatingText("FRENZY! x7 for 30s!", 
            goldenCookie.x, goldenCookie.y + 40, true));
    }

    public boolean tryBuy(int buildingIndex) {
        Building b = buildings[buildingIndex];
        if (cookies >= b.currentCost) {
            cookies -= b.currentCost;
            b.buy();
            updateCps();
            updateClickPower();
            return true;
        }
        return false;
    }

    public boolean tryPrestige() {
        if (totalCookiesEarned >= getPrestigeCost()) {
            prestigeLevel++;
            prestigeBonus = 1f + prestigeLevel * 0.25f;
            cookies = 0;
            totalCookiesEarned = 0;
            for (Building b : buildings) {
                b.count = 0;
                b.currentCost = b.baseCost;
            }
            updateCps();
            updateClickPower();
            return true;
        }
        return false;
    }

    public long getPrestigeCost() {
        return 1000000000L * (long)Math.pow(10, prestigeLevel);
    }

    public String formatCookies(long n) {
        if (n < 1000) return "" + n;
        if (n < 1_000_000) return String.format("%.1fK", n / 1000.0);
        if (n < 1_000_000_000) return String.format("%.1fM", n / 1_000_000.0);
        if (n < 1_000_000_000_000L) return String.format("%.2fB", n / 1_000_000_000.0);
        return String.format("%.2fT", n / 1_000_000_000_000.0);
    }

    public String formatCps(double cps) {
        double actual = cps * prestigeBonus * (goldenFrenzy ? goldenFrenzyMultiplier : 1.0);
        if (actual < 1000) return String.format("%.1f", actual);
        if (actual < 1_000_000) return String.format("%.1fK", actual / 1000.0);
        return String.format("%.2fM", actual / 1_000_000.0);
    }
}
