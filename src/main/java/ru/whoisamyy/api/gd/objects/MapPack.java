package ru.whoisamyy.api.gd.objects;

import lombok.Getter;
import lombok.Setter;
import ru.whoisamyy.api.utils.RGBColor;
import ru.whoisamyy.api.utils.Utils;
import ru.whoisamyy.api.utils.comparators.MapPackComparators;
import ru.whoisamyy.api.utils.enums.Difficulty;
import ru.whoisamyy.api.utils.exceptions.InvalidValueException;
import ru.whoisamyy.core.Core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Getter
public class MapPack extends GDObject {
    public static Hashtable<Integer, MapPack> mapPacks = getMapPacksHashtable();

    @Setter @Getter private static int lastMapPackID;

    private int id;
    private String name;
    private List<Level> levels;
    private int rewardStars;
    private int rewardCoins;
    private Difficulty displayedDiff;
    private RGBColor textColor;
    private RGBColor barColor;

    public static Hashtable<Integer, MapPack> getMapPacksHashtable() {
        if (mapPacks==null) mapPacks = new Hashtable<>();
        getMapPacks();
        return mapPacks;
    }

    public static void getMapPacks() {
        for (int i = 1; i < lastMapPackID; i++) {
            MapPack mp = map(i);
            if (mapPacks.replace(i, mp)==null) {
                mapPacks.put(i, mp);
            }
        }
    }

    public MapPack() {}

    public MapPack(int id, String name, List<Level> levels, int rewardStars, int rewardCoins, Difficulty displayedDiff, RGBColor textColor, RGBColor barColor) {
        this.id = id;
        this.name = name;
        this.levels = levels;
        this.rewardStars = rewardStars;
        this.rewardCoins = rewardCoins;
        this.displayedDiff = displayedDiff;
        this.textColor = textColor;
        this.barColor = barColor;
    }

    public MapPack(String name, List<Level> levels, int rewardStars, int rewardCoins, Difficulty displayedDiff, RGBColor textColor, RGBColor barColor) {
        this.name = name;
        this.levels = levels;
        this.rewardStars = rewardStars;
        this.rewardCoins = rewardCoins;
        this.displayedDiff = displayedDiff;
        this.textColor = textColor;
        this.barColor = barColor;
    }

    public MapPack(String name, int[] levelIDs, int rewardStars, int rewardCoins, int diff, int r1, int g1, int b1, int r2, int g2, int b2) {
        this.name = name;
        this.rewardStars = rewardStars;
        this.rewardCoins = rewardCoins;

        List<Level> levels = new ArrayList<>();
        for (int id : levelIDs) {
            levels.add(Level.map(id, true));
        }
        this.levels = levels;

        this.displayedDiff = Difficulty.fromInt(diff);

        try {
            this.textColor = new RGBColor(r1, g1, b1);
            this.barColor = new RGBColor(r2, g2, b2);
        } catch (InvalidValueException e) {
            throw new RuntimeException(e);
        }
    }

    public MapPack(int id, String name, int[] levelIDs, int rewardStars, int rewardCoins, int diff, int r1, int g1, int b1, int r2, int g2, int b2) {
        this.id = id;
        this.name = name;
        this.rewardStars = rewardStars;
        this.rewardCoins = rewardCoins;

        List<Level> levels = new ArrayList<>();
        for (int lid : levelIDs) {
            levels.add(Level.map(lid, true));
        }
        this.levels = levels;

        this.displayedDiff = Difficulty.fromInt(diff);

        try {
            this.textColor = new RGBColor(r1, g1, b1);
            this.barColor = new RGBColor(r2, g2, b2);
        } catch (InvalidValueException e) {
            throw new RuntimeException(e);
        }
    }

    public MapPack(int id, String name, String levels, int rewardStars, int rewardCoins, int diff, String rgb1, String rgb2) {
        RGBColor textColor = new RGBColor(rgb1), barColor = new RGBColor(rgb2);

        String[] levelsArr = levels.split(",");
        List<Level> levelsList = new ArrayList<>();

        for (String l : levelsArr) {
            levelsList.add(Level.map(Integer.parseInt(l), true));
        }

        this.id=id;
        this.name=name;
        this.levels=levelsList;
        this.rewardCoins=rewardCoins;
        this.rewardStars=rewardStars;
        this.displayedDiff = Difficulty.fromInt(diff);
        this.textColor=textColor;
        this.barColor=barColor;
    }

