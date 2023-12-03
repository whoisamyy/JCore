package ru.whoisamyy.api.plugins.events;

import ru.whoisamyy.core.endpoints.RequestManager;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuggestLevelStarsEvent extends Event {

    @Override
    void init() {
        try {
            Method m = RequestManager.Levels.class.getMethod("suggestGJStars", int.class, int.class, boolean.class, int.class, String.class, String.class);
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

    public SuggestLevelStarsEvent(Object... parameterValues) throws NoSuchFieldException {
        super(parameterValues);
    }
}
