//
// Source code recreated from a .class file by Quiltflower
//

package ru.whoisamyy.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.whoisamyy.api.plugins.Plugin;
import ru.whoisamyy.api.plugins.annotations.EndpointParameter;
import ru.whoisamyy.api.plugins.annotations.PluginClass;
import ru.whoisamyy.api.utils.Utils;
import ru.whoisamyy.api.utils.enums.EndpointName;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginManager {
    private static PluginManager instance;
    private String pluginsFolderPath = "plugins";
    private Hashtable<Integer, Object> plugins = new Hashtable();
    private Hashtable<Object, Hashtable<EndpointName, Method>> pluginsMethods = new Hashtable();
    private static final Logger logger = LogManager.getLogger(PluginManager.class);

    private PluginManager() {
    }

    public void initializePlugins() {
        logger.info("Initializing plugins...");
        long st = System.currentTimeMillis();
        File pluginsFolder = new File(Utils.resources + "/plugins");
        if (!pluginsFolder.exists()) {
            pluginsFolder.mkdirs();
        }

        File[] plugins = pluginsFolder.listFiles();

        for(File file : plugins) {
            String filePath = file.getPath();
            if (filePath.endsWith(".jar")) {
                try {
                    JarFile jarFile = new JarFile(file);
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while(entries.hasMoreElements()) {
                        try {
                            JarEntry entry = (JarEntry)entries.nextElement();
                            String className = entry.getName();

                            if (!className.endsWith(".class")) continue;

                            className = className.replaceAll("/", ".").replace(".class", "");
                            //URLClassLoader classLoader = new URLClassLoader(new URL[]{new URL("file:" + file.getPath())});
                            ClassLoader classLoader = java.net.URLClassLoader.newInstance(
                                    new java.net.URL[] { file.toURI().toURL() },
                                    getClass().getClassLoader()
                            );
                            Class<?> loadedClass = classLoader.loadClass(className);

                            if (loadedClass.isAnnotationPresent(PluginClass.class)) {
                                String name = loadedClass.getAnnotation(PluginClass.class).pluginName();
                                Object instance = loadedClass.getDeclaredConstructor(String.class).newInstance(name);
                                if (instance instanceof Plugin p) {
                                    Plugin pl = p.getInstance(Core.conn, className);

                                    this.plugins.put(pl.getPriority(), pl);
                                    Hashtable<EndpointName, Method> pluginMethods = pl.getMethods();
                                    this.pluginsMethods.put(pl, pluginMethods);

                                    if (pl.getClass().getMethod("run").isAnnotationPresent(Override.class)) {
                                        Thread t = new Thread(pl);
                                        pl.logger.info("Plugin run thread started");
                                    } else {
                                        pl.logger.info("No plugin run thread found. Skipping");
                                    }

                                    pl.logger.info("Loaded plugin!");
                                }
                            }

                        } catch (MalformedURLException | ClassNotFoundException | InvocationTargetException |
                                 InstantiationException | IllegalAccessException | NoSuchMethodException var20) {
                            throw new RuntimeException(var20);
                        }
                    }
                } catch (IOException var21) {
                    throw new RuntimeException(var21);
                }
            }
        }

        logger.info("Initialized plugins. " + (System.currentTimeMillis() - st) + "ms");
    }

    public Object methodInvoke(Object pluginObject, String methodName, Object[] parameters) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (pluginObject.getClass().getMethod(methodName).getParameters().length == 0) {
            pluginObject.getClass().getMethod(methodName).invoke(pluginObject);
            Class<?> returnType = pluginObject.getClass().getMethod(methodName).getReturnType();
            return pluginObject.getClass().getMethod(methodName).invoke(pluginObject);
        } else {
            Parameter[] params = pluginObject.getClass().getMethod(methodName).getParameters();
            Class<?> returnType = pluginObject.getClass().getMethod(methodName).getReturnType();
            return pluginObject.getClass().getMethod(methodName).invoke(pluginObject, parameters);
        }
    }

    public Object methodInvoke(Object pluginObject, String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        pluginObject.getClass().getMethod(methodName).invoke(pluginObject);
        Class<?> returnType = pluginObject.getClass().getMethod(methodName).getReturnType();
        return pluginObject.getClass().getMethod(methodName).invoke(pluginObject);
    }

    public Object getField(Object pluginObject, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = pluginObject.getClass().getDeclaredField(fieldName);
        Object fieldVal = field.get(pluginObject);
        return fieldVal == null ? null : fieldVal;
    }

    public Hashtable<String, Method> getPluginEndpointMethods(Object pluginObject) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String methodName = "getMethods";
        Object methods = pluginObject.getClass().getMethod(methodName).invoke(pluginObject);
        return methods instanceof Hashtable ? (Hashtable)methods : null;
    }

    public SortedMap<Integer, Object> getSortedPlugins() {
        SortedMap<Integer, Object> map = new TreeMap(Comparator.naturalOrder());
        map.putAll(this.plugins);
        return map;
    }

    public static void runEndpointMethods(Object[] vals, Parameter[] pars, EndpointName endpointName) throws InvocationTargetException, IllegalAccessException {
        HashMap<String, Object> parsVals = new HashMap();

        for(int i = 0; i < pars.length; ++i) {
            parsVals.put(pars[i].getName(), vals[i]);
        }

        for(Entry<Integer, Object> entry : getInstance().getSortedPlugins().entrySet()) {
            Method md = (Method)((Hashtable)getInstance().getPluginsMethods().get(entry.getValue())).get(endpointName);
            if (md != null) {
                if (md.getParameters().length == 0) {
                    md.invoke(entry.getValue());
                } else {
                    List<Parameter> params = new ArrayList(List.of(md.getParameters()));
                    params.removeIf(x -> !x.isAnnotationPresent(EndpointParameter.class));
                    List<Object> arguments = new ArrayList();

                    for(int i = 0; i < params.size(); ++i) {
                        String s = ((EndpointParameter)((Parameter)params.get(i)).getAnnotation(EndpointParameter.class)).parameterName();
                        arguments.add(parsVals.get(s));
                    }

                    Object[] args = arguments.toArray();
                    md.invoke(entry.getValue(), args);
                }
            }
        }
    }

    public static PluginManager getInstance() {
        if (instance == null) {
            instance = new PluginManager();
        }

        return instance;
    }

    public Hashtable<Integer, Object> getPlugins() {
        return this.plugins;
    }

    public Hashtable<Object, Hashtable<EndpointName, Method>> getPluginsMethods() {
        return this.pluginsMethods;
    }
}
