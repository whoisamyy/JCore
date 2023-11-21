package ru.whoisamyy.api.plugins;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import ru.whoisamyy.api.plugins.annotations.EventListener;
import ru.whoisamyy.api.plugins.annotations.PluginClass;
import ru.whoisamyy.api.utils.Utils;
import ru.whoisamyy.api.utils.enums.EndpointName;

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
    public int priority;
    @Setter public String packageName;
    public Logger logger;
    public Hashtable<EndpointName, Method> methods = new Hashtable<>(); //method endpoint, method
    public Connection connection;

    public Plugin(String name) {
        this.name = name;
        getVersion();
        getPriority();
    }

    public Plugin(String name, String version, int priority) {
        this.name = name;
        this.version = new Version(version);
        this.priority = priority;
    }

    public Plugin(String name, Version version, int priority) {
        this.name = name;
        this.version = version;
        this.priority = priority;
    }

    public Plugin getInstance(Connection connection, String packageName) {
        setPackageName(packageName);
        long startTime;
        startTime = init(connection);
        initialize();
        getLogger().info("Initialized plugin: "+ (System.currentTimeMillis()-startTime) +"ms");
        return this;
    }

    public abstract void initialize();

    public void run() {}

    protected long init(Connection connection) {
        //Class<?> clazz = Plugin.class;
        //Package pkg = clazz.getPackage();
        //if (pkg==null) throw new RuntimeException();
        //this.packageName = pkg.getName();

        setLogger(LogManager.getLogger(packageName));
        getLogger().info("Successfully registered logger");
        getLogger().info("Package name: "+packageName);
        getLogger().info("Initializing plugin...");
        long startTime = System.currentTimeMillis();

        getLogger().info("Getting version...");
        long l = System.currentTimeMillis();
        getVersion();
        getLogger().info("Done! "+(System.currentTimeMillis()-l)+"ms");
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
     * Unfortunately laods methods only from main plugin class. To be fixed in future
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
            logger.info(md.getName());
            if (!md.isAnnotationPresent(EventListener.class)) continue;
            this.methods.put(md.getAnnotation(EventListener.class).endpointName(), md);
        }
        //}
        logger.info("Loaded methods "+ (System.currentTimeMillis()-startTime)+"ms. Count: "+this.methods.size());
    }

    //todo fix
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

    public Version getVersion() {
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
            e.printStackTrace();
            return new Version("0.0.1:SNAPSHOT");
        }
    }

    public int getPriority() {
        try {
            String configPath = "plugins/configs/" + getName() + ".yml";

            InputStream input = Plugin.class.getResourceAsStream(configPath);
            if (input == null) {
                Utils.createFile(getName()+".yml", Utils.createDirs(configPath));
                return 0;
            }
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(input);
            this.priority = (int) data.get("priority");
            return this.priority;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    protected void log(String s) {
        logger.info(s);
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
