package ru.whoisamyy.api.gd.commands;

import ru.whoisamyy.api.gd.objects.Account;
import ru.whoisamyy.api.gd.objects.Level;
import ru.whoisamyy.api.plugins.annotations.CommandHandler;
import ru.whoisamyy.api.plugins.commands.Command;
import ru.whoisamyy.api.plugins.commands.CommandArgument;
import ru.whoisamyy.api.utils.enums.DemonDifficulty;
import ru.whoisamyy.api.utils.enums.LevelDifficulty;
import ru.whoisamyy.api.utils.enums.ModType;

@CommandHandler
public class RateCommand extends Command {
    @CommandHandler(commandName = "rate")
    @Override
    public void execute(CommandArgument args) {
        Account acc = Account.map(args.getInt("senderID"), true);
        if (acc.getMod()== ModType.ELDER) {
            if (args.getBoolean("demon")) {
                Level.rateLevel(args.getInt("id"), args.getInt("stars"), args.getBoolean("epic"), DemonDifficulty.sequenceNumberToDemonDifficulty(args.getInt("diff")));
            } else {
                Level.rateLevel(args.getInt("id"), args.getInt("stars"), args.getBoolean("epic"), LevelDifficulty.toLevelDifficulty(args.getInt("diff")));
            }
        }

        logger.info(acc.getUsername() + " rated (or at least tried to) level with name " + Level.map(args.getInt("id")));
    }
}