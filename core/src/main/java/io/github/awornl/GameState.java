package io.github.awornl;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import java.util.ArrayList;
import java.util.List;

public class GameState {

    public double cookies = 0;
    public double totalCookiesEarned = 0;
    public long totalClicks = 0;
    public long cookiesPerClick = 1;
    public double cookiesPerSecond = 0;

    public int prestigeLevel = 0;
    public float prestigeBonus = 1f;

    public boolean goldenFrenzy = false;
    public float goldenFrenzyMultiplier = 7f;
    public float goldenFrenzyTimer = 0f;

    public Building[] buildings;
    public Milestone[] milestones;
    public List<Achievement> achievements;

    public GoldenCookie goldenCookie;
    public Array<FloatingText> floatingTexts = new Array<>();
    public Array<Particle> particles = new Array<>();

    public float autoSaveTimer = 0f;
    public float cpsTimer = 0f;

    public long lastMilestoneShown = -1;
    public Milestone activeMilestone = null;
    public Achievement activeAchievement = null;

    public GameState() {
        goldenCookie = new GoldenCookie();
        initMilestones();
        initAchievements();
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
            new Milestone("Cookie Overlord",  "Earn 1,000,000,000,000 cookies", 1000000000000L),
        };
    }

    void initAchievements() {
        achievements = new ArrayList<>();
        achievements.add(new Achievement("click_100", "Click Master", "100 clicks", 1, 100, -1));
        achievements.add(new Achievement("click_1000", "Click Maniac", "1000 clicks", 1, 1000, -1));
        achievements.add(new Achievement("cookies_1M", "Millionaire", "1M total cookies", 0, 1_000_000L, -1));
        achievements.add(new Achievement("cookies_1B", "Billionaire", "1B total cookies", 0, 1_000_000_000L, -1));
        achievements.add(new Achievement("cursor_10", "Cursor Army", "10 Cursors", 2, 10, 0));
        achievements.add(new Achievement("grandma_10", "Grandma's Army", "10 Grandmas", 2, 10, 1));
        achievements.add(new Achievement("prestige_1", "Ascended", "First prestige", 3, 1, -1));
    }

    public void update(float delta) {
        double cps = cookiesPerSecond * prestigeBonus;
        if (goldenFrenzy) cps *= goldenFrenzyMultiplier;
        addCookies(cps * delta);

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
        checkAchievements(delta);
        updateClickPower();
        updateCps();
    }

    void checkMilestones(float delta) {
        if (activeMilestone != null) {
            activeMilestone.showTimer -= delta;
            if (activeMilestone.showTimer <= 0) activeMilestone = null;
        }

        for (Milestone m : milestones) {
            if (m.check((long)totalCookiesEarned) && activeMilestone == null) {
                activeMilestone = m;
            }
        }
    }

    void checkAchievements(float delta) {
        if (activeAchievement != null) {
            activeAchievement.showTimer -= delta;
            if (activeAchievement.showTimer <= 0) activeAchievement = null;
        }
        for (Achievement a : achievements) {
            if (a.check(this) && activeAchievement == null) {
                activeAchievement = a;
            }
        }
    }

    void updateClickPower() {
        long base = 1 + buildings[0].count;
        cookiesPerClick = (long)(base * prestigeBonus);
        if (goldenFrenzy) cookiesPerClick = SafeMath.multiply(cookiesPerClick, (long)goldenFrenzyMultiplier);
    }

    void updateCps() {
        double total = 0;
        for (Building b : buildings) {
            total += b.getTotalCps();
        }
        cookiesPerSecond = total;
    }

    public void clickCookie(float x, float y) {
        totalClicks++;
        boolean isCrit = Math.random() < 0.05 + prestigeLevel * 0.01;
        long gained = cookiesPerClick * (isCrit ? 5 : 1);
        addCookies(gained);

        String text = (isCrit ? "CRIT! +" : "+") + formatCookies(gained);
        floatingTexts.add(new FloatingText(text, x, y, isCrit));

        int particleCount = isCrit ? 28 : 10;
        for (int i = 0; i < particleCount; i++) {
            particles.add(new Particle(x, y, isCrit));
        }
    }

    private void addCookies(double amount) {
        cookies += amount;
        totalCookiesEarned += amount;
    }

    public void clickGoldenCookie() {
        goldenFrenzy = true;
        goldenFrenzyTimer = 30f;
        double bonus = cookiesPerSecond * 60 * prestigeBonus;
        if (bonus < 1000) bonus = 1000;
        addCookies(bonus);
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
            totalClicks = 0;
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
        long cost = 1_000_000_000L;
        for (int i = 0; i < prestigeLevel; i++) {
            cost = SafeMath.multiply(cost, 10L);
            if (cost >= Long.MAX_VALUE / 10) return Long.MAX_VALUE;
        }
        return cost;
    }

    public String formatCookies(double n) {
        if (n < 1000) return "" + (long)n;
        if (n < 1_000_000) return String.format("%.1fK", n / 1000.0);
        if (n < 1_000_000_000) return String.format("%.1fM", n / 1_000_000.0);
        if (n < 1_000_000_000_000L) return String.format("%.2fB", n / 1_000_000_000.0);
        return String.format("%.2fT", n / 1_000_000_000_000.0);
    }

    public String formatCps(double cps) {
        double actual = cps * prestigeBonus * (goldenFrenzy ? goldenFrenzyMultiplier : 1.0);
        if (actual < 1000) return String.format("%.1f", actual);
        if (actual < 1_000_000) return String.format("%.1fK", actual / 1000.0);
        if (actual < 1_000_000_000) return String.format("%.2fM", actual / 1_000_000.0);
        return String.format("%.2fB", actual / 1_000_000_000.0);
    }

    Preferences prefs = Gdx.app.getPreferences("cookie_save");

    public void save() {
        prefs.putLong("cookies", (long)cookies);
        prefs.putLong("totalCookiesEarned", (long)totalCookiesEarned);
        prefs.putLong("totalClicks", totalClicks);
        prefs.putLong("cookiesPerClick", cookiesPerClick);
        prefs.putInteger("prestigeLevel", prestigeLevel);
        prefs.putFloat("prestigeBonus", prestigeBonus);
        if (buildings != null) {
            prefs.putInteger("buildings_count", buildings.length);
            for (int i = 0; i < buildings.length; i++) {
                prefs.putInteger("building_count_" + i, buildings[i].count);
                prefs.putLong("building_cost_" + i, buildings[i].currentCost);
            }
        }
        for (Achievement a : achievements) {
            prefs.putBoolean("ach_" + a.id, a.unlocked);
        }
        prefs.flush();
    }

    public void load() {
        cookies = prefs.getLong("cookies", 0);
        totalCookiesEarned = prefs.getLong("totalCookiesEarned", 0);
        totalClicks = prefs.getLong("totalClicks", 0);
        cookiesPerClick = prefs.getLong("cookiesPerClick", 1);
        prestigeLevel = prefs.getInteger("prestigeLevel", 0);
        prestigeBonus = prefs.getFloat("prestigeBonus", 1f);
        if (buildings != null) {
            for (int i = 0; i < buildings.length; i++) {
                buildings[i].count = prefs.getInteger("building_count_" + i, 0);
                buildings[i].currentCost = prefs.getLong("building_cost_" + i, buildings[i].baseCost);
            }
        }
        for (Achievement a : achievements) {
            a.unlocked = prefs.getBoolean("ach_" + a.id, false);
        }
        updateCps();
        updateClickPower();
    }
}
