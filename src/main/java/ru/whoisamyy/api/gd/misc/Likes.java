package ru.whoisamyy.api.gd.misc;

import ru.whoisamyy.core.Core;
import ru.whoisamyy.api.utils.enums.ItemType;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Likes {
    public static byte like(int id, ItemType type, boolean isLike) {
        try {
            switch (type) {
                case LEVEL -> {
                    PreparedStatement ps = Core.conn.prepareStatement("UPDATE levels SET likes = likes "+(isLike?"+":"-")+" 1 WHERE levelID = ?");
                    ps.setInt(1, id);
                    ps.execute();
                }
                case LEVEL_COMMENT, ACC_COMMENT -> {
                    PreparedStatement ps = Core.conn.prepareStatement("UPDATE comments SET likes = likes "+(isLike?"+":"-")+" 1 WHERE ID = ?");
                    ps.setInt(1, id);
                    ps.execute();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }
}
