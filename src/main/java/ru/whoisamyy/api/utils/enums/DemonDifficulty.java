package ru.whoisamyy.api.utils.enums;

public enum DemonDifficulty {
    HARD_DEMON(0),
    EASY_DEMON(3),
    MEDIUM_DEMON(4),
    INSANE_DEMON(5),
    EXTREME_DEMON(6);

    final int numerator;

    DemonDifficulty(int numerator) {
        this.numerator = numerator;
    }

    public int toInt() {
        return numerator;
    }

    public static DemonDifficulty toDemonDifficulty(int val) {
        switch (val) {
            case 0 -> {return HARD_DEMON;}
            case 3 -> {return EASY_DEMON;}
            case 4 -> {return MEDIUM_DEMON;}
            case 5 -> {return INSANE_DEMON;}
            case 6 -> {return EXTREME_DEMON;}
        }
        return HARD_DEMON;
    }
}
