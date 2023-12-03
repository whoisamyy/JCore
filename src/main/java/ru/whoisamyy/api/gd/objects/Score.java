package ru.whoisamyy.api.gd.objects;

import lombok.Getter;
import ru.whoisamyy.api.gd.misc.GDObject;
import ru.whoisamyy.api.utils.comparators.AccountComparators;
import ru.whoisamyy.api.utils.comparators.ScoreComparators;
import ru.whoisamyy.api.utils.enums.LeaderboardType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.TreeSet;

public class Score extends GDObject { //it does because 1. can be seen in gd 2. can upload from gd
    //public static Connection conn;

    private static final Hashtable<Integer, Score> scores = new Hashtable<>();
    public static int lastScoreID;

    @Getter Account player; //keys 1, 2, 9, 10, 11 and 16
    @Getter String percentage = "---";
    @Getter int levelID = 0;
    @Getter int rank;
    @Getter int coins;
    @Getter String age;

    public Score() {}

    public static void getScores() {
        if (!scores.isEmpty()) scores.clear();
        for (int i = 1; i <= lastScoreID; i++) {
            scores.put(i, map(i));
        }
    }

    public static Hashtable<Integer, Score> getScoresHashtable() {
        getScores();
        return scores;
    }

    public int getHighestPercentage() {
        String[] percents = getPercentage().split(",");
        TreeSet<Integer> sortedPercents = new TreeSet<>(Comparator.reverseOrder());
        for (String percent : percents) {
            sortedPercents.add(Integer.parseInt(percent));
        }
        return sortedPercents.first();
    }

    public Score(Account player, String percentage, int levelID, int rank, int coins, String age) { //mapping
        this.player = player;
        this.percentage = percentage;
        this.levelID = levelID;
        this.rank = rank;
        this.coins = coins;
        this.age = age;
    }

    public Score(Account player, String percentage, int rank, int coins, String age) { //levelscore
        this.player = player;
        this.percentage = percentage;
        this.rank = rank;
        this.coins = coins;
        this.age = age;
    }

    public Score(Account player, int rank, String age) { //other
        this.player = player;
        this.rank = rank;
        this.age = age;
    }

    public static String getLevelScores(Account player, LeaderboardType type, int count, int levelID, int percent, int attempts, int coins) {
        try(PreparedStatement ps = conn.prepareStatement("INSERT INTO scores (accountID, levelID, percent, attempts, coins, isAcc) VALUES(?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, player.getUserID());
            ps.setInt(2, levelID);
            ps.setInt(3, percent);
            ps.setInt(4, attempts);
            ps.setInt(5, coins);
            ps.setBoolean(6, false);

            ps.execute();
        } catch (SQLException e) {
            return "-1";
        }


        TreeSet<Score> levelScores = new TreeSet<>();
        TreeSet<Score> sortedLevelScores = new TreeSet<>(new ScoreComparators.UserIDComparator());
        for (Score score : scores.values()) {
            if (score.percentage!="---" && score.getLevelID()==levelID)
                levelScores.add(score);
        }

        switch (type) {
            case FRIENDS -> {
                levelScores.removeIf(score -> !score.player.getFriendsHashtable().contains(player));
            }
            case TOP -> {
                sortedLevelScores = new TreeSet<>(new ScoreComparators.HighestPercentageComparator());
                sortedLevelScores.addAll(levelScores);
            }
            default -> sortedLevelScores = new TreeSet<>(levelScores);
        }

        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Score score : sortedLevelScores) {
            if (i>=count);
            sb.append(score.toString()).append("|");
            i++;
        }

        try {
            sb.deleteCharAt(sb.length() - 1);
        } catch (IndexOutOfBoundsException ignored) {}

        return sb.toString();
    }

    public static String getLevelScores(int playerID, LeaderboardType type, int count, int levelID, int percent, int attempts, int coins) {
        return getLevelScores(Account.map(playerID, true), type, count, levelID, percent, attempts, coins);
    }

    public static String getScores(Account player, LeaderboardType type, int count) {
        TreeSet<Account> accounts = new TreeSet<>(new AccountComparators.StarsComparator()); //za4?
        accounts.addAll(Account.getAccountsHashtable().values());
        TreeSet<Account> sortedAccounts;

        switch (type) {
            case TOP, RELATIVE -> {
                sortedAccounts = new TreeSet<>(new AccountComparators.StarsComparator());
                sortedAccounts.addAll(accounts);
            }
            case FRIENDS -> {
                sortedAccounts = new TreeSet<>(new AccountComparators.StarsComparator());
                sortedAccounts.addAll(accounts);
                sortedAccounts.removeIf(acc -> !player.getFriendsHashtable().contains(acc)); //never works cuz u have no friends :gg: :trollface: :heh: :наивные_хех)):
            }
            case CREATORS -> {
                sortedAccounts = new TreeSet<>(new AccountComparators.CPComparator());
                sortedAccounts.addAll(accounts);
            }
            default -> {
                sortedAccounts = accounts;
            }
        }

        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Account acc : sortedAccounts) {
            if (i>= count) break;
            sb.append(acc.toString()).append("|");
            i++;
        }

        return sb.toString();
    }

    public static String getScores(int playerID, LeaderboardType type, int count) {
        return getScores(Account.map(playerID, true), type, count);
    }

    public String toString() {
        String sb = "1:" + getPlayer().getUsername() + ":" +
                "2:" + getPlayer().getUserID() + ":" +
                "3:" + getHighestPercentage() + ":" +
                "6:" + getRank() + ":" +
                "9:" + getPlayer().getIconID() + ":" +
                "10:" + getPlayer().getPlayerColor() + ":" +
                "11:" + getPlayer().getPlayerColor2() + ":" +
                "13:" + getCoins() + ":" +
                "14:" + getPlayer().getIconType() + ":" +
                "15:" + getPlayer().getSpecial() + ":" +
                "16:" + getPlayer().getUserID() + ":" +
                "42:" + getAge();

        return sb;
    }

    public String toString(String sep) {
        String sb = "1" + sep + getPlayer().getUsername() + sep +
                "2" + sep + getPlayer().getUserID() + sep +
                "3" + sep + getHighestPercentage() + sep +
                "6" + sep + getRank() + sep +
                "9" + sep + getPlayer().getIconID() + sep +
                "10" + sep + getPlayer().getPlayerColor() + sep +
                "11" + sep + getPlayer().getPlayerColor2() + sep +
                "13" + sep + getCoins() + sep +
                "14" + sep + getPlayer().getIconType() + sep +
                "15" + sep + getPlayer().getSpecial() + sep +
                "16" + sep + getPlayer().getUserID() + sep +
                "42" + sep + getAge();

        return sb;
    }

    public static Score map(int id) {
        if (scores.containsKey(id)) return scores.get(id);
        String sql = "SELECT * FROM scores WHERE scoreID = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return new Score(Account.map(rs.getInt("accountID"), true), rs.getString("progresses"), rs.getInt("levelID"), rs.getInt("coins"), "0");
            return new Score();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
