package net.cliff3.maven.data.aop;

import net.cliff3.maven.security.model.AbstractUser;

/**
 * 사용자 정보 반환
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
public interface DefaultControllerForUser {
    /**
     * 현재 사용자 정보를 반환
     * <p>
     * 주어진 인자에 따라 DB로부터 새로운 사용자 정보를 호출할 수 있다. 인자가 true일 경우에는 현재 세션에 저장된 사용자 정보를 무시하고 DB에서
     * 새로운 사용자 정보를 조회하며, false일 경우 현재 세션에 저장된 사용자 정보를 조회하여 반환한다.
     * </p>
     *
     * @param forceReloadInCurrentSession 현재 세션에 저장된 정보가 아닌 DB로 부터 새로운 사용자 정보를 호출할지 여부
     *
     * @return 사용자
     */
    AbstractUser getCurrentUser(boolean forceReloadInCurrentSession);

    /**
     * 현재 사용자 정보를 반환
     * <p>
     * {@link #getCurrentUser(boolean)} 메서드의 인자를 false로 전달하여 처리.
     * </p>
     *
     * @return 사용자
     * @see #getCurrentUser(boolean)
     */
    AbstractUser getCurrentUser();
}
