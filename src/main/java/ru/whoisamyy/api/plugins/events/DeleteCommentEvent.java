package ru.whoisamyy.api.plugins.events;

import ru.whoisamyy.core.endpoints.RequestManager;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeleteCommentEvent extends Event {

    @Override
    void init() {
        try {
            Method m = RequestManager.Comments.class.getMethod("deleteGJComment", String.class, int.class, String.class, int.class, int.class);
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
    public DeleteCommentEvent(Object... parameterValues) throws NoSuchFieldException {
        super(parameterValues);
    }
}
