package ru.whoisamyy.api.plugins.events;

import lombok.Getter;
import ru.whoisamyy.api.gd.objects.*;
import ru.whoisamyy.api.plugins.commands.CommandArgument;
import ru.whoisamyy.api.plugins.events.listeners.EventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Base Event class, used to call event listeners.
 */
public abstract class Event {
    HashMap<String, Object> eventParameters = new HashMap<>();

    CommandArgument eventParametersArgument;

    protected static List<String> parameterNames = new ArrayList<>();
    protected static List<Class<?>> allowedTypes = new ArrayList<>(); //last allowedType must be return type of corresponding method in ru.whoisamyy.core.endpoints.RequestManager

    @Getter private final Object returnValue;

    public Event(Object... parameterValues) {
        init();
        for (int i = 0; i < parameterValues.length; i++) {
            eventParameters.put(parameterNames.get(i), parameterValues[i]);
        }
        eventParametersArgument = new CommandArgument(eventParameters);
        returnValue = eventParametersArgument.get("returnValue");
    }

    abstract void init();


    //очень по смешному выглядит честно, вы только представьте!
    //эта строка возвращает либо четотамнепомнюче либо returnValue, ЧЕРЕЗ СТОРОННИЙ МЕТОД!!!
    //мне кажется это неправильно
    /**
     * I have nothing to say here. It does what its name is.
     * @return returns last called event handler output
     * @param <R> stands for return type
     */
    public <R> R callEvent() {
        var r = allowedTypes.get(allowedTypes.size()-1);

        var ret = EventListener.getInstance().callEvent(this);
        if (ret==null) return (R) r.cast(getReturnValue());
        //ide пишет предупреждение о том, что cast is unchecked, но поху ваще, всё проверяется в .callEvent(this)
        return (R) r.cast(ret);
    }

    //api
    final public Object getParameterValue(String parameterName) {
        return eventParameters.get(parameterName);
    }

    final public int getInt(String parameterName) {
        return eventParametersArgument.getInt(parameterName);
    }
    final public String getString(String parameterName) {
        return eventParametersArgument.getString(parameterName);
    }
    final public double getDouble(String parameterName) {
        return eventParametersArgument.getDouble(parameterName);
    }
    final public float getFloat(String parameterName) {
        return eventParametersArgument.getFloat(parameterName);
    }
    final public boolean getBoolean(String parameterName) {
        return eventParametersArgument.getBoolean(parameterName);
    }
    final public Level getLevel(String parameterName) {
        return eventParametersArgument.getLevel(parameterName);
    }
    final public Account getAccount(String parameterName) {
        return eventParametersArgument.getAccount(parameterName);
    }
    final public Song getSong(String parameterName) {
        return eventParametersArgument.getSong(parameterName);
    }
    final public Comment getComment(String parameterName) {
        return eventParametersArgument.getComment(parameterName);
    }
    final public Score getScore(String parameterName) {
        return eventParametersArgument.getScore(parameterName);
    }
    final public Message getMessage(String parameterName) {
        return eventParametersArgument.getMessage(parameterName);
    }

    final public Object get(String parameterName) {
        return eventParametersArgument.get(parameterName);
    }

    /**
     * Do not touch this unless you know what you do!!
     * @param newValue new value to be set as return value
     * @param <T> return type
     */
    final public <T> void setReturnValue(T newValue) {
        var o = allowedTypes.get(allowedTypes.size()-1);

        if (newValue.getClass()==o) {
            eventParameters.replace("returnValue", newValue);
            return;
        }
        throw new ClassCastException("Cannot cast "+newValue.getClass()+" to "+o);
    }

    //todo add more events inheriting this class
}
