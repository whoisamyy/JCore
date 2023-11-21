package ru.whoisamyy.api.utils.enums;

import ru.whoisamyy.api.utils.exceptions.InvalidValueException;

public enum Length {
    TINY(0),
    SHORT(1),
    MEDIUM(2),
    LONG(3),
    XL(4);

    final int val;

    private Length(int val) {
        this.val = val;
    }

    public int toInt() {
        return this.val;
    }
    public static Length toLength(int val) throws InvalidValueException {
        switch (val) {
            case 0 -> {
                return TINY;
            }
            case 1 -> {
                return SHORT;
            }
            case 2 -> {
                return MEDIUM;
            }
            case 3 -> {
                return LONG;
            }
            case 4 -> {
                return XL;
            }
            default -> throw new InvalidValueException(val + " no such length!");
        }
    }
}
