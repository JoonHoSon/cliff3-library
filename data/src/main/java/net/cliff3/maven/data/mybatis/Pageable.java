package net.cliff3.maven.data.mybatis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MyBatis에서 지정된 {@link #countMapperID()}를 호출하여
 * 페이징 처리를 위한 전체 데이터 건수를 자동으로 조회하도록 처리하는데 사용된다.
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Pageable {
    /**
     * Counting 처리용 mapper id
     *
     * @return mapper id
     */
    String countMapperID() default "";

    /**
     * Paging 처리시 시작 값
     *
     * @return 시작 값
     */
    String fromKey() default "fromData";

    /**
     * Paging 처리시 시작 값(MySQL용)
     *
     * @return 시작 값
     */
    String fromIndex() default "fromIndex";

    /**
     * Paging 처리시 종료 값
     *
     * @return 종료 값
     */
    String toKey() default "toData";
}
