package ru.whoisamyy.api.gd.objects;

import lombok.Getter;
import lombok.Setter;
import ru.whoisamyy.api.utils.comparators.CommentComparators;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Getter
public class Comment extends GDObject {
    public static int lastCommentID;
    private static final HashSet<Comment> comments = new HashSet<>();
    //public static Connection conn;

    @Setter int ID;
    @Setter int accountID; //comment author id
    @Setter String username;
    @Setter String comment;
    @Setter int levelID = 0;
    @Setter int percentage = 0;
    @Setter int likes;
    @Setter boolean isSpam;
    @Setter boolean isAcc = false;


    public static HashSet<Comment> getCommentsHashset() {
        getComments();
        return comments;
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

    public int upload() {
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

    public int delete() {
        try(PreparedStatement ps = conn.prepareStatement("DELETE FROM comments WHERE ID = ? LIMIT 1")) {
            ps.setInt(1, getID());

            ps.execute();

            comments.removeIf(x->x.getID()==getID());
            return 1;
        } catch (SQLException e) {
            return -1;
        }
    }

    @Deprecated
    public int uploadAccComment() {
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
     * Same as getGJAccountComments or getGJComments
     * @param ID level/account id based on acc param
     * @param acc get account or level comment (true=account, false=level)
     * @param page page to return
     * @param mode sort mode: 0 = default(id descension), 1 = likes(likes descension)
     * @return commentsString
     */
    public static Map<String, List<Comment>> getComments(int ID, int page, boolean acc, int mode) {
        getComments();
        TreeSet<Comment> commentTreeSet = new TreeSet<>(mode==0? new CommentComparators.IDComparatorDescension():new CommentComparators.LikeComparator());
        commentTreeSet.addAll(comments);
        List<Comment> commentArrayList = new ArrayList<>(commentTreeSet);
        List<Comment> subList = new ArrayList<>();

        if (acc) {
            commentArrayList.removeIf(x -> !x.isAcc() || x.getAccountID()!=ID || x.getLevelID()!=0);
        } else {
            commentArrayList.removeIf(x -> x.isAcc() || x.getLevelID()!=ID);
        }

        int totalPages = (int) Math.ceil((double) commentArrayList.size() / 10);

        if (page < totalPages) {
            int startIndex = page * 10;
            int endIndex = Math.min(startIndex + 10, commentArrayList.size());

            subList = commentArrayList.subList(startIndex, endIndex);
        }

        StringBuilder sb = new StringBuilder();
        for (Comment comment : subList) {
            sb.append(comment.toString(true)).append("|");
        }
        try {
            sb.deleteCharAt(sb.lastIndexOf("|"));
        } catch (StringIndexOutOfBoundsException ignored) {}
        sb.append("#").append(commentArrayList.size()).append(":").append(page*10).append(":").append("10");
        return new Hashtable<>(Map.of(sb.toString(), subList));
    }


    /**
     * Gets and returns comment object from database
     * @param id id of the comment
     * @return Comment object with given id
     */
    public static Comment map(int id) {
        try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM comments WHERE ID = ?")) {
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return new Comment();
            return new Comment(rs.getInt("ID"), rs.getInt("userID"), rs.getString("userName"),
                    rs.getString("comment"), rs.getInt("levelID"), rs.getInt("percent"),
                    rs.getInt("likes"), rs.getBoolean("isSpam"), rs.getBoolean("isAcc"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
   }
}