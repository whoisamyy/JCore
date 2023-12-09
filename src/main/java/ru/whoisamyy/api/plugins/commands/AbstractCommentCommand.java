package ru.whoisamyy.api.plugins.commands;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractCommentCommand implements CommentCommandHandler {
    protected Logger logger;
    @Getter protected String name;

    public AbstractCommentCommand(String name) {
        this.name = name;
        this.logger = LogManager.getLogger(this.getClass());
    }

    public AbstractCommentCommand() {
        this.logger = LogManager.getLogger(this.getClass());
    }
}
