package ru.whoisamyy.api.console.commands

import ru.whoisamyy.api.console.AbstractConsoleCommand
import ru.whoisamyy.api.gd.objects.MapPack
import ru.whoisamyy.api.plugins.annotations.ConsoleCommand

class CreateMapPackConsoleCommand : AbstractConsoleCommand() {
    @ConsoleCommand(name = "createMapPack", help = "creates map pack")
    fun createMapPack(name: String, levels: String, rewardStars: Int, rewardCoins: Int, diff: Int, rgb1: String, rgb2: String): String {
        val mp = MapPack(name, levels, rewardStars, rewardCoins, diff, rgb1, rgb2)
        mp.upload()
        return "Created new map pack with levels: $levels and name: $name. Map pack id: ${mp.id}"
    }
}