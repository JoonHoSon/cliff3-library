package net.cliff3.maven.data.aop;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.cliff3.maven.data.mybatis.AbstractPageable;
import net.cliff3.maven.data.mybatis.Countable;
import net.cliff3.maven.data.mybatis.Pageable;
import net.cliff3.maven.data.mybatis.Pagination;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * 페이징 처리 after advice. {@link Pageable} annotation 및 {@link Countable}의 구현여부에 따라 출력될 데이터의 번호(순서)를 할당한다.
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
@Slf4j
public class DefaultPageableAfterAdvice extends AbstractPageable {
    public void processAfter(JoinPoint point, Object returnValue) {
        Method method = ((MethodSignature)point.getSignature()).getMethod();
        Object[] args = point.getArgs();

        log.debug("------------------------------------------------------------------------");
        log.debug("start paging after advice");
        log.debug("clazz : {}", method.getDeclaringClass());

        Pageable annotation = method.getAnnotation(Pageable.class);

        if (isPageable(annotation, args)) {
            processPaging(returnValue);
        }

        log.debug("end paging after advice");
        log.debug("------------------------------------------------------------------------");
    }

    @SuppressWarnings("unchecked")
    private void processPaging(Object returnValue) {
        if (returnValue instanceof List) {
            int base = Pagination.totalCount.get() - getFromData() + 1;
            List<?> convertedList = (List<?>)returnValue;

            log.debug("start position idx base : {}", base);

            for (Object target : convertedList) {
                if (target instanceof Countable) {
                    Countable countable = (Countable)target;

                    countable.setPositionIdx(base);
                } else if (returnValue instanceof Map) {
                    ((Map<String, Object>)target).put("positionIdx", base);
                }

                base--;

                log.debug("after base : {}", base);
            }
        }
    }

    private int getFromData() {
        Integer currentPage = Pagination.currentPage.get();
        Integer dataPerPage = Pagination.dataPerPage.get();

        if (currentPage == null) {
            currentPage = 1;
        }

        if (dataPerPage == null) {
            dataPerPage = NG_DEFAULT_DATA_PER_PAGE;
        }

        return (dataPerPage * (currentPage - 1)) + 1;
    }
}
