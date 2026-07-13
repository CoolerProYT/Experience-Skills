package com.coolerpromc.experienceskills.util;

public class XpMath {
    public static int getLevel(int totalPoints) {
        if (totalPoints <= 0) return 0;
        if (totalPoints <= 352) {
            return (int) Math.floor(Math.sqrt(totalPoints + 9) - 3);
        } else if (totalPoints <= 1628) {
            return (int) Math.floor((81 + Math.sqrt(40.0 * totalPoints - 7839)) / 10.0);
        } else {
            return (int) Math.floor((325 + Math.sqrt(72.0 * totalPoints - 54215)) / 18.0);
        }
    }

    public static int getXpNeededForNextLevel(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else if (level >= 15) {
            return 37 + (level - 15) * 5;
        } else {
            return 7 + level * 2;
        }
    }

    public static int getTotalForLevel(int level) {
        int total = 0;
        for (int l = 0; l < level; l++) total += getXpNeededForNextLevel(l);
        return total;
    }

    public static float getProgress(int totalPoints) {
        int level = getLevel(totalPoints);
        int base = getTotalForLevel(level);
        int needed = getXpNeededForNextLevel(level);
        return (totalPoints - base) / (float) needed;
    }
}