package ru.whoisamyy.api.plugins.events;

import ru.whoisamyy.api.plugins.Plugin;
import ru.whoisamyy.api.plugins.events.listeners.EventListener;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Every class that inherits from this must have {Class<?>[] allowedTypes} const.
 */
public abstract class Event {
    public Hashtable<String, Object> eventParameters = new Hashtable<>();
    static final List<String> parameterNames = new ArrayList<>();
    static final List<Class<?>> allowedTypes = new ArrayList<>(); //last allowedType must be return type of corresponding method in ru.whoisamyy.core.endpoints.RequestManager

    public Event(Object... parameterValues) throws NoSuchFieldException {
        for (int i = 0; i < allowedTypes.size(); i++) {
            if (parameterValues[i].getClass()== allowedTypes.get(i))
                eventParameters.put(parameterNames.get(i), parameterValues[i]);
            else throw new NoSuchFieldException();
        }
    }

    public List<Object> callEvent() {
        return EventListener.getInstance().callEvent(this);
    }


    //todo add more events inheriting this class
}
