package ru.whoisamyy.api.gd.commands;

import ru.whoisamyy.api.gd.objects.Level;
import ru.whoisamyy.api.plugins.annotations.CommandHandler;
import ru.whoisamyy.api.plugins.commands.AbstractCommentCommand;
import ru.whoisamyy.api.plugins.commands.CommandArgument;
import ru.whoisamyy.api.utils.enums.ModType;

@CommandHandler
public class UnrateCommand extends AbstractCommentCommand {
    @Override
    @CommandHandler(commandName = "unrate")
    public void execute(CommandArgument args) {
        if (args.getAccount("senderID").getMod().getVal() == ModType.ELDER.getVal()) {
            Level.unrateLevel(args.getInt("id"));
        }

        //logger.info(args.getAccount("senderID").getUsername() + " unrated(or at least tried to) a level with name: "+ args.getLevel("id").getLevelName()); //ааэээ эта хрень должна быть в плагинах, я забыл её убрать в релизе простите
    }
}
