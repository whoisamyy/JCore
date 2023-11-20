package ru.whoisamyy.api.utils.comparators;

import ru.whoisamyy.api.gd.objects.Account;

import java.util.Comparator;

public class AccountComparators {
    public static class StarsComparator implements Comparator<Account> {

        @Override
        public int compare(Account o1, Account o2) {
            return Integer.compare(o1.getStars(), o2.getStars());
        }
    }

    public static class CPComparator implements Comparator<Account> { //ALL LEGAL CP = Creator Poins
        @Override
        public int compare(Account o1, Account o2) {
            return Integer.compare(o1.getCreatorPoints(), o2.getCreatorPoints());
        }
    }
}
