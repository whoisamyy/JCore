package ru.whoisamyy.api.utils.comparators;

import ru.whoisamyy.api.gd.objects.MapPack;

import java.util.Comparator;

public class MapPackComparators {
    public static class IDComparator implements Comparator<MapPack> {
        @Override
        public int compare(MapPack o1, MapPack o2) {
            return Integer.compare(o1.getId(), o2.getId());
        }
    }
}
