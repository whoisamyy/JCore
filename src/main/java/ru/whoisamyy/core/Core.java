package ru.whoisamyy.core;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.yaml.snakeyaml.Yaml;
import ru.whoisamyy.api.console.ConsoleManager;
import ru.whoisamyy.api.console.commands.EchoConsoleCommand;
import ru.whoisamyy.api.gd.commands.RateCommand;
import ru.whoisamyy.api.gd.commands.UnrateCommand;
import ru.whoisamyy.api.gd.objects.*;
import ru.whoisamyy.api.plugins.annotations.CommandHandler;
import ru.whoisamyy.api.plugins.commands.AbstractCommentCommand;
import ru.whoisamyy.api.plugins.commands.CommandManager;
import ru.whoisamyy.api.utils.Utils;
import ru.whoisamyy.core.endpoints.RequestManager;

import java.io.*;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.Hashtable;
import java.util.Map;

@SpringBootApplication
@Log4j2
public class Core {
	private static String url = "jdbc:mysql://localhost:3306/gdps_new";
	private static String username = "root";
	private static String password = "";
	public static String SALT = "$2a$13$dBoO9eZ3R4VA3HRV5bPFMeuBMhblPt.4dPXZczJU/0G2Ut04H8UGO";
	public static String serverURL = getServerURL();
	public static Connection conn;
	public static Hashtable<String, String> secrets = new Hashtable<>();

	public static Logger logger = LogManager.getLogger(Core.class);

