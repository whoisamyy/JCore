package ru.whoisamyy.api.plugins.commands;

@FunctionalInterface
public interface CommandHandler {
    void execute(CommandArgument args);
}