package ru.whoisamyy.api.console.commands

import ru.whoisamyy.api.console.AbstractConsoleCommand
import ru.whoisamyy.api.console.ConsoleManager
import ru.whoisamyy.api.plugins.annotations.ConsoleCommand

/**
 * Console command, that returns list of commands and their description/help
 * You can use this as an example for creating custom console commands in kotlin
 */
class HelpConsoleCommand : AbstractConsoleCommand() {
    @ConsoleCommand(name = "help", help = "returns help for all commands")
    fun help(): String {
        val availableCommands = ConsoleManager.getInstance().commands
        val sb = StringBuilder()
        sb.append("Available commands: ").append('\t')
        availableCommands.forEach {(key, value) ->
            sb.append(key).append(" - ").append(value.commandHelp).append('\n').append('\t')
        }

        return sb.toString()
    }
}