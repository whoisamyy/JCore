package ru.whoisamyy.api.utils.enums;

import lombok.Getter;
import ru.whoisamyy.api.plugins.events.*;

public enum EndpointName {
    LEVELS_GET("getGJLevels", 0, GetLevelsEvent.class),
    LEVELS_UPLOAD("uploadGJLevel", 1, UploadLevelEvent.class),
    LEVELS_UPDATE_DESCRIPTION("updateGJDesc", 2, UpdateLevelDescEvent.class),
    LEVELS_DOWNLOAD("downloadGJLevel", 3, DownloadLevelEvent.class),
    LEVELS_DELETE("deleteGJLevel", 4, DeleteLevelEvent.class),
    ACCOUNTS_REGISTER("registerGJAccount", 5, RegisterAccountEvent.class),
    ACCOUNTS_LOGIN("loginGJAccount", 6, LoginAccountEvent.class),
    ACCOUNTS_BACKUP("backupGJAccount", 7, BackupAccountEvent.class),
    ACCOUNTS_SYNC("syncGJAccount", 8, SyncAccountEvent.class),
    ACCOUNTS_GETINFO("getGJUserInfo", 9, GetUserInfoEvent.class),
    ACCOUNTS_GETURL("getAccountURL", 10, GetAccountURLEvent.class),
    ACCOUNTS_SETTINGS_UPDATE("updateGJAccSettings", 11, UpdateAccountSettingsEvent.class),
    ACCOUNTS_COMMENTS_UPLOAD("uploadGJAccComment", 12, UploadAccountCommentEvent.class),
    ACCOUNTS_COMMENTS_GET("getGJAccountComments", 13, GetAccountCommentsEvent.class),
    ACCOUNTS_GETLIST("getGJUserList", 14, GetUserListEvent.class),
    COMMENTS_UPLOAD("uploadGJComment", 15, UploadCommentEvent.class),
    COMMENTS_GET("getGJComments", 16, GetCommentsEvent.class),
    BLOCK_USER("blockGJUser", 17, BlockUserEvent.class),
    UNBLOCK_USER("unblockGJUser", 18, UnblockUserEvent.class),
    FRIEND_REQUESTS_UPLOAD("uploadGJFriendRequest", 19, UploadFriendRequestEvent.class),
    FRIEND_REQUESTS_ACCEPT("acceptGJFriendRequest", 20, AcceptFriendRequestEvent.class),
    FRIEND_REQUESTS_DELETE("deleteGJFriendRequest", 21, DeleteFriendRequestEvent.class),
    FRIEND_REQUESTS_READ("readGJFriendRequest", 22, ReadFriendRequestEvent.class),
    FRIEND_REQUESTS_GET("getGJFriendRequests", 23, GetFriendRequestsEvent.class),
    SCORES_GET("getGJScores", 24, GetScoresEvent.class),
    SCORES_LEVELS_GET("getGJLevelScores", 25, GetLevelScoresEvent.class),
    MESSAGES_DOWNLOAD("downloadGJMessage", 26, DownloadMessageEvent.class),
    MESSAGES_GET("getGJMessages", 27, GetMessagesEvent.class),
    MESSAGES_UPLOAD("uploadGJMessage", 28, UploadMessageEvent.class),
    SONGS_GET_INFO("getGJSongInfo", 29, GetSongInfoEvent.class),
    SONGS_UPLOAD("songAdd", 30, UploadSongEvent.class),
    LIKE("likeGJItem", 31, LikeEvent.class),
    MOD_REQUEST_USER_ACCESS("requestGJUserAccess", 32, RequestUserAccessEvent.class),
    FRIEND_REMOVE("removeGJFriend", 33, RemoveFriendEvent.class),
    LEVELS_GET_DAILY("getGJDailyLevel", 34, GetDailyLevelEvent.class),
    COMMENT_DELETE("deleteGJComment", 35, DeleteCommentEvent.class),
    GAUNTLETS_GET("getGJGauntlets", 36, GetGauntletsEvent.class),
    LEVELS_RATE_STARS("rateGJStars", 37, RateLevelStarsEvent.class),
    LEVELS_RATE_DEMON("rateGJDemon", 38, RateLevelDemonEvent.class);
    //TODO add all endpoints


    private final String name;
    private final int index;
    @Getter
    public final Class<? extends Event> event;
    EndpointName(String name, int index, Class<? extends Event> event) {
        this.name = name;
        this.index = index;
        this.event = event;
    }

    public static EndpointName getByName(String val) {
        return valueOf(val);
    }
    public static String getValue(EndpointName endpointName) {
        return endpointName.name;
    }
}
