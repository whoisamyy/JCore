package ru.whoisamyy.api.utils.enums;

public enum DemonDifficulty implements Difficulty {
    HARD_DEMON(0, 1, 6),
    EASY_DEMON(3, 2, 7),
    MEDIUM_DEMON(4, 3, 8),
    INSANE_DEMON(5, 4, 9),
    EXTREME_DEMON(6, 5, 10);

    final int numerator;
    final int sequenceNumber;
    final int mpn;

    DemonDifficulty(int numerator, int sequenceNumber, int mpn) {
        this.numerator = numerator;
        this.sequenceNumber = sequenceNumber;
        this.mpn = mpn;
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

    public static DemonDifficulty sequenceNumberToDemonDifficulty(int val) {
        switch (val) {
            case 1 -> {return EASY_DEMON;}
            case 2 -> {return MEDIUM_DEMON;}
            case 3 -> {return HARD_DEMON;}
            case 4 -> {return INSANE_DEMON;}
            case 5 -> {return EXTREME_DEMON;}
        }
        return HARD_DEMON;
    }

    @Override
    public int getMPN() {
        return this.mpn;
    }
}
