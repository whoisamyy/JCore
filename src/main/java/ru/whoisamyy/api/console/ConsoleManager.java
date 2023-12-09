package ru.whoisamyy.api.console;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ConsoleManager {
    private static ConsoleManager instance;
    private final Hashtable<String, AbstractConsoleCommand> commands = new Hashtable<>();
    private final Logger logger = LogManager.getLogger(ConsoleManager.class);

    public ConsoleManager() {}

    public void invokeCommand(String commandString) {
        String[] splittedCommandString = commandString.split("\\(");
        String commandName = splittedCommandString[0];
        String commandArgs = splittedCommandString[1];
        commandArgs = commandArgs.substring(0, commandArgs.lastIndexOf(")"));
        Object[] args = commandArgs.split(", ");

        AbstractConsoleCommand consoleCommand = commands.get(commandName);
        if (consoleCommand==null) {
            logger.info("No such command: \""+commandName+"\"");
            return;
        }
        logger.info(consoleCommand.invoke(args));
    }

    public void registerCommand(Class<? extends AbstractConsoleCommand> commandClass) {
        try {
            AbstractConsoleCommand command = commandClass.getDeclaredConstructor().newInstance();
            registerCommand(command);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerCommand(AbstractConsoleCommand command) {
        commands.put(command.getCommandName(), command);
    }

    public static ConsoleManager getInstance() {
        if (instance==null) instance= new ConsoleManager();
        return instance;
    }
}
