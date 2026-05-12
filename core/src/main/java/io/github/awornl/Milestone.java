package io.github.awornl;

public class Milestone {

    public String title;
    public String description;
    public long requiredCookies;
    public boolean unlocked;
    public float showTimer;

    public Milestone(String title, String description, long requiredCookies) {
        this.title = title;
        this.description = description;
        this.requiredCookies = requiredCookies;
        this.unlocked = false;
        this.showTimer = 0f;
    }

    public boolean check(long totalCookiesEarned) {
        if (!unlocked && totalCookiesEarned >= requiredCookies) {
            unlocked = true;
            showTimer = 4f;
            return true;
        }
        return false;
    }
}
