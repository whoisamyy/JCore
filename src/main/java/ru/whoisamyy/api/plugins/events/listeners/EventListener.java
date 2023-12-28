package ru.whoisamyy.api.plugins.events.listeners;

import ru.whoisamyy.api.plugins.events.Event;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class EventListener {
    public static EventListener instance;

    EventListener() {}

    public Hashtable<Class<? extends Event>, Set<EventHandler>> eventHandlers = new Hashtable<>();

    public void addHandler(Class<? extends Event> event, EventHandler[] handlers) {
        var eventHandlerSet = Set.of(handlers);
        eventHandlers.put(event, eventHandlerSet);
    }

    public <R> R callEvent(Event sentEvent) {
        var eventHandlersSet = eventHandlers.get(sentEvent.getClass());
        if (eventHandlersSet == null) return null;
        //for (Map.Entry<Class<? extends Event>, Set<EventHandler>> entry : eventHandlers.entrySet()) {
        //    if (entry.getKey()!=sentEvent.getClass()) continue;
        //
        //}

        Object ret = sentEvent.getReturnValue();
        for (EventHandler eh : eventHandlersSet) {
            ret = eh.handleEvent(sentEvent);
            if (ret!=null)  {
                sentEvent.setReturnValue(ret);
            }
        }
        if (ret!=null)
            return (R) ret;
        else return null;
    }

    public void registerHandler(Class<? extends Event> eventClass, EventHandler handler) {
        addHandler(eventClass, new EventHandler[]{handler});
    }

    public void registerHandlers(Class<? extends Event> eventClass, EventHandler[] handlers) {
        addHandler(eventClass, handlers);
    }

    public static EventListener getInstance() {
        if (instance==null) {
            instance = new EventListener();
        }
        return instance;
    }
}