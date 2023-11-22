package ru.whoisamyy.core.endpoints;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.whoisamyy.api.gd.misc.Likes;
import ru.whoisamyy.api.gd.misc.RelationshipsManager;
import ru.whoisamyy.api.gd.objects.*;
import ru.whoisamyy.api.utils.Utils;
import ru.whoisamyy.api.utils.enums.EndpointName;
import ru.whoisamyy.api.utils.enums.ItemType;
import ru.whoisamyy.api.utils.enums.LeaderboardType;
import ru.whoisamyy.api.utils.exceptions.InvalidValueException;
import ru.whoisamyy.core.Core;
import ru.whoisamyy.core.PluginManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

@RestController
@PropertySource("file:settings.yml")
public class RequestManager {
    public static Hashtable<Integer, Level> levels = new Hashtable<>();
    public static Hashtable<Integer, Account> accounts = new Hashtable<>(); //<id, account>
    public static HashSet<Comment> comments = new HashSet<>();
    public static Hashtable<Integer, Score> scores = new Hashtable<>();
    public static Hashtable<Integer, Song> songs = new Hashtable<>();
    public static Hashtable<Integer, Message> messages = new Hashtable<>();

    @Value("${server_url}")
    public String serverURL;
    private static Logger logger = LogManager.getLogger(RequestManager.class);
    private final static boolean experimental = false;

    @RestController
    public class Levels {
        //через EndpointMethods
        @PostMapping("/{serverURL}/uploadGJLevel21.php")
        public int uploadGJLevel(@RequestParam int gameVersion,
                                 @RequestParam int accountID,
                                 @RequestParam @Nullable Integer levelID,
                                 @RequestParam String levelName,
                                 @RequestParam String levelDesc,
                                 @RequestParam int levelVersion,
                                 @RequestParam int levelLength,
                                 @RequestParam int audioTrack,
                                 @RequestParam int auto,
                                 @RequestParam int password,
                                 @RequestParam int original,
                                 @RequestParam boolean twoPlayer,
                                 @RequestParam int songID,
                                 @RequestParam int objects,
                                 @RequestParam int coins,
                                 @RequestParam int requestedStars,
                                 @RequestParam boolean unlisted,
                                 @RequestParam boolean ldm,
                                 @RequestParam String levelString, @RequestParam String secret, @RequestParam String gjp) {
            
            if (!Objects.equals(secret, Core.secrets.get("common"))) return -1;
            if (!Account.map(accountID, true).checkGJP(gjp)) return -1;
            Level lvl = null;
            if (levelID != null)
                try {
                    lvl = new Level(gameVersion, accountID, levelID, levelName, levelDesc, levelVersion, levelLength, audioTrack, auto, password, original, twoPlayer, songID, objects, coins, requestedStars, unlisted, ldm, levelString);
                } catch (InvalidValueException e) {
                    return -1;
                }
            else
                try {
                    lvl = new Level(gameVersion, accountID, levelName, levelDesc, levelVersion, levelLength, audioTrack, auto, password, original, twoPlayer, songID, objects, coins, requestedStars, unlisted, ldm, levelString);
                } catch (InvalidValueException e) {
                    return -1;
                }

            try {
                Object[] vals = {gameVersion, accountID, levelID, levelName, levelDesc, levelVersion, levelLength, audioTrack, auto, password, original, twoPlayer, songID, objects, coins, requestedStars, unlisted, ldm, levelString, secret, gjp};
                Parameter[] pars = RequestManager.Levels.class.getMethod("uploadGJLevel", int.class, int.class, Integer.class, String.class, String.class, int.class, int.class, int.class, int.class, int.class, int.class, boolean.class, int.class, int.class, int.class, int.class, boolean.class, boolean.class, String.class, String.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.LEVELS_UPLOAD);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }

            int lvlid = lvl.upload(false);
            levels = Level.getLevelsHashtable(); //gl
            return lvlid;
        }

        @PostMapping("/{serverURL}/updateGJDesc20.php")
        public int updateGJDesc(@RequestParam int accountID, @RequestParam int levelID, @RequestParam String levelDesc, @RequestParam String secret, @RequestParam String gjp) {
            //"/updateGJDesc20.php").equals(request.getRequestURI())) return -1;
            if (!Objects.equals(secret, Core.secrets.get("common"))) return -1;
            if (!Account.map(accountID, true).checkGJP(gjp)) return -1;
            Level lvl = levels.get(levelID);

            try {
                Object[] vals = {accountID, levelID, levelDesc, secret, gjp};
                Parameter[] pars = RequestManager.Levels.class.getMethod("updateGJDesc", int.class, int.class, String.class, String.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.LEVELS_UPDATE_DESCRIPTION);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }

            int ret = lvl.updateDesc(levelDesc);
            return ret;
        }

