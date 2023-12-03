package ru.whoisamyy.api.gd.objects;

import lombok.Getter;
import ru.whoisamyy.api.gd.misc.GDObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

public class Song extends GDObject {
    public static int lastSongID;

    private static final Hashtable<Integer, Song> songs = new Hashtable<>();
    //private static Connection conn;

    @Getter private int id;
    @Getter private String name = "heh), naivniye";
    @Getter private final int artistID = 1;
    @Getter private String artistName = "songreupload";
    @Getter private double size = 2.8;
    @Getter private final String videoID = "";
    @Getter private final String youtubeURL = "UCejLri1RVC7kj8ZVNX2a53g";
    @Getter private boolean isVerified = true;
    @Getter private final int songPriority = 0;
    @Getter private String link;

    public static void setConn(Connection conn) {
        Song.conn = conn;
    }

    public static Hashtable<Integer, Song> getSongsHashtable() {
        getSongs();
        return songs;
    }

    public static void getSongs() {
        if (!songs.isEmpty()) songs.clear();
        for (int i = 1; i <= lastSongID; i++) {
            songs.put(i, map(i));
        }
    }

    public String toString(String sep) { // 1 ~|~ 223469 ~|~  2 ~|~ ParagonX9 - HyperioxX ~|~  3 ~|~ 31 ~|~  4 ~|~ ParagonX9 ~|~  5 ~|~ 3.77 ~|~  6 ~|~ ~|~  10 ~|~ http%3A%2F%2Faudio.ngfiles.com%2F223000%2F223469_ParagonX9___HyperioxX.mp3 ~|~  7~|~ ~|~  8 ~|~ 1
        String sb = 1 + sep + getId() + sep +
                2 + sep + getArtistID() + sep +
                3 + sep + getName() + sep +
                4 + sep + getArtistName() + sep +
                5 + sep + getSize() + sep +
                6 + sep + sep +
                10 + sep + getLink() + sep +
                7 + sep + getYoutubeURL();
        //sb.append(8).append(sep).append(isVerified()?1:0);
        return sb;
    }

    public String toString() { //  1 ~|~ 223469 ~|~  2 ~|~ ParagonX9 - HyperioxX ~|~  3 ~|~ 31 ~|~  4 ~|~ ParagonX9 ~|~  5 ~|~ 3.77 ~|~  6 ~|~ ~|~  10 ~|~ http%3A%2F%2Faudio.ngfiles.com%2F223000%2F223469_ParagonX9___HyperioxX.mp3 ~|~  7~|~ ~|~  8 ~|~ 1
        String sep = "~|~";
        String sb = 1 + sep + getId() + sep +
                2 + sep + getName() + sep +
                3 + sep + 2159 + sep +
                4 + sep + getArtistName() + sep +
                5 + sep + getSize() + sep +
                6 + sep + sep +
                10 + sep + getLink() + sep +
                7 + sep + getYoutubeURL();
        //sb.append(8).append(sep).append(isVerified()?1:0);
        return sb;
    }

    public Song() {}

    public Song(String name, String artistName, double size, String link) {
        this.name = name;
        this.artistName = artistName;
        this.size = size;
        this.link = link;
        this.isVerified = true;
    }

    public Song(String name, String artistName, double size, String link, int id) {
        this.name = name;
        this.artistName = artistName;
        this.size = size;
        this.link = link;
        this.id = id;
        this.isVerified = true;
    }

    public int songAdd() {
        try(PreparedStatement ps = conn.prepareStatement("INSERT INTO songs (name, artistName, size, link) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, name);
            ps.setString(2, artistName);
            ps.setDouble(3, size);
            ps.setString(4, link);

            ps.execute();

            this.id = lastSongID;
            songs.put(id, this);
            lastSongID++;

            return id;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getSongInfo(int id) {
        return map(id).toString();
    }

    public static Song map(int songID) {
        if (songs.containsKey(songID)) return songs.get(songID);
        try(PreparedStatement ps = conn.prepareStatement("SELECT name, artistName, size, link FROM songs WHERE ID = ?")) {
            ps.setInt(1, songID);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return new Song();
            return new Song(rs.getString("name"),
                    rs.getString("artistName"),
                    rs.getDouble("size"),
                    rs.getString("link"), songID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
