package ru.whoisamyy.api.plugins.commands;

import ru.whoisamyy.api.gd.objects.*;

import java.util.Hashtable;

public class CommandArgument {
    Hashtable<String, Object> args = new Hashtable<>();
    public CommandArgument(Hashtable<String, Object> namedArgs) {
        args.putAll(namedArgs);
    }

    public CommandArgument(String args) {
        this.args.putAll(mapArgs(args, "="));
    }

    static Hashtable<String, Object> mapArgs(String argsString, String separator) {
        String[] nameArg = argsString.split(" ");
        Hashtable<String, Object> namesArgs = new Hashtable<>();
        for (int i = 1; i < nameArg.length; i++) {
            String[] nameArgPair = nameArg[i].split(separator);
            namesArgs.put(nameArgPair[0], nameArgPair[1]);
        }
        return namesArgs;
    }

    public int getInt(String argName) throws NumberFormatException {
        if (args.get(argName)==null) {
            setDefault(argName, 0);
            return getInt(argName);
        }
        return Integer.parseInt((String) args.get(argName));
    }

    public String getString(String argName) {
        if (args.get(argName)==null) {
            setDefault(argName, "null");
            return getString(argName);
        }
        return args.get(argName).toString();
    }

    public boolean getBoolean(String argName) {
        String arg = getString(argName);
        if (arg.equalsIgnoreCase("null")) {
            setDefault(argName, "0");
            return getBoolean(argName);
        }
        if (arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("false")) {
            return arg.equalsIgnoreCase("true");
        }
        if (arg.equalsIgnoreCase("1") || arg.equalsIgnoreCase("0")) {
            return arg.equalsIgnoreCase("1");
        }
        throw new ClassCastException("Cannot cast "+args.get(argName)+" to boolean type.");
    }

    public double getDouble(String argName) {
        if (args.get(argName)==null) {
            setDefault(argName, 0);
            return getDouble(argName);
        }
        return Double.parseDouble(((String) args.get(argName)));
    }

    public float getFloat(String argName) {
        if (args.get(argName)==null) {
            setDefault(argName, 0f);
            return getFloat(argName);
        }
        return Float.parseFloat(((String) args.get(argName)));
    }

    public Level getLevel(String argName) {
        return Level.map(getInt(argName), true);
    }

    public Account getAccount(String argName) {
        try {
            return Account.map(getInt(argName), true);
        } catch (NumberFormatException e) {
            return Account.map(getString(argName), true);
        }
    }

    public Comment getComment(String argName) {
        return Comment.map(getInt(argName));
    }

    public Message getMessage(String argName) {
        return Message.map(getInt(argName));
    }

    public Score getScore(String argName) {
        return Score.map(getInt(argName));
    }

    public Song getSong(String argName) {
        return Song.map(getInt(argName));
    }

    void setDefault(String argName, Object value) {
        args.put(argName, value);
    }
}