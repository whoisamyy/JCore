package ru.whoisamyy.api.console.commands

import ru.whoisamyy.api.console.AbstractConsoleCommand
import ru.whoisamyy.api.console.ConsoleManager
import ru.whoisamyy.api.plugins.annotations.ConsoleCommand

/* да, для создания плагинов и просто работы с этим ядром можно использовать котлин, да и вообще любой jvm яп
на вопрос зачем котлин ответ прост: да прост по приколу почему не
 */
class HelpConsoleCommand : AbstractConsoleCommand() {
    @ConsoleCommand(name = "help", help = "returns help for all commands")
    fun help(): String {
        val availableCommands = ConsoleManager.getInstance().commands
        val sb = StringBuilder()
        sb.append("Available commands: ").append('\t')
        availableCommands.forEach {(key, value) ->
            sb.append(key).append(" - ").append(value.commandDesc).append('\n').append('\t')
        }

        return sb.toString()
    }
}