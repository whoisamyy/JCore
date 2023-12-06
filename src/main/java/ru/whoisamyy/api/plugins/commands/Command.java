package ru.whoisamyy.api.plugins.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Command implements CommandHandler {
    protected Logger logger;
    protected String name;

    public Command(String name) {
        this.name = name;
        this.logger = LogManager.getLogger(this.getClass());
    }

    public Command() {
        this.logger = LogManager.getLogger(this.getClass());
    }
}
