package net.cliff3.maven.data.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import lombok.extern.slf4j.Slf4j;
import net.cliff3.maven.security.model.AbstractUser;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * 작성자/수정자의 정보를 처리한다. 이를 위해서는 {@link DefaultControllerForUser}를 구현해야 한다.
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
@Slf4j
public class DefaultDataChangeBeforeAdvice {
    /**
     * Default constructor
     */
    public DefaultDataChangeBeforeAdvice() {
    }

    /**
     * Before Advice
     *
     * @param point JoinPoint
     */
    public void processBefore(JoinPoint point) {
        Method method = ((MethodSignature)point.getSignature()).getMethod();
        Class<?> clazz = method.getDeclaringClass();
        Object[] args = point.getArgs();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        log.debug("parameter annotation length : {}", parameterAnnotations.length);

        for (Annotation[] aa : parameterAnnotations) {
            for (Annotation bb : aa) {
                log.debug("@ >>> {}", bb);
            }
        }


        log.debug("------------------------------------------------------------------------");
        log.debug("start dataChangeInfo before advice");
        log.debug("clazz : {}", method.getDeclaringClass());

        if (DefaultControllerForUser.class.isAssignableFrom(clazz) && args != null) {
            // NGControllerForUser 구현 했음
            for (Object parameter : args) {
                // 인자의 순서 확인
                int index = ArrayUtils.indexOf(args, parameter);
                DataTarget an = null;

                if (parameterAnnotations.length > index && parameterAnnotations[index].length > 0) {
                    for (Annotation aa : parameterAnnotations[index]) {
                        if (aa.annotationType().equals(DataTarget.class)) {
                            an = (DataTarget)aa;

                            break;
                        }
                    }
                }

                if (an != null) {
                    log.debug("data change target for user : {}", point.getTarget());

                    processApplyDataTargets(parameter, (DefaultControllerForUser)point.getTarget(), an);
                }
            }
        }

        log.debug("end dataChangeInfo before advice");
        log.debug("------------------------------------------------------------------------");
    }

    /**
     * {@link DataTarget}을 이용하여 사용자/관리자 정보를 객체에 저장한다.
     *
     * @param parameter {@link AbstractUser}를 구현한 사용자/관리자등 객체
     * @param clazz     {@link DefaultControllerForUser}를 구현한 컨트롤러
     * @param target    {@link DataTargetAdmin} 혹은 {@link DataTargetUser}를 대상 값으로 가지는 {@link DataTarget}
     *
     * @see AbstractUser
     * @see DefaultControllerForUser
     * @see DataTargetBase
     * @see DataTargetAdmin
     * @see DataTargetUser
     */
    private void processApplyDataTargets(Object parameter, DefaultControllerForUser clazz, DataTarget target) {
        log.debug("start processApplyDataTargets >>>");

        AbstractUser user = clazz.getCurrentUser(false);

        for (Class<? extends DataTargetBase> toInterface : target.targets()) {
            log.debug("toInterface : {}", toInterface.getName());

            if (toInterface == DataTargetAdmin.class && DataChangeInfoForAdmin.class.isAssignableFrom(parameter.getClass())) {
                try {
                    DataChangeInfoForAdmin<? extends AbstractUser> _parameter = (DataChangeInfoForAdmin<? extends AbstractUser>)parameter;

                    _parameter.setCreatedByAdmin(user);
                    _parameter.setUpdatedByAdmin(user);

                    log.debug("applied to admin.");
                } catch (ClassCastException ignored) {
                }
            } else if (toInterface == DataTargetUser.class && DataChangeInfoForUser.class.isAssignableFrom(parameter.getClass())) {
                try {
                    DataChangeInfoForUser<? extends AbstractUser> _parameter = (DataChangeInfoForUser<? extends AbstractUser>)parameter;

                    _parameter.setCreatedByUser(user);
                    _parameter.setUpdatedByUser(user);

                    log.debug("applied to user.");
                } catch (ClassCastException ignore) {
                }
            }
        }
    }
}