        @PostMapping("/{serverURL}/getGJLevels21.php")
        public String getGJLevels(@RequestParam String secret, @RequestParam @Nullable Integer gameVersion, @RequestParam @Nullable Integer binaryVersion,
                                       @RequestParam Integer type, @RequestParam @Nullable String str, @RequestParam @Nullable Integer page,
                                       @RequestParam @Nullable Integer total, @RequestParam @Nullable String gjp, @RequestParam @Nullable Integer accountID,
                                       @RequestParam @Nullable Integer gdw, @RequestParam @Nullable Integer gauntlet, @RequestParam @Nullable String diff,
                                       @RequestParam @Nullable String demonFilter, @RequestParam @Nullable String len, @RequestParam @Nullable Integer uncompleted,
                                       @RequestParam @Nullable Integer onlyCompleted, @RequestParam @Nullable String completedLevels,
                                       @RequestParam @Nullable Integer featured, @RequestParam @Nullable Integer original, @RequestParam @Nullable Integer twoPlayer,
                                       @RequestParam @Nullable Integer coins, @RequestParam @Nullable Integer epic, @RequestParam @Nullable Integer noStar,
                                       @RequestParam @Nullable Integer star, @RequestParam @Nullable Integer song, @RequestParam @Nullable Integer customSong,
                                       @RequestParam @Nullable String followed, @RequestParam @Nullable Integer local) {
            //"/getGJLevels21.php").equals(request.getRequestURI())) return "-1";
            if (!Objects.equals(secret, Core.secrets.get("common"))) return "-1";
            if (accountID!=null && gjp!=null && !Account.map(accountID, true).checkGJP(gjp)) return "-1";
            customSong = customSong!=null?customSong:0;
            assert diff != null;
            if (diff.equals("-")) diff = null;
            if (len.equals("-")) len = null;
            try {
                Object[] vals = {secret, gameVersion, binaryVersion, type, str, page, total, gjp, accountID, gdw, gauntlet, diff, demonFilter, len, uncompleted, onlyCompleted, completedLevels, featured, original, twoPlayer, coins, epic, noStar, star, song, customSong, followed, local};
                Parameter[] pars = RequestManager.Levels.class.getMethod("getGJLevels", String.class, Integer.class, Integer.class, Integer.class, String.class, Integer.class, Integer.class, String.class, Integer.class, Integer.class, Integer.class, String.class, String.class, String.class, Integer.class, Integer.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, String.class, Integer.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.LEVELS_GET);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            var ret = Level.getLevels(secret, gameVersion, binaryVersion, type, str, page, total, gjp, accountID, gdw, gauntlet, diff, demonFilter, len, uncompleted, onlyCompleted, completedLevels, featured, original, twoPlayer, coins, epic, noStar, star, song, customSong, followed, local);
            return ret;
        }

        @PostMapping("/{serverURL}/downloadGJLevel22.php")
        public String downloadGJLevel(@RequestParam int levelID, @RequestParam String secret, @RequestParam @Nullable Integer accountID, @RequestParam @Nullable String gjp) {
            //"/downloadGJLevel22.php").equals(request.getRequestURI())) return "-1";
            //logger.info(secret+" "+Core.secrets.get("common"));
            if (!Objects.equals(secret, Core.secrets.get("common"))) return "-1";
            accountID = accountID==null?0:accountID;
            gjp = gjp==null?"":gjp;
            if (gjp!="" && accountID != 0 && !Account.map(accountID, true).checkGJP(gjp)) return "-1";

            try {
                Object[] vals = {levelID, secret, accountID, gjp};
                Parameter[] pars = RequestManager.Levels.class.getMethod("downloadGJLevel", int.class, String.class, Integer.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.LEVELS_DOWNLOAD);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }

            try {
                String s = Level.download(secret, levelID);
                return s;
            } catch (Exception e) {
                e.printStackTrace();
                return "-1";
            }
        }

        @PostMapping("/{serverURL}/deleteGJLevelUser20.php")
        public int deleteGJLevel(@RequestParam int accountID, @RequestParam String gjp, @RequestParam int levelID, @RequestParam String secret) {
            //"/deleteGJLevelUser20.php").equals(request.getRequestURI())) return -1;
            if (!Objects.equals(secret, Core.secrets.get("level"))) return -1;
            if (!Account.map(accountID, true).checkGJP(gjp)) return -1;

            try {
                Object[] vals = {accountID, gjp, levelID, secret};
                Parameter[] pars = RequestManager.Levels.class.getMethod("deleteGJLevel", int.class, String.class, int.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.LEVELS_DELETE);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }

            try {
                int ret = Level.delete(accountID, gjp, levelID, secret, false);
                return ret;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }
    }

    @RestController
    public class Accounts {
        @PostMapping("/{serverURL}/accounts/registerGJAccount.php")
        public int registerGJAccount(@RequestParam String userName, @RequestParam String password, @RequestParam String email, @RequestParam String secret) {
            //"/accounts/registerGJAccount.php").equals(request.getRequestURI())) return -1;
            if (!Objects.equals(secret, Core.secrets.get("account"))) return -1;
            accounts = Account.getAccountsHashtable();
            try {
                Account acc = new Account(userName, password, email);
                try {
                    Object[] vals = {userName, password, email, secret};
                    Parameter[] pars = Accounts.class.getMethod("registerGJAccount", String.class, String.class, String.class, String.class).getParameters();

                    PluginManager.runEndpointMethods(vals, pars, EndpointName.ACCOUNTS_REGISTER);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }

                int ret = acc.register();
                accounts = Account.getAccountsHashtable();
                return ret;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        @PostMapping("/{serverURL}/accounts/loginGJAccount.php")
        public String loginGJAccount(@RequestParam String userName, @RequestParam String password, @RequestParam @Nullable String email, @RequestParam String secret) {
            //"/accounts/loginGJAccount.php").equals(request.getRequestURI())) return "-1";
            if (!Objects.equals(secret, Core.secrets.get("account"))) return "-1";
            accounts = Account.getAccountsHashtable();
            try {
                Account acc = accounts.get(Account.map(userName, true).getUserID());
                try {
                    Object[] vals = {userName, password, email, secret};
                    Parameter[] pars = Accounts.class.getMethod("loginGJAccount", String.class, String.class, String.class, String.class).getParameters();

                    PluginManager.runEndpointMethods(vals, pars, EndpointName.ACCOUNTS_LOGIN);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                var ret = acc.login(userName, password);
                return ret;
            } catch (Exception e) {
                e.printStackTrace();
                return "-1";
            }
        }

        @PostMapping("/{serverURL}/database/accounts/backupGJAccountNew.php")
        public int backupGJAccount(@RequestParam String saveData, @RequestParam String password, @RequestParam String userName, @RequestParam String secret) {
            //"/database/accounts/backupGJAccountNew.php").equals(request.getRequestURI())) return -1;
            if (!Objects.equals(secret, Core.secrets.get("account"))) return -1;
            Account acc = accounts.getOrDefault(Account.map(userName, true).getUserID(), new Account());
            try {
                Object[] vals = {saveData, password, userName, secret};
                Parameter[] pars = Accounts.class.getMethod("backupGJAccount", String.class, String.class, String.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.ACCOUNTS_BACKUP);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            var ret = acc.backup(saveData, password);
            return ret;
        }

        @PostMapping("/{serverURL}/database/accounts/syncGJAccountNew.php")
        public String syncGJAccount(@RequestParam String userName, @RequestParam String secret, @RequestParam String password) {
            //"/database/accounts/syncGJAccountNew.php").equals(request.getRequestURI())) return "-1";
            if (!Objects.equals(secret, Core.secrets.get("account"))) return "-1";
            Account acc = accounts.getOrDefault(Account.map(userName, true).getUserID(), new Account());
            try {
                Object[] vals = {userName, secret, password};
                Parameter[] pars = Accounts.class.getMethod("syncGJAccount", String.class, String.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.ACCOUNTS_SYNC);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            String ret = acc.sync();
            return ret;
        }

        @PostMapping("/{serverURL}/getGJUserInfo20.php")
        public String getGJUserInfo(@RequestParam int targetAccountID, @RequestParam String secret, @RequestParam @Nullable Integer accountID, @RequestParam @Nullable String gjp) {
            //"/getGJUserInfo20.php").equals(request.getRequestURI())) return "-1";
            if (!Objects.equals(secret, Core.secrets.get("common"))) return "-1";
            if (accountID!=null && gjp!=null && !Account.map(accountID, true).checkGJP(gjp)) return "-1";
            try {
                Object[] vals = {targetAccountID, secret, accountID, gjp};
                Parameter[] pars = Accounts.class.getMethod("getGJUserInfo", int.class, String.class, Integer.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.ACCOUNTS_GETINFO);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            String ret = accounts.get(targetAccountID).toString();
            return ret;
        }

        @RequestMapping("/{serverURL}/getAccountURL.php")
        public String getAccUrl() {
            //"/getAccountURL.php").equals(request.getRequestURI())) return "-1";
            //logger.info(serverURL);
            try {
                for (Map.Entry<Integer, Object> entry : PluginManager.getInstance().getSortedPlugins().entrySet()) {
                    Method md = PluginManager.getInstance().getPluginsMethods().get(entry.getValue()).get(EndpointName.ACCOUNTS_GETURL); //хех))
                    if (md==null) continue;
                    if (md.getParameters().length==0) md.invoke(entry.getValue());
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            var ret = serverURL;
            return ret;
        }
        @PostMapping("/{serverURL}/updateGJAccSettings20.php")
        public int updateGJAccSettings(@RequestParam int accountID, @RequestParam @Nullable Integer mS, @RequestParam @Nullable Integer frS, @RequestParam @Nullable Integer cS, @RequestParam @Nullable String yt, @RequestParam @Nullable String twitter, @RequestParam @Nullable String twitch, @RequestParam String secret, @RequestParam String gjp) {
            //"/updateGJAccSettings20.php").equals(request.getRequestURI())) return -1;
            if (!Objects.equals(secret, Core.secrets.get("account"))) return -1;
            Account acc = Account.map(accountID, true);
            try {
                Object[] vals = {accountID, mS, frS, cS, yt, twitter, twitch, secret, gjp};
                Parameter[] pars = Accounts.class.getMethod("updateGJAccSettings", int.class, Integer.class, Integer.class, Integer.class, String.class, String.class, String.class, String.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.ACCOUNTS_SETTINGS_UPDATE);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            var ret = acc.updateAccSettings(mS, frS, cS, yt, twitter, twitch);
            return ret;
        }
    }

    @RestController
    public class Comments {
        @PostMapping("/{serverURL}/uploadGJComment21.php")
        public int uploadGJComment(@RequestParam int accountID, @RequestParam String userName, @RequestParam String comment, @RequestParam int levelID, @RequestParam @Nullable Integer percent, @RequestParam String secret, @RequestParam String gjp) {
            //"/uploadGJComment21.php").equals(request.getRequestURI())) return -1;
            if (!Objects.equals(secret, Core.secrets.get("common"))) return -1;
            if (!Account.map(accountID, true).checkGJP(gjp)) return -1;
            if (percent==null) percent = 0;
            Comment com = new Comment(accountID, userName, comment, levelID, percent);
            try {
                Object[] vals = {accountID, userName, comment, levelID, percent, secret, gjp};
                Parameter[] pars = Comments.class.getMethod("uploadGJComment", int.class, String.class, String.class, int.class, Integer.class, String.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.COMMENTS_UPLOAD);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            int ret = com.upload();
            return ret;
        }

        @PostMapping("/{serverURL}/uploadGJAccComment20.php")
        public int uploadGJAccComment(@RequestParam int accountID, @RequestParam String userName, @RequestParam String comment, @RequestParam String secret, @RequestParam String gjp) {
            //"/uploadGJAccComment20.php").equals(request.getRequestURI())) return -1;
            if (!Objects.equals(secret, Core.secrets.get("common"))) return -1;
            if (!Account.map(accountID, true).checkGJP(gjp)) return -1;
            Comment com = new Comment(accountID, userName, comment);
            try {
                Object[] vals = {accountID, userName, comment, secret, gjp};
                Parameter[] pars = Comments.class.getMethod("uploadGJComment", int.class, String.class, String.class, int.class, Integer.class, String.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.ACCOUNTS_COMMENTS_UPLOAD);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            int ret = com.upload();
            return ret;
        }

        @PostMapping("/{serverURL}/getGJComments21.php")
        public String getGJComments(@RequestParam int levelID, @RequestParam int page, @RequestParam @Nullable Integer mode, @RequestParam String secret) {
            //"/getGJComments21.php").equals(request.getRequestURI())) return "-1";
            if (!Objects.equals(secret, Core.secrets.get("common"))) return "-1";
            if (mode==null) mode = 0;
            try {
                Object[] vals = {levelID, page, mode, secret};
                Parameter[] pars = Comments.class.getMethod("getGJComments", int.class, int.class, Integer.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.COMMENTS_GET);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            String ret = Comment.getComments(levelID, page, false, mode);
            return ret;
        }

        @PostMapping("/{serverURL}/getGJAccountComments20.php")
        public String getGJAccountComments(@RequestParam int accountID, @RequestParam int page, @RequestParam String secret) {
            //"/getGJAccountComments20.php").equals(request.getRequestURI())) return "-1";
            if (!Objects.equals(secret, Core.secrets.get("common"))) return "-1";
            try {
                Object[] vals = {accountID, page, secret};
                Parameter[] pars = Comments.class.getMethod("getGJAccountComments", int.class, int.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.ACCOUNTS_COMMENTS_GET);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            String ret = Comment.getComments(accountID, page, true, 0);
            return ret;
        }

    }

    @RestController
    public class Relationships {
        @PostMapping("/{serverURL}/blockGJUser20.php")
        public int blockGJUser(@RequestParam String secret, @RequestParam int accountID, @RequestParam int targetAccountID, @RequestParam String gjp) {
            //"/blockGJUser20.php").equals(request.getRequestURI())) return -1;
            if (!Objects.equals(secret, Core.secrets.get("common"))) return 1;
            if (!Account.map(accountID, true).checkGJP(gjp)) return 1;
            try {
                Object[] vals = {secret, accountID, targetAccountID, gjp};
                Parameter[] pars = Relationships.class.getMethod("blockGJUser", String.class, int.class, int.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.BLOCK_USER);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            RelationshipsManager.blockUser(accountID, targetAccountID);
            return 1;
        }

        @PostMapping("/{serverURL}/unblockGJUser20.php")
        public int unblockGJUser(@RequestParam String secret, @RequestParam int accountID, @RequestParam int targetAccountID, @RequestParam String gjp) {
            //"/unblockGJUser20.php").equals(request.getRequestURI())) return -1;
            if (!Objects.equals(secret, Core.secrets.get("common"))) return 1;
            if (!Account.map(accountID, true).checkGJP(gjp)) return 1;
            try {
                Object[] vals = {secret, accountID, targetAccountID, gjp};
                Parameter[] pars = Relationships.class.getMethod("unblockGJUser", String.class, int.class, int.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.UNBLOCK_USER);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            RelationshipsManager.unblockUser(accountID, targetAccountID);
            return 1;
        }

        @PostMapping("/{serverURL}/getGJUserList20.php")
        public String getGJUserList(@RequestParam String secret, @RequestParam int accountID, @RequestParam @Nullable Boolean type, @RequestParam String gjp) {
            //"/getGJUserList20.php").equals(request.getRequestURI())) return "-1";
            if (!Objects.equals(secret, Core.secrets.get("common"))) return "-1";
            if (!Account.map(accountID, true).checkGJP(gjp)) return "-1";
            if (type==null) type = false;
            try {
                Object[] vals = {secret, accountID, type, gjp};
                Parameter[] pars = Relationships.class.getMethod("getGJUserList", String.class, int.class, Boolean.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.ACCOUNTS_GETLIST);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            String ret = RelationshipsManager.getUserList(accountID, type);
            return ret;
        }

        @PostMapping("/{serverURL}/readGJFriendRequest20.php")
        public int readFriendRequest(@RequestParam int accountID, @RequestParam int requestID, @RequestParam String secret, @RequestParam String gjp) {
            //"/readGJFriendRequest20.php").equals(request.getRequestURI())) return -1;
            if (!Objects.equals(secret, Core.secrets.get("common"))) return -1;
            if (!Account.map(accountID, true).checkGJP(gjp)) return -1;
            try {
                Object[] vals = {accountID, requestID, secret, gjp};
                Parameter[] pars = Relationships.class.getMethod("readFriendRequest", int.class, int.class, String.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.FRIEND_REQUESTS_READ);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            int ret = RelationshipsManager.readFriendRequest(requestID, accountID);
            return ret;
        }

        @PostMapping("/{serverURL}/removeGJFriend20.php")
        public int removeGJFriend(@RequestParam int accountID, @RequestParam int targetAccountID, @RequestParam String secret, String gjp) {
            //"/removeGJFriend20.php").equals(request.getRequestURI())) return -1;
            if (!Objects.equals(secret, Core.secrets.get("common"))) return -1;
            if (!Account.map(accountID, true).checkGJP(gjp)) return -1;
            try {
                Object[] vals = {secret, accountID, targetAccountID, gjp};
                Parameter[] pars = Relationships.class.getMethod("readFriendRequest", int.class, int.class, String.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.FRIEND_REMOVE);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            int ret = RelationshipsManager.removeFriend(accountID, targetAccountID);
            return ret;
        }

        @PostMapping("/{serverURL}/uploadFriendRequest20.php")
        public String uploadGJFriendRequest(@RequestParam int accountID, @RequestParam int toAccountID, @RequestParam @Nullable String comment, @RequestParam String secret, @RequestParam String gjp) {
            //"/uploadFriendRequest20.php").equals(request.getRequestURI())) return "-1";
            if (!Objects.equals(secret, Core.secrets.get("common"))) return "-1";
            if (!Account.map(accountID, true).checkGJP(gjp)) return "-1";
            if (comment == null) comment = Utils.base64UrlSafeEncode("hi user!");
            try {
                Object[] vals = {accountID, toAccountID, comment, secret, gjp};
                Parameter[] pars = Relationships.class.getMethod("uploadGJFriendRequest", int.class, int.class, String.class, String.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.FRIEND_REQUESTS_UPLOAD);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            int ret = RelationshipsManager.sendFriendRequest(accountID, toAccountID, comment);
            if (ret==-1) return "-1";
            else return String.valueOf(ret);
        }

        @PostMapping("/{serverURL}/acceptGJFriendRequest20.php")
        public int acceptGJFriendRequest(@RequestParam int accountID, @RequestParam int targetAccountID, @RequestParam String secret, @RequestParam String gjp) {
            //"/acceptGJFriendRequest20.php").equals(request.getRequestURI())) return -1;
            if (!Objects.equals(secret, Core.secrets.get("common"))) return -1;
            if (!Account.map(accountID, true).checkGJP(gjp)) return -1;
            try {
                Object[] vals = {accountID, targetAccountID, secret, gjp};
                Parameter[] pars = Relationships.class.getMethod("acceptGJFriendRequest", int.class, int.class, String.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.FRIEND_REQUESTS_ACCEPT);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            int ret = RelationshipsManager.addFriend(accountID, targetAccountID);
            return ret;
        }

        @PostMapping("/{serverURL}/deleteGJFriendRequest20.php")
        public int deleteGJFriendRequest(@RequestParam int accountID, @RequestParam int targetAccountID, @RequestParam String secret, @RequestParam String gjp) {
            //"/deleteGJFriendRequest20.php").equals(request.getRequestURI())) return -1;
            if (!Objects.equals(secret, Core.secrets.get("common"))) return -1;
            if (!Account.map(accountID, true).checkGJP(gjp)) return -1;
            try {
                Object[] vals = {accountID, targetAccountID, secret, gjp};
                Parameter[] pars = Relationships.class.getMethod("deleteGJFriendRequest", int.class, int.class, String.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.FRIEND_REQUESTS_DELETE);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            int ret = RelationshipsManager.deleteFriendRequest(accountID, targetAccountID);
            return ret;
        }

        @PostMapping("/{serverURL}/getGJFriendRequests20.php")
        public String getGJFriendRequests(@RequestParam int accountID, @RequestParam @Nullable Boolean getSent, @RequestParam @Nullable Integer page, @RequestParam String secret, @RequestParam String gjp) {
            //"/getGJFriendRequests20.php").equals(request.getRequestURI())) return "-1";
            if (!Objects.equals(secret, Core.secrets.get("common"))) return "-1";
            if (!Account.map(accountID, true).checkGJP(gjp)) return "-1";
            if (getSent==null) getSent = false;
            if (page==null) page = 0;
            try {
                Object[] vals = {accountID, getSent, page, secret, gjp};
                Parameter[] pars = Relationships.class.getMethod("getGJFriendRequests", int.class, Boolean.class, Integer.class, String.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.FRIEND_REQUESTS_GET);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            String ret = RelationshipsManager.getFriendRequests(accountID, getSent, page);
            return ret;
        }
    }

    @RestController
    public class Scores {
        @PostMapping("/{serverURL}/getGJScores20.php")
        public String getGJScores(@RequestParam String secret, @RequestParam @Nullable Integer accountID, @RequestParam @Nullable String type, @RequestParam @Nullable Integer count, @RequestParam @Nullable String gjp) {
            //"/getGJScores20.php").equals(request.getRequestURI())) return "-1";
            if (!Objects.equals(secret, Core.secrets.get("common"))) return "-1";
            if (accountID!=null && gjp!=null && !Account.map(accountID, true).checkGJP(gjp)) return "-1";
            accountID= accountID==null?1:accountID;
            type= type==null?"top":type;
            count= count==null?0:count;

            try {
                Object[] vals = {secret, accountID, type, count, gjp};
                Parameter[] pars = Scores.class.getMethod("getGJScores", String.class, Integer.class, String.class, Integer.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.SCORES_GET);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }

            try {
                var ret = Score.getScores(accountID, LeaderboardType.getLeaderboardType(type), count);
                return ret;
            } catch (InvalidValueException e) {
                e.printStackTrace();
                return "-1";
            }
        }

        @PostMapping("/{serverURL}/getGJLevelScores211.php")
        public String getGJLevelScores(@RequestParam int accountID, @RequestParam int levelID, @RequestParam String gjp, @RequestParam String secret,
                                              @RequestParam @Nullable Integer percent, @RequestParam @Nullable Integer type, @RequestParam @Nullable Integer s8, @RequestParam @Nullable Integer s9, @RequestParam @Nullable Integer count, @RequestParam @Nullable String s6, @RequestParam @Nullable Integer s1, @RequestParam @Nullable Integer s2) {
            //"/getGJLevelScores211.php").equals(request.getRequestURI())) return "-1";
            if (!Objects.equals(secret, Core.secrets.get("common"))) return "-1";
            if (!Account.map(accountID, true).checkGJP(gjp)) return "-1";
            type = type==null?0:type;
            s8 = s8==null?0:s8;
            s9 = s9==null?0:s9-5819;
            count = count==null?0:count;
            percent = percent==null?0:percent;

            LeaderboardType t = null;
            try {
                t = LeaderboardType.getLeaderboardType(type);
            } catch (InvalidValueException e) {
                e.printStackTrace();
            }
            try {
                Object[] vals = {accountID, levelID, gjp, secret, percent, type, s8, s9, count, s6, s1, s2};
                Parameter[] pars = Scores.class.getMethod("getGJLevelScores", int.class, int.class, String.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, String.class, Integer.class, Integer.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.SCORES_LEVELS_GET);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            var ret = Score.getLevelScores(accountID, t, count, levelID, percent, s8, s9);
            return ret;
        }
    }

    @RestController
    public class Messages {
        @PostMapping("/{serverURL}/downloadGJMessage20.php")
        public String downloadGJMessage(@RequestParam String secret, @RequestParam int accountID, @RequestParam int messageID) {
            //"/downloadGJMessage20.php").equals(request.getRequestURI())) return "";
            if (!experimental) return "";
            if (!Objects.equals(secret, Core.secrets.get("common"))) return "-1";
            try {
                Object[] vals = {secret, accountID, messageID};
                Parameter[] pars = RequestManager.Messages.class.getMethod("downloadGJMessage", String.class, int.class, int.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.MESSAGES_DOWNLOAD);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            String ret = Message.download(messageID, Message.map(messageID).getSenderID()==accountID);
            return ret;
        }

        @PostMapping("/{serverURL}/getGJMessages20.php")
        public String getGJMessages(@RequestParam String secret, @RequestParam int accountID, @RequestParam @Nullable Integer page, @RequestParam @Nullable Boolean getSent) {
            //"/getGJMessages20.php").equals(request.getRequestURI())) return "";
            if (!experimental) return "";
            if (!Objects.equals(secret, Core.secrets.get("common"))) return "-1";
            if (page==null) page = 0;
            if (getSent==null) getSent = false;
            try {
                Object[] vals = {secret, accountID, page, getSent};
                Parameter[] pars = RequestManager.Messages.class.getMethod("getGJMessages", String.class, int.class, Integer.class, Boolean.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.MESSAGES_GET);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            String ret = Message.getMessages(accountID, page, getSent);
            return ret;
        }

        @PostMapping("/{serverURL}/uploadGJMessage20.php")
        public int uploadGJMessage(@RequestParam String secret, @RequestParam int accountID, @RequestParam int toAccountID, @RequestParam String subject, @RequestParam String body) {
            //"/uploadGJMessage20.php").equals(request.getRequestURI())) return -1;
            if (!experimental) return 1;
            if (!Objects.equals(secret, Core.secrets.get("common"))) return 1;
            if (accountID == toAccountID) return -1;
            Message message = new Message(accountID, toAccountID, subject, body, true);
            try {
                Object[] vals = {secret, accountID, toAccountID, subject, body};
                Parameter[] pars = RequestManager.Messages.class.getMethod("uploadGJMessage", String.class, int.class, int.class, String.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.MESSAGES_UPLOAD);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            int ret = message.send();
            return ret;
        }
    }


    @PostMapping("/{serverURL}/getGJSongInfo.php")
    public String getSongInfo(@RequestParam int songID) {
        //"/getGJSongInfo.php").equals(request.getRequestURI())) return "-1";
        try {
            Object[] vals = {songID};
            Parameter[] pars = RequestManager.class.getMethod("getSongInfo", int.class).getParameters();

            PluginManager.runEndpointMethods(vals, pars, EndpointName.SONGS_GET_INFO);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        String ret = Song.getSongInfo(songID);
        return ret;
    }

    @PostMapping("/{serverURL}/songAdd")
    public int songAdd(@RequestParam String name, @RequestParam String artistName, @RequestParam double size, @RequestParam String link) {
        //"/songAdd.php").equals(request.getRequestURI())) return -1;
        Song song = new Song(name, artistName, size, link);
        try {
            Object[] vals = {name, artistName, size, link};
            Parameter[] pars = RequestManager.class.getMethod("songAdd", String.class, String.class, double.class, String.class).getParameters();

            PluginManager.runEndpointMethods(vals, pars, EndpointName.SONGS_UPLOAD);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        var ret = song.songAdd();
        return ret;
    }

    @PostMapping("/{serverURL}/likeGJItem211.php")
    public int like(@RequestParam String secret, @RequestParam int itemID, @RequestParam int type, @RequestParam boolean like, @RequestParam @Nullable Integer accountID, @RequestParam @Nullable String gjp) {
        //"/likeGJItem211.php").equals(request.getRequestURI())) return -1;
        if (!Objects.equals(secret, Core.secrets.get("common"))) return -1;
        if (accountID!=null && gjp!= null && !Account.map(accountID, true).checkGJP(gjp)) return -1;
        ItemType itype = null;
        try {
            itype = ItemType.getLikeType(type);
        } catch (InvalidValueException e) {
            e.printStackTrace();
        }
        try {
            Object[] vals = {secret, itemID, type, like, accountID, gjp};
            Parameter[] pars = RequestManager.class.getMethod("like", String.class, int.class, int.class, boolean.class, Integer.class, String.class).getParameters();

            PluginManager.runEndpointMethods(vals, pars, EndpointName.LIKE);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return Likes.like(itemID, itype, like);
    }

    @PostMapping("/{serverURL}/requestGJUserAccess.php")
    public int requestUserAccess(@RequestParam String secret, @RequestParam int accountID, @RequestParam String gjp) {
        //"/requestGJUserAccess.php").equals(request.getRequestURI())) return -1;
        if (!Objects.equals(secret, Core.secrets.get("common"))) return -1;
        if (!Account.map(accountID, true).checkGJP(gjp)) return -1;
        try {
            Object[] vals = {secret, accountID, gjp};
            Parameter[] pars = RequestManager.class.getMethod("requestUserAccess", String.class, int.class, String.class).getParameters();

            PluginManager.runEndpointMethods(vals, pars, EndpointName.MOD_REQUEST_USER_ACCESS);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        var ret = Account.requestModAccess(accountID, gjp);
        return ret;
    }

    @RequestMapping("/serverURL")
    public String testhi() {
        return serverURL;
    }
}



//COMMENTS: DONE
//RELATIONSHIPS: DONE
//TODO REWARDS мне в падлу, потом займус( прокрастинация убивает проекты )
//SCORES: LEFT:
//updateGJUserScore
//LEVELS: LEFT:
//getGJDailyLevel.php, rateGJDemon.php, rateGJStars.php, reportGJLevel.php, suggestGJStars.php, getGJGauntlets.php, getGJMapPacks.php