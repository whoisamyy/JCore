package ru.whoisamyy.api.gd.misc;

import ru.whoisamyy.api.gd.objects.GDObject;

public abstract class Reward extends GDObject {
    protected static final String rs = "leplo";
    protected static final String udid = "lepslox228kizaru";

    protected int timeLeft = 24*60*60;

    public Reward() {}
}
