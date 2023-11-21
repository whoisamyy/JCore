package ru.whoisamyy.api.plugins.annotations;


import ru.whoisamyy.api.utils.enums.EndpointName;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @see EndpointName
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventListener {
    /**
     * @see EndpointName
     */
    EndpointName endpointName();
    int endpointIntValue() default 0;
}
