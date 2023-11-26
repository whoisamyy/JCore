package ru.whoisamyy.api.plugins.annotations;


import ru.whoisamyy.api.utils.enums.EndpointName;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation is used to mark a method as an event listener.
 * It is important to note that this annotation should not be confused with
 * {@link ru.whoisamyy.api.plugins.events.listeners.EventListener}.
 * <p></p>
 * The annotated method will be invoked when a specific event occurs.
 *
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
