package ru.whoisamyy.api.console.commands;

import ru.whoisamyy.api.console.AbstractConsoleCommand;
import ru.whoisamyy.api.plugins.annotations.ConsoleCommand;

public class EchoConsoleCommand extends AbstractConsoleCommand {
    @ConsoleCommand(name = "echo")
    public String echo(String text) {
        return text;
    }
}
