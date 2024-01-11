package ru.whoisamyy.api.utils;

import ru.whoisamyy.api.utils.exceptions.InvalidValueException;

public class RGBColor {
    int r;
    int g;
    int b;

    public RGBColor(int r, int g, int b) throws InvalidValueException {
        if (r>255 || g>255 || b>255) throw new InvalidValueException("Impossible value");
        this.r=r;
        this.g=g;
        this.b=b;
    }

    public RGBColor(String rgb) {
        String[] rgbs = rgb.split(",");
        int r, g, b;
        r = Integer.parseInt(rgbs[0]);
        g = Integer.parseInt(rgbs[1]);
        b = Integer.parseInt(rgbs[2]);
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    public String toString() {
        return r+","+g+","+b;
    }
}
