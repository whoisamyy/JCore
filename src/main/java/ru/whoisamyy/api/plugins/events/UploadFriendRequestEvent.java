package ru.whoisamyy.api.plugins.events;

import ru.whoisamyy.core.endpoints.RequestManager;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UploadFriendRequestEvent extends Event {
    //static final List<Class<?>> allowedTypes; //allowedTypes is parameters from corresponding method in ru.whoisamyy.core.endpoints.RequestManager
    //static final List<String> parameterNames; //parameter names from correspongind method in ru.whoisamyy.core.endpoints.RequestManager

    void init() {
        try {
            Method m = RequestManager.Relationships.class.getMethod("uploadGJFriendRequest", int.class, int.class, String.class, String.class, String.class);
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

    public UploadFriendRequestEvent(Object... parameterValues) throws NoSuchFieldException {
        super(parameterValues);
    }
}
