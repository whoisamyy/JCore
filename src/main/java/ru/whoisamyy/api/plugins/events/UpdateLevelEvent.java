package ru.whoisamyy.api.plugins.events;

import ru.whoisamyy.core.endpoints.RequestManager;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This one is the same as {@link ru.whoisamyy.api.plugins.events.UploadLevelEvent} because both of them triggered in {@link RequestManager.Levels#uploadGJLevel}
 */
public class UpdateLevelEvent extends Event {
    static final List<Class<?>> allowedTypes; //allowedTypes is parameters from corresponding method in ru.whoisamyy.core.endpoints.RequestManager
    static final List<String> parameterNames; //parameter names from correspongind method in ru.whoisamyy.core.endpoints.RequestManager

    static {
        try {
            Method m = RequestManager.Levels.class.getMethod("uploadGJLevel", int.class, int.class, Integer.class, String.class, String.class, int.class, int.class, int.class, int.class, int.class, int.class, boolean.class, int.class, int.class, int.class, int.class, boolean.class, boolean.class, String.class, String.class, String.class);
            List<Class<?>> at = new ArrayList<>(List.of(m.getParameterTypes()));
            at.add(m.getReturnType());
            allowedTypes = at;
            Parameter[] params= m.getParameters();
            List<String> strings = new ArrayList<>();
            Arrays.stream(params).forEach(x->strings.add(x.getName()));
            parameterNames = new ArrayList<>(strings);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public UpdateLevelEvent(Object... parameterValues) throws NoSuchFieldException {
        super(parameterValues);
    }
}
