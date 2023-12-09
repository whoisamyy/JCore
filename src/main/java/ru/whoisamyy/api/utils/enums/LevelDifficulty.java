package ru.whoisamyy.api.utils.enums;

public enum LevelDifficulty {
    AUTO(-3),
    UNRATED(0),
    EASY(10),
    NORMAL(20),
    HARD(30),
    HARDER(40),
    INSANE(50);

    final int numerator;
    LevelDifficulty(int numerator) {
        this.numerator = numerator;
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
}
