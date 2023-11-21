package ru.whoisamyy.api.utils.enums;

import ru.whoisamyy.api.utils.exceptions.InvalidValueException;

public enum LeaderboardType {
    TOP("top"),
    RELATIVE("relative"),
    FRIENDS("friends"),
    CREATORS("creators"),
    WEEK("week");

    String val;

    LeaderboardType(String val) {
        this.val = val;
    }

    public static LeaderboardType getLeaderboardType(String val) throws InvalidValueException {
        switch (val) {
            case "top" -> {
                return TOP;
            }
            case "relative" -> {
                return RELATIVE;
            }
            case "friends" -> {
                return FRIENDS;
            }
            case "creators" -> {
                return CREATORS;
            }
            default -> throw new InvalidValueException(val+" no such value!!");
        }
    }

    public static LeaderboardType getLeaderboardType(int val) throws InvalidValueException {
        switch (val) {
            case 0 -> {
                return FRIENDS;
            }
            case 1 -> {
                return TOP;
            }
            case 2 -> {
                return WEEK;
            }
            case 3 -> {
                return RELATIVE;
            }
            case 4 -> {
                return CREATORS;
            }
            default -> throw new InvalidValueException(val+" no such value!!");
        }
    }
}
