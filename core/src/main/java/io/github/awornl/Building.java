package io.github.awornl;

import com.badlogic.gdx.graphics.Texture;

public class Building {

    public String name;
    public String description;
    public Texture icon;
    public int count;
    public long baseCost;
    public long currentCost;
    public double baseCps;

    public Building(String name, String description, Texture icon, long baseCost, double baseCps) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.count = 0;
        this.baseCost = baseCost;
        this.currentCost = baseCost;
        this.baseCps = baseCps;
    }

    public boolean canBuy(long cookies) {
        return cookies >= currentCost;
    }

    public void buy() {
        count++;
        currentCost = (long)(baseCost * Math.pow(1.15, count));
    }

    public double getTotalCps() {
        return baseCps * count;
    }

    public long getNextCost() {
        return currentCost;
    }
}
