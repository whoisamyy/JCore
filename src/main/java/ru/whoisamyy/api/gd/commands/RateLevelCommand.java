package ru.whoisamyy.api.gd.commands;

import ru.whoisamyy.api.gd.objects.Account;
import ru.whoisamyy.api.gd.objects.Level;
import ru.whoisamyy.api.plugins.annotations.CommentCommandHandler;
import ru.whoisamyy.api.utils.enums.DemonDifficulty;
import ru.whoisamyy.api.utils.enums.LevelDifficulty;
import ru.whoisamyy.api.utils.enums.ModType;
import ru.whoisamyy.core.Core;

public class RateLevelCommand extends Command<Void> {
    public RateLevelCommand(CommandArgument arg) {
        super(arg);
    }

    @Override
    @CommentCommandHandler(commandName = "rate")
    public Void executeCommand() {
        Core.logger.info(namedArgs.get("senderID"));
        if (Account.map((Integer.parseInt((String) namedArgs.get("senderID"))), true).getMod()!=ModType.ELDER) return null;

        if (namedArgs.get("diff") instanceof LevelDifficulty) {
            Level.rateLevel((Integer.parseInt((String) namedArgs.get("id"))),
                    (Integer.parseInt((String) namedArgs.get("stars"))),
                    (Integer.parseInt((String) namedArgs.get("epic"))==1),
                    (LevelDifficulty.toLevelDifficulty(Integer.parseInt((String) namedArgs.get("diff")))));
        }
        if (namedArgs.get("diff") instanceof DemonDifficulty) {
            Level.rateLevel((Integer.parseInt((String) namedArgs.get("id"))),
                    (Integer.parseInt((String) namedArgs.get("stars"))),
                    (Integer.parseInt((String) namedArgs.get("epic"))==1),
                    (DemonDifficulty.sequenceNumberToDemonDifficulty(Integer.parseInt((String) namedArgs.get("diff")))));
        }
        return null;
    }
}
