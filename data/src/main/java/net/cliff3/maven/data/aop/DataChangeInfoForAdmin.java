package net.cliff3.maven.data.aop;

import net.cliff3.maven.security.model.AbstractUser;

/**
 * 데이터 변경 관련 정보 처리
 *
 * @author JoonHo Son
 * @since 0.3.0
 * @see AbstractUser
 */
public interface DataChangeInfoForAdmin<T extends AbstractUser> {
    /**
     * 등록자 반환
     *
     * @return 등록자
     */
    public T getCreatedByAdmin();

    /**
     * 등록자 지정
     *
     * @param user 등록자
     */
    public void setCreatedByAdmin(AbstractUser user);

    /**
     * 수정자 반환
     *
     * @return 수정자
     */
    public T getUpdatedByAdmin();

    /**
     * 수정자 지정
     *
     * @param user 수정자
     */
    public void setUpdatedByAdmin(AbstractUser user);
}
