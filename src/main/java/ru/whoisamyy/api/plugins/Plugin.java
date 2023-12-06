package ru.whoisamyy.api.plugins;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import ru.whoisamyy.api.plugins.annotations.CommandHandler;
import ru.whoisamyy.api.plugins.annotations.EventListener;
import ru.whoisamyy.api.plugins.annotations.PluginClass;
import ru.whoisamyy.api.plugins.commands.Command;
import ru.whoisamyy.api.plugins.commands.CommandManager;
import ru.whoisamyy.api.plugins.events.Event;
import ru.whoisamyy.api.plugins.events.listeners.EventHandler;
import ru.whoisamyy.api.utils.Utils;
import ru.whoisamyy.api.utils.enums.EndpointName;
import ru.whoisamyy.api.utils.enums.Priority;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.Connection;
import java.util.*;


/**
 * Every class, that needs to be loaded must have {@link PluginClass} annotation or else it won't be loaded.
 * @see PluginClass
 * @see ru.whoisamyy.core.PluginManager
 */
@Getter
public abstract class Plugin implements Runnable {
    public String name;
    public Version version;
    /**
     * Priority is used to define order of running plugin's methods and event handlers.
     */
    public Priority priority;
    @Setter public String packageName;
    public Logger logger;
    public Hashtable<EndpointName, Method> methods = new Hashtable<>(); //method endpoint, method
    public Hashtable<Class<? extends Event>, Set<EventHandler>> eventHandlers = new Hashtable<>();
    public Connection connection;
    @Setter public ru.whoisamyy.api.plugins.events.listeners.EventListener eventListener;
    @Setter public CommandManager commandManager;

    public Plugin(String name) {
        this.name = name;
        getVersion();
        getPriority();
    }

    public Plugin(String name, String version, int priority) {
        this.name = name;
        this.version = new Version(version);
        this.priority = Priority.getByValue(priority);
    }

    public Plugin(String name, Version version, int priority) {
        this.name = name;
        this.version = version;
        this.priority = Priority.getByValue(priority);
    }

    /**
     * Initializes plugin objects
     * @param connection    Database connection for sql queries.
     * @param eventListener Instance of {@link ru.whoisamyy.api.plugins.events.listeners.EventListener}. Event listener is used in {@link Plugin#onEvent} method.
     * @return returns initialized plugin object
     */
    final public Plugin initializePlugin(Connection connection, ru.whoisamyy.api.plugins.events.listeners.EventListener eventListener, CommandManager commandManager) {
        setPackageName(getClass().getPackageName());
        long startTime;
        startTime = init(connection);
        setEventListener(eventListener);
        setCommandManager(commandManager);
        initialize();
        getLogger().info("Initialized plugin: "+ (System.currentTimeMillis()-startTime) +"ms");
        return this;
    }

    /**
     * This method can be used to initialize variables of class from config file, or for whatever reason
     */
    public abstract void initialize();

    /**
     * This method can be used for <code>while(true)</code> loops or as alternative to<p></p>
     * {@code public static void main(String[] args) {}}
     */
    public void run() {}

    /**
     * Main initialize method in plugin.
     * @param connection database connection
     * @return start time of initializing, used in {@link Plugin#initializePlugin} method
     */
    final protected long init(Connection connection) {
        setLogger(LogManager.getLogger(packageName));
        getLogger().info("Successfully registered logger");
        getLogger().info("Package name: "+packageName);
        getLogger().info("Initializing plugin...");
        long startTime = System.currentTimeMillis();

        getLogger().info("Getting version...");
        long l = System.currentTimeMillis();
        getVersion();
        getLogger().info("Done! "+(System.currentTimeMillis()-l)+"ms");
        getLogger().info("Version of "+name+" is "+this.version);
        getLogger().info("Getting priority...");
        l = System.currentTimeMillis();
        getPriority();
        getLogger().info("Done! "+(System.currentTimeMillis()-l)+"ms");


        setConnection(connection);

        try {
            loadMethods();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return startTime;
    }


    /**
     * Unfortunately, loads methods only from main plugin class. To be fixed in future
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void loadMethods() throws IOException, ClassNotFoundException {
        //Set<Class<?>> classes = getClasses(packageName);
        logger.info("loading methods...");
        long startTime = System.currentTimeMillis();
        //for (Class<?> clazz : classes) {
        //    logger.info(clazz.getName());
        Class<?> clazz = this.getClass();
        Method[] methods = clazz.getMethods();
        for (Method md : methods) {
            if (!md.isAnnotationPresent(EventListener.class)) continue;
            this.methods.put(md.getAnnotation(EventListener.class).endpointName(), md);
        }
        //}
        logger.info("Loaded methods "+ (System.currentTimeMillis()-startTime)+"ms. Count: "+this.methods.size());
    }

    //todo fix
    @Deprecated
    private Set<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        List<File> directories = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            directories.add(new File(resource.getFile()));
        }


        Set<Class<?>> classes = new HashSet<>();
        for (File directory : directories) {
            classes.addAll(findClasses(directory, packageName));
        }
        //var classez = new Reflections(packageName).getSubTypesOf(Object.class);
        //Reflections reflections = new Reflections(packageName);
        //var classes = reflections.getSubTypesOf(Object.class);

        return classes;
    }

    private List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        if (files!=null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    assert !file.getName().contains(".");
                    classes.addAll(findClasses(file, packageName+"."+file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().substring(0, file.getName().length()-6);
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(PluginClass.class)) {
                        classes.add(Class.forName(className));
                    }
                }
            }
        }

        return classes;
    }

    final public Version getVersion() {
        try {
            String configPath = "plugins/configs/" + getName() + ".yml";

            InputStream input = Plugin.class.getResourceAsStream(configPath);
            if (input == null) {
                Utils.createFile(getName()+".yml", Utils.createDirs(configPath));
                return new Version("0.0.1:SNAPSHOT");
            }
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(input);
            this.version = new Version((String) data.get("version"));

            return this.version;
        } catch (IOException e) {
            logger.warn("Error occurred while trying to get version from config file");
            return new Version("0.0.1:SNAPSHOT");
        }
    }

    final public Priority getPriority() {
        try {
            String configPath = "plugins/configs/" + getName() + ".yml";

            InputStream input = Plugin.class.getResourceAsStream(configPath);
            if (input == null) {
                Utils.createFile(getName()+".yml", Utils.createDirs(configPath));
                return Priority.HIGHEST;
            }
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(input);
            this.priority = Priority.getByValue((int) data.get("priority"));
            return this.priority;
        } catch (IOException e) {
            logger.warn("Error occurred while trying to get priority from config file");
            return Priority.HIGHEST;
        }
    }

    final protected void onEvent(Class<? extends Event> event, EventHandler[] eventHandlers) {
        //this.eventHandlers.put(event, Set.of(eventHandlers));
        getEventListener().registerHandlers(event, eventHandlers);
    }

    final protected void onEvent(Class<? extends Event> event, EventHandler eventHandler) {
        //this.eventHandlers.put(event, Set.of(eventHandlers));
        getEventListener().registerHandler(event, eventHandler);
    }

    final protected <T extends Command> void registerCommand(String commandPrefix, T commandClass) {
        for (Method md : commandClass.getClass().getMethods()) {
            if (md.isAnnotationPresent(CommandHandler.class)) {
                commandManager.addCommand(commandPrefix, md.getAnnotation(CommandHandler.class).commandName(), commandClass);
            }
        }
    }

    final protected void log(String s) {
        logger.info(s);
    }

    final public void setConnection(Connection connection) {
        this.connection = connection;
    }

    final public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
