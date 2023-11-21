package ru.whoisamyy.api.utils.enums;

import ru.whoisamyy.api.utils.exceptions.InvalidValueException;

public enum ItemType {
    LEVEL(1),
    LEVEL_COMMENT(2),
    ACC_COMMENT(3);

    final int val;
    private ItemType(int val) {
        this.val = val;
    }

    public static ItemType getLikeType(int val) throws InvalidValueException {
        switch (val) {
            case 1 -> {
                return LEVEL;
            }
            case 2-> {
                return LEVEL_COMMENT;
            }
            case 3-> {
                return ACC_COMMENT;
            }
            default -> {
                throw new InvalidValueException(val + " no such type!");
            }
        }
    }
}
