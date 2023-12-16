package ru.whoisamyy.api.console.commands;

import ru.whoisamyy.api.console.AbstractConsoleCommand;
import ru.whoisamyy.api.plugins.annotations.ConsoleCommand;


/**
 * useless, but very simple example for creating custom console commands
 */
public class EchoConsoleCommand extends AbstractConsoleCommand {
    @ConsoleCommand(name = "echo", help = "returns text, given in argument")
    public String echo(String text) {
        return text;
    }
}