package ru.whoisamyy.api.plugins.events;

import ru.whoisamyy.api.plugins.events.listeners.EventListener;
import ru.whoisamyy.core.Core;

import java.util.*;

/**
 * Every class that inherits from this must have {Class<?>[] allowedTypes} const.
 */
public abstract class Event {
    public HashMap<String, Object> eventParameters = new HashMap<>();
    protected static List<String> parameterNames = new ArrayList<>();
    protected static List<Class<?>> allowedTypes = new ArrayList<>(); //last allowedType must be return type of corresponding method in ru.whoisamyy.core.endpoints.RequestManager

    public Event(Object... parameterValues) throws NoSuchFieldException {
        init();
        for (int i = 0; i < parameterValues.length; i++) {
            //if (parameterValues[i].getClass()== allowedTypes.get(i))
            eventParameters.put(parameterNames.get(i), parameterValues[i]);
            //else throw new NoSuchFieldException();
        }
    }

    abstract void init();

    public void callEvent() {
        EventListener.getInstance().callEvent(this);
        //parameterNames.forEach(x->Core.logger.info(x));
    }


    //todo add more events inheriting this class
}
