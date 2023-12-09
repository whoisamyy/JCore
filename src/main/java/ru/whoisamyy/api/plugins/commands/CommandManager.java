package ru.whoisamyy.api.plugins.commands;

import ru.whoisamyy.api.plugins.events.UploadCommentEvent;
import ru.whoisamyy.api.plugins.events.listeners.EventListener;
import ru.whoisamyy.api.utils.Utils;

import java.util.Hashtable;

public class CommandManager {
    private static CommandManager instance;
    public Hashtable<String, CommentCommandHandler> commands = new Hashtable<>();

    public void addCommand(String prefix, String commandName, CommentCommandHandler handler) {
        commands.put(commandName, handler);
        //adds event handler, that executes command
        EventListener.getInstance().registerHandler(UploadCommentEvent.class, (event) -> {
            String rawStr = Utils.base64UrlSafeDecode(((String) event.eventParameters.get("comment")));

            if (rawStr.startsWith(prefix+commandName)) {
                rawStr = rawStr.replace("!"+commandName, "");
                rawStr = rawStr + " senderID="+event.eventParameters.get("accountID")+" id="+event.eventParameters.get("levelID");
                handler.execute(new CommandArgument(CommandArgument.mapArgs(rawStr, "=")));
            }
        });
    }

    public void addCommands(String prefix, String commandName, CommentCommandHandler[] handlers) {
        for (CommentCommandHandler handler : handlers) {
            addCommand(prefix, commandName, handler);
        }
    }

    public static CommandManager getInstance() {
        if (instance==null) instance = new CommandManager();
        return instance;
    }
}
