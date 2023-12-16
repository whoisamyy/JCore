package ru.whoisamyy.api.plugins.events;

import ru.whoisamyy.core.endpoints.RequestManager;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetRewardsEvent extends Event {
    @Override
    void init() {
        try {
            Method m = RequestManager.class.getMethod("getGJRewards", Integer.class, String.class, String.class, String.class, String.class, Integer.class);
            List<Class<?>> at = new ArrayList<>(List.of(m.getParameterTypes()));
            at.add(m.getReturnType());
            allowedTypes = at;
            Parameter[] params= m.getParameters();
            List<String> strings = new ArrayList<>();
            Arrays.stream(params).forEach(x->strings.add(x.getName()));
            strings.add("returnValue");
            parameterNames = new ArrayList<>(strings);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    public GetRewardsEvent(Object... parameterValues) throws NoSuchFieldException {
        super(parameterValues);
    }
}
