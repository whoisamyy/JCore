package ru.whoisamyy.api.gd.objects;

import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@Getter
public class Gauntlet extends GDObject {
    public static Hashtable<Integer, Gauntlet> gauntlets = new Hashtable<>();
    //@Setter private static Connection conn;

    private int id = 1;
    private List<Level> levels;

    public Gauntlet() {}

    public Gauntlet(int id, List<Level> levels) {
        this.id = id;
        this.levels = levels;
    }

    public Gauntlet(int id, Level[] levels) {
        this.id = id;
        this.levels = new ArrayList<>(List.of(levels)); //so it can be changed
    }

    public Gauntlet(int id, String levels) {
        this.id = id;
        List<Level> levelList = new ArrayList<>();
        for (String s : levels.split(",")) {
            try {
                int lvlID = Integer.parseInt(s);

                levelList.add(Level.map(lvlID, true));
            } catch (NumberFormatException ignored) {}
        }
        this.levels = levelList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(1).append(":").append(getId()).append(":");
        StringBuilder s = new StringBuilder();
        for (Level l : getLevels()) {
            s.append(l.getLevelID()).append(",");
        }
        sb.append(3).append(":").append(s.subSequence(0, s.length()-2));
        return sb.toString();
    }

    public String toString(String sep) {
        StringBuilder sb = new StringBuilder();
        sb.append(1).append(":").append(getId()).append(":");
        StringBuilder s = new StringBuilder();
        for (Level l : getLevels()) {
            s.append(l.getLevelID()).append(",");
        }
        sb.append(3).append(":").append(s.subSequence(0, s.length()-2));
        return sb.toString();
    }

    public static String getGauntlets() {
        StringBuilder sb = new StringBuilder();
        gauntlets.forEach((x,y)->sb.append(y.toString()).append("|"));
        try {
            sb.deleteCharAt(sb.length() - 1);
        } catch (IndexOutOfBoundsException ignored) {}
        return sb.toString();
    }

    public static Gauntlet map(int id) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM gauntlets WHERE gauntletID = ?")) {
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Gauntlet(rs.getInt("gauntletID"), rs.getString("levels"));
            }
            else return new Gauntlet();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
