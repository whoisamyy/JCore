package ru.whoisamyy.api.utils.enums;

import ru.whoisamyy.api.utils.exceptions.InvalidValueException;

public enum ModType {
    NO_MOD(0),
    MOD(1),
    ELDER(2);


    private final int val;
    private ModType(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public static ModType getModType(int val) throws InvalidValueException {
        switch (val) {
            case 0 -> {
                return NO_MOD;
            }
            case 1 -> {
                return MOD;
            }
            case 2 -> {
                return ELDER;
            }
            default -> throw new InvalidValueException("No such type! " + val);
        }
    }
}
