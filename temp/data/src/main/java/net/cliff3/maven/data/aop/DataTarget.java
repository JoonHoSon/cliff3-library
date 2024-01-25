package net.cliff3.maven.data.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * DataTarget
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
@Target(value = {ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataTarget {
    Class<? extends DataTargetBase>[] targets() default {};
}