    public MapPack(String name, String levels, int rewardStars, int rewardCoins, int diff, String rgb1, String rgb2) {
        RGBColor textColor = new RGBColor(rgb1), barColor = new RGBColor(rgb2);

        String[] levelsArr = levels.split(",");
        List<Level> levelsList = new ArrayList<>();

        for (String l : levelsArr) {
            levelsList.add(Level.map(Integer.parseInt(l), true));
        }

        this.name=name;
        this.levels=levelsList;
        this.rewardCoins=rewardCoins;
        this.rewardStars=rewardStars;
        this.displayedDiff = Difficulty.fromInt(diff);
        this.textColor=textColor;
        this.barColor=barColor;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String levelsString = "";
        for (Level l : levels) levelsString += l.getLevelID() + ",";
        sb.append(1).append(":").append(id).append(":");
        sb.append(2).append(":").append(name).append(":");
        sb.append(3).append(":").append(levelsString.subSequence(0, levelsString.length()-1)).append(":");
        sb.append(4).append(":").append(rewardStars).append(":");
        sb.append(5).append(":").append(rewardCoins).append(":");
        sb.append(6).append(":").append(displayedDiff.getMPN()).append(":");
        sb.append(7).append(":").append(textColor.toString()).append(":");
        sb.append(8).append(":").append(barColor.toString());
        return sb.toString();
    }

    public String toString(String sep) {
        StringBuilder sb = new StringBuilder();
        String levelsString = "";
        for (Level l : levels) levelsString += l.getLevelID() + ",";
        sb.append(1).append(sep).append(id).append(sep);
        sb.append(2).append(sep).append(name).append(sep);
        sb.append(3).append(sep).append(levelsString.subSequence(0, levelsString.length()-1)).append(sep);
        sb.append(4).append(sep).append(rewardStars).append(sep);
        sb.append(5).append(sep).append(rewardCoins).append(sep);
        sb.append(6).append(sep).append(displayedDiff.getMPN()).append(sep);
        sb.append(7).append(sep).append(textColor.toString()).append(sep);
        sb.append(8).append(sep).append(barColor.toString());
        return sb.toString();
    }

    public void upload() {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO mapPacks(ID, mapPackName, levels, rewardStars, rewardCoins, displayedDiff, textColor, barColor) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?)")) {
            String levelsString = "";
            for (Level l : getLevels()) levelsString += l.getLevelID() + ",";
            levelsString = levelsString.subSequence(0, levelsString.length()-1).toString();
            ps.setInt(1, lastMapPackID);
            ps.setString(2, getName());
            ps.setString(3, levelsString);
            ps.setInt(4, getRewardStars());
            ps.setInt(5, getRewardCoins());
            ps.setInt(6, getDisplayedDiff().getMPN());
            ps.setString(7, getTextColor().toString());
            ps.setString(8, getBarColor().toString());

            this.id = lastMapPackID;
            lastMapPackID++;
            mapPacks.put(getId(), this);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static String getPacks(int page, int amount) {
        getMapPacks();
        StringBuilder sb = new StringBuilder();
        StringBuilder hashBuilder = new StringBuilder();
        List<MapPack> pageList = new ArrayList<>();
        TreeSet<MapPack> pageSortedSet = new TreeSet<>(new MapPackComparators.IDComparator());

        int totalPages = (int) Math.ceil((double) mapPacks.size() / amount);

        //Core.logger.info(page+" "+totalPages+" "+amount+" "+mapPacks.size());
        if (page < totalPages) {
            int startIndex = page * amount;
            int endIndex = Math.min(startIndex + amount, mapPacks.size());

            if (mapPacks.size()<=amount)
                pageList = new ArrayList<>(mapPacks.values());
            else
                pageList = new ArrayList<>(mapPacks.values()).subList(startIndex, endIndex);
        }
        pageSortedSet.addAll(pageList);

        for (MapPack mp : pageSortedSet) {
            sb.append(mp.toString()).append("|");
            String idString = String.valueOf(mp.getId());
            hashBuilder.append(idString.charAt(0)).append(idString.charAt(idString.length()-1)).append(mp.getRewardStars()).append(mp.getRewardCoins());
        }
        //1:1:2:based:3:1,2,3:4:28:5:28:6:5:7:123,123,123:8:123,123,123#1:0:10# 895b500cfc5ebcff307f906e9fcf4e852d9a20e6
        //                                                                      485cbedbb9974c928e410f5b15af0b54ee5599a3
        String hash = hashBuilder.toString();
        Core.logger.info(hash);
        hash = Utils.SHA1(hash, "xI25fpAapCQg");
        try {
            sb.deleteCharAt(sb.length() - 1);
        } catch (IndexOutOfBoundsException ignored) {}
        sb.append("#").append(mapPacks.size()).append(":").append(page).append(":").append(amount);
        sb.append("#").append(hash);
        return sb.toString();
    }
    public static void main(String[] args) {
        boolean y = true;
        Integer i = 0; //102828
        String s;
        while (y) {
            if ((s = Utils.SHA1(i.toString(), "xI25fpAapCQg")).equals("895b500cfc5ebcff307f906e9fcf4e852d9a20e6")) {
                System.out.println(s);
                break;
            }
        }
    }

    public static MapPack map(int id) {
        //bsaic
        if (mapPacks.containsKey(id)) return mapPacks.get(id);
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM mapPacks WHERE ID = ?")) {
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return new MapPack();
            return new MapPack(
                    rs.getInt("ID"),
                    rs.getString("mapPackName"),
                    rs.getString("levels"),
                    rs.getInt("rewardStars"),
                    rs.getInt("rewardCoins"),
                    rs.getInt("displayedDiff"),
                    rs.getString("textColor"),
                    rs.getString("barColor")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}