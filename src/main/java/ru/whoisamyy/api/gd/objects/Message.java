package ru.whoisamyy.api.gd.objects;

import lombok.Getter;
import ru.whoisamyy.api.gd.misc.RelationshipsManager;
import ru.whoisamyy.api.gd.misc.GDObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import static ru.whoisamyy.api.gd.misc.RelationshipsManager.isBlocked;

public class Message extends GDObject {
    public static int lastMessageID;
    private static Hashtable<Integer, Message> messages = new Hashtable<>();

    public static Connection conn;

    @Getter
    int ID;
    @Getter
    int senderID;
    @Getter
    int targetID;
    @Getter
    String subject;
    @Getter
    String body;
    @Getter
    boolean isNew = true; //is read?


    public static Hashtable<Integer, Message> getMessagesHashtable() {
        getMessages();
        return messages;
    }

    public static void getMessages() {
        if (!messages.isEmpty()) messages.clear();
        for (int i = 1; i <= lastMessageID; i++) {
            messages.put(i, map(i));
        }
    }

    public Message() {}

    public Message(int senderID, int targetID, String subject, String body, boolean isNew) {
        this.senderID = senderID;
        this.targetID = targetID;
        this.subject = subject;
        this.body = body;
        this.isNew = isNew;
    }



    public byte send() {
        String sql = "INSERT INTO messages (subject, body, person1ID, person2ID, isNew) VALUES (?, ?, ?, ?, 1)";
        if (RelationshipsManager.isBlocked(senderID, targetID)) return -1;
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, getSubject());
            ps.setString(2, getBody());
            ps.setInt(3, getSenderID());
            ps.setInt(4, getTargetID());

            ps.execute();
            lastMessageID++;
            return 1;
        } catch (SQLException e) {
            return -1;
        }
    }

    public static String getMessages(int accountID, int page, boolean isSender) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        if (!isSender) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT ID FROM messages WHERE person1ID = ? LIMIT 10 OFFSET ?")) {
                ps.setInt(1, accountID);
                ps.setInt(2, page * 10);

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    sb.append(map(rs.getInt("ID"))).append("|");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try(PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM messages WHERE person1ID = ?")) {
                ps.setInt(1, accountID);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) count = 0;
                count = rs.getInt(1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            try (PreparedStatement ps = conn.prepareStatement("SELECT ID FROM messages WHERE person2ID = ? LIMIT 10 OFFSET ?")) {
                ps.setInt(1, accountID);
                ps.setInt(2, page * 10);

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    sb.append(map(rs.getInt("ID"))).append("|");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            try(PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM messages WHERE person2ID = ?")) {
                ps.setInt(1, accountID);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) count = 0;
                count = rs.getInt(1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if (sb.isEmpty()) return "";
        sb.deleteCharAt(sb.length()-1);
        return sb.toString()+"#"+count+":"+page*10+":10";
    }

    public static byte delete(int id) {
        try(PreparedStatement ps = conn.prepareStatement("DELETE FROM messages WHERE ID = ?")) {
            ps.setInt(1, id);
            ps.execute();
            return 1;
        } catch (SQLException e) {
            return 1;
        }
    }

    public static String download(int id, boolean isSender) {
        if (!isSender) {
            try (PreparedStatement ps = conn.prepareStatement("UPDATE messages SET isNew = 1 WHERE ID = ?")) {
                ps.setInt(1, id);

                ps.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return map(id).toString(isSender);
    }

    public String toString(boolean isSender) {
        StringBuilder sb = new StringBuilder();
        sb.append("1:").append(ID).append(":");
        sb.append("2:").append(senderID).append(":");
        sb.append("3:").append(targetID).append(":");
        sb.append("4:").append(subject).append(":");
        sb.append("5:").append(body).append(":");
        sb.append("6:").append(Account.map(targetID).getUsername()).append(":");
        sb.append("8:").append(isNew?1:0).append(":");
        sb.append("9:").append(isSender?1:0).append(":");
        return sb.toString();
    }

    @Override
    public String toString() {
        return toString(":");
    }

    public String toString(String sep) {
        StringBuilder sb = new StringBuilder();
        sb.append("1").append(sep).append(ID).append(sep);
        sb.append("2").append(sep).append(senderID).append(sep);
        sb.append("3").append(sep).append(targetID).append(sep);
        sb.append("4").append(sep).append(subject).append(sep);
        sb.append("5").append(sep).append(body).append(sep);
        sb.append("6").append(sep).append(Account.map(targetID).getUsername()).append(sep);
        sb.append("8").append(sep).append(isNew?1:0).append(sep);
        sb.append("9").append(sep).append(0).append(sep);
        return sb.toString();
    }

    public static Message map(int messageID) {
        try(PreparedStatement ps = conn.prepareStatement("SELECT person1ID, person2ID, subject, body, isNew FROM messages WHERE ID = ?")) {
            ps.setInt(1, messageID);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) new Message();

            return new Message(rs.getInt("person1ID"), rs.getInt("person2ID"), rs.getString("subject"), rs.getString("body"), rs.getBoolean("isNew"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
