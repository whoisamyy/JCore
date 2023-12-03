package ru.whoisamyy.api.plugins.annotations;


import ru.whoisamyy.api.plugins.events.Event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommentCommandHandler {
    String commandName();
}
