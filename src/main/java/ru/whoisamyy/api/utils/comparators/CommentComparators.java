package ru.whoisamyy.api.utils.comparators;

import ru.whoisamyy.api.gd.objects.Comment;

import java.util.Comparator;

public class CommentComparators {
    public static class IDComparatorDescension implements Comparator<Comment> {
        @Override
        public int compare(Comment o1, Comment o2) {
            return Integer.compare(o2.getID(), o1.getID());
        }
    }

    public static class LikeComparator implements Comparator<Comment> {
        @Override
        public int compare(Comment o1, Comment o2) {
            return Integer.compare(o1.getLikes(), o2.getLikes());
        }
    }
}
