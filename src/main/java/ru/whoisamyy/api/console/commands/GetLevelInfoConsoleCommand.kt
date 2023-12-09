package ru.whoisamyy.api.console.commands

import ru.whoisamyy.api.console.AbstractConsoleCommand
import ru.whoisamyy.api.gd.objects.Level
import ru.whoisamyy.api.plugins.annotations.ConsoleCommand
import ru.whoisamyy.api.utils.Utils
import kotlin.math.absoluteValue

class GetLevelInfoConsoleCommand : AbstractConsoleCommand() {
    @ConsoleCommand(
        name = "getLevelInfo",
        help = "gets and returns level's name, description, length, downloads, likes and objects count"
    )
    fun getLevelInfo(levelID: Int): String {
        val l = Level.map(levelID, true)

        val levelName: String = l.levelName
        val levelDesc: String = Utils.base64UrlSafeDecode(l.description)
        val levelLength: String = l.length.toString()
        val levelDownloads: Int = l.downloads
        val levelLikes: Int = l.likes
        val levelObjCount: Int = l.objects

        return "Level name: $levelName \nLevel desc: $levelDesc \nLevel length: $levelLength" +
                " \nDownloads: $levelDownloads \n${if (levelLikes < 0) "dislikes" else "likes"}: ${levelLikes.absoluteValue} \nObjects: $levelObjCount"
    }
}