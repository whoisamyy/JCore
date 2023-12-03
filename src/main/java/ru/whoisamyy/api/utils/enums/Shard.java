package ru.whoisamyy.api.utils.enums;

public enum Shard {
    FIRE(0),
    ICE(1),
    POISON(2),
    SHADOW(3),
    LAVA(4);

    public final int val;
    Shard(int val) {
        this.val = val;
    }

    public static Shard intToShard(int val) {
        switch (val) {
            case 0 -> {return FIRE;}
            case 1 -> {return ICE;}
            case 2 -> {return POISON;}
            case 3 -> {return SHADOW;}
            case 4 -> {return LAVA;}
        }
        return FIRE;
    }
}
