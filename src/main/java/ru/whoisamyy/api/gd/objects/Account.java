package ru.whoisamyy.api.gd.objects;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.bcrypt.BCrypt;
import ru.whoisamyy.api.utils.Utils;
import ru.whoisamyy.api.utils.enums.ModType;
import ru.whoisamyy.api.utils.exceptions.InvalidEmailException;
import ru.whoisamyy.api.utils.exceptions.InvalidPasswordException;
import ru.whoisamyy.api.utils.exceptions.InvalidUsernameException;
import ru.whoisamyy.api.utils.exceptions.InvalidValueException;
import ru.whoisamyy.core.Core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

public class Account extends GDObject {
    public static Logger logger = LogManager.getLogger(Core.class);

    public static String secret;
    public static int lastAccountID;

    private static final Hashtable<Integer, Account> accounts = new Hashtable<>(); //<id, account>
    //private static Connection conn;
    @Getter
    private static final String accountsResourcesPath = Utils.resources+"/data/accounts";

    private final Hashtable<Integer, Account> friends = new Hashtable<>();

    public void removeFromFriendsByKey(int key) {
         friends.remove(key);
    }
    public void removeFromFriendsByValue(Account value) {
        friends.remove(value.getUserID());
    }
    public void addFriend(Account account) {
        friends.put(account.getUserID(), map(account.getUserID(), true));
    }
    
    private int userID = 0;
    @Setter @Getter private String username;
    @Setter @Getter private String gjpSalt;
    @Setter @Getter private String gjp; //hashed password
    @Setter @Getter private String gjp2; //hashed username (robtop, ty shto, sasal???)
    @Setter @Getter private String email;
    @Setter @Getter private String twitter;
    @Setter @Getter private String twitch;
    @Setter @Getter private String youtube;
    @Setter @Getter private int stars = 0;
    @Setter @Getter private int demons = 0;
    @Setter @Getter private int ranking = 0;
    @Setter @Getter private int creatorPoints = 0;
    @Setter @Getter private int iconID = 0;
    @Setter @Getter private int playerColor = 0;
    @Setter @Getter private int playerColor2 = 0;
    @Setter @Getter private int secretCoins = 0;
    @Setter @Getter private int iconType = 0;
    @Setter @Getter private int special = 0;
    @Setter @Getter private int userCoins = 0;
    @Setter @Getter private int messagesState = 0;
    @Setter @Getter private int friendsState = 0;
    @Setter @Getter private int commentsState = 0;
    @Setter @Getter private int accIcon = 0;
    @Setter @Getter private int accShip = 0;
    @Setter @Getter private int accBall = 0;
    @Setter @Getter private int accBird = 0;
    @Setter @Getter private int accDart = 0;
    @Setter @Getter private int accRobot = 0;
    @Setter @Getter private int accGlow = 0;
    @Setter @Getter private int isRegistered = 0;
    @Setter @Getter private int globalRank = 0;
    @Setter @Getter private int messages = 0;
    @Setter @Getter private int friendRequests = 0;
    @Setter @Getter private int newFriends = 0;
    @Setter @Getter private int newFriendRequest = 0;
    @Setter @Getter private int age = 0;
    @Setter @Getter private int accSpider = 0;
    @Setter @Getter private int diamonds = 0;
    @Setter @Getter private int accExplosion = 0;
    @Setter @Getter private int modLevel = 0;
    @Setter @Getter private ModType mod = ModType.NO_MOD;

    public Hashtable<Integer, Account> getFriendsHashtable() {
        getFriends();
        return this.friends;
    }

