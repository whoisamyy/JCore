package ru.whoisamyy.api.plugins.events.listeners;

import ru.whoisamyy.api.plugins.events.Event;

@FunctionalInterface
public interface EventHandler<T extends Event, R> {
     R handleEvent(T eventClass);
}
