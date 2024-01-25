package net.cliff3.maven.data.aop;

import net.cliff3.maven.security.model.AbstractUser;

/**
 * 데이터 변경 관련 정보 처리. {@link AbstractUser}를 상속한 사용자 정보를 반환 및 할당한다.
 *
 * @author JoonHo Son
 * @see AbstractUser
 * @see DefaultControllerForUser
 * @see DataTargetAdmin
 * @see DataTargetUser
 * @see DefaultDataChangeBeforeAdvice
 * @since 0.3.0
 */
public interface DataChangeInfoForAdmin<T extends AbstractUser> {
    /**
     * 등록자 반환
     *
     * @return 등록자
     */
    T getCreatedByAdmin();

    /**
     * 등록자 지정
     *
     * @param user 등록자
     */
    void setCreatedByAdmin(AbstractUser user);

    /**
     * 수정자 반환
     *
     * @return 수정자
     */
    T getUpdatedByAdmin();

    /**
     * 수정자 지정
     *
     * @param user 수정자
     */
    void setUpdatedByAdmin(AbstractUser user);
}