    public void getFriends() {
        try(PreparedStatement ps = conn.prepareStatement("SELECT person2ID FROM friendships WHERE person1ID = ?")) {
            ps.setInt(1, getUserID());

            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                friends.put(rs.getInt(1), Account.map(rs.getInt(1), true));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try(PreparedStatement ps = conn.prepareStatement("SELECT person1ID FROM friendships WHERE person2ID = ?")) {
            ps.setInt(1, getUserID());

            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                friends.put(rs.getInt(1), Account.map(rs.getInt(1), true));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Hashtable<Integer, Account> getAccountsHashtable() {
        getAccounts();
        return accounts;
    }

    public int getUserID() {
        if (this.userID != 0)
            return userID;
        try(PreparedStatement ps = conn.prepareStatement("SELECT userID FROM users WHERE userName = ? AND gjp = ?")) {
            ps.setString(1, getUsername());
            ps.setString(2, getGjp());

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return 0;
            return rs.getInt(1);
        } catch (SQLException e) {
            return 0;
        }
    }


    public static void getAccounts() {
        if (!accounts.isEmpty()) accounts.clear();
        for (int i = 1; i <= lastAccountID; i++) {
            accounts.put(i, map(i, true));
        }
    }
    public static Account getAccountByID(int userID) {
        if (accounts.containsKey(userID)) return accounts.get(userID);
        return map(userID, true);
    }

    public static void setConn(Connection conn) {
        Account.conn = conn;
    }

    public String toCreatorString() {
        return getUserID() + ":" + getUsername() + ":" + getUserID();
    }

    public String toString() {
        String sb = "1:" + userID + ":" +
                "2:" + username + ":" +
                "3:" + stars + ":" +
                "4:" + demons + ":" +
                "6:" + ranking + ":" +
                "8:" + creatorPoints + ":" +
                "9:" + iconID + ":" +
                "10:" + playerColor + ":" +
                "11:" + playerColor2 + ":" +
                "13:" + secretCoins + ":" +
                "14:" + iconType + ":" +
                "15:" + special + ":" +
                "16:" + userID + ":" +
                "17:" + userCoins + ":" +
                "18:" + messagesState + ":" +
                "19:" + friendsState + ":" +
                "20:" + youtube + ":" +
                "21:" + accIcon + ":" +
                "22:" + accShip + ":" +
                "23:" + accBall + ":" +
                "24:" + accBird + ":" +
                "25:" + accDart + ":" +
                "26:" + accRobot + ":" +
                "28:" + accGlow + ":" +
                "29:" + isRegistered + ":" +
                "30:" + globalRank + ":" +
                "31:" + friendsState + ":" +
                "38:" + messages + ":" +
                "39:" + friendRequests + ":" +
                "40:" + newFriends + ":" +
                "41:" + newFriendRequest + ":" +
                "42:" + age + ":" +
                "43:" + accSpider + ":" +
                "44:" + twitter + ":" +
                "45:" + twitch + ":" +
                "46:" + diamonds + ":" +
                "48:" + accExplosion + ":" +
                "49:" + modLevel;
        return sb;
    }

    public String toString(String sep) {
        String sb = "1" + sep + userID + sep +
                "2" + sep + username + sep +
                "3" + sep + stars + sep +
                "4" + sep + demons + sep +
                "6" + sep + ranking + sep +
                "8" + sep + creatorPoints + sep +
                "9" + sep + iconID + sep +
                "10" + sep + playerColor + sep +
                "11" + sep + playerColor2 + sep +
                "13" + sep + secretCoins + sep +
                "14" + sep + iconType + sep +
                "15" + sep + special + sep +
                "16" + sep + userID + sep +
                "17" + sep + userCoins + sep +
                "18" + sep + messagesState + sep +
                "19" + sep + friendsState + sep +
                "20" + sep + youtube + sep +
                "21" + sep + accIcon + sep +
                "22" + sep + accShip + sep +
                "23" + sep + accBall + sep +
                "24" + sep + accBird + sep +
                "25" + sep + accDart + sep +
                "26" + sep + accRobot + sep +
                "28" + sep + accGlow + sep +
                "29" + sep + isRegistered + sep +
                "30" + sep + globalRank + sep +
                "31" + sep + friendsState + sep +
                "38" + sep + messages + sep +
                "39" + sep + friendRequests + sep +
                "40" + sep + newFriends + sep +
                "41" + sep + newFriendRequest + sep +
                "42" + sep + age + sep +
                "43" + sep + accSpider + sep +
                "44" + sep + twitter + sep +
                "45" + sep + twitch + sep +
                "46" + sep + diamonds + sep +
                "48" + sep + accExplosion + sep +
                "49" + sep + modLevel;
        return sb;
    }

    public String toString(String sep, boolean isLvlCommentCalled) {
        if (!isLvlCommentCalled) return toString(sep);
        String sb = "1" + sep + username + sep +
                "9" + sep + iconID + sep +
                "10" + sep + playerColor + sep +
                "11" + sep + playerColor2 + sep +
                "14" + sep + iconType + sep +
                "15" + sep + special + sep +
                "16" + sep + userID;
        return sb;
    }

    public Account(String username, String gjpSalt, String gjp,
                   String email, String twitter, String twitch,
                   String youtube, int userID, int stars,
                   int demons, int ranking, int creatorPoints,
                   int iconID, int playerColor, int playerColor2,
                   int secretCoins, int iconType, int special,
                   int userCoins, int messageState, int friendsState,
                   int accIcon, int accBall, int accBird, int accShip,
                   int accDart, int accRobot, int accGlow,
                   int isRegistered, int globalRank, int messages, int friendRequests, int newFriends,
                   int newFriendRequest, int age, int accSpider,
                   int diamonds, int accExplosion, int modType) {
        this.username = username;
        this.gjpSalt = gjpSalt;
        this.gjp = gjp;
        this.gjp2 = Utils.GJP.createGJPHash(username);
        this.email = email;
        this.twitter = twitter;
        this.twitch = twitch;
        this.youtube = youtube;
        this.userID = userID;
        this.stars = stars;
        this.demons = demons;
        this.ranking = ranking;
        this.creatorPoints = creatorPoints;
        this.iconID = iconID;
        this.playerColor = playerColor;
        this.playerColor2 = playerColor2;
        this.secretCoins = secretCoins;
        this.iconType = iconType;
        this.special = special;
        this.userCoins = userCoins;
        this.messagesState = messageState;
        this.friendsState = friendsState;
        this.accIcon = accIcon;
        this.accBall = accBall;
        this.accBird = accBird;
        this.accShip = accShip;
        this.accDart = accDart;
        this.accRobot = accRobot;
        this.accGlow = accGlow;
        this.isRegistered = isRegistered;
        this.globalRank = globalRank;
        this.messages = messages;
        this.friendRequests = friendRequests;
        this.newFriends = newFriends;
        this.newFriendRequest = newFriendRequest;
        this.age = age;
        this.accSpider = accSpider;
        this.diamonds = diamonds;
        this.accExplosion = accExplosion;
        try {
            this.mod = ModType.getModType(modType);
        } catch (InvalidValueException e) {
            throw new RuntimeException(e);
        }
    }
    public Account(String username, String password, String email) throws Exception {
        this.gjpSalt = Core.SALT;
        if (!Utils.emailRegex(email)) throw new InvalidEmailException("Incorrect email");
        else this.email = email;
        if (password.length()<9) throw new InvalidPasswordException("Password too short!");
        else this.gjp = Utils.GJP.createGJPHash(password);
        if (username.length()<4) throw new InvalidUsernameException("Username too short!");
        else {
            this.username = username;
            this.gjp2 = Utils.GJP.createGJPHash(username);
        }
    }

    public Account() {}

    public void save(String data) throws IOException {
        File file = new File(Utils.resources+"/data/accounts/"+getUsername()+".jac");
        Utils.writeToFile(file, data);
    }

    public byte register() {
        if (username.length()<4) return -1;
        if (!Utils.emailRegex(email)) return -1;

        try(PreparedStatement ps = conn.prepareStatement("INSERT INTO users (userName, gjp, email, gjpSalt, gjp2) VALUES(?, ?, ?, ?, ?)")) {
            ps.setString(1, getUsername());
            ps.setString(2, getGjp());
            ps.setString(3, getEmail());
            ps.setString(4, getGjpSalt());
            ps.setString(5, getGjp2());

            ps.execute();
            this.userID=lastAccountID;

            accounts.put(userID, map(getUserID(), true));

            lastAccountID++;
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public String login(String username, String password) throws InvalidPasswordException, InvalidUsernameException {
        //if (!Utils.emailRegex(email) && !Objects.equals(email, getEmail())) {
        //    throw new InvalidEmailException("Incorrect email");
        //}
        if (password.length()<9 || !checkGJP(Utils.GJP.createGJP(password))) {
            throw new InvalidPasswordException("Password is incorrect!");
        }
        if (username.length()<4 || !username.equals(getUsername())) {
            throw new InvalidUsernameException("Username too short!");
        }

        try(PreparedStatement ps = conn.prepareStatement("UPDATE users SET isRegistered = 1 WHERE userName = ? AND gjp = ? AND userID = ?")) {
            ps.setString(1, getUsername());
            ps.setString(2, getGjp());
            ps.setInt(3, getUserID());

            ps.execute();

            return getUserID() + "," + getUserID();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param saveData saveData
     * @param password account password
     * @return if success: 1,
     *         if not: -1
     * @devnote PLS DEBUG THIS
     */
    public byte backup(String saveData, String password) { //pls debug
        try(PreparedStatement ps = conn.prepareStatement("SELECT userID FROM users WHERE username = ? AND gjp = ?")) {
            ps.setString(1, getUsername());
            ps.setString(2, getGjp());

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return -1;

            String orbs, lvls;

            String newSaveData = saveData.split(";")[0];
            newSaveData = newSaveData.replace("<k>GJA_002</k><s>" + password + "</s>", "<k>GJA_002</k><s>password</s>");

            // Разделение для orbs
            String[] orbsSplit = newSaveData.split("</s><k>14</k><s>");
            if (orbsSplit.length > 1) {
                orbs = orbsSplit[1].split("</s>")[0];
            } else {
                orbs = "0";
            }

            // Разделение для lvls
            String[] lvlsSplit = newSaveData.split("<k>GS_value</k>");
            if (lvlsSplit.length > 1) {
                lvls = lvlsSplit[1].split("</s><k>4</k><s>")[1].split("</s>")[0];
            } else {
                lvls = "0";
            }

            PreparedStatement ps2 = conn.prepareStatement("UPDATE users SET orbs = ?, completedLevels = ?");
            ps2.setInt(1, Integer.parseInt(orbs));
            ps2.setInt(2, Integer.parseInt(lvls));

            ps2.execute();

            //newSaveData = Utils.base64UrlSafeEncode(newSaveData);
            //newSaveData = Utils.DataEncoder.encode(newSaveData);
            String saveString = newSaveData+/*Utils.DataEncoder.encode(*/saveData.split(";")[1];//);

            save(saveString); //free tyep bieat
            return 1;
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String sync() {
        String saveData;
        File saveFile = new File(Utils.resources+"/data/accounts/"+getUsername()+".jac");
        logger.info(saveFile.exists());
        if (!saveFile.exists()) return "-1";

        try {
            byte[] sdbarr = Files.readAllBytes(saveFile.toPath()); //sdbarr = save data byte array
            saveData = new String(sdbarr);
            logger.info(saveData);
            if (!saveData.startsWith("H4s")) throw new Exception("dayum");
            save(saveData);
            return saveData;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int updateUserScore(String username, int gameVersion, int coins, String secret, int stars, int demons,
                               int icon, int color1, int color2, int iconType, int userCoins, int special,
                               int accIcon, int accShip, int accBall, int accBird, int accDart,
                               int accRobot, int accGlow, int lastPlayed, int accSpider,
                               int accExplosion, int diamonds) {
        if (update(username, gameVersion, coins, secret, stars, demons, icon, color1, color2, iconType, userCoins, special, accIcon,
                accShip, accBall, accBird, accDart, accRobot, accGlow, lastPlayed, accSpider, accExplosion, diamonds)!=0) {
            return getUserID();
        }
        return -1;
    }

    public int update(String username, int gameVersion, int coins, String secret, int stars, int demons,
                      int icon, int color1, int color2, int iconType, int userCoins, int special,
                      int accIcon, int accShip, int accBall, int accBird, int accDart,
                      int accRobot, int accGlow, int lastPlayed, int accSpider,
                      int accExplosion, int diamonds) {
        try(PreparedStatement ps = conn.prepareStatement("UPDATE users SET " +
                " userName= ?," +
                " secretCoins= ?," +
                " stars= ?," +
                " demons= ?," +
                " iconID = ?," +
                " playerColor= ?," +
                " playerColor2= ?," +
                " iconType= ?," +
                " userCoins= ?," +
                " special= ?," +
                " accIcon= ?," +
                " accShip= ?," +
                " accBall= ?," +
                " accBird= ?," +
                " accDart= ?, " +
                "accRobot= ?, " +
                "accGlow= ?, " +
                "lastPlayed= ?, " +
                "accSpider= ?, " +
                "accExplosion= ?, " +
                "diamonds= ? WHERE userID=?")) {
            ps.setInt(22, getUserID());

            ps.setString(1, username);
            ps.setInt(2, coins);
            ps.setInt(3, stars);
            ps.setInt(4, demons);
            ps.setInt(5, icon);
            ps.setInt(6, color1);
            ps.setInt(7, color2);
            ps.setInt(8, iconType);
            ps.setInt(9, userCoins);
            ps.setInt(10, special);
            ps.setInt(11, accIcon);
            ps.setInt(12, accShip);
            ps.setInt(13, accBall);
            ps.setInt(14, accBird);
            ps.setInt(15, accDart);
            ps.setInt(16, accRobot);
            ps.setInt(17, accGlow);
            ps.setInt(18, lastPlayed);
            ps.setInt(19, accSpider);
            ps.setInt(20, accExplosion);
            ps.setInt(21, diamonds);

            logger.info(iconType);

            logger.info(ps.executeUpdate());

            accounts.remove(getUserID());
            accounts.put(getUserID(), map(getUserID(), true));
            return getUserID();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int requestModAccess(int accountID, String gjp) {
        Account acc = map(accountID, true);
        if (!BCrypt.checkpw(gjp, acc.getGjp())) return -1;
        return acc.getMod().getVal();
    }

    public int updateAccSettings(@Nullable Integer messagesState, @Nullable Integer friendsState, @Nullable Integer commentsState, @Nullable String youtube, @Nullable String twitter, @Nullable String twitch) {
        setMessagesState(messagesState==null?0:messagesState);
        setFriendsState(friendsState==null?0:friendsState);
        setCommentsState(commentsState==null?0:commentsState);
        setYoutube(youtube==null?"":youtube);
        setTwitter(twitter==null?"":twitter);
        setTwitch(twitch==null?"":twitch);

        try(PreparedStatement ps = conn.prepareStatement("UPDATE users SET messageState = ?, friendsState = ?, commentsState = ?, youtube = ?, twitter = ?, twitch = ? WHERE userID = ?")) {
            ps.setInt(1, getMessagesState());
            ps.setInt(2, getFriendsState());
            ps.setInt(3, getCommentsState());
            ps.setString(4, getYoutube());
            ps.setString(5, getTwitter());
            ps.setString(6, getTwitch());
            ps.setInt(7, getUserID());

            ps.execute();
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean checkGJP(String gjp) {
        //gjp = Utils.base64UrlSafeDecode(gjp);
        return BCrypt.checkpw(gjp, getGjp());
    }

    public static Account map(int userID) {
        if (accounts.containsKey(userID)) return accounts.get(userID);
        try(PreparedStatement ps = conn.prepareStatement("SELECT userName, gjp, email FROM users WHERE userID = ?")) {
            ps.setInt(1, userID);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Account(rs.getString("userName"),
                        rs.getString("gjp"),
                        rs.getString("email"));
            }
            else return new Account();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Account map(int userID, boolean full) {
        if (accounts.containsKey(userID)) return accounts.get(userID);
        if (!full) return map(userID);
        try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE userID = ? LIMIT 1")) {
            ps.setInt(1, userID);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return new Account(rs.getString("userName"),
                        rs.getString("gjpSalt"),
                        rs.getString("gjp"),
                        rs.getString("email"),
                        rs.getString("twitter"),
                        rs.getString("twitch"),
                        rs.getString("youtube"),
                        rs.getInt("userID"),
                        rs.getInt("stars"),
                        rs.getInt("demons"),
                        rs.getInt("ranking"),
                        rs.getInt("creatorpoints"),
                        rs.getInt("iconID"),
                        rs.getInt("playerColor"),
                        rs.getInt("playerColor2"),
                        rs.getInt("secretCoins"),
                        rs.getInt("iconType"),
                        rs.getInt("special"),
                        rs.getInt("usercoins"),
                        rs.getInt("messageState"),
                        rs.getInt("friendsState"),
                        rs.getInt("accIcon"),
                        rs.getInt("accBall"),
                        rs.getInt("accBird"),
                        rs.getInt("accShip"),
                        rs.getInt("accDart"),
                        rs.getInt("accRobot"),
                        rs.getInt("accGlow"),
                        rs.getInt("isRegistered"),
                        rs.getInt("globalRank"),
                        rs.getInt("messages"),
                        rs.getInt("friendRequests"),
                        rs.getInt("newFriends"),
                        rs.getInt("newFriendRequest"),
                        rs.getInt("age"),
                        rs.getInt("accSpider"),
                        rs.getInt("diamonds"),
                        rs.getInt("accExplosion"),
                        rs.getInt("modType"));
            return new Account();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Account map(int userID, boolean full, boolean fromDB) {
        if (accounts.containsKey(userID) && !fromDB) return accounts.get(userID);
        if (!full) return map(userID);
        try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE userID = ? LIMIT 1")) {
            ps.setInt(1, userID);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return new Account(rs.getString("userName"),
                        rs.getString("gjpSalt"),
                        rs.getString("gjp"),
                        rs.getString("email"),
                        rs.getString("twitter"),
                        rs.getString("twitch"),
                        rs.getString("youtube"),
                        rs.getInt("userID"),
                        rs.getInt("stars"),
                        rs.getInt("demons"),
                        rs.getInt("ranking"),
                        rs.getInt("creatorpoints"),
                        rs.getInt("iconID"),
                        rs.getInt("playerColor"),
                        rs.getInt("playerColor2"),
                        rs.getInt("secretCoins"),
                        rs.getInt("iconType"),
                        rs.getInt("special"),
                        rs.getInt("userCoins"),
                        rs.getInt("messageState"),
                        rs.getInt("friendsState"),
                        rs.getInt("accIcon"),
                        rs.getInt("accBall"),
                        rs.getInt("accBird"),
                        rs.getInt("accShip"),
                        rs.getInt("accDart"),
                        rs.getInt("accRobot"),
                        rs.getInt("accGlow"),
                        rs.getInt("isRegistered"),
                        rs.getInt("globalRank"),
                        rs.getInt("messages"),
                        rs.getInt("friendRequests"),
                        rs.getInt("newFriends"),
                        rs.getInt("newFriendRequest"),
                        rs.getInt("age"),
                        rs.getInt("accSpider"),
                        rs.getInt("diamonds"),
                        rs.getInt("accExplosion"),
                        rs.getInt("modType"));
            return new Account();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Account map(String username) {
        try(PreparedStatement ps = conn.prepareStatement("SELECT userName, gjp, email, userID FROM users WHERE userName = ?")) {
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (accounts.containsKey(rs.getInt("userID"))) return accounts.get(rs.getInt("userID"));
                return new Account(rs.getString("userName"),
                        rs.getString("gjp"),
                        rs.getString("email"));
            }
            return new Account();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Account map(String username, boolean full) {
        if (!full) return map(username);
        try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE userName = ?")) {
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return map(rs.getInt("userID"), true);
            return new Account();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}