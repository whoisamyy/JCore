package ru.whoisamyy.api.gd.objects;

import ru.whoisamyy.api.gd.misc.GDObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

public class Song extends GDObject {
    public static int lastSongID;

    private static Hashtable<Integer, Song> songs = new Hashtable<>();
    private static Connection conn;

    private int id;
    private String name = "heh), naivniye";
    private int artistID = 1;
    private String artistName = "songreupload";
    private double size = 2.8;
    private String videoID = "";
    private String youtubeURL = "UCejLri1RVC7kj8ZVNX2a53g";
    private boolean isVerified = true;
    private int songPriority = 0;
    private String link;

    public static int getLastSongID() {
        return lastSongID;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getArtistID() {
        return artistID;
    }

    public String getArtistName() {
        return artistName;
    }

    public double getSize() {
        return size;
    }

    public String getVideoID() {
        return videoID;
    }

    public String getYoutubeURL() {
        return youtubeURL;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public int getSongPriority() {
        return songPriority;
    }

    public String getLink() {
        return link;
    }

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
        StringBuilder sb = new StringBuilder();
        sb.append(1).append(sep).append(getId()).append(sep);
        sb.append(2).append(sep).append(getArtistID()).append(sep);
        sb.append(3).append(sep).append(getName()).append(sep);
        sb.append(4).append(sep).append(getArtistName()).append(sep);
        sb.append(5).append(sep).append(getSize()).append(sep);
        sb.append(6).append(sep).append(sep);
        sb.append(10).append(sep).append(getLink()).append(sep);
        sb.append(7).append(sep).append(getYoutubeURL());
        //sb.append(8).append(sep).append(isVerified()?1:0);
        return sb.toString();
    }

    public String toString() { //  1 ~|~ 223469 ~|~  2 ~|~ ParagonX9 - HyperioxX ~|~  3 ~|~ 31 ~|~  4 ~|~ ParagonX9 ~|~  5 ~|~ 3.77 ~|~  6 ~|~ ~|~  10 ~|~ http%3A%2F%2Faudio.ngfiles.com%2F223000%2F223469_ParagonX9___HyperioxX.mp3 ~|~  7~|~ ~|~  8 ~|~ 1
        String sep = "~|~";
        StringBuilder sb = new StringBuilder();
        sb.append(1).append(sep).append(getId()).append(sep);
        sb.append(2).append(sep).append(getName()).append(sep);
        sb.append(3).append(sep).append(2159).append(sep);
        sb.append(4).append(sep).append(getArtistName()).append(sep);
        sb.append(5).append(sep).append(getSize()).append(sep);
        sb.append(6).append(sep).append(sep);
        sb.append(10).append(sep).append(getLink()).append(sep);
        sb.append(7).append(sep).append(getYoutubeURL());
        //sb.append(8).append(sep).append(isVerified()?1:0);
        return sb.toString();
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
