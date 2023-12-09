package ru.whoisamyy.api.plugins.commands;

@FunctionalInterface
public interface CommentCommandHandler {
    void execute(CommandArgument args);
}