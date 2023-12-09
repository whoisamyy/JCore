package ru.whoisamyy.core.endpoints;

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
import ru.whoisamyy.api.gd.misc.Reward;
import ru.whoisamyy.api.gd.objects.*;
import ru.whoisamyy.api.plugins.events.*;
import ru.whoisamyy.api.utils.Utils;
import ru.whoisamyy.api.utils.enums.EndpointName;
import ru.whoisamyy.api.utils.enums.ItemType;
import ru.whoisamyy.api.utils.enums.LeaderboardType;
import ru.whoisamyy.api.utils.enums.Priority;
import ru.whoisamyy.api.utils.exceptions.InvalidValueException;
import ru.whoisamyy.core.Core;
import ru.whoisamyy.core.PluginManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

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
    private static final Logger logger = LogManager.getLogger(RequestManager.class);
    private final static boolean experimental = false;

    @RestController
    public class Levels {
        //через EndpointMethods
        @PostMapping("/{serverURL}/uploadGJLevel21.php")
        public int uploadGJLevel(
                @RequestParam int gameVersion,
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
                @RequestParam String levelString,
                @RequestParam String secret,
                @RequestParam String gjp) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return -1;
            }
            if (!Account.map(accountID, true).checkGJP(gjp)) {
                return -1;
            }

            Level lvl = null;
            if (levelID != null) {
                try {
                    lvl = new Level(gameVersion, accountID, levelID, levelName, levelDesc, levelVersion, levelLength, audioTrack, auto, password, original, twoPlayer, songID, objects, coins, requestedStars, unlisted, ldm, levelString);
                } catch (InvalidValueException e) { 
                    throw new RuntimeException(e);
                    //return -1;
                }
            } else {
                try {
                    lvl = new Level(gameVersion, accountID, levelName, levelDesc, levelVersion, levelLength, audioTrack, auto, password, original, twoPlayer, songID, objects, coins, requestedStars, unlisted, ldm, levelString);
                } catch (InvalidValueException e) { 
                    throw new RuntimeException(e);
                    //return -1;
                }
            }

            Object[] vals = {gameVersion, accountID, levelID, levelName, levelDesc, levelVersion, levelLength, audioTrack, auto, password, original, twoPlayer, songID, objects, coins, requestedStars, unlisted, ldm, levelString, secret, gjp};
            try {
                Parameter[] pars = RequestManager.Levels.class.getMethod("uploadGJLevel", int.class, int.class, Integer.class, String.class, String.class, int.class, int.class, int.class, int.class, int.class, int.class, boolean.class, int.class, int.class, int.class, int.class, boolean.class, boolean.class, String.class, String.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.LEVELS_UPLOAD);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            try {
                new UploadLevelEvent(vals).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            int lvlid = lvl.upload(true);
            levels = Level.getLevelsHashtable(); //gl
            return lvlid;
        }

        @PostMapping("/{serverURL}/updateGJDesc20.php")
        public int updateGJDesc(
                @RequestParam int accountID,
                @RequestParam int levelID,
                @RequestParam String levelDesc,
                @RequestParam String secret,
                @RequestParam String gjp) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return -1;
            }
            if (!Account.map(accountID, true).checkGJP(gjp)) {
                return -1;
            }
            Level lvl = levels.get(levelID);

            Object[] vals = {accountID, levelID, levelDesc, secret, gjp};
            try {
                Parameter[] pars = RequestManager.Levels.class.getMethod("updateGJDesc", int.class, int.class, String.class, String.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.LEVELS_UPDATE_DESCRIPTION);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            try {
                new UpdateLevelDescEvent(vals).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            int ret = lvl.updateDesc(levelDesc);
            return ret;
        }

        @PostMapping("/{serverURL}/getGJLevels21.php")
        public String getGJLevels(
                @RequestParam String secret,
                @RequestParam @Nullable Integer gameVersion,
                @RequestParam @Nullable Integer binaryVersion,
                @RequestParam Integer type,
                @RequestParam @Nullable String str,
                @RequestParam @Nullable Integer page,
                @RequestParam @Nullable Integer total,
                @RequestParam @Nullable String gjp,
                @RequestParam @Nullable Integer accountID,
                @RequestParam @Nullable Integer gdw,
                @RequestParam @Nullable Integer gauntlet,
                @RequestParam @Nullable String diff,
                @RequestParam @Nullable String demonFilter,
                @RequestParam @Nullable String len,
                @RequestParam @Nullable Integer uncompleted,
                @RequestParam @Nullable Integer onlyCompleted,
                @RequestParam @Nullable String completedLevels,
                @RequestParam @Nullable Integer featured,
                @RequestParam @Nullable Integer original,
                @RequestParam @Nullable Integer twoPlayer,
                @RequestParam @Nullable Integer coins,
                @RequestParam @Nullable Integer epic,
                @RequestParam @Nullable Integer noStar,
                @RequestParam @Nullable Integer star,
                @RequestParam @Nullable Integer song,
                @RequestParam @Nullable Integer customSong,
                @RequestParam @Nullable String followed,
                @RequestParam @Nullable Integer local) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return "-1";
            }
            if (accountID != null && gjp != null && !Account.map(accountID, true).checkGJP(gjp)) {
                return "-1";
            }
            customSong = customSong != null ? customSong : 0;
            assert diff != null;
            if (diff.equals("-")) {
                diff = null;
            }
            if (len.equals("-")) {
                len = null;
            }
            Object[] vals = {secret, gameVersion, binaryVersion, type, str, page, total, gjp, accountID, gdw, gauntlet, diff, demonFilter, len, uncompleted, onlyCompleted, completedLevels, featured, original, twoPlayer, coins, epic, noStar, star, song, customSong, followed, local};
            try {
                Parameter[] pars = RequestManager.Levels.class.getMethod("getGJLevels", String.class, Integer.class, Integer.class, Integer.class, String.class, Integer.class, Integer.class, String.class, Integer.class, Integer.class, Integer.class, String.class, String.class, String.class, Integer.class, Integer.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, String.class, Integer.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.LEVELS_GET);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            var retList = Level.getLevels(secret, gameVersion, binaryVersion, type, str, page, total, gjp, accountID, gdw, gauntlet, diff, demonFilter, len, uncompleted, onlyCompleted, completedLevels, featured, original, twoPlayer, coins, epic, noStar, star, song, customSong, followed, local);
            var ret = Level.levelsListToString(retList, page, retList.size(), 10);

            //List<Object> values = new ArrayList<>(Arrays.asList(vals));
            //values.add(retList);

            try {
                new GetLevelsEvent(vals).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            return ret;
        }

        @PostMapping("/{serverURL}/getGJDailyLevel.php")
        public String getGJDailyLevel(@RequestParam @Nullable Boolean weekly) {
            weekly = weekly != null && weekly;
            Object[] vals = {weekly};
            try {
                Parameter[] pars = RequestManager.Levels.class.getMethod("getGJDailyLevel", Boolean.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.LEVELS_GET_DAILY);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            try {
                new GetDailyLevelEvent(vals).callEvent();
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            var ret = Level.getDaily(weekly);
            //logger.info(ret);
            return ret;
        }

        @PostMapping("/{serverURL}/downloadGJLevel22.php")
        public String downloadGJLevel(
                @RequestParam int levelID,
                @RequestParam String secret,
                @RequestParam @Nullable Integer accountID,
                @RequestParam @Nullable String gjp) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return "-1";
            }
            accountID = accountID == null ? 0 : accountID;
            gjp = gjp == null ? "" : gjp;
            if (gjp != "" && accountID != 0 && !Account.map(accountID, true).checkGJP(gjp)) {
                return "-1";
            }

            Object[] vals = {levelID, secret, accountID, gjp};
            try {
                Parameter[] pars = RequestManager.Levels.class.getMethod("downloadGJLevel", int.class, String.class, Integer.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.LEVELS_DOWNLOAD);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            try {

                try {
                    new DownloadLevelEvent(vals).callEvent();
                } catch (NoSuchFieldException e) { 
                    throw new RuntimeException(e);
                }

                String s = Level.download(levelID);
                return s;
            } catch (Exception e) { 
                throw new RuntimeException(e);
                //return "-1";
            }
        }

        @PostMapping("/{serverURL}/deleteGJLevelUser20.php")
        public int deleteGJLevel(
                @RequestParam int accountID,
                @RequestParam String gjp,
                @RequestParam int levelID,
                @RequestParam String secret) {
            if (!Objects.equals(secret, Core.secrets.get("level"))) {
                return -1;
            }
            if (!Account.map(accountID, true).checkGJP(gjp)) {
                return -1;
            }

            Object[] vals = {accountID, gjp, levelID, secret};
            try {
                Parameter[] pars = RequestManager.Levels.class.getMethod("deleteGJLevel", int.class, String.class, int.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.LEVELS_DELETE);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            try {

                try {
                    new DeleteLevelEvent(vals).callEvent();
                } catch (NoSuchFieldException e) { 
                    throw new RuntimeException(e);
                }

                int ret = Level.delete(accountID, gjp, levelID, secret, true);
                return ret;
            } catch (Exception e) { 
                throw new RuntimeException(e);
                //return -1;
            }
        }

        @PostMapping("/{}/rateGJDemon21.php")
        public int rateGJDemon(
                @RequestParam Integer levelID,
                @RequestParam Integer rating,
                @RequestParam Integer accountID,
                @RequestParam String gjp,
                @RequestParam String secret
        ) {
            if (!Objects.equals(secret, Core.secrets.get("mod"))) {
                return -1;
            }
            if (!Account.map(accountID, true).checkGJP(gjp)) {
                return -1;
            }

            Object[] vals = {accountID, gjp, levelID, rating, secret};
            try {
                Parameter[] pars = RequestManager.Levels.class.getMethod("rateGJDemon", Integer.class, Integer.class, Integer.class, String.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.LEVELS_RATE_DEMON);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            try {
                new RateLevelDemonEvent(vals).callEvent();
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }

            Level.rateDemon(rating, levelID);
            return 1;
        }

        @PostMapping("/{serverURL}/rateGJStars211.php")
        public int rateGJStars(
                @RequestParam Integer levelID,
                @RequestParam Integer stars,
                @RequestParam Integer accountID,
                @RequestParam String gjp,
                @RequestParam String secret
        ) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return -1;
            }
            if (!Account.map(accountID, true).checkGJP(gjp)) {
                return -1;
            }


            Object[] vals = {accountID, gjp, levelID, stars, secret};
            try {
                Parameter[] pars = RequestManager.Levels.class.getMethod("rateGJStars", Integer.class, String.class, Integer.class, Integer.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.LEVELS_RATE_STARS);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            try {
                new RateLevelStarsEvent(vals).callEvent();
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }

            Level.rateStars(stars, levelID);
            return 1;
        }

        @PostMapping("/{serverURL}/suggestGJStars20.php")
        public int suggestGJStars(
                int levelID,
                int stars,
                boolean feature,
                int accountID,
                String gjp,
                String secret
        ) {
            if (!Objects.equals(secret, Core.secrets.get("mod"))) {
                return -1;
            }
            if (!Account.map(accountID, true).checkGJP(gjp)) {
                return -1;
            }

            Object[] vals = {accountID, stars, feature, accountID, gjp, secret};
            try {
                Parameter[] pars = RequestManager.Levels.class.getMethod("suggestGJStars", int.class, int.class, boolean.class, int.class, String.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.LEVELS_SUGGEST_STARS);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            try {
                new SuggestLevelStarsEvent(vals).callEvent();
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }

            Level.suggestStars(levelID, stars, feature);
            return 1;
        }
    }

    @RestController
    public class Accounts {
        @PostMapping("/{serverURL}/accounts/registerGJAccount.php")
        public int registerGJAccount(
                @RequestParam String userName,
                @RequestParam String password,
                @RequestParam String email,
                @RequestParam String secret) {
            if (!Objects.equals(secret, Core.secrets.get("account"))) {
                return -1;
            }

            accounts = Account.getAccountsHashtable();
            try {
                Account acc = new Account(userName, password, email);
                Object[] vals = {userName, password, email, secret};
                try {
                    Parameter[] pars = Accounts.class.getMethod("registerGJAccount", String.class, String.class, String.class, String.class).getParameters();
                    PluginManager.runEndpointMethods(vals, pars, EndpointName.ACCOUNTS_REGISTER);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                    throw new RuntimeException(e);
                }

                try {
                    new RegisterAccountEvent(vals).callEvent();
                } catch (NoSuchFieldException e) { 
                    throw new RuntimeException(e);
                }

                int ret = acc.register();
                accounts = Account.getAccountsHashtable();
                return ret;
            } catch (Exception e) { 
                throw new RuntimeException(e);
                //return -1;
            }
        }

        @PostMapping("/{serverURL}/accounts/loginGJAccount.php")
        public String loginGJAccount(
                @RequestParam String userName,
                @RequestParam String password,
                @RequestParam @Nullable String email,
                @RequestParam String secret) {
            if (!Objects.equals(secret, Core.secrets.get("account"))) {
                return "-1";
            }

            accounts = Account.getAccountsHashtable();
            try {
                Account acc = accounts.get(Account.map(userName, true).getUserID());
                Object[] vals = {userName, password, email, secret};
                try {
                    Parameter[] pars = Accounts.class.getMethod("loginGJAccount", String.class, String.class, String.class, String.class).getParameters();
                    PluginManager.runEndpointMethods(vals, pars, EndpointName.ACCOUNTS_LOGIN);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                    throw new RuntimeException(e);
                }

                try {
                    new LoginAccountEvent(vals).callEvent();
                } catch (NoSuchFieldException e) { 
                    throw new RuntimeException(e);
                }

                var ret = acc.login(userName, password);
                return ret;
            } catch (Exception e) { 
                throw new RuntimeException(e);
                //return "-1";
            }
        }

        @PostMapping("{serverURL}/database/accounts/backupGJAccountNew.php")
        public int backupGJAccountNew(
                @RequestParam String saveData,
                @RequestParam String password,
                @RequestParam String userName,
                @RequestParam String secret) {
            if (!Objects.equals(secret, Core.secrets.get("account"))) {
                return -1;
            }
            Account acc = accounts.getOrDefault(Account.map(userName, true).getUserID(), new Account());
            Object[] vals = {saveData, password, userName, secret};
            try {
                Parameter[] pars = Accounts.class.getMethod("backupGJAccountNew", String.class, String.class, String.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.ACCOUNTS_BACKUP);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            try {
                new BackupAccountEvent(vals).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }
            var ret = acc.backup(saveData, password);
            return ret;
        }

        @PostMapping("{serverURL}/database/accounts/syncGJAccountNew.php")
        public String syncGJAccountNew(
                @RequestParam String userName,
                @RequestParam String secret,
                @RequestParam String password) {
            if (!Objects.equals(secret, Core.secrets.get("account"))) {
                return "-1";
            }
            Account acc = accounts.getOrDefault(Account.map(userName, true).getUserID(), new Account());
            Object[] vals = {userName, secret, password};
            try {
                Parameter[] pars = Accounts.class.getMethod("syncGJAccountNew", String.class, String.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.ACCOUNTS_SYNC);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            try {
                new SyncAccountEvent(vals).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            String ret = acc.sync();
            //logger.info(ret);
            return ret;
        }

        @PostMapping("/{serverURL}/getGJUserInfo20.php")
        public String getGJUserInfo(
                @RequestParam int targetAccountID,
                @RequestParam String secret,
                @RequestParam @Nullable Integer accountID,
                @RequestParam @Nullable String gjp) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return "-1";
            }
            if (accountID != null && gjp != null && !Account.map(accountID, true).checkGJP(gjp)) {
                return "-1";
            }
            if (accountID == null) {
                accountID = -1;
            }
            Object[] vals = {targetAccountID, secret, accountID, gjp};
            try {
                Parameter[] pars = Accounts.class.getMethod("getGJUserInfo", int.class, String.class, Integer.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.ACCOUNTS_GETINFO);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            try {
                new GetUserInfoEvent(vals).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            String ret = Account.map(targetAccountID, true, true).toString();
            return ret;
        }

        @PostMapping("/{serverURL}/getAccountURL.php")
        public String getAccUrl() {
            try {
                for (Map.Entry<Priority, Object> entry : PluginManager.getInstance().getSortedPlugins().entrySet()) {
                    Method md = PluginManager.getInstance().getPluginsMethods().get(entry.getValue()).get(EndpointName.ACCOUNTS_GETURL);
                    if (md == null) {
                        continue;
                    }
                    if (md.getParameters().length == 0) {
                        md.invoke(entry.getValue());
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException e) { 
                throw new RuntimeException(e);
            }

            try {
                new GetAccountURLEvent().callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            var ret = serverURL;
            //logger.info(serverURL);
            return ret;
        }

        @PostMapping("/{serverURL}/updateGJAccSettings20.php")
        public int updateGJAccSettings(
                @RequestParam Integer accountID,
                @RequestParam @Nullable Integer mS,
                @RequestParam @Nullable Integer frS,
                @RequestParam @Nullable Integer cS,
                @RequestParam @Nullable String yt,
                @RequestParam @Nullable String twitter,
                @RequestParam @Nullable String twitch,
                @RequestParam String secret,
                @RequestParam String gjp) {
            if (!Objects.equals(secret, Core.secrets.get("account"))) {
                return -1;
            }

            if (accountID != null && gjp != null && !Account.map(accountID, true).checkGJP(gjp)) {
                return -1;
            }
            Account acc = Account.map(accountID, true);
            Object[] vals = {accountID, mS, frS, cS, yt, twitter, twitch, secret, gjp};
            try {
                Parameter[] pars = Accounts.class.getMethod("updateGJAccSettings", Integer.class, Integer.class, Integer.class, Integer.class, String.class, String.class, String.class, String.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.ACCOUNTS_SETTINGS_UPDATE);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            try {
                new UpdateAccountSettingsEvent(vals).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            var ret = acc.updateAccSettings(mS, frS, cS, yt, twitter, twitch);
            return ret;
        }

        @PostMapping("/{serverURL}/updateGJUserScore22.php")
        public int updateGJUserScore(
                @RequestParam Integer accountID,
                @RequestParam String gjp,
                @RequestParam String userName,
                @RequestParam int stars,
                @RequestParam int demons,
                @RequestParam int diamonds,
                @RequestParam int icon,
                @RequestParam int iconType,
                @RequestParam int coins,
                @RequestParam int userCoins,
                @RequestParam int accIcon,
                @RequestParam int accShip,
                @RequestParam int accBall,
                @RequestParam int accBird,
                @RequestParam int accDart,
                @RequestParam int accRobot,
                @RequestParam int accGlow,
                @RequestParam int accSpider,
                @RequestParam int accExplosion,
                @RequestParam int special,
                @RequestParam int color1,
                @RequestParam int color2,
                @RequestParam String secret
        ) {

            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return -1;
            }

            if (accountID != null && gjp != null && !Account.map(accountID, true).checkGJP(gjp)) {
                return -1;
            }

            Object[] vals = {accountID, gjp, userName, stars, demons, diamonds, icon, iconType, coins, userCoins, accIcon, accShip, accBall, accBird, accDart, accRobot, accGlow, accSpider, accExplosion, special, color1, color2, secret};
            try {
                Parameter[] pars = Accounts.class.getMethod("updateGJUserScore", Integer.class, String.class, String.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.USER_SCORE_UPDATE);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            try {
                new UpdateUserScoreEvent(vals).callEvent();
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }

            int ret = accounts.get(accountID).updateUserScore(userName, 21, coins, secret, stars, demons, icon, color1, color2, iconType, userCoins, special, accIcon, accShip, accBall, accBird, accDart, accRobot, accGlow, 0, accSpider, accExplosion, diamonds);
            accounts = Account.getAccountsHashtable();
            return ret;
        }
    }

    @RestController
    public class Comments {
        @PostMapping("/{serverURL}/uploadGJComment21.php")
        public int uploadGJComment(
                @RequestParam int accountID,
                @RequestParam String userName,
                @RequestParam String comment,
                @RequestParam int levelID,
                @RequestParam @Nullable Integer percent,
                @RequestParam String secret,
                @RequestParam String gjp) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return -1;
            }

            if (!Account.map(accountID, true).checkGJP(gjp)) {
                return -1;
            }

            if (percent == null) {
                percent = 0;
            }

            Comment com = new Comment(accountID, userName, comment, levelID, percent);
            Object[] vals = {accountID, userName, comment, levelID, percent, secret, gjp};
            try {
                Parameter[] pars = Comments.class.getMethod("uploadGJComment", int.class, String.class, String.class, int.class, Integer.class, String.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.COMMENTS_UPLOAD);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            try {
                new UploadCommentEvent(vals).callEvent();
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }

            int ret = com.upload();
            return ret;
        }

        @PostMapping("/{serverURL}/uploadGJAccComment20.php")
        public int uploadGJAccComment(
                @RequestParam int accountID,
                @RequestParam String userName,
                @RequestParam String comment,
                @RequestParam String secret,
                @RequestParam String gjp) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return -1;
            }

            if (!Account.map(accountID, true).checkGJP(gjp)) {
                return -1;
            }

            Comment com = new Comment(accountID, userName, comment);
            Object[] vals = {accountID, userName, comment, secret, gjp};
            try {
                Parameter[] pars = Comments.class.getMethod("uploadGJAccComment", int.class, String.class, String.class, String.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.ACCOUNTS_COMMENTS_UPLOAD);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            try {
                new UploadAccountCommentEvent(vals).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            int ret = com.upload();
            return ret;
        }

        @PostMapping("/{serverURL}/getGJComments21.php")
        public String getGJComments(
                @RequestParam int levelID,
                @RequestParam int page,
                @RequestParam @Nullable Integer mode,
                @RequestParam String secret) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return "-1";
            }

            if (mode == null) {
                mode = 0;
            }

            Object[] vals = {levelID, page, mode, secret};
            try {
                Parameter[] pars = Comments.class.getMethod("getGJComments", int.class, int.class, Integer.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.COMMENTS_GET);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }
            var r = Comment.getComments(levelID, page, false, mode);

            //List<Object> values = new ArrayList<>(Arrays.asList(vals));
            //values.add(r.values().toArray()[0]);

            try {
                new GetCommentsEvent(vals).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            String ret = (String) r.keySet().toArray()[0];
            return ret;
        }

        @PostMapping("/{serverURL}/getGJAccountComments20.php")
        public String getGJAccountComments(
                @RequestParam int accountID,
                @RequestParam int page,
                @RequestParam String secret) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return "-1";
            }

            Object[] vals = {accountID, page, secret};
            try {
                Parameter[] pars = Comments.class.getMethod("getGJAccountComments", int.class, int.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.ACCOUNTS_COMMENTS_GET);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }
            var r = Comment.getComments(accountID, page, true, 0);

            List<Object> values = new ArrayList<>(Arrays.asList(vals));
            values.add(r.values().toArray()[0]);

            try {
                new GetAccountCommentsEvent(values).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            String ret = (String) r.keySet().toArray()[0];
            return ret;
        }

        @PostMapping("/{serverURL}/deleteGJComment20.php")
        public int deleteGJComment(
                @RequestParam String secret,
                @RequestParam int accountID,
                @RequestParam String gjp,
                @RequestParam int commentID,
                @RequestParam int levelID
        ) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return -1;
            }

            if (!Account.map(accountID, true).checkGJP(gjp)) {
                return -1;
            }

            Object[] vals = {secret, accountID, gjp, commentID, levelID};
            try {
                Parameter[] pars = Comments.class.getMethod("deleteGJComment", String.class, int.class, String.class, int.class, int.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.COMMENT_DELETE);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            try {
                new DeleteCommentEvent(vals).callEvent();
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }

            var ret = Comment.map(commentID).delete();
            return ret;
        }

        @PostMapping("/{serverURL}/deleteGJAccComment20.php")
        public int deleteGJAccComment(
                @RequestParam String secret,
                @RequestParam int accountID,
                @RequestParam String gjp,
                @RequestParam int commentID
        ) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return -1;
            }

            if (!Account.map(accountID, true).checkGJP(gjp)) {
                return -1;
            }

            Object[] vals = {secret, accountID, gjp, commentID};
            try {
                Parameter[] pars = Comments.class.getMethod("deleteGJAccComment", String.class, int.class, String.class, int.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.COMMENT_DELETE);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            try {
                new DeleteAccCommentEvent(vals).callEvent();
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }

            var ret = Comment.map(commentID).delete();
            return ret;
        }
    }

    @RestController
    public class Relationships {
        @PostMapping("/{serverURL}/blockGJUser20.php")
        public int blockGJUser(
                @RequestParam String secret,
                @RequestParam int accountID,
                @RequestParam int targetAccountID,
                @RequestParam String gjp) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return 1;
            }
            if (!Account.map(accountID, true).checkGJP(gjp)) {
                return 1;
            }
            Object[] vals = {secret, accountID, targetAccountID, gjp};
            try {
                Parameter[] pars = Relationships.class.getMethod("blockGJUser", String.class, int.class, int.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.BLOCK_USER);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            try {
                new BlockUserEvent(vals).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            var ret = RelationshipsManager.blockUser(accountID, targetAccountID);
            return ret;
        }

        @PostMapping("/{serverURL}/unblockGJUser20.php")
        public int unblockGJUser(
                @RequestParam String secret,
                @RequestParam int accountID,
                @RequestParam int targetAccountID,
                @RequestParam String gjp) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return 1;
            }
            if (!Account.map(accountID, true).checkGJP(gjp)) {
                return 1;
            }
            Object[] vals = {secret, accountID, targetAccountID, gjp};
            try {
                Parameter[] pars = Relationships.class.getMethod("unblockGJUser", String.class, int.class, int.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.UNBLOCK_USER);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            try {
                new UnblockUserEvent(vals).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            var ret = RelationshipsManager.unblockUser(accountID, targetAccountID);
            return ret;
        }

        @PostMapping("/{serverURL}/getGJUserList20.php")
        public String getGJUserList(
                @RequestParam String secret,
                @RequestParam int accountID,
                @RequestParam @Nullable Boolean type,
                @RequestParam String gjp) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return "-1";
            }
            if (!Account.map(accountID, true).checkGJP(gjp)) {
                return "-1";
            }
            if (type == null) {
                type = false;
            }
            Object[] vals = {secret, accountID, type, gjp};
            try {
                Parameter[] pars = Relationships.class.getMethod("getGJUserList", String.class, int.class, Boolean.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.ACCOUNTS_GETLIST);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }
            var r = RelationshipsManager.getUserList(accountID, type);

            List<Account> ret = (List<Account>) r.values().toArray()[0];
            List<Object> values = new ArrayList<>(Arrays.asList(vals));
            values.add(ret);

            try {
                new GetUserListEvent(values).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            return (String) r.keySet().toArray()[0];
        }

        @PostMapping("/{serverURL}/readGJFriendRequest20.php")
        public int readFriendRequest(
                @RequestParam int accountID,
                @RequestParam int requestID,
                @RequestParam String secret,
                @RequestParam String gjp) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return -1;
            }

            if (!Account.map(accountID, true).checkGJP(gjp)) {
                return -1;
            }

            Object[] vals = {accountID, requestID, secret, gjp};
            try {
                Parameter[] pars = Relationships.class.getMethod("readFriendRequest", int.class, int.class, String.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.FRIEND_REQUESTS_READ);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            try {
                new ReadFriendRequestEvent(vals).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            int ret = RelationshipsManager.readFriendRequest(requestID, accountID);
            return ret;
        }

        @PostMapping("/{serverURL}/removeGJFriend20.php")
        public int removeGJFriend(
                @RequestParam int accountID,
                @RequestParam int targetAccountID,
                @RequestParam String secret,
                @RequestParam String gjp) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return -1;
            }

            if (!Account.map(accountID, true).checkGJP(gjp)) {
                return -1;
            }

            Object[] vals = {secret, accountID, targetAccountID, gjp};
            try {
                Parameter[] pars = Relationships.class.getMethod("removeGJFriend", int.class, int.class, String.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.FRIEND_REMOVE);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            try {
                new RemoveFriendEvent(vals).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            int ret = RelationshipsManager.removeFriend(accountID, targetAccountID);
            return ret;
        }

        @PostMapping("/{serverURL}/uploadFriendRequest20.php")
        public String uploadGJFriendRequest(
                @RequestParam int accountID,
                @RequestParam int toAccountID,
                @RequestParam @Nullable String comment,
                @RequestParam String secret,
                @RequestParam String gjp) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return "-1";
            }

            if (!Account.map(accountID, true).checkGJP(gjp)) {
                return "-1";
            }

            if (comment == null) {
                comment = Utils.base64UrlSafeEncode("hi user!");
            }

            Object[] vals = {accountID, toAccountID, comment, secret, gjp};
            try {
                Parameter[] pars = Relationships.class.getMethod("uploadGJFriendRequest", int.class, int.class, String.class, String.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.FRIEND_REQUESTS_UPLOAD);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            try {
                new UploadFriendRequestEvent(vals).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            int ret = RelationshipsManager.sendFriendRequest(accountID, toAccountID, comment);
            String r = null;
            if (ret == -1) {
                r =  "-1";
            } else {
                r = String.valueOf(ret);
            }
            return r;
        }

        @PostMapping("/{serverURL}/acceptGJFriendRequest20.php")
        public int acceptGJFriendRequest(
                @RequestParam int accountID,
                @RequestParam int targetAccountID,
                @RequestParam String secret,
                @RequestParam String gjp) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return -1;
            }

            if (!Account.map(accountID, true).checkGJP(gjp)) {
                return -1;
            }

            Object[] vals = {accountID, targetAccountID, secret, gjp};
            try {
                Parameter[] pars = Relationships.class.getMethod("acceptGJFriendRequest", int.class, int.class, String.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.FRIEND_REQUESTS_ACCEPT);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            try {
                new AcceptFriendRequestEvent(vals).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            int ret = RelationshipsManager.addFriend(accountID, targetAccountID);
            return ret;
        }

        @PostMapping("/{serverURL}/deleteGJFriendRequest20.php")
        public int deleteGJFriendRequest(
                @RequestParam int accountID,
                @RequestParam int targetAccountID,
                @RequestParam String secret,
                @RequestParam String gjp) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return -1;
            }

            if (!Account.map(accountID, true).checkGJP(gjp)) {
                return -1;
            }

            Object[] vals = {accountID, targetAccountID, secret, gjp};
            try {
                Parameter[] pars = Relationships.class.getMethod("deleteGJFriendRequest", int.class, int.class, String.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.FRIEND_REQUESTS_DELETE);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            try {
                new DeleteFriendRequestEvent(vals).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            int ret = RelationshipsManager.deleteFriendRequest(accountID, targetAccountID);
            return ret;
        }

        @PostMapping("/{serverURL}/getGJFriendRequests20.php")
        public String getGJFriendRequests(
                @RequestParam int accountID,
                @RequestParam @Nullable Boolean getSent,
                @RequestParam @Nullable Integer page,
                @RequestParam String secret,
                @RequestParam String gjp) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return "-1";
            }

            if (!Account.map(accountID, true).checkGJP(gjp)) {
                return "-1";
            }

            if (getSent == null) {
                getSent = false;
            }

            if (page == null) {
                page = 0;
            }

            Object[] vals = {accountID, getSent, page, secret, gjp};
            try {
                Parameter[] pars = Relationships.class.getMethod("getGJFriendRequests", int.class, Boolean.class, Integer.class, String.class, String.class).getParameters();
                PluginManager.runEndpointMethods(vals, pars, EndpointName.FRIEND_REQUESTS_GET);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            try {
                new GetFriendRequestsEvent(vals).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            String ret = RelationshipsManager.getFriendRequests(accountID, getSent, page);
            return ret;
        }
    }

    @RestController
    public class Scores {
        private static final int ERROR_CODE_GENERIC = -1;

        @PostMapping("/{serverURL}/getGJScores20.php")
        public String getGJScores(@RequestParam String secret, @RequestParam(required = false) Integer accountID,
                                  @RequestParam(required = false) String type, @RequestParam(required = false) Integer count,
                                  @RequestParam(required = false) String gjp) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return String.valueOf(ERROR_CODE_GENERIC);
            }

            if (accountID != null && gjp != null && !Account.map(accountID, true).checkGJP(gjp)) {
                return String.valueOf(ERROR_CODE_GENERIC);
            }

            accountID = accountID==null?1:accountID;
            type = type==null?"top":type;
            count = count==null?0:count;

            Object[] vals = {secret, accountID, type, count, gjp};
            try {
                Parameter[] pars = Scores.class.getMethod("getGJScores", String.class, Integer.class, String.class, Integer.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.SCORES_GET);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            try {

                try {
                    new GetScoresEvent(vals).callEvent();
                } catch (NoSuchFieldException e) { 
                    throw new RuntimeException(e);
                }

                String ret = Score.getScores(accountID, LeaderboardType.getLeaderboardType(type), count);
                return ret;
            } catch (InvalidValueException e) { 
                throw new RuntimeException(e);
                //return String.valueOf(ERROR_CODE_GENERIC);
            }
        }

        @PostMapping("/{serverURL}/getGJLevelScores211.php")
        public String getGjLevelScores(@RequestParam int accountID, @RequestParam int levelID, @RequestParam String gjp,
                                       @RequestParam String secret, @RequestParam(required = false) Integer percent,
                                       @RequestParam(required = false) Integer type, @RequestParam(required = false) Integer s8,
                                       @RequestParam(required = false) Integer s9, @RequestParam(required = false) Integer count,
                                       @RequestParam(required = false) String s6, @RequestParam(required = false) Integer s1,
                                       @RequestParam(required = false) Integer s2) {
            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return String.valueOf(ERROR_CODE_GENERIC);
            }

            if (!Account.map(accountID, true).checkGJP(gjp)) {
                return String.valueOf(ERROR_CODE_GENERIC);
            }

            type = type==null? 0: type;
            s8 = s8==null? 0: s8;
            s9 = s9==null? 0 : s9-5819;
            count = count==null? 25: count;
            percent = percent==null? 0: percent;

            LeaderboardType leaderboardType = null;
            try {
                leaderboardType = LeaderboardType.getLeaderboardType(type);
            } catch (InvalidValueException e) { 
                throw new RuntimeException(e);
            }

            Object[] vals = {accountID, levelID, gjp, secret, percent, type, s8, s9, count, s6, s1, s2};
            try {
                Parameter[] pars = Scores.class.getMethod("getGjLevelScores", int.class, int.class, String.class, String.class,
                        Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, String.class, Integer.class, Integer.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.SCORES_LEVELS_GET);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            try {
                new GetLevelScoresEvent(vals).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            String ret = Score.getLevelScores(accountID, leaderboardType, count, levelID, percent, s8, s9);
            return ret;
        }
    }

    @RestController
    public class Messages {
        private static final int ERROR_CODE_GENERIC = -1;
        private static final int ERROR_CODE_INVALID_INPUT = 1;

        @PostMapping("/{serverURL}/downloadGJMessage20.php")
        public String downloadGJMessage(@RequestParam String secret, @RequestParam int accountID, @RequestParam int messageID) {
            if (!experimental) {
                return "";
            }

            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return String.valueOf(ERROR_CODE_GENERIC);
            }

            Object[] vals = {secret, accountID, messageID};
            try {
                Parameter[] pars = RequestManager.Messages.class.getMethod("downloadGJMessage", String.class, int.class, int.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.MESSAGES_DOWNLOAD);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            try {
                new DownloadMessageEvent(vals).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            return Message.download(messageID, Message.map(messageID).getSenderID() == accountID);
        }

        @PostMapping("/{serverURL}/getGJMessages20.php")
        public String getGJMessages(@RequestParam String secret, @RequestParam int accountID,
                                    @RequestParam(required = false) Integer page,
                                    @RequestParam(required = false) Boolean getSent) {
            if (!experimental) {
                return "";
            }

            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return String.valueOf(ERROR_CODE_GENERIC);
            }

            page = page==null?0:page;
            getSent = getSent != null && getSent;

            Object[] vals = {secret, accountID, page, getSent};
            try {
                Parameter[] pars = RequestManager.Messages.class.getMethod("getGJMessages", String.class, int.class, Integer.class, Boolean.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.MESSAGES_GET);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            try {
                new GetMessagesEvent(vals).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            return Message.getMessages(accountID, page, getSent);
        }

        @PostMapping("/{serverURL}/uploadGJMessage20.php")
        public int uploadGJMessage(@RequestParam String secret, @RequestParam int accountID,
                                   @RequestParam int toAccountID, @RequestParam String subject,
                                   @RequestParam String body) {
            if (!experimental) {
                return ERROR_CODE_INVALID_INPUT;
            }

            if (!Objects.equals(secret, Core.secrets.get("common"))) {
                return ERROR_CODE_INVALID_INPUT;
            }

            if (accountID == toAccountID) {
                return ERROR_CODE_GENERIC;
            }

            Message message = new Message(accountID, toAccountID, subject, body, true);
            Object[] vals = {secret, accountID, toAccountID, subject, body};
            try {
                Parameter[] pars = RequestManager.Messages.class.getMethod("uploadGJMessage", String.class, int.class, int.class, String.class, String.class).getParameters();

                PluginManager.runEndpointMethods(vals, pars, EndpointName.MESSAGES_UPLOAD);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
                throw new RuntimeException(e);
            }

            try {
                new UploadMessageEvent(vals).callEvent();
            } catch (NoSuchFieldException e) { 
                throw new RuntimeException(e);
            }

            return message.send();
        }
    }


    @PostMapping("/{serverURL}/getGJSongInfo.php")
    public String getSongInfo(@RequestParam int songID) {
        var vals = new Object[]{songID};
        try {
            PluginManager.runEndpointMethods(vals,
                    RequestManager.class.getMethod("getSongInfo", int.class).getParameters(),
                    EndpointName.SONGS_GET_INFO);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) { 
            throw new RuntimeException(e);
        }

        try {
            new GetSongInfoEvent(vals).callEvent();
        } catch (NoSuchFieldException e) { 
            throw new RuntimeException(e);
        }

        return Song.getSongInfo(songID);
    }

    @PostMapping("/{serverURL}/songAdd")
    public int songAdd(@RequestParam String name, @RequestParam String artistName, @RequestParam double size, @RequestParam String link) {
        Song song = new Song(name, artistName, size, link);

        var vals = new Object[]{name, artistName, size, link};
        try {
            PluginManager.runEndpointMethods(vals,
                    RequestManager.class.getMethod("songAdd", String.class, String.class, double.class, String.class).getParameters(),
                    EndpointName.SONGS_UPLOAD);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) { 
            throw new RuntimeException(e);
        }

        try {
            new AddSongEvent(vals).callEvent();
        } catch (NoSuchFieldException e) { 
            throw new RuntimeException(e);
        }

        return song.songAdd();
    }

    @PostMapping("/{serverURL}/likeGJItem211.php")
    public int like(@RequestParam String secret, @RequestParam int itemID, @RequestParam int type, @RequestParam boolean like,
                          @RequestParam @Nullable Integer accountID, @RequestParam @Nullable String gjp) {
        if (!Objects.equals(secret, Core.secrets.get("common"))) {
            return -1;
        }

        if (accountID != null && gjp != null && !Account.map(accountID, true).checkGJP(gjp)) {
            return -1;
        }

        var vals = new Object[]{secret, itemID, type, like, accountID, gjp};
        ItemType itemType = null;
        try {
            itemType = ItemType.getLikeType(type);
        } catch (InvalidValueException e) { 
            throw new RuntimeException(e);
        }
        try {
            PluginManager.runEndpointMethods(vals,
                    RequestManager.class.getMethod("like", String.class, int.class, int.class, boolean.class, Integer.class, String.class).getParameters(),
                    EndpointName.LIKE);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) { 
            throw new RuntimeException(e);
        }

        try {
            new LikeEvent(vals).callEvent();
        } catch (NoSuchFieldException e) { 
            throw new RuntimeException(e);
        }

        return Likes.like(itemID, itemType, like);
    }

    @PostMapping("/{serverURL}/requestUserAccess.php")
    public int requestUserAccess(@RequestParam String secret, @RequestParam int accountID, @RequestParam String gjp) {
        if (!Objects.equals(secret, Core.secrets.get("common"))) {
            return -1;
        }

        if (!Account.map(accountID, true).checkGJP(gjp)) {
            return -1;
        }

        Object[] values = {secret, accountID, gjp};

        try {
            Parameter[] parameters = RequestManager.class
                    .getMethod("requestUserAccess", String.class, int.class, String.class)
                    .getParameters();

            PluginManager.runEndpointMethods(values, parameters, EndpointName.MOD_REQUEST_USER_ACCESS);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { 
            throw new RuntimeException(e);
        }

        try {
            new RequestUserAccessEvent(values).callEvent();
        } catch (NoSuchFieldException e) { 
            throw new RuntimeException(e);
        }

        int result = Account.requestModAccess(accountID, gjp);
        return result;
    }

    @PostMapping("/{serverURL}/getGJGauntlets21.php")
    public String getGJGauntlets(
            @RequestParam String secret
    ) {
        if (!Objects.equals(secret, Core.secrets.get("common"))) {
            return "-1";
        }

        Object[] vals = new Object[]{secret};
        try {
            Parameter[] parameters = RequestManager.class
                    .getMethod("getGJGauntlets", String.class)
                    .getParameters();

            PluginManager.runEndpointMethods(vals, parameters, EndpointName.GAUNTLETS_GET);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        try {
            new GetGauntletsEvent(vals).callEvent();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        var ret = Gauntlet.getGauntlets();
        return ret;
    }

    @PostMapping("/{serverURL}/getGJChallenges.php")
    public String getGJChallenges(
            @RequestParam Integer accountID,
            @RequestParam String secret,
            @RequestParam String chk,
            @RequestParam String udid,
            @RequestParam String gjp
    ) {
        if (!Objects.equals(secret, Core.secrets.get("common"))) {
            return "-1";
        }

        if (!Account.map(accountID, true).checkGJP(gjp)) {
            return "-1";
        }

        Object[] vals = new Object[]{accountID, secret, chk, udid, gjp};
        try {
            Parameter[] parameters = RequestManager.class
                    .getMethod("getGJChallenges", Integer.class, String.class, String.class, String.class, String.class)
                    .getParameters();

            PluginManager.runEndpointMethods(vals, parameters, EndpointName.CHALLENGES_GET);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        try {
            new GetChallengesEvent(vals).callEvent();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        String s = Challenge.getInstance().toString(accountID, chk, udid);
        return "lEpSa"+s; // хз почему не работает
    }

    @PostMapping("/{serverURL}/getGJRewards.php")
    public String getGJRewards(
            @RequestParam Integer accountID,
            @RequestParam String secret,
            @RequestParam String chk,
            @RequestParam String udid,
            @RequestParam String gjp,
            @RequestParam @Nullable Integer rewardType
    ) {
        if (!Objects.equals(secret, Core.secrets.get("common"))) {
            return "-1";
        }

        if (!Account.map(accountID, true).checkGJP(gjp)) {
            return "-1";
        }

        if (rewardType==null) rewardType = 0;

        Object[] vals = new Object[]{accountID, secret, chk, udid, gjp, rewardType};
        try {
            Parameter[] parameters = RequestManager.class
                    .getMethod("getGJRewards", Integer.class, String.class, String.class, String.class, String.class, Integer.class)
                    .getParameters();

            PluginManager.runEndpointMethods(vals, parameters, EndpointName.REWARDS_GET);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        try {
            new GetRewardsEvent(vals).callEvent();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        String s = Chest.getInstance().toString(accountID, rewardType, chk, udid);
        //logger.info(s);
        return "lEpSa" + s; // хз почему не работает
    }

    @RequestMapping("/serverURL")
    public String testhi() {
        return serverURL;
    }
}

////LEVELS: LEFT:
//getGJMapPacks.php