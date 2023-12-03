package ru.whoisamyy.api.gd.commands;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import ru.whoisamyy.api.plugins.annotations.CommentCommandHandler;
import ru.whoisamyy.api.plugins.events.Event;
import ru.whoisamyy.api.plugins.events.UploadCommentEvent;
import ru.whoisamyy.api.plugins.events.listeners.EventHandler;
import ru.whoisamyy.api.plugins.events.listeners.EventListener;
import ru.whoisamyy.api.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CommandManager {
    public static CommandManager instance;

    public Hashtable<String, Class<? extends Command>> commands = new Hashtable<>();

    CommandManager() {}

    public void initializeCommands(String prefix) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackage(prefix)
                .addScanners(Scanners.TypesAnnotated, Scanners.SubTypes)
        );
        Set<Class<? extends Command>> classes = reflections.getSubTypesOf(Command.class);

        for (Class<? extends Command> clazz : classes) {
            for (Method md : clazz.getMethods()) {
                if (md.isAnnotationPresent(CommentCommandHandler.class))
                    commands.put(md.getAnnotation(CommentCommandHandler.class).commandName(), clazz);
            }
        }

        for (Map.Entry<String, Class<? extends Command>> entry : commands.entrySet()) {
            for (Method md : entry.getValue().getMethods()) {
                if (!md.isAnnotationPresent(CommentCommandHandler.class)) continue;
                EventListener.getInstance().registerHandler(UploadCommentEvent.class, (event) -> {
                    try {
                        String rawStr = Utils.base64UrlSafeDecode((String) event.eventParameters.get("comment"));
                        if (rawStr.startsWith("!")) {
                            CommandArgument arg = mapArgs(rawStr);
                            Object o = entry.getValue().getDeclaredConstructor(CommandArgument.class).newInstance(arg);
                            md.invoke(o);
                        }
                    } catch (IllegalAccessException | InvocationTargetException | InstantiationException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        //for (Map.Entry<Class<? extends Event>, EventHandler> entry : eventHandlers.entrySet()) {
        //    EventListener.getInstance().registerHandler(entry.getKey(), entry.getValue());
        //}
    }

    public CommandArgument mapArgs(String commandString) {
        CommandArgument arg = new CommandArgument();
        String[] commandQueue = commandString.split(" ");
        for (int i = 1; i < commandQueue.length; i++) {
            String[] commandKwarg = commandQueue[i].split("=");
            arg.namedArgs().put(commandKwarg[0], commandKwarg[1]);
        }
        return arg;
    }

    public static CommandManager getInstance() {
        if (instance==null) instance = new CommandManager();
        return instance;
    }
}
