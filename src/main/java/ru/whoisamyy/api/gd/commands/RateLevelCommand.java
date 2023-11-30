package ru.whoisamyy.api.gd.commands;

import ru.whoisamyy.api.gd.objects.Account;
import ru.whoisamyy.api.gd.objects.Level;
import ru.whoisamyy.api.utils.enums.DemonDifficulty;
import ru.whoisamyy.api.utils.enums.LevelDifficulty;
import ru.whoisamyy.api.utils.enums.ModType;

public class RateLevelCommand extends Command<Void> {
    @Override
    public Void executeCommand() {
        if (Account.map(((int) namedArgs.get("senderID")), true).getMod()!=ModType.ELDER) return null;

        if (namedArgs.get("diff") instanceof LevelDifficulty) {
            Level.rateLevel(((int) namedArgs.get("id")),
                    ((int) namedArgs.get("stars")),
                    ((boolean) namedArgs.get("epic")),
                    ((LevelDifficulty) namedArgs.get("diff")));
        }
        if (namedArgs.get("diff") instanceof DemonDifficulty) {
            Level.rateLevel(((int) namedArgs.get("id")),
                    ((int) namedArgs.get("stars")),
                    ((boolean) namedArgs.get("epic")),
                    ((DemonDifficulty) namedArgs.get("diff")));
        }
        return null;
    }
}
