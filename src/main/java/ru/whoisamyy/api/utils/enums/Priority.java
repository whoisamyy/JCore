package ru.whoisamyy.api.utils.enums;

public enum Priority {
    HIGHEST(0),
    HIGH(1),
    MEDIUM(2),
    LOW(3),
    LOWEST(4);
    final int val;

    Priority(int val) {
        this.val = val;
    }

    public static Priority getByValue(int val) {
        switch (val) {
            case 1 -> {
                return HIGH;
            }
            case 2 -> {
                return MEDIUM;
            }
            case 3 -> {
                return LOW;
            }
            case 4 -> {
                return LOWEST;
            }
            default -> {return HIGHEST;}
        }
    }
}
