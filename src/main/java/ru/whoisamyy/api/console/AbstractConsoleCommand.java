package ru.whoisamyy.api.console;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import ru.whoisamyy.api.plugins.annotations.ConsoleCommand;

import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class AbstractConsoleCommand {
    //нужно: единный, иммутабельный массив типов аргументов для каждого объекта класса

    @Getter private String commandName;
    @Getter private Class<?> returnType;
    private Object returnValue;
    private Method executable;

    public AbstractConsoleCommand() {
        initializeCommand();
        LogManager.getLogger(AbstractConsoleCommand.class).info(commandName);
    }

    private void initializeCommand() {
        for (Method md : this.getClass().getMethods()) {
            if (md.isAnnotationPresent(ConsoleCommand.class)) {
                String name = md.getAnnotation(ConsoleCommand.class).name();
                if (!name.equals("")) commandName = name;
                else commandName = md.getName();
                executable = md;
                returnType = md.getReturnType();
            }
        }
    }

    public Object invoke(Object[] args) {
        if (executable==null) return null;
        try {
            returnValue = executable.invoke(this, args);
            return returnValue;
        } catch (IllegalAccessException | InvocationTargetException e) {
            returnValue = null;
            return null;
        }
    }
}
