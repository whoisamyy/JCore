package ru.whoisamyy.api.console;

import lombok.Getter;
import ru.whoisamyy.api.plugins.annotations.ConsoleCommand;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class AbstractConsoleCommand {
    @Getter private String commandName;
    @Getter private Class<?> returnType;
    @Getter private String commandDesc;
    @Getter private String commandHelp;
    private Object returnValue;
    private Method executable;

    public AbstractConsoleCommand() {
        initializeCommand();
    }

    private void initializeCommand() {
        for (Method md : this.getClass().getMethods()) {
            if (md.isAnnotationPresent(ConsoleCommand.class)) {
                String name = md.getAnnotation(ConsoleCommand.class).name();
                String desc = md.getAnnotation(ConsoleCommand.class).help();

                if (!name.isEmpty()) commandName = name;
                else commandName = md.getName();
                commandHelp = desc;
                commandDesc = commandHelp;

                executable = md;
                returnType = md.getReturnType();
            }
        }
    }

    Object parseArg(String arg, Class<?> toType) {
        Object retArg = null;
        if (arg.startsWith("\"") && arg.endsWith("\""))
            return arg.replaceAll("\"", "");
        if (toType == int.class) {
            retArg= Integer.parseInt(arg);
        } else if (toType == boolean.class) {
            retArg= Boolean.parseBoolean(arg);
        } else if (toType == float.class) {
            retArg= Float.parseFloat(arg);
        } else if (toType == double.class) {
            retArg= Double.parseDouble(arg);
        } else if (toType == long.class) {
            retArg= Long.parseLong(arg);
        } else if (toType == char.class) {
            retArg= arg.charAt(0);
        } else if (toType == short.class) {
            retArg= Short.parseShort(arg);
        } else if (toType == String.class) {
            retArg=arg;
        }
        return retArg;
    }

    public Object invoke(Object[] args) {
        if (executable==null) return null;
        try {
            Class<?>[] parameterTypes = executable.getParameterTypes();
            Object[] invokeArgs = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                invokeArgs[i] = parseArg(args[i].toString(), parameterTypes[i]);
            }
            returnValue = executable.invoke(this, invokeArgs);
            return returnValue;
        } catch (IllegalAccessException | InvocationTargetException | NumberFormatException e) {
            returnValue = null;
            return null;
        }
    }
}
