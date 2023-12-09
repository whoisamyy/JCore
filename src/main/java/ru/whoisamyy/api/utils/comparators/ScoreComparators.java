package ru.whoisamyy.api.utils.comparators;

import ru.whoisamyy.api.gd.objects.Score;

import java.util.Comparator;

public class ScoreComparators  {
    public static class UserIDComparator implements Comparator<Score> {
        @Override
        public int compare(Score o1, Score o2) {
            return Integer.compare(o1.getPlayer().getUserID(), o2.getPlayer().getUserID());
        }
    }

    public static class HighestPercentageComparator implements Comparator<Score> { // use this only when Score.percentage != "---"
        @Override
        public int compare(Score o1, Score o2) {
            return Integer.compare(o1.getPercentage(), o2.getPercentage());
        }
    }
}
