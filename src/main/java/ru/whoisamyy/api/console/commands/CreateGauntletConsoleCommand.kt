package ru.whoisamyy.api.console.commands

import ru.whoisamyy.api.console.AbstractConsoleCommand
import ru.whoisamyy.api.gd.objects.Gauntlet
import ru.whoisamyy.api.plugins.annotations.ConsoleCommand

class CreateGauntletConsoleCommand : AbstractConsoleCommand() {
    @ConsoleCommand(name = "createGauntlet", help = "creates gauntlet with given arguments. More about this command in docs")
    fun createGauntlet(id: Int, level1: Int, level2: Int, level3: Int, level4: Int, level5: Int): String {
        val g = Gauntlet(id, "$level1,$level2,$level3,$level4,$level5")
        g.upload()
        return "Created gauntlet"
    }
}