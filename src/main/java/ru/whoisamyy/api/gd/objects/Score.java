package ru.whoisamyy.api.gd.objects;

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
    public static Connection conn;

    private static Hashtable<Integer, Score> scores = new Hashtable<>();
    public static int lastScoreID;

    Account player; //keys 1, 2, 9, 10, 11 and 16
    String percentage = "---";
    int levelID = 0;
    int rank;
    int coins;
    String age;

    public Account getPlayer() {
        return player;
    }

    public String getPercentage() {
        return percentage;
    }

    public int getRank() {
        return rank;
    }

    public int getCoins() {
        return coins;
    }

    public String getAge() {
        return age;
    }

    public int getLevelID() {
        return levelID;
    }

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
        TreeSet<Score> sortedLevelScores = null;
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

        return sb.substring(0, sb.toString().length()-1);
    }

    public static String getLevelScores(int playerID, LeaderboardType type, int count, int levelID, int percent, int attempts, int coins) {
        return getLevelScores(Account.map(playerID, true), type, count, levelID, percent, attempts, coins);
    }

    public static String getScores(Account player, LeaderboardType type, int count) {
        TreeSet<Account> accounts = new TreeSet<>(Account.getAccountsHashtable().values()); //za4?
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
        StringBuilder sb = new StringBuilder();
        sb.append("1:").append(getPlayer().getUsername()).append(":");
        sb.append("2:").append(getPlayer().getUserID()).append(":");
        sb.append("3:").append(getHighestPercentage()).append(":");
        sb.append("6:").append(getRank()).append(":");
        sb.append("9:").append(getPlayer().getIconID()).append(":");
        sb.append("10:").append(getPlayer().getPlayerColor()).append(":");
        sb.append("11:").append(getPlayer().getPlayerColor2()).append(":");
        sb.append("13:").append(getCoins()).append(":");
        sb.append("14:").append(getPlayer().getIconType()).append(":");
        sb.append("15:").append(getPlayer().getSpecial()).append(":");
        sb.append("16:").append(getPlayer().getUserID()).append(":");
        sb.append("42:").append(getAge());

        return sb.toString();
    }

    public String toString(String sep) {
        StringBuilder sb = new StringBuilder();
        sb.append("1").append(sep).append(getPlayer().getUsername()).append(sep);
        sb.append("2").append(sep).append(getPlayer().getUserID()).append(sep);
        sb.append("3").append(sep).append(getHighestPercentage()).append(sep);
        sb.append("6").append(sep).append(getRank()).append(sep);
        sb.append("9").append(sep).append(getPlayer().getIconID()).append(sep);
        sb.append("10").append(sep).append(getPlayer().getPlayerColor()).append(sep);
        sb.append("11").append(sep).append(getPlayer().getPlayerColor2()).append(sep);
        sb.append("13").append(sep).append(getCoins()).append(sep);
        sb.append("14").append(sep).append(getPlayer().getIconType()).append(sep);
        sb.append("15").append(sep).append(getPlayer().getSpecial()).append(sep);
        sb.append("16").append(sep).append(getPlayer().getUserID()).append(sep);
        sb.append("42").append(sep).append(getAge());

        return sb.toString();
    }

    public static Score map(int id) {
        String sql = "SELECT * FROM scores WHERE scoreID = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            return new Score(Account.map(rs.getInt("accountID"), true), rs.getString("progresses"), rs.getInt("levelID"), rs.getInt("coins"), "0");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
