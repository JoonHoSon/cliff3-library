package net.cliff3.maven.data.aop;

import net.cliff3.maven.security.model.AbstractUser;

/**
 * 데이터 변경 관련 정보 처리.
 *
 * @author JoonHo Son
 * @see AbstractUser
 * @since 0.3.0
 */
public interface DataChangeInfoForUser<T extends AbstractUser> {
    /**
     * 등록자 반환
     *
     * @return 등록자
     */
    public T getCreatedByUser();

    /**
     * 등록자 지정
     *
     * @param user 등록자
     */
    public void setCreatedByUser(AbstractUser user);

    /**
     * 수정자 반환
     *
     * @return 수정자
     */
    public T getUpdatedByUser();

    /**
     * 수정자 지정
     *
     * @param user 수정자
     */
    public void setUpdatedByUser(AbstractUser user);
}
