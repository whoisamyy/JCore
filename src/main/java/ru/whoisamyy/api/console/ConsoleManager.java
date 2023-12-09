package ru.whoisamyy.api.console;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.parameters.P;

import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

public class ConsoleManager {
    private static ConsoleManager instance;
    @Getter
    private final Hashtable<String, AbstractConsoleCommand> commands = new Hashtable<>();
    private final Logger logger = LogManager.getLogger(ConsoleManager.class);

    public ConsoleManager() {}

    public void invokeCommand(String commandString) {
        String[] splittedCommandString = commandString.split("\\(");
        String commandName = splittedCommandString[0];
        String commandArgs = splittedCommandString[1];
        commandArgs = commandArgs.substring(0, commandArgs.lastIndexOf(")"));
        Object[] args = commandArgs.split(", ");
        if (args.length==1 && args[0].toString().isBlank()||args[0].toString().isEmpty()) {
            args = new Object[]{}; //heh
        }

        AbstractConsoleCommand consoleCommand = commands.get(commandName);
        if (consoleCommand==null) {
            logger.info("No such command: \""+commandName+"\"");
            return;
        }
        Object retOfInvoke = consoleCommand.invoke(args);

        //multiline string output
        if (retOfInvoke instanceof String) {
            if (((String) retOfInvoke).contains("\n")) {
                for (String s : ((String) retOfInvoke).split("\n")) {
                    logger.info(s);
                }
                return;
            }
        }
        if (retOfInvoke==null) {
            logger.info("An error occurred during the execution of command "+commandName+", perhaps the command returns void?");
            return;
        }
        logger.info(retOfInvoke.toString());
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
