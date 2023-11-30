package ru.whoisamyy.api.utils.enums;

public enum DemonDifficulty {
    HARD_DEMON(0, 1),
    EASY_DEMON(3, 2),
    MEDIUM_DEMON(4, 3),
    INSANE_DEMON(5, 4),
    EXTREME_DEMON(6, 5);

    final int numerator;
    final int sequenceNumber;

    DemonDifficulty(int numerator, int sequenceNumber) {
        this.numerator = numerator;
        this.sequenceNumber = sequenceNumber;
    }

    public int toInt() {
        return numerator;
    }

    public static DemonDifficulty valToDemonDifficulty(int val) {
        switch (val) {
            case 0 -> {return HARD_DEMON;}
            case 3 -> {return EASY_DEMON;}
            case 4 -> {return MEDIUM_DEMON;}
            case 5 -> {return INSANE_DEMON;}
            case 6 -> {return EXTREME_DEMON;}
        }
        return HARD_DEMON;
    }

    public static DemonDifficulty sequenceNumberToDemonDifficulty(int val) { //это пиздос
        switch (val) {
            case 1 -> {return EASY_DEMON;}
            case 2 -> {return MEDIUM_DEMON;}
            case 3 -> {return HARD_DEMON;}
            case 4 -> {return INSANE_DEMON;}
            case 5 -> {return EXTREME_DEMON;}
        }
        return HARD_DEMON;
    }
}
