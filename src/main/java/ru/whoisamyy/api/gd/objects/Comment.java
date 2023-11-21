package ru.whoisamyy.api.gd.objects;

import ru.whoisamyy.api.utils.comparators.CommentComparators;
import ru.whoisamyy.api.gd.misc.GDObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

public class Comment extends GDObject {
    public static int lastCommentID;
    private static HashSet<Comment> comments = new HashSet<>();
    public static Connection conn;

    int ID;
    int accountID; //comment author id
    String username;
    String comment;
    int levelID = 0;
    int percentage = 0;
    int likes;
    boolean isSpam;
    boolean isAcc = false;


    public int getID() {
        return ID;
    }

    public int getAccountID() {
        return accountID;
    }

    public String getUsername() {
        return username;
    }

    public String getComment() {
        return comment;
    }

    public int getLevelID() {
        return levelID;
    }

    public int getPercentage() {
        return percentage;
    }

    public int getLikes() {
        return likes;
    }

    public static HashSet<Comment> getCommentsHashset() {
        getComments();
        return comments;
    }

    public boolean isSpam() {
        return isSpam;
    }

    public boolean isAcc() {
        return isAcc;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setLevelID(int levelID) {
        this.levelID = levelID;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setSpam(boolean spam) {
        isSpam = spam;
    }

    public void setAcc(boolean acc) {
        isAcc = acc;
    }
    public Comment() {

    }

    public Comment(int accountID, String username, String comment, int levelID, int percentage) { //level comment
        this.accountID = accountID;
        this.username = username;
        this.comment = comment;
        this.levelID = levelID;
        this.percentage = percentage;
        this.isAcc = false;
    }

    public Comment(int accountID, String username, String comment) { //acc comment
        this.accountID = accountID;
        this.username = username;
        this.comment = comment;
        this.isAcc = true;
    }

    public Comment(int ID, int accountID, String username, String comment, int levelID, int percentage, int likes, boolean isSpam, boolean isAcc) { // map
        this.ID = ID;
        this.accountID = accountID;
        this.username = username;
        this.comment = comment;
        this.levelID = levelID;
        this.percentage = percentage;
        this.likes = likes;
        this.isSpam = isSpam;
        this.isAcc = isAcc;
    }


    public String toString(boolean noLvlId) {
        StringBuilder sb = new StringBuilder();

        if (!noLvlId) sb.append(1).append("~").append(getLevelID()).append("~");
        sb.append(2).append("~").append(getComment()).append("~");
        sb.append(3).append("~").append(getAccountID()).append("~");
        sb.append(4).append("~").append(getLikes()).append("~");
        sb.append(6).append("~").append(getID()).append("~");
        sb.append(7).append("~").append(isSpam()).append("~");
        sb.append(8).append("~").append(getAccountID()).append("~");
        sb.append(9).append("~").append("now").append("~");
        if (!isAcc) {
            sb.append(10).append("~").append(getPercentage()).append("~");
        }
        sb.append(":");
        sb.append(Account.map(getAccountID(), true).toString("~", true));

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(1).append("~").append(getLevelID()).append("~");
        sb.append(2).append("~").append(getComment()).append("~");
        sb.append(3).append("~").append(getAccountID()).append("~");
        sb.append(4).append("~").append(getLikes()).append("~");
        sb.append(6).append("~").append(getID()).append("~");
        sb.append(7).append("~").append(isSpam()).append("~");
        sb.append(8).append("~").append(getAccountID()).append("~");
        sb.append(9).append("~").append("now").append("~");
        if (!isAcc) {
            sb.append(10).append("~").append(getPercentage()).append("~");
        }
        sb.append(":");
        sb.append(Account.map(getAccountID(), true).toString("~", true));

        return sb.toString();
    }

    public String toString(String sep) {
        StringBuilder sb = new StringBuilder();

        sb.append(1).append(sep).append(getLevelID()).append(sep);
        sb.append(2).append(sep).append(getComment()).append(sep);
        sb.append(3).append(sep).append(getAccountID()).append(sep);
        sb.append(4).append(sep).append(getLikes()).append(sep);
        sb.append(6).append(sep).append(getID()).append(sep);
        sb.append(7).append(sep).append(isSpam()).append(sep);
        sb.append(8).append(sep).append(getAccountID()).append(sep);
        sb.append(9).append(sep).append("now").append(sep);
        if (!isAcc) {
            sb.append(10).append(sep).append(getPercentage()).append(sep);
        }
        sb.append(":");
        sb.append(Account.map(getAccountID(), true).toString(sep));

        return sb.substring(0, sb.length()-1);
    }

    public byte upload() {
        if (!Account.getAccountsHashtable().containsKey(accountID) && isAcc) return -1;
        if (!Level.getLevelsHashtable().containsKey(levelID) && !isAcc) return -1;

        try(PreparedStatement ps = conn.prepareStatement("INSERT INTO comments (userID, userName, levelID, comment, percent, isAcc) VALUES (?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, getAccountID());
            ps.setString(2, getUsername());
            ps.setInt(3, getLevelID());
            ps.setString(4, getComment());
            ps.setInt(5, getPercentage());
            ps.setBoolean(6, isAcc());

            ps.execute();
            this.ID = lastCommentID;
            comments.add(this);
            lastCommentID++;
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public byte delete() {
        try(PreparedStatement ps = conn.prepareStatement("DELETE FROM comments WHERE ID = ? LIMIT 1")) {
            ps.setInt(1, getID());

            ps.execute();
            return 1;
        } catch (SQLException e) {
            return -1;
        }
    }

    @Deprecated
    public byte uploadAccComment() {
        if (!isAcc) return -1;
        if (!Account.getAccountsHashtable().containsKey(accountID)) return -1;
        if (!Level.getLevelsHashtable().containsKey(levelID)) return -1;
        try(PreparedStatement ps = conn.prepareStatement("INSERT INTO comments (username, userID, comment, isAcc) VALUES (?, ?, ?, 1)")) {
            ps.setString(1, getUsername());
            ps.setInt(2, getAccountID());
            ps.setString(3, getComment());

            ps.execute();
            lastCommentID++;
            return 1;
        } catch (SQLException e) {
            return -1;
        }
    }

    /** Clears(if not empty already) and then refills comments hashset
     * @see Comment#comments
     */
    public static void getComments() { //this does not work like in Account or Level with reason of datatype
        if (!comments.isEmpty()) comments.clear();
        for (int i = 1; i <= lastCommentID; i++) {
            comments.add(map(i));
        }
    }


    /**
     * updates comment in comments hashset
     * @param ID comment id to be updated
     * @see Comment#comments
     */
    public static void updateComments(int ID) {
        comments.removeIf(comm -> comm.getID() == ID);
        comments.add(map(ID));
    }

    /**
     * getGJAccountComments or getGJComments
     * @param ID level/account id based on acc param
     * @param acc get account or level comment (true=account, false=level)
     * @return commentsString
     */
    public static String getComments(int ID, int page, boolean acc, int mode) {
        getComments();
        TreeSet<Comment> commentTreeSet = new TreeSet<>(mode==0? new CommentComparators.IDComparatorDescension():new CommentComparators.LikeComparator());
        commentTreeSet.addAll(comments);
        if (acc) commentTreeSet.removeIf(x->x.getAccountID()!=ID);
        else commentTreeSet.removeIf(x->x.getLevelID()!=ID);
        ArrayList<Comment> commentArrayList = new ArrayList<>(commentTreeSet);
        ArrayList<Comment> subList = new ArrayList<>();
        if (commentArrayList.size()==1) {
            subList.add(commentArrayList.get(0));
        } else {
            subList.addAll(commentArrayList.subList(page*10, commentArrayList.size()>=10?page*10+10:commentArrayList.size()));
        }

        StringBuilder sb = new StringBuilder();
        for (Comment comment : subList) {
            if (comment.isAcc() && acc && comment.getAccountID()==ID)
                sb.append(comment.toString(true)).append("|");
            else if (!comment.isAcc() && !acc && comment.getLevelID()==ID)
                sb.append(comment.toString(true)).append("|");
        }
        try {
            sb.deleteCharAt(sb.lastIndexOf("|"));
        } catch (StringIndexOutOfBoundsException ignored) {
        }
        sb.append("#").append(commentTreeSet.size()).append(":").append(page).append(":").append("10");
        return sb.toString();
    }


    /**
     * Gets and returns comment object from database
     * @param id id of the comment
     * @return Comment object with given id
     */
    public static Comment map(int id) {
        try(PreparedStatement ps = conn.prepareStatement("SELECT ID, userID, userName, levelID, comment, likes, percent, isSpam FROM comments WHERE ID = ?")) {
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return new Comment();
            return new Comment(rs.getInt("ID"), rs.getInt("userID"), rs.getString("userName"),
                    rs.getString("comment"), rs.getInt("levelID"), rs.getInt("percent"),
                    rs.getInt("likes"), rs.getBoolean("isSpam"), rs.getInt("levelID")<=0);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
   }
}