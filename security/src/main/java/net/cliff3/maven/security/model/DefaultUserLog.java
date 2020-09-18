package net.cliff3.maven.security.model;

/**
 * 접속 로그 인터페이스
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
public interface DefaultUserLog {
    /**
     * 사용자 정보 지정
     *
     * @param user 일련번호 및 아이디를 포함하는 사용자 정보
     */
    public void setUser(AbstractUser user);

    /**
     * 로그인 결과
     *
     * @param resultKey 로그인 결과
     *
     * @see net.cliff3.maven.security.DefaultMemberAuthenticationProvider
     */
    public void setResult(String resultKey);
}
