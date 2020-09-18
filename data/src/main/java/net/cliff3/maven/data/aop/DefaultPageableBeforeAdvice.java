package net.cliff3.maven.data.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.cliff3.maven.data.mybatis.AbstractPageable;
import net.cliff3.maven.data.mybatis.Pageable;
import net.cliff3.maven.data.mybatis.Pagination;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * 페이징 처리 before advice. {@link Pageable} annotation이 존재할 경우 선행 처리하여 해당 조건의 전체 데이터 건수를 조회하여
 * {@link Pagination}에 저장한다.
 *
 * @author JoonHo Son
 * @see Pageable
 * @see Pagination
 * @since 0.3.0
 */
@Slf4j
@SuppressWarnings("unchecked")
public class DefaultPageableBeforeAdvice extends AbstractPageable {
    /**
     * Default suffix for countMapperID
     */
    public static final String DEFAULT_MAPPER_ID = "PageCount";

    /**
     * Default constructor
     */
    public DefaultPageableBeforeAdvice() {
    }

    /**
     * Before advice
     *
     * @param point {@code JoinPoint}
     *
     * @throws InvocationTargetException count method invoke fail
     * @throws IllegalAccessException    count method access error
     * @throws NoSuchMethodException     count method not found
     */
    public void processBefore(JoinPoint point)
        throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = ((MethodSignature)point.getSignature()).getMethod();
        Class<?> clazz = method.getDeclaringClass();
        Object[] args = point.getArgs();

        log.debug("------------------------------------------------------------------------");
        log.debug("start paging before advice");
        log.debug("clazz : {}", method.getDeclaringClass());

        Pageable annotation = method.getAnnotation(Pageable.class);

        // dao 객체이며 @NGPageable annotation이 존재할 경우
        if (isPageable(annotation, args)) {
            processPreparing(method, point, clazz, args, annotation);
        }

        log.debug("end paging before advice");
        log.debug("------------------------------------------------------------------------");
    }

    /**
     * Count mapper 실행 처리
     *
     * @param method     count mappter를 호출하는 메서드
     * @param point      {@code JoinPoint}
     * @param clazz      현재 클래스 원형
     * @param args       록 조회 메서드에 저달되는 인자 배열
     * @param annotation {@link Pageable}
     *
     * @throws IllegalAccessException    count method invoke fail
     * @throws InvocationTargetException count method access error
     * @throws NoSuchMethodException     count method not found
     */
    private void processPreparing(Method method, JoinPoint point, Class<?> clazz, Object[] args, Pageable annotation)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String methodName = method.getName();

        String countMapperID = annotation.countMapperID();

        if (StringUtils.isEmpty(countMapperID)) {
            countMapperID = methodName + DEFAULT_MAPPER_ID;
        }

        Method countMapperMethod = clazz.getDeclaredMethod(countMapperID, method.getParameterTypes());

        log.debug("invoke count mapper!");
        Integer totalCount = (Integer)countMapperMethod.invoke(point.getTarget(), args);

        Integer currentPage = Pagination.currentPage.get();
        Integer dataPerPage = Pagination.dataPerPage.get();

        if (currentPage == null) {
            currentPage = 1;
        }

        if (dataPerPage == null) {
            dataPerPage = NG_DEFAULT_DATA_PER_PAGE;
        }

        int fromData = (dataPerPage * (currentPage - 1)) + 1;
        int fromIndex = fromData - 1;
        int toData = fromData + dataPerPage - 1;

        Map<String, Object> parameter = (Map<String, Object>)args[0];

        log.debug("currentPage : {}", currentPage);
        log.debug("dataPerPage : {}", dataPerPage);
        log.debug("fromData : {}", fromData);
        log.debug("fromIndex : {}", fromIndex);
        log.debug("toData : {}", toData);

        parameter.put(annotation.fromKey(), fromData);
        parameter.put(annotation.fromIndex(), fromIndex);
        parameter.put(annotation.toKey(), toData);
        parameter.put(NG_DATA_PER_PAGE_KEY, dataPerPage);
        parameter.put(NG_CURRENT_PAGE_KEY, currentPage);

        Pagination.totalCount.set(totalCount);
    }
}
