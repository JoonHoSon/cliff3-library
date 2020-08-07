package net.cliff3.maven.common.util.web.aop;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nhncorp.lucy.security.xss.XssFilter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;

/**
 * {@link EscapeHTML} annotation을 이용하여 허용된 HTML tag 이외의 문자열을 escape 처리한다.
 *
 * @author JoonHo Son
 * @see EscapeHTML
 * @since 0.2.0
 */
@Slf4j
public class EscapeHTMLBeforeAdvice {
    /**
     * filter 설정 기본값
     */
    private static final String DEFAULT_CONFIG = "lucy-xss-cliff3-default.xml";

    /**
     * filter 설정 파일. 설정파일은 classpath root에 위치하여야 한다.
     */
    @Setter
    private String configFileName;

    /**
     * {@link XssFilter}
     */
    private static XssFilter filter;

    /**
     * {@link XssFilter}를 이용하여 escape 처리.
     *
     * @param point {@link JoinPoint}
     *
     * @throws NoSuchMethodException     대상 필드의 getter/setter 메서드가 존재하지 않을 경우 발생
     * @throws InvocationTargetException 대상 필드의 getter/setter method invoke 실패
     * @throws IllegalAccessException    대상 필드의 getter/setter method invoke 실패(접근 오류)
     * @see #checkFilter()
     * @see #doEscape(Object)
     */
    public void processHTMLEscape(JoinPoint point)
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        checkFilter();

        log.debug("------------------------------------------------------------------------");
        log.debug("starting escape html aspect!");
        log.debug("{}", point.getSignature());

        Object[] parameters = point.getArgs();

        if (parameters != null) {
            for (Object parameter : parameters) {
                if (parameter == null) {
                    continue;
                }

                doEscape(parameter);
            }
        }
    }

    /**
     * 대상 인스턴스의 필드를 추출하여 {@link #doEscapeField(Field, Object)} 호출.
     *
     * @param parameter {@link EscapeHTML} 필드를 포함하는 인스턴스
     *
     * @throws NoSuchMethodException     대상 필드의 getter/setter 메서드가 존재하지 않을 경우 발생
     * @throws InvocationTargetException 대상 필드의 getter/setter method invoke 실패
     * @throws IllegalAccessException    대상 필드의 getter/setter method invoke 실패(접근 오류)
     * @see #doEscapeField(Field, Object)
     */
    private void doEscape(Object parameter)
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Package packageInformation = parameter.getClass().getPackage();

        if (packageInformation != null) {

            Field[] fields = parameter.getClass().getDeclaredFields();

            List<Field> fieldList = new ArrayList<>(Arrays.asList(fields));

            Class<?> current = parameter.getClass();
            while (current.getSuperclass() != null) {
                current = current.getSuperclass();
                Field[] superClassFields = current.getDeclaredFields();
                fieldList.addAll(Arrays.asList(superClassFields));
            }

            for (Field field : fieldList) {
                doEscapeField(field, parameter);
            }
        }
    }

    /**
     * 인자로 전달된 {@link Field}가 {@link EscapeHTML} annotation을 포함할 경우 escape 처리.
     *
     * @param field     대상 필드
     * @param parameter 대상 인스턴스
     *
     * @throws InvocationTargetException 대상 필드의 getter/setter method invoke 실패
     * @throws IllegalAccessException    대상 필드의 getter/setter method invoke 실패(접근 오류)
     * @throws NoSuchMethodException     대상 필드의 getter/setter 메서드가 존재하지 않을 경우 발생
     */
    @SuppressWarnings("unchecked")
    private void doEscapeField(Field field, Object parameter)
        throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        EscapeHTML annotation = field.getAnnotation(EscapeHTML.class);

        if (annotation != null) {
            log.debug("***");
            log.debug("field name : {}", field.getName());
            log.debug("field type : {}", field.getType());

            // 문자열일 경우에만 치환
            if (field.getType() == String.class) {
                String getMethodName = "get" + field.getName()
                                                    .substring(0, 1)
                                                    .toUpperCase() + field.getName()
                                                                          .substring(1);
                String setMethodName = "set" + field.getName()
                                                    .substring(0, 1)
                                                    .toUpperCase() + field.getName()
                                                                          .substring(1);

                field.setAccessible(true);

                Method getMethod = parameter.getClass().getMethod(getMethodName);
                Method setMethod = parameter.getClass().getMethod(setMethodName, String.class);

                log.debug("before field value : {}", getMethod.invoke(parameter));

                if (getMethod.invoke(parameter) != null) {
                    String replaceString = filter.doFilter((String)getMethod.invoke(parameter));

                    // lucy filter 에서 생성한 주석 제거 확인
                    if (annotation.removeCommentTag()) {
                        replaceString = StringUtils.replace(StringUtils.replace(replaceString,
                                                                                EscapeHTML.COMMENT_TAG,
                                                                                ""),
                                                            EscapeHTML.COMMENT_ATTRIBUTE, "");
                    }

                    setMethod.invoke(parameter, replaceString);

                    log.debug("after field value : {}", getMethod.invoke(parameter));
                }
            } else if (field.getType().isArray()) {
                field.setAccessible(true);
                Object[] fieldArray = (Object[])field.get(parameter);
                if (fieldArray != null) {
                    for (Object o : fieldArray) {
                        doEscape(o);
                    }
                }
            } else if (List.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                List<Object> fieldList = (List<Object>)field.get(parameter);
                if (fieldList != null) {
                    for (Object o : fieldList) {
                        doEscape(o);
                    }
                }
            }
        }
    }

    /**
     * 별도의 {@link #configFileName}이 설정된 경우 해당 파일을 이용하여 {@link XssFilter} 인스턴스 생성.
     */
    private void checkFilter() {
        if (filter != null) {
            return;
        }

        if (StringUtils.isNotEmpty(configFileName)) {
            filter = XssFilter.getInstance(configFileName);
        } else {
            URL configUrl = getClass().getClassLoader().getResource(DEFAULT_CONFIG);

            if (configUrl != null) {
                String fullPath = configUrl.getPath();
                filter = XssFilter.getInstance(fullPath);
            } else {
                throw new NullPointerException();
            }
        }
    }
}
