package ru.whoisamyy.api.gd.commands;

import java.util.HashMap;

public abstract class Command<T> implements CommentCommand<T> {
    public String commandName;

    public HashMap<String, Object> namedArgs = new HashMap<>();

    public Command(CommandArgument... args) {
        for (CommandArgument arg: args) {
            namedArgs.put(arg.argumentName(), arg.value());
        }
    }


    /**
     * Not recommended
     * @param args
     */
    public Command(Object... args) {
        for (int i = 0; i < args.length; i++) {
            namedArgs.put("arg"+i, args[i]);
        }
    }
}