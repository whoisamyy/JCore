package ru.whoisamyy.api.utils.comparators;

import ru.whoisamyy.api.gd.objects.Level;

import java.util.Comparator;

public class LevelComparators {
    public static class DownloadsComparator implements Comparator<Level> {
        @Override
        public int compare(Level o1, Level o2) {
            return Integer.compare(o1.getDownloads(), o2.getDownloads());
        }
    }

    public static class DownloadsComparatorDescension implements Comparator<Level> {
        @Override
        public int compare(Level o1, Level o2) {
            return Integer.compare(o2.getDownloads(), o1.getDownloads());
        }
    }

    public static class LikesComparator implements Comparator<Level> {
        @Override
        public int compare(Level o1, Level o2) {
            if (o1.getLikes()==o2.getLikes()) {
                return Integer.compare(o1.getLevelID(), o2.getLevelID());
            }
            return Integer.compare(o1.getLikes(), o2.getLikes());
        }
    }

    public static class LikesComparatorDescension implements Comparator<Level> {
        @Override
        public int compare(Level o1, Level o2) {
            if (o1.getLikes()==o2.getLikes()) {
                return Integer.compare(o2.getLevelID(), o1.getLevelID());
            }
            return Integer.compare(o2.getLikes(), o1.getLikes());
        }
    }

    public static class IDComparator implements Comparator<Level> {
        @Override
        public int compare(Level o1, Level o2) {
            return Integer.compare(o1.getLevelID(), o2.getLevelID());
        }
    }

    public static class IDComparatorDescension implements Comparator<Level> {
        @Override
        public int compare(Level o1, Level o2) {
            return Integer.compare(o2.getLevelID(), o1.getLevelID());
        }
    }

    public static class FeatureScoreComparator implements Comparator<Level> {
        @Override
        public int compare(Level o1, Level o2) {
            return Integer.compare(o1.getFeatureScore(), o2.getFeatureScore());
        }
    }
}

