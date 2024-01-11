package ru.whoisamyy.api.utils.enums;

public enum LevelDifficulty implements Difficulty {
    AUTO(-3, 0),
    UNRATED(0, -1),
    EASY(10, 1),
    NORMAL(20, 2),
    HARD(30, 3),
    HARDER(40, 4),
    INSANE(50, 5);

    final int numerator;
    final int mpn;
    LevelDifficulty(int numerator, int mpn) {
        this.numerator = numerator;
        this.mpn = mpn;
    }

    public int toInt() {
        return numerator;
    }

    public static LevelDifficulty toLevelDifficulty(int val) {
        switch (val) {
            case 0 -> {return UNRATED;}
            case 1 -> {return EASY;}
            case 2 -> {return NORMAL;}
            case 3 -> {return HARD;}
            case 4 -> {return HARDER;}
            case 5 -> {return INSANE;}
            case -3 -> {return AUTO;}
        }
        return UNRATED;
    }

    @Override
    public int getMPN() {
        return this.mpn;
    }
}
