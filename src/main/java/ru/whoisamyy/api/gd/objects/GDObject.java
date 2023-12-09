package ru.whoisamyy.api.gd.objects;

import lombok.Getter;

import java.sql.Connection;

public abstract class GDObject {
    //public abstract String toString();
    //public abstract String toString(String sep);
    @Getter
    protected static Connection conn;

    public static void setConn(Connection conn) {
        GDObject.conn = conn;
    }
}
