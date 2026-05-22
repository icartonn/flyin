package io.github.awornl;

public class Achievement {
    public String id;
    public String title;
    public String description;
    public boolean unlocked;
    public float showTimer;
    public long requiredValue;
    public int type;
    public int buildingIndex;

    public Achievement(String id, String title, String description, int type, long requiredValue, int buildingIndex) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.requiredValue = requiredValue;
        this.buildingIndex = buildingIndex;
        this.unlocked = false;
        this.showTimer = 0f;
    }

    public boolean check(GameState state) {
        if (unlocked) return false;
        double current = 0;
        switch (type) {
            case 0: current = state.totalCookiesEarned; break;
            case 1: current = state.totalClicks; break;
            case 2: if (buildingIndex >= 0 && buildingIndex < state.buildings.length)
                current = state.buildings[buildingIndex].count; break;
            case 3: current = state.prestigeLevel; break;
        }
        if (current >= requiredValue) {
            unlocked = true;
            showTimer = 4f;
            return true;
        }
        return false;
    }
}