	public static void main(String[] args) {
		Utils.logger = logger;
        getSettings();
		SpringApplication.run(Core.class, args);
		PluginManager.getInstance().initializePlugins();
		registerCommand("!", new RateCommand());
		registerCommand("!", new UnrateCommand());
		ConsoleManager.getInstance().registerCommand(EchoConsoleCommand.class);

		logger.info("Resources folder located at "+ Utils.resources.toString());
		long curtime;
		try {
			logger.info("Initializing database...");
			curtime = System.currentTimeMillis();
			conn = DriverManager.getConnection(url, username, password);
			//createDatabase();

			GDObject.setConn(conn);

			//Account.setConn(conn);
			//Song.setConn(conn);
			//Level.setConn(conn);
			//Score.conn = conn;
			//Message.conn = conn;
			//Comment.conn = conn;
			//RelationshipsManager.conn = conn;

			Statement s = conn.createStatement();
			ResultSet rs;
			rs = s.executeQuery("SELECT MAX(levelID) FROM levels");
			Level.lastLevelID = rs.next()?rs.getInt(1):1;
			logger.info("Level last id: "+Level.lastLevelID);

			rs = s.executeQuery("SELECT MAX(dailyNumber) FROM levels WHERE dailyNumber != 0");
			Level.currentDailyNumber = rs.next()?rs.getInt(1):0;
			logger.info("Last daily number: "+Level.getCurrentDailyNumber());

			rs = s.executeQuery("SELECT levelID FROM levels WHERE dailyNumber != 0 ORDER BY dailyNumber DESC");
			Level.currentDailyLevelID = rs.next()?rs.getInt(1):1;
			logger.info("Daily level ID: "+Level.getCurrentDailyLevelID());

			rs = s.executeQuery("SELECT MAX(ID) FROM songs");
			Song.lastSongID = rs.next()?rs.getInt(1):1;
			logger.info("Custom song last id: "+Song.lastSongID);

			rs = s.executeQuery("SELECT MAX(userID) FROM users");
			Account.lastAccountID = rs.next()?rs.getInt(1):1;
			logger.info("Account last id: "+Account.lastAccountID);

			rs = s.executeQuery("SELECT MAX(scoreID) FROM scores");
			Score.lastScoreID = rs.next()?rs.getInt(1):1;
			logger.info("Score last id: "+Score.lastScoreID);

			rs = s.executeQuery("SELECT MAX(ID) FROM comments");
			Comment.lastCommentID = rs.next()?rs.getInt(1):1;
			logger.info("Comment last id: "+Comment.lastCommentID);

			rs = s.executeQuery("SELECT MAX(ID) FROM messages");
			Message.lastMessageID = rs.next()?rs.getInt(1):1;
			logger.info("Message last id: "+Message.lastMessageID);


			logger.info("Initialized database: "+((System.currentTimeMillis()-curtime))+"ms");

			secrets.put("account", "Wmfv3899gc9");
			secrets.put("level", "Wmfv2898gc9");
			secrets.put("common", "Wmfd2893gb7");
			secrets.put("mod", "Wmfp3879gc3");

			Level.secret = Core.secrets.get("level");
			Account.secret = Core.secrets.get("account");

			logger.info("Initializing storages...");
			curtime = System.currentTimeMillis();
			long t = System.currentTimeMillis();
			RequestManager.accounts = Account.getAccountsHashtable();
			logger.info("Accounts initialized. "+(System.currentTimeMillis()-t)+"ms");
			t = System.currentTimeMillis();
			RequestManager.levels = Level.getLevelsHashtable();
			logger.info("Levels initialized. "+(System.currentTimeMillis()-t)+"ms");
			t = System.currentTimeMillis();
			RequestManager.comments = Comment.getCommentsHashset();
			logger.info("Comments initialized. "+(System.currentTimeMillis()-t)+"ms");
			t = System.currentTimeMillis();
			RequestManager.messages = Message.getMessagesHashtable();
			logger.info("Messages initialized. "+(System.currentTimeMillis()-t)+"ms");
			t = System.currentTimeMillis();
			RequestManager.scores = Score.getScoresHashtable();
			logger.info("Scores initialized. "+(System.currentTimeMillis()-t)+"ms");
			t = System.currentTimeMillis();
			RequestManager.songs = Song.getSongsHashtable();
			logger.info("Songs initialized. "+(System.currentTimeMillis()-t)+"ms");

			Utils.createDirs();
			logger.info("Initialized storages: "+((System.currentTimeMillis()-curtime))+"ms");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		Console con = System.console();
		while (con != null) {
			String input = con.readLine(" >>> ");

			ConsoleManager.getInstance().invokeCommand(input);
		}

        //Scanner scanner = new Scanner(System.in); //зародыш консольных команд
		/*
		File file = new File("src/main/resources/database.sql");
		String sql;
		StringBuilder sb = new StringBuilder();
		int i=0;
		try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
			while (i != -1) {
				i = reader.read();
				sb.append(i);
			}
			sql = sb.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try(Statement ps = conn.createStatement()) {
			if (!ps.execute(sql))
				throw new SQLException();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		 */
	}

	/**
	 * @deprecated do it manually in phpmyadmin
	 */
	@Deprecated
	static void createDatabase() {
		File file = new File("database.sql");
		if (!file.exists()) {
			logger.info("database.sql file does not exists");
			return;
		}
		String sql;
		StringBuilder sb = new StringBuilder();
		int i=0;
		try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
			while (i != -1) {
				i = reader.read();
				sb.append(i);
			}
			sql = sb.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try(Statement ps = conn.createStatement()) {
			ps.execute(sql);
		} catch (SQLException e) {
			logger.info(e.getErrorCode());
			throw new RuntimeException(e);
		}
	}

	static void getSettings() {
        File file;
        try {
            file = new File("settings.yml");
            if (!file.exists()) {
                file.createNewFile();
                getSettings();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

		FileInputStream input = null;
		try {
			input = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(input);

        password = (String) data.get("db_password");
        username = (String) data.get("db_username");
        url = "jdbc:mysql:" + data.get("db_url");

        SALT = (String) data.get("salt");

		//logger.info("sql password: "+password);
		//logger.info("sql username: "+username);
		//logger.info("sql url: "+url);
		//logger.info("salt: "+SALT);
    }

	public static String getServerURL() {
		File file;
		try {
			file = new File("settings.yml");
			if (!file.exists()) {
				file.createNewFile();
				throw new RuntimeException("Created settings file! Please restart!");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		FileInputStream input = null;
		try {
			input = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		Yaml yaml = new Yaml();
		Map<String, Object> data = yaml.load(input);
		serverURL = (String) data.get("server_url");
		return serverURL;
	}

	 static <T extends AbstractCommentCommand> void registerCommand(String commandPrefix, T commandClass) {
		for (Method md : commandClass.getClass().getMethods()) {
			if (md.isAnnotationPresent(CommandHandler.class)) {
				CommandManager.getInstance().addCommand(commandPrefix, md.getAnnotation(CommandHandler.class).commandName(), commandClass);
			}
		}
	}
}