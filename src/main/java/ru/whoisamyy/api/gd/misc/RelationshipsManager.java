package ru.whoisamyy.api.gd.misc;

import ru.whoisamyy.api.gd.objects.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class RelationshipsManager {
    public static Connection conn;

    /**
     * same as uploadGJFriendRequest (may produce NullPointerException)
     * @see RelationshipsManager#sendFriendRequest(Account, Account, String)
     * @param senderAccountID account id of sender
     * @param toAccountID account id of person request to be sent
     * @param comment base64 encoded string
     * @return 1 if success, otherwise -1
     */
    public static byte sendFriendRequest(int senderAccountID, int toAccountID, String comment) {
        return sendFriendRequest(Account.map(senderAccountID, true), Account.map(toAccountID, true), comment);
    }

    /**
     * same as acceptGJFriendRequest (may produce NullPointerException)
     * @see RelationshipsManager#addFriend(Account, Account)
     * @param senderAccountID sender of the request
     * @param friendAccountID account id whom request to be accepted
     * @return 1 if success, otherwise -1
     */
    public static byte addFriend(int senderAccountID, int friendAccountID) {
        return addFriend(Account.map(senderAccountID, true), Account.map(friendAccountID, true));
    }

    /**
     * same as removeGJFriend (may produce NullPointerException)
     * @see RelationshipsManager#removeFriend(Account, Account)
     * @param senderAccountID account id of person which removes targetAccount from friend list
     * @param targetAccountID person's account id to be removed from friend list of senderAccountID
     * @return 1 if success, otherwise -1
     */
    public static byte removeFriend(int senderAccountID, int targetAccountID) {
        return removeFriend(Account.map(senderAccountID, true), Account.map(targetAccountID, true));
    }


    /**
     * same as readGJFriendRequest (may produce NullPointerException)
     * @see RelationshipsManager#readFriendRequest(int, Account)
     * @param requestID request id (я вообще не понимаю)
     * @param targetAccountID target account id (omg real???)
     * @return 1 if success, otherwise -1
     */
    public static byte readFriendRequest(int requestID, int targetAccountID) {
        return readFriendRequest(requestID, Account.map(targetAccountID, true));
    }


    /**
     * same as deleteGJFriendRequest (may produce NullPointerException)
     * @see RelationshipsManager#deleteFriendRequest(Account, Account)
     * @param senderAccountID sender account id
     * @param targetAccountID target account id
     * @return always 1
     */
    public static byte deleteFriendRequest(int senderAccountID, int targetAccountID) {
        return deleteFriendRequest(Account.map(senderAccountID, true), Account.map(targetAccountID, true));
    }

    /**
     * same as blockGJUser (may produce NullPointerException)
     * @param senderAccountID sender account id
     * @param targetAccountID target account id
     * @return always 1
     */
    public static byte blockUser(int senderAccountID, int targetAccountID) {
        return blockUser(Account.map(senderAccountID, true), Account.map(targetAccountID, true));
    }

    /**
     * same as unblockGJUser (may produce NullPointerException)
     * @param senderAccountID sender account id
     * @param targetAccountID target account id
     * @return always 1
     */
    public static byte unblockUser(int senderAccountID, int targetAccountID) {
        return unblockUser(Account.map(senderAccountID, true), Account.map(targetAccountID, true));
    }

    public static byte readFriendRequest(int requestID, Account targetAccount) {
        try(PreparedStatement ps = conn.prepareStatement("UPDATE friendreqs SET isNew = 0 WHERE ID = ? AND person2ID = ?")) {
            ps.setInt(1, requestID);
            ps.setInt(2, targetAccount.getUserID());

            ps.execute();
            return 1;
        } catch (SQLException e) {
            return -1;
        }
    }

    public static byte deleteFriendRequest(Account sender, Account targetAccount) { //зассал понятно
        try(PreparedStatement ps = conn.prepareStatement("DELETE FROM friendreqs WHERE (person1ID = ? AND person2ID = ?) OR (person2ID = ? AND person1ID = ?)")) {
            ps.setInt(1, sender.getUserID());
            ps.setInt(2, targetAccount.getUserID());

            ps.setInt(3, sender.getUserID());
            ps.setInt(4, targetAccount.getUserID());

            ps.execute();
        } catch (SQLException e) {
            return 1;
        }
        return 1;
    }

    public static byte removeFriend(Account sender, Account targetAccount) {
        //sad
        try(PreparedStatement ps = conn.prepareStatement("DELETE FROM friendships WHERE (person1ID = ? AND person2ID = ?) OR (person2ID = ? AND person1ID = ?)")) {
            ps.setInt(1, sender.getUserID());
            ps.setInt(2, targetAccount.getUserID());

            ps.setInt(3, sender.getUserID());
            ps.setInt(4, targetAccount.getUserID());

            ps.execute();
            sender.removeFromFriendsByKey(targetAccount.getUserID());
            targetAccount.removeFromFriendsByKey(sender.getUserID());
            return 1;
        } catch (SQLException e) {
            return -1;
        }
    }

    public static byte sendFriendRequest(Account sender, Account toAccount, String comment) {
        try(PreparedStatement ps2 = conn.prepareStatement("SELECT COUNT(*) AS c FROM friendreqs WHERE (person1ID = ? AND person2ID = ?) OR (person1ID = ? AND person2ID = ?)")) {
            ps2.setInt(1, sender.getUserID());
            ps2.setInt(2, toAccount.getUserID());

            ps2.setInt(3, toAccount.getUserID());
            ps2.setInt(4, sender.getUserID());

            ResultSet rs = ps2.executeQuery();

            if (rs.next())
                if (rs.getInt("c")!=0) return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

        try(PreparedStatement ps = conn.prepareStatement("INSERT INTO friendreqs (person1ID, person2ID, comment, isNew) VALUES (?, ?, ?, 1)")) {
            ps.setInt(1, sender.getUserID());
            ps.setInt(2, toAccount.getUserID());
            ps.setString(3, comment);

            ps.execute();
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static byte addFriend(Account sender, Account friendAccount) {
        try(PreparedStatement ps = conn.prepareStatement("INSERT INTO friendships (person1ID, person2ID) VALUES(?, ?)")) {
            ps.setInt(1, sender.getUserID());
            ps.setInt(2, friendAccount.getUserID());

            ps.execute();
            sender.addFriend(friendAccount);
            friendAccount.addFriend(sender);
            return 1;
        } catch (SQLException e) {
            return -1;
        }
    }
    public static byte blockUser(Account sender, Account targetAccount) {
        try(PreparedStatement ps = conn.prepareStatement("INSERT INTO blocks (person1ID, person2ID) VALUES (?, ?)")) {
            ps.setInt(1, sender.getUserID());
            ps.setInt(2, targetAccount.getUserID());
            ps.execute();
            return 1;
        } catch (SQLException e) {
            return 1;
        }
    }

    public static byte unblockUser(Account sender, Account targetAccount) {
        try(PreparedStatement ps = conn.prepareStatement("DELETE FROM blocks WHERE person1ID = ? AND person2ID = ?")) {
            ps.setInt(1, sender.getUserID());
            ps.setInt(2, targetAccount.getUserID());

            ps.execute();
            return 1;
        } catch (SQLException e) {
            return 1;
        }
    }

    public static boolean isBlocked(Account sender, Account target) {
        try(PreparedStatement ps = conn.prepareStatement("SELECT ID FROM blocks WHERE (person1ID = ? AND person2ID = ?) OR (person1ID = ? AND person2ID = ?)")) {
            ps.setInt(1, sender.getUserID());
            ps.setInt(2, target.getUserID());

            ps.setInt(3, target.getUserID());
            ps.setInt(4, sender.getUserID());

            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isBlocked(int senderAccountID, int targetAccountID) {
        return isBlocked(Account.map(senderAccountID, true), Account.map(targetAccountID, true));
    }

    public static Map<String, List<Account>> getUserList(int accountID, boolean isBlocklist) {
        StringBuilder sb = new StringBuilder();
        List<Account> accounts = new ArrayList<>();
        if (!isBlocklist) {
            for (Account account : Account.map(accountID, true).getFriendsHashtable().values()) {
                accounts.add(account);
                sb.append(account.toString()).append("|");
            }
        } else {
            for (Account account : Account.getAccountsHashtable().values()) {
                if (isBlocked(accountID, account.getUserID())) {
                    accounts.add(account);
                    sb.append(account).append("|");
                }
            }
        }
        try {
            sb.deleteCharAt(sb.lastIndexOf("|"));
        } catch (IndexOutOfBoundsException ignored) {

        }
        return new Hashtable<>(Map.of(sb.toString(), accounts));
    }

    public static String getFriendRequests(int accountID, boolean getSent, int page) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        if (getSent) {
            try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM friendreqs WHERE person1ID = ? LIMIT 20 OFFSET ?")) {
                ps.setInt(1, accountID);
                ps.setInt(2, page*20);
                ResultSet rs = ps.executeQuery();

                PreparedStatement countreq = conn.prepareStatement("SELECT COUNT(*) FROM friendreqs WHERE person1ID = ? LIMIT 20 OFFSET ?");
                countreq.setInt(1, accountID);
                countreq.setInt(2, page*20);
                ResultSet rs2 = countreq.executeQuery();
                if (rs2.next())
                    count = rs2.getInt(1);

                while (rs.next()) {
                    Account acc = Account.map(rs.getInt("person2ID"), true);
                    sb.append("1:").append(acc.getUsername()).append(":");
                    sb.append("2:").append(acc.getUserID()).append(":");
                    sb.append("9:").append(acc.getIconID()).append(":");
                    sb.append("10:").append(acc.getPlayerColor()).append(":");
                    sb.append("11:").append(acc.getPlayerColor2()).append(":");
                    sb.append("14:").append(acc.getIconType()).append(":");
                    sb.append("15:").append(acc.getAccGlow()).append(":");
                    sb.append("16:").append(acc.getUserID()).append(":");
                    sb.append("32:").append(rs.getInt("ID")).append(":");
                    sb.append("35:").append(rs.getString("comment")).append(":");
                    sb.append("37:").append(0).append(":");
                    sb.append("41:").append(rs.getBoolean("isNew")).append("|");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return "-1";
            }
        }
        else {
            try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM friendreqs WHERE person2ID = ? LIMIT 20 OFFSET ?")) {
                ps.setInt(1, accountID);
                ps.setInt(2, page*20);
                ResultSet rs = ps.executeQuery();

                PreparedStatement countreq = conn.prepareStatement("SELECT COUNT(*) FROM friendreqs WHERE person2ID = ? LIMIT 20 OFFSET ?");
                countreq.setInt(1, accountID);
                countreq.setInt(2, page*20);
                ResultSet rs2 = countreq.executeQuery();
                if (rs2.next());
                    count = rs2.getInt(1);

                while (rs.next()) {
                    Account acc = Account.map(rs.getInt("person1ID"), true);
                    sb.append("1:").append(acc.getUsername()).append(":");
                    sb.append("2:").append(acc.getUserID()).append(":");
                    sb.append("9:").append(acc.getIconID()).append(":");
                    sb.append("10:").append(acc.getPlayerColor()).append(":");
                    sb.append("11:").append(acc.getPlayerColor2()).append(":");
                    sb.append("14:").append(acc.getIconType()).append(":");
                    sb.append("15:").append(acc.getAccGlow()).append(":");
                    sb.append("16:").append(acc.getUserID()).append(":");
                    sb.append("32:").append(rs.getInt("ID")).append(":");
                    sb.append("35:").append(rs.getString("comment")).append(":");
                    sb.append("37:").append(0).append(":");
                    sb.append("41:").append(rs.getBoolean("isNew")).append("|");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return "-1";
            }
        }
        try {
            sb.deleteCharAt(sb.lastIndexOf("|"));
        } catch (IndexOutOfBoundsException ignored) {}

        sb.append("#").append(count).append(":").append(page).append(":").append(20);

        return sb.toString();
    }
}
