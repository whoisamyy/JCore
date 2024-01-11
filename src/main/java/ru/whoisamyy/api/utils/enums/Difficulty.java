package ru.whoisamyy.api.utils.enums;

/**
 * Base interface of {@code DemonDifficulty} and {@code LevelDifficulty}
 * @see DemonDifficulty
 * @see LevelDifficulty
 */
public interface Difficulty {
    static Difficulty fromInt(int i) {
        switch (i) {
            case 0 -> {
                return LevelDifficulty.AUTO;
            }
            case 1 -> {
                return LevelDifficulty.EASY;
            }
            case 3 -> {
                return LevelDifficulty.HARD;
            }
            case 4 -> {
                return LevelDifficulty.HARDER;
            }
            case 5 -> {
                return LevelDifficulty.INSANE;
            }
            case 6 -> {
                return DemonDifficulty.HARD_DEMON;
            }
            case 7 -> {
                return DemonDifficulty.EASY_DEMON;
            }
            case 8 -> {
                return DemonDifficulty.MEDIUM_DEMON;
            }
            case 9 -> {
                return DemonDifficulty.INSANE_DEMON;
            }
            case 10 -> {
                return DemonDifficulty.EXTREME_DEMON;
            }
            default -> {
                return LevelDifficulty.NORMAL;
            }
        }
    }

    int getMPN();
}
