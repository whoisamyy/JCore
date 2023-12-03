package ru.whoisamyy.api.gd.commands;

import java.util.HashMap;
import java.util.Objects;

public final class CommandArgument {
    private HashMap<String, Object> namedArgs = new HashMap<>();

    public CommandArgument(HashMap<String, Object> namedArgs) {
        this.namedArgs = namedArgs;
    }

    public CommandArgument() {}

    public HashMap<String, Object> namedArgs() {
        return namedArgs;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CommandArgument) obj;
        return Objects.equals(this.namedArgs, that.namedArgs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namedArgs);
    }

    @Override
    public String toString() {
        return "CommandArgument[" +
                "namedArgs=" + namedArgs + ']';
    }

}
