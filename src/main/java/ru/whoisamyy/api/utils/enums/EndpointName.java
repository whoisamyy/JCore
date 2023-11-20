package ru.whoisamyy.api.utils.enums;

public enum EndpointName {
    LEVELS_GET("getGJLevels", 0),
    LEVELS_UPLOAD("uploadGJLevel", 1),
    LEVELS_UPDATE_DESCRIPTION("updateGJDesc", 2),
    LEVELS_DOWNLOAD("downloadGJLevel", 3),
    LEVELS_DELETE("deleteGJLevel", 4),
    ACCOUNTS_REGISTER("registerGJAccount", 5),
    ACCOUNTS_LOGIN("loginGJAccount", 6),
    ACCOUNTS_BACKUP("backupGJAccount", 7),
    ACCOUNTS_SYNC("syncGJAccount", 8),
    ACCOUNTS_GETINFO("getGJUserInfo", 9),
    ACCOUNTS_GETURL("getAccountURL", 10),
    ACCOUNTS_SETTINGS_UPDATE("updateGJAccSettings", 11),
    ACCOUNTS_COMMENTS_UPLOAD("uploadGJAccComment", 12),
    ACCOUNTS_COMMENTS_GET("getGJAccountComments", 13),
    ACCOUNTS_GETLIST("getGJUserList", 14),
    COMMENTS_UPLOAD("uploadGJComment", 15),
    COMMENTS_GET("getGJComments", 16),
    BLOCK_USER("blockGJUser", 17),
    UNBLOCK_USER("unblockGJUser", 18),
    FRIEND_REQUESTS_UPLOAD("uploadGJFriendRequest", 19),
    FRIEND_REQUESTS_ACCEPT("acceptGJFriendRequest", 20),
    FRIEND_REQUESTS_DELETE("deleteGJFriendRequest", 21),
    FRIEND_REQUESTS_READ("readGJFriendRequest", 22),
    FRIEND_REQUESTS_GET("getGJFriendRequests", 23),
    SCORES_GET("getGJScores", 24),
    SCORES_LEVELS_GET("getGJLevelScores", 25),
    MESSAGES_DOWNLOAD("downloadGJMessage", 26),
    MESSAGES_GET("getGJMessages", 27),
    MESSAGES_UPLOAD("uploadGJMessage", 28),
    SONGS_GET_INFO("getGJSongInfo", 29),
    SONGS_UPLOAD("songAdd", 30),
    LIKE("likeGJItem", 31),
    MOD_REQUEST_USER_ACCESS("requestGJUserAccess", 32),
    FRIEND_REMOVE("removeGJFriend", 33); //TODO add all endpoints


    private String name;
    private int index;
    private EndpointName(String name, int index ) {
        this.name = name;
        this.index = index;
    }

    public static EndpointName getByName(String val) {
        return valueOf(val);
    }
}
