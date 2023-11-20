package ru.whoisamyy.api.gd.objects;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.bcrypt.BCrypt;
import ru.whoisamyy.api.gd.misc.GDObject;
import ru.whoisamyy.api.utils.Utils;
import ru.whoisamyy.api.utils.comparators.LevelComparators;
import ru.whoisamyy.api.utils.enums.Length;
import ru.whoisamyy.api.utils.exceptions.InvalidValueException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Level extends GDObject {
    public static String secret;
    public static int lastLevelID;

    private static Hashtable<Integer, Level> levels = new Hashtable<>();
    private static Connection conn;

    @Getter
    public static Path levelResourcesPath = Paths.get(Utils.resources.toString()+"/data/levels");
    public static Logger logger = LogManager.getLogger(Level.class);

    private int levelID = -1;
    private String levelName = "zero";
    @Setter
    private String description = "zero";
    private String levelString = "zero";
    private int version = 1;
    private int authorID;
    private int difficultyDenominator = 0;
    private int difficultyNumerator = 0;
    private int downloads = 1;
    private int audioTrack = 0;
    private int gameVersion = 21;
    private int likes = 0;
    private Length length = Length.TINY;
    private int dislikes = 0;
    private boolean demon;
    private int stars = 0;
    private int featureScore = 0;
    private int auto = 0;
    private int password = 0;
    private String uploadDate = "0";
    private String updateDate = "0";
    private int copiedID = 0;
    private boolean twoPlayer = false;
    private int customSongID = 0;
    private int coins = 0;
    private boolean verifiedCoins = false;
    private int starsRequested = 0;
    private int dailyNumber = 0;
    private boolean epic = false;
    private int demonDifficulty = 0;
    private int isGauntlet = 0;
    private int objects = 0;
    private int editorTime = 0;
    private boolean unlisted = false;
    private int original = 0;
    private boolean ldm = false;

    public void setDownloads(int value) {
        try(PreparedStatement ps = conn.prepareStatement("UPDATE levels SET downloads = ? WHERE levelID = ?")) {
            ps.setInt(1, value);
            ps.setInt(2, getLevelID());

            this.downloads=value;
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addDownloads(int amount) {
        try(PreparedStatement ps = conn.prepareStatement("UPDATE levels SET downloads = ? WHERE levelID = ?")) {
            ps.setInt(1, getDownloads()+amount);
            ps.setInt(2, getLevelID());

            this.downloads+=amount;
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static int getLastLevelID() {
        return lastLevelID;
    }

    public static Connection getConn() {
        return conn;
    }

    public int getLevelID() {
        return levelID;
    }

    public String getLevelName() {
        return levelName;
    }

    public String getDescription() {
        return description;
    }

    public String getLevelString() {
        return levelString;
    }

    public int getVersion() {
        return version;
    }

    public int getAuthorID() {
        return authorID;
    }

    public int getDifficultyDenominator() {
        return difficultyDenominator;
    }

    public int getDifficultyNumerator() {
        return difficultyNumerator;
    }

    public int getDownloads() {
        return downloads;
    }

    public int getAudioTrack() {
        return audioTrack;
    }

    public int getGameVersion() {
        return gameVersion;
    }

    public int getLikes() {
        return likes;
    }

    public Length getLength() {
        return length;
    }

    public int getLengthInt() throws InvalidValueException {
        int ret;
        switch (length) {
            case TINY -> ret = 0;
            case SHORT -> ret = 1;
            case LONG -> ret = 2;
            case MEDIUM -> ret = 3;
            case XL -> ret = 4;
            default -> throw new InvalidValueException("не"); //на самом деле ниче не кидает, это обманка чтобы жава не ругалась
        }
        return ret;
    }

    public int getDislikes() {
        return dislikes;
    }

    public boolean isDemon() {
        return demon;
    }

    public int getStars() {
        return stars;
    }

    public int getFeatureScore() {
        return featureScore;
    }

    public int getAuto() {
        return auto;
    }

    public int getPassword() {
        return password;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public int getCopiedID() {
        return copiedID;
    }

    public boolean isTwoPlayer() {
        return twoPlayer;
    }

    public int getCustomSongID() {
        return customSongID;
    }

    public int getCoins() {
        return coins;
    }

    public boolean isVerifiedCoins() {
        return verifiedCoins;
    }

    public int getStarsRequested() {
        return starsRequested;
    }

    public int getDailyNumber() {
        return dailyNumber;
    }

    public boolean isEpic() {
        return epic;
    }

    public int getDemonDifficulty() {
        return demonDifficulty;
    }

    public int getIsGauntlet() {
        return isGauntlet;
    }

    public int getObjects() {
        return objects;
    }

    public int getEditorTime() {
        return editorTime;
    }

    public boolean isUnlisted() {
        return unlisted;
    }

    public int getOriginal() {
        return original;
    }

    public boolean isLdm() {
        return ldm;
    }

    public Account getAuthor() throws Exception {
        return Account.getAccountByID(getAuthorID());
    }

    /**
     * fills levels hashtable
     * @param safe if true clears levels hashtable and refills then. else does not. setting to false may cause data loss, but save resources
     */
    public static void getLevels(boolean safe) {
        if (!levels.isEmpty() && safe) levels.clear();
        try(PreparedStatement ps = conn.prepareStatement("SELECT levelID FROM levels")) {
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                levels.put(rs.getInt(1), Level.map(rs.getInt(1), true));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Hashtable<Integer, Level> getLevelsHashtable() {
        getLevels(true);
        return levels;
    }

    public static void setConn(Connection conn) {
        Level.conn = conn;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(1).append(':').append(getLevelID()).append(':');
        sb.append(2).append(':').append(getLevelName()).append(':');
        sb.append(3).append(':').append(getDescription()).append(':');
        sb.append(4).append(':').append(getLevelString()).append(':');
        sb.append(5).append(':').append(getVersion()).append(':');
        sb.append(6).append(':').append(getAuthorID()).append(':');
        sb.append(8).append(':').append(getDifficultyDenominator()).append(':');
        sb.append(9).append(':').append(getDifficultyNumerator()).append(':');
        sb.append(10).append(':').append(getDownloads()).append(':');
        sb.append(12).append(':').append(getAudioTrack()).append(':');
        sb.append(13).append(':').append(getGameVersion()).append(':');
        sb.append(14).append(':').append(getLikes()).append(':');
        try {
            sb.append(15).append(':').append(getLengthInt()).append(':'); // никогда не кидает ошибок...
        } catch (InvalidValueException e) {
            throw new RuntimeException(e);
        }
        sb.append(16).append(':').append(getDislikes()).append(':');
        sb.append(17).append(':').append(isDemon()?1:0).append(':');
        sb.append(18).append(':').append(getStars()).append(':');
        sb.append(19).append(':').append(getFeatureScore()).append(':');
        sb.append(25).append(':').append(getAuto()).append(':');
        sb.append(27).append(':').append(getPassword()).append(':');
        sb.append(28).append(':').append(getUploadDate()).append(':');
        sb.append(29).append(':').append(getUpdateDate()).append(':');
        sb.append(30).append(':').append(getCopiedID()).append(':');
        sb.append(31).append(':').append(isTwoPlayer()?1:0).append(':');
        sb.append(35).append(':').append(getCustomSongID()).append(':');
        sb.append(36).append(':').append(0).append(':');
        sb.append(37).append(':').append(getCoins()).append(':');
        sb.append(38).append(':').append(isVerifiedCoins()?1:0).append(':');
        sb.append(39).append(':').append(getStarsRequested()).append(':');
        sb.append(40).append(':').append(isLdm()?1:0).append(':');
        sb.append(41).append(':').append(getDailyNumber()).append(':');
        sb.append(42).append(':').append(isEpic()?1:0).append(':');
        sb.append(43).append(':').append(getDemonDifficulty()).append(':');
        sb.append(44).append(':').append(getIsGauntlet()).append(':');
        sb.append(45).append(':').append(getObjects()).append(':');
        sb.append(46).append(':').append(getEditorTime());
        return sb.toString();
    }

    public String toString(boolean noLevelString) {
        if (!noLevelString) return toString();
        StringBuilder sb = new StringBuilder();

        sb.append("1:").append(getLevelID()).append(":");
        sb.append("2:").append(getLevelName()).append(":");
        sb.append("5:").append(getVersion()).append(":");
        sb.append("6:").append(getAuthorID()).append(":");
        sb.append("8:").append(getDifficultyDenominator()).append(":");
        sb.append("9:").append(getDifficultyNumerator()).append(":");
        sb.append("10:").append(getDownloads()).append(":");
        sb.append("12:").append(getAudioTrack()).append(":");
        sb.append("13:").append(getGameVersion()).append(":");
        sb.append("14:").append(getLikes()).append(":");
        sb.append("17:").append(isDemon() ? 1 : 0).append(":");
        sb.append("43:").append(getDemonDifficulty()).append(":");
        sb.append("25:").append(getAuto()).append(":");
        sb.append("18:").append(getStars()).append(":");
        sb.append("19:").append(getFeatureScore()).append(":");
        sb.append("42:").append(isEpic() ? 1 : 0).append(":");
        sb.append("45:").append(getObjects()).append(":");
        sb.append("3:").append(getDescription()).append(":");
        try {
            sb.append("15:").append(getLengthInt()).append(":");
             // никогда не кидает ошибок...
        } catch (InvalidValueException e) {
            throw new RuntimeException(e);
        }
        sb.append("30:").append(getEditorTime()).append(":");
        sb.append("31:").append(isTwoPlayer() ? 1 : 0).append(":");
        sb.append("37:").append(getCoins()).append(":");
        sb.append("38:").append(isVerifiedCoins() ? 1 : 0).append(":");
        sb.append("39:").append(getStarsRequested()).append(":");
        sb.append("46:").append(getIsGauntlet()).append(":");
        sb.append("47:").append(2).append(":");
        sb.append("35:").append(getCustomSongID());
        return sb.toString();
    }

    public String toString(String sep) {
        StringBuilder sb = new StringBuilder();
        sb.append(1).append(sep).append(getLevelID()).append(sep);
        sb.append(2).append(sep).append(getLevelName()).append(sep);
        sb.append(3).append(sep).append(getDescription()).append(sep);
        sb.append(4).append(sep).append(getLevelString()).append(sep);
        sb.append(5).append(sep).append(getVersion()).append(sep);
        sb.append(6).append(sep).append(getAuthorID()).append(sep);
        sb.append(8).append(sep).append(getDifficultyDenominator()).append(sep);
        sb.append(9).append(sep).append(getDifficultyNumerator()).append(sep);
        sb.append(10).append(sep).append(getDownloads()).append(sep);
        sb.append(12).append(sep).append(getAudioTrack()).append(sep);
        sb.append(13).append(sep).append(getGameVersion()).append(sep);
        sb.append(14).append(sep).append(getLikes()).append(sep);
        try {
            sb.append(15).append(sep).append(getLengthInt()).append(sep); // никогда не кидает ошибок...
        } catch (InvalidValueException e) {
            throw new RuntimeException(e);
        }
        sb.append(16).append(sep).append(getDislikes()).append(sep);
        sb.append(17).append(sep).append(isDemon()?1:0).append(sep);
        sb.append(18).append(sep).append(getStars()).append(sep);
        sb.append(19).append(sep).append(getFeatureScore()).append(sep);
        sb.append(25).append(sep).append(getAuto()).append(sep);
        sb.append(27).append(sep).append(getPassword()).append(sep);
        sb.append(28).append(sep).append(getUploadDate()).append(sep);
        sb.append(29).append(sep).append(getUpdateDate()).append(sep);
        sb.append(30).append(sep).append(getCopiedID()).append(sep);
        sb.append(31).append(sep).append(isTwoPlayer()?1:0).append(sep);
        sb.append(35).append(sep).append(getCustomSongID()).append(sep);
        sb.append(36).append(sep).append(sep);
        sb.append(37).append(sep).append(getCoins()).append(sep);
        sb.append(38).append(sep).append(isVerifiedCoins()?1:0).append(sep);
        sb.append(39).append(sep).append(getStarsRequested()).append(sep);
        sb.append(40).append(sep).append(isLdm()?1:0).append(sep);
        sb.append(41).append(sep).append(getDailyNumber()).append(sep);
        sb.append(42).append(sep).append(isEpic()?1:0).append(sep);
        sb.append(43).append(sep).append(getDemonDifficulty()).append(sep);
        sb.append(44).append(sep).append(getIsGauntlet()).append(sep);
        sb.append(45).append(sep).append(getObjects()).append(sep);
        sb.append(46).append(sep).append(getEditorTime());
        return sb.toString();
    }
    public Level() {}

    public Level(int levelID, String levelName, String description,
                 String levelString, int version, int authorID,
                 int difficultyDenominator, int difficultyNumerator, int downloads,
                 int audioTrack, int gameVersion, int likes,
                 Length length, int dislikes, boolean demon,
                 int stars, int featureScore, int auto,
                 int password, String uploadDate, String updateDate,
                 int copiedID, boolean twoPlayer, int customSongID,
                 int coins, boolean verifiedCoins, int starsRequested,
                 int dailyNumber, boolean epic, int demonDifficulty,
                 int isGauntlet, int objects, int editorTime,
                 boolean unlisted, int original, boolean ldm) {
        this.levelID = levelID;
        this.levelName = levelName;
        this.description = description;
        this.levelString = levelString;
        this.version = version;
        this.authorID = authorID;
        this.difficultyDenominator = difficultyDenominator;
        this.difficultyNumerator = difficultyNumerator;
        this.downloads = downloads;
        this.audioTrack = audioTrack;
        this.gameVersion = gameVersion;
        this.likes = likes;
        this.length = length;
        this.dislikes = dislikes;
        this.demon = demon;
        this.stars = stars;
        this.featureScore = featureScore;
        this.auto = auto;
        this.password = password;
        this.uploadDate = uploadDate;
        this.updateDate = updateDate;
        this.copiedID = copiedID;
        this.twoPlayer = twoPlayer;
        this.customSongID = customSongID;
        this.coins = coins;
        this.verifiedCoins = verifiedCoins;
        this.starsRequested = starsRequested;
        this.dailyNumber = dailyNumber;
        this.epic = epic;
        this.demonDifficulty = demonDifficulty;
        this.isGauntlet = isGauntlet;
        this.objects = objects;
        this.editorTime = editorTime;
        this.unlisted = unlisted;
        this.original = original;
        this.ldm = ldm;
    }

    public Level(int gameVersion,
                 int accountID,
                 int levelID,
                 String levelName,
                 String levelDesc,
                 int levelVersion,
                 int levelLength,
                 int audioTrack,
                 int auto,
                 int password,
                 int original,
                 boolean twoPlayer,
                 int songID,
                 int objects,
                 int coins,
                 int requestedStars,
                 boolean unlisted,
                 boolean ldm,
                 String levelString) throws InvalidValueException {
        this.gameVersion = gameVersion;
        this.authorID = accountID;
        this.levelID = lastLevelID;
        this.levelName = levelName;
        this.description = levelDesc;
        this.version = levelVersion;
        switch (levelLength) {
            case 0:
                this.length = Length.TINY;
                break;
            case 1:
                this.length = Length.SHORT;
                break;
            case 2:
                this.length = Length.MEDIUM;
                break;
            case 3:
                this.length = Length.LONG;
                break;
            case 4:
                this.length = Length.XL;
                break;
            default:
                //implement or leave
                throw new InvalidValueException("Invalid length");
        }
        this.audioTrack = audioTrack;
        this.auto = auto;
        this.password = password;
        this.original = original;
        this.twoPlayer = twoPlayer;
        this.customSongID = songID;
        this.objects = objects;
        this.coins = coins;
        this.starsRequested = requestedStars;
        this.unlisted = unlisted;
        this.ldm = ldm;
        this.levelString = levelString;
    }

    public Level(int gameVersion,
                 int accountID,
                 String levelName,
                 String levelDesc,
                 int levelVersion,
                 int levelLength,
                 int audioTrack,
                 int auto,
                 int password,
                 int original,
                 boolean twoPlayer,
                 int songID,
                 int objects,
                 int coins,
                 int requestedStars,
                 boolean unlisted,
                 boolean ldm,
                 String levelString) throws InvalidValueException {
        this.gameVersion = gameVersion;
        this.authorID = accountID;
        this.levelName = levelName;
        this.description = levelDesc;
        this.version = levelVersion;
        switch (levelLength) {
            case 0:
                this.length = Length.TINY;
                break;
            case 1:
                this.length = Length.SHORT;
                break;
            case 2:
                this.length = Length.MEDIUM;
                break;
            case 3:
                this.length = Length.LONG;
                break;
            case 4:
                this.length = Length.XL;
                break;
            default:
                //implement or leave
                throw new InvalidValueException("Invalid length");
        }
        this.audioTrack = audioTrack;
        this.auto = auto;
        this.password = password;
        this.original = original;
        this.twoPlayer = twoPlayer;
        this.customSongID = songID;
        this.objects = objects;
        this.coins = coins;
        this.starsRequested = requestedStars;
        this.unlisted = unlisted;
        this.ldm = ldm;
        this.levelString = levelString;
    }

    public int upload(boolean saveAsFile) {
        if (getLevelID() != -1) {
            this.levelID = lastLevelID;
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO levels (gameVersion, authorID, authorGjp, authorName," +
                    " levelName, description, version," +
                    " length, officialSong, auto, password," +
                    " copiedID, twoPlayer, songID, objects," +
                    " coins, starsRequested, unlisted, ldm, levelString)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")) {
                ps.setInt(1, getGameVersion());
                ps.setInt(2, getAuthorID());
                ps.setString(3, getAuthor().getGjp());
                ps.setString(4, getAuthor().getUsername());
                ps.setString(5, getLevelName());
                ps.setString(6, getDescription());
                ps.setInt(7, getVersion());
                ps.setInt(8, getLengthInt());
                ps.setInt(9, getAudioTrack());
                ps.setInt(10, getAuto());
                ps.setInt(11, getPassword());
                ps.setInt(12, getOriginal());
                ps.setBoolean(13, isTwoPlayer());
                ps.setInt(14, getCustomSongID());
                ps.setInt(15, getObjects());
                ps.setInt(16, getCoins());
                ps.setInt(17, getStarsRequested());
                ps.setBoolean(18, isUnlisted());
                ps.setBoolean(19, isLdm());
                ps.setString(20, getLevelString());

                ps.execute();
                levels.put(levelID, this);
                lastLevelID++;
                if (saveAsFile) save();
                return levelID;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }
        else {
            try (PreparedStatement ps = conn.prepareStatement("UPDATE levels SET gameVersion = ?, authorID = ?, authorGjp = ?, authorName = ?," +
                    " levelName = ?, description = ?, version = ?," +
                    " length = ?, audioTrack = ?, auto = ?, password = ?," +
                    " copiedID = ?, twoPlayer = ?, songID = ?, objects = ?," +
                    " coins = ?, starsRequested = ?, unlisted = ?, ldm = ?, levelString = ? WHERE levelID = ?")) {
                ps.setInt(1, getGameVersion());
                ps.setInt(2, getAuthorID());
                ps.setString(3, getAuthor().getGjp());
                ps.setString(4, getAuthor().getUsername());
                ps.setString(5, getLevelName());
                ps.setString(6, getDescription());
                ps.setInt(7, getVersion());
                ps.setInt(8, getLengthInt());
                ps.setInt(9, getAudioTrack());
                ps.setInt(10, getAuto());
                ps.setInt(11, getPassword());
                ps.setInt(12, getOriginal());
                ps.setBoolean(13, isTwoPlayer());
                ps.setInt(14, getCustomSongID());
                ps.setInt(15, getObjects());
                ps.setInt(16, getCoins());
                ps.setInt(17, getStarsRequested());
                ps.setBoolean(18, isUnlisted());
                ps.setBoolean(19, isLdm());
                ps.setString(20, getLevelString());
                ps.setInt(21, getLevelID());

                ps.execute();

                updateLevels(levelID);
                if (saveAsFile) save();
                return levelID;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }
    }

    public int updateDesc(String newDesc) {
        try(PreparedStatement ps = conn.prepareStatement("UPDATE levels SET descripton = ? WHERE levelID = ?")) {
            ps.setString(1, newDesc);
            ps.setInt(2, getLevelID());

            ps.execute();
            setDescription(newDesc);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

    public static String getLevels(String secret, @Nullable Integer gameVersion, @Nullable Integer binaryVersion,
                                   Integer type, @Nullable String str, @Nullable Integer page,
                                   @Nullable Integer total, @Nullable String gjp, @Nullable Integer accountID,
                                   @Nullable Integer gdw, @Nullable Integer gauntlet, @Nullable String diff,
                                   @Nullable String demonFilter, @Nullable String len, @Nullable Integer uncompleted,
                                   @Nullable Integer onlyCompleted, @Nullable String completedLevels,
                                   @Nullable Integer featured, @Nullable Integer original, @Nullable Integer twoPlayer,
                                   @Nullable Integer coins, @Nullable Integer epic, @Nullable Integer noStar,
                                   @Nullable Integer star, @Nullable Integer song, @Nullable Integer customSong,
                                   @Nullable String followed, @Nullable Integer local) {
        if (str==null) str = "";
        if (completedLevels==null) completedLevels = "";
        if (demonFilter==null) demonFilter="";
        if (page==null) page = 0;
        TreeSet<Level> sortedLvlsTree = new TreeSet<>(new LevelComparators.IDComparatorDescension());
        for (Map.Entry<Integer, Level> entry : levels.entrySet()) {
            sortedLvlsTree.add(entry.getValue());
        }


        switch (type) {
            case 0 -> {
                String finalStr = str;
                sortedLvlsTree.removeIf(x -> !x.getLevelName().startsWith(finalStr));
            }
            case 1-> {
                sortedLvlsTree = new TreeSet<>(new LevelComparators.DownloadsComparatorDescension());
                sortedLvlsTree.addAll(getLevelsHashtable().values());
            }
            case 4 -> {
                sortedLvlsTree = new TreeSet<>(new LevelComparators.IDComparatorDescension());
                sortedLvlsTree.addAll(getLevelsHashtable().values());
            }
            case 5 -> {
                int id = Integer.parseInt(str);
                sortedLvlsTree.removeIf(x->x.getAuthorID()!=id);
            }
            case 6 -> {
                sortedLvlsTree.removeIf(x->x.featureScore==0);
            }
            case 10 -> {
                String[] idsString = str.split(",");
                List<Integer> ids = new ArrayList<>(idsString.length);
                for (int i = 0; i < idsString.length; i++) {
                    ids.add(Integer.parseInt(idsString[i]));
                }
                sortedLvlsTree.removeIf(x->!ids.contains(x.getLevelID()));
            }
            case 12 -> {
                String[] idsString = followed.split(",");
                List<Integer> ids = new ArrayList<>(idsString.length);
                for (int i = 0; i < idsString.length; i++) {
                    ids.add(Integer.parseInt(idsString[i]));
                }
                sortedLvlsTree.removeIf(x->!ids.contains(x.getAuthorID()));
            }
            case 13 -> {
                if (accountID==null) break;
                sortedLvlsTree.removeIf(x -> !Account.map(accountID, true).getFriendsHashtable().contains(x.getAuthorID()));
            }
            default -> {}
        }

        if (diff!=null) {
            try {
                int d = Integer.parseInt(diff);
                sortedLvlsTree.removeIf(x -> x.getDifficultyDenominator() != d);
                if (d == -2) {
                    String finalDemonFilter = demonFilter;
                    switch (demonFilter) {
                        case "1", "2", "3", "4", "5" ->
                                sortedLvlsTree.removeIf(x -> x.getDemonDifficulty() != Integer.parseInt(finalDemonFilter));
                        default -> {
                            String[] dfsString = demonFilter.split(",");
                            List<Integer> dfs = new ArrayList<>(dfsString.length);
                            for (int i = 0; i < dfsString.length; i++) {
                                dfs.add(Integer.parseInt(dfsString[i]));
                            }
                            sortedLvlsTree.removeIf(x -> !dfs.contains(x.getDemonDifficulty()));
                        }
                    }
                }
            } catch (NumberFormatException e) {
                String d = diff;
                String[] diffsString = d.split(",");
                List<Integer> diffs = new ArrayList<>(diffsString.length);
                for (String s : diffsString) {
                    try {
                        diffs.add(Integer.parseInt(s));
                    } catch (NumberFormatException ignored) {
                    }
                }
                sortedLvlsTree.removeIf(x -> !diffs.contains(x.getDifficultyDenominator()));
            }
        }

        if (len!=null) {
            try {
                int l = Integer.parseInt(len);
                sortedLvlsTree.removeIf(x -> x.getLength().toInt() != l);
            } catch (NumberFormatException e) {
                String l = len;
                String[] lensString = l.split(",");
                List<Integer> lens = new ArrayList<>(lensString.length);
                if (l != "") {
                    for (String s : lensString) {
                        try {
                            lens.add(Integer.parseInt(s));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    sortedLvlsTree.removeIf(x -> !lens.contains(x.getLength().toInt()));
                }
            }
        }

        List<String> cl = new ArrayList<>(List.of(completedLevels.split(",")));
        onlyCompleted=onlyCompleted==null?0:onlyCompleted;
        uncompleted=uncompleted==null?0:uncompleted;
        if (onlyCompleted==1) {
            sortedLvlsTree.removeIf(x->!cl.contains(String.valueOf(x.getLevelID())));
        }
        if (uncompleted==1) {
            sortedLvlsTree.removeIf(x->cl.contains("\\("+x.getLevelID()+"\\)"));
        }

        featured = featured==null?0:featured;
        if (featured==1) {
            sortedLvlsTree.removeIf(x->x.getFeatureScore()!=1);
        }

        original = original==null?0:original;
        if (original==1) {
            sortedLvlsTree.removeIf(x->x.getOriginal()!=1);
        }

        twoPlayer = twoPlayer==null?0:twoPlayer;
        if (twoPlayer==1) {
            sortedLvlsTree.removeIf(x -> !x.isTwoPlayer());
        }

        coins = coins==null?0:coins;
        if (coins==1) {
            sortedLvlsTree.removeIf(x->!x.isVerifiedCoins());
        }

        epic = epic==null?0:epic;
        if (epic==1) {
            sortedLvlsTree.removeIf(x->!x.isEpic());
        }

        noStar = noStar==null?0:noStar;
        if (noStar==1) {
            sortedLvlsTree.removeIf(x->x.getStars()!=0);
        }

        star = star==null?0:star;
        if (star==1) {
            sortedLvlsTree.removeIf(x->x.getStars()!=0);
        }

        if (sortedLvlsTree.isEmpty()) {
            int randid = ((int) (Math.random() * 100000));
            char[] randids = String.valueOf(randid).toCharArray();
            StringBuilder randidsb = new StringBuilder();
            randidsb.append(randids[0]).append(randids[randids.length-1]).append(0).append(0);
            return "1:" + randid + ":2:search by id doesn't work:5:1:6:1:8:0:9:0:10:0:12:0:13:21:14:0:17:0:43:0:25:0:18:0:19:0:42:0:45:0:3:ZG9lc24ndCB3b3Jr:15:0:30:0:31:0:37:0:38:0:39:10:46:0:47:2:35:0#1:server:1##1:0:10#" +
                    Utils.SHA1(randidsb.toString(), "xI25fpAapCQg");
        }

        TreeSet<Level> subSet = new TreeSet<>(sortedLvlsTree.comparator());

        List<Level> levelsList = new ArrayList<>(sortedLvlsTree);

        for (Level lvl :
                levelsList) {
            logger.debug(lvl.toString());
        }

        //Iterator<Level> iter = sortedLvlsTree.iterator();
        /*
        if (sortedLvlsTree.size()>10) {
            Level from = null;

            for (int i = 0; i < page * 10 && iter.hasNext(); i++) {
                if (i == page * 10 - 2)
                    from = iter.next();
            }

            Level to = null;


            for (int i = 0; i < page * 10 + 10 && iter.hasNext(); i++) {
                if (i == page * 10 + 10 - 2)
                    to = iter.next();
            }

            subSet = (TreeSet<Level>) sortedLvlsTree.subSet(from, true, to, true);
        }
        else {
            subSet = (TreeSet<Level>) sortedLvlsTree.subSet(sortedLvlsTree.first(), true, sortedLvlsTree.last(), true);
        }

         */
        List<Level> subList;
        if (levelsList.size()>10) {
            subList = levelsList.subList(page*10, page*10 + 10);
        }
        else {
            subList = levelsList.subList(0, levelsList.size());
        }

        String pageString = "";

        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        char[] s;
        for (Level lvl : subList) {
            sb.append(lvl.toString(true)).append("|");

            s = (String.valueOf(lvl.getLevelID())).toCharArray();
            sb2.append(s[0]).append(s[s.length-1]).append(lvl.getStars()!=0?1:0).append(lvl.isVerifiedCoins()?1:0);
        }
        String hash = sb2.toString();

        sb.setCharAt(sb.lastIndexOf("|"), '#');

        try {
            for (Level lvl : subList) {
                sb.append(lvl.getAuthor().toCreatorString()).append("|");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        sb.setCharAt(sb.lastIndexOf("|"), '#');

        for (Level lvl : subList) {
            if (lvl.getCustomSongID()!=0) sb.append(Song.map(lvl.getCustomSongID()).toString()).append("~:~");
        }

        try {
            sb.setCharAt(sb.lastIndexOf("~:~"), '#');
            sb.deleteCharAt(sb.lastIndexOf(":~"));
            sb.deleteCharAt(sb.lastIndexOf("~"));
        } catch (IndexOutOfBoundsException e) {
            sb.append("#");
        }

        page = page==null?1:page;

        sb.append(subList.size()).append(":").append(page*10).append(":").append(10).append("#");

        sb.append(Utils.SHA1(hash, "xI25fpAapCQg"));

        pageString = sb.toString();

        return pageString;
    }

    public static String download(String secret, int id) throws Exception {
        Level l = map(id, true);
        String s = l.toString()+"#"+Utils.genSolo(l.getLevelString());
        String hash = l.getAuthorID()+","+(l.getStars()!=0?1:0)+","+(l.isDemon()?1:0)+","+l.getLevelID()+","+(l.isVerifiedCoins()?1:0)+","+(l.getFeatureScore()==0?0:1)+","+l.getPassword()+","+0;
        l.addDownloads(1);
        return s+"#"+Utils.SHA1(hash, "xI25fpAapCQg");
    }

    public void save() {
        Utils.writeToFile(new File(levelResourcesPath.toUri()), toString());
    }

    public static byte delete(int accountID, String gjp, int levelID, String secret, boolean safeDelete) throws Exception {
        gjp = Utils.base64UrlSafeEncode(Utils.base64UrlSafeDecode(gjp)); //я хз но оно так работает
        if (map(levelID, true).getAuthorID()==accountID && BCrypt.checkpw(gjp, Account.map(accountID).getGjp())) {
            if (safeDelete) map(levelID, true).save();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM levels WHERE levelID = ? LIMIT 1");
                ps.setInt(1, levelID);
                ps.execute();
                levels.remove(levelID);
                getLevels(true);
            return 1;
        }
        return -1;
    }

    public static void updateLevels(int id) {
        levels.remove(id);
        levels.put(id, map(id, true));
    }

    public static Level map(int levelID) {
        String sql = "SELECT * FROM levels WHERE levelID = ?";
        try(PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, levelID);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return new Level(); //or throw error
            return new Level(rs.getInt("gameVersion"), rs.getInt("authorID"), levelID,
                    rs.getString("levelName"), rs.getString("description"), rs.getInt("version"),
                    rs.getInt("length"), rs.getInt("audioTrack"), rs.getInt("auto"),
                    rs.getInt("password"), rs.getInt("copiedID"), rs.getBoolean("twoPlayer"), rs.getInt("songID"),
                    rs.getInt("objects"), rs.getInt("coins"), rs.getInt("starsRequested"),
                    rs.getBoolean("unlisted"), rs.getBoolean("ldm"), rs.getString("levelString"));
        } catch (SQLException | InvalidValueException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets Level object by ID from database via mapping
     * @see Level#map(int)
     * @param levelID level id
     * @param full gets full object if true. Else gets incomplete object
     * @return Level object
     */
    public static Level map(int levelID, boolean full) {
        if (!full) return map(levelID);
        String sql = "SELECT * FROM `levels` WHERE levelID = ?";
        try(PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, levelID);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return new Level();

            levelID = rs.getInt("levelID");
            String levelName = rs.getString("levelName");
            String description = rs.getString("description");
            String levelString = rs.getString("levelString");
            int version = rs.getInt("version");
            int authorID = rs.getInt("authorID");
            int difficultyDenominator = rs.getInt("difficultyDenominator");
            int difficultyNumerator = rs.getInt("difficultyNumerator");
            int downloads = rs.getInt("downloads");
            int officialSong = rs.getInt("officialSong");
            int gameVersion = rs.getInt("gameVersion");
            int likes = rs.getInt("likes");
            Length length = Length.toLength(rs.getInt("length"));
            int dislikes = rs.getInt("dislikes");
            boolean demon = rs.getBoolean("demon");
            int stars = rs.getInt("stars");
            int featureScore = rs.getInt("featureScore");
            int auto = rs.getInt("auto");
            int password = rs.getInt("password");
            String uploadDate = rs.getString("uploadDate");
            String updateDate = rs.getString("updateDate");
            int copiedID = rs.getInt("copiedID");
            boolean twoPlayer = rs.getBoolean("twoPlayer");
            int customSongID = rs.getInt("songID");
            int coins = rs.getInt("coins");
            boolean verifiedCoins = rs.getBoolean("verifiedCoins");
            int starsRequested = rs.getInt("starsRequested");
            int dailyNumber = rs.getInt("dailyNumber");
            boolean epic = rs.getBoolean("epic");
            int demonDifficulty = rs.getInt("demonDifficulty");
            int isGauntlet = rs.getInt("isGauntlet");
            int objects = rs.getInt("objects");
            int editorTime = rs.getInt("editorTime");
            boolean unlisted = rs.getBoolean("unlisted");
            int original = rs.getInt("copiedID");
            boolean ldm = rs.getBoolean("ldm");

            return new Level(levelID,
                    levelName,
                    description,
                    levelString,
                    version,
                    authorID,
                    difficultyDenominator,
                    difficultyNumerator,
                    downloads,
                    officialSong,
                    gameVersion,
                    likes,
                    length,
                    dislikes,
                    demon,
                    stars,
                    featureScore,
                    auto,
                    password,
                    uploadDate,
                    updateDate,
                    copiedID,
                    twoPlayer,
                    customSongID,
                    coins,
                    verifiedCoins,
                    starsRequested,
                    dailyNumber,
                    epic,
                    demonDifficulty,
                    isGauntlet,
                    objects,
                    editorTime,
                    unlisted,
                    copiedID,
                    ldm);
        } catch (SQLException | InvalidValueException e) {
            throw new RuntimeException(e);
        }
    }
}
