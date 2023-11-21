package ru.whoisamyy.api.plugins.annotations;

import org.springframework.stereotype.Component;
import ru.whoisamyy.core.PluginManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to define is class needs to be loaded by {@link PluginManager} or not.
 *<p></p>
 * <p> If {@link ru.whoisamyy.api.plugins.annotations.PluginClass#isMainClass()} is true you need to inherit {@link ru.whoisamyy.api.plugins.Plugin}. </p>
 *
 * @see ru.whoisamyy.core.PluginManager
 * @see PluginManager#initializePlugins()
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PluginClass {
    String pluginName();
    boolean isMainClass() default false;
}
