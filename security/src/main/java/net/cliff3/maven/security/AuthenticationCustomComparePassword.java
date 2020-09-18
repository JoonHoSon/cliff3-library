package net.cliff3.maven.security;

/**
 * 비밀번호 수동 비교 처리<br>
 * {@link DefaultMemberAuthenticationProvider}에서 비밀번호 비교 처리를 수동으로 구현하도록 지정한다.<br>
 * 해당 메서드를 구현, 결과를 <strong>Boolean</strong> 형태로 반환하면, {@link DefaultMemberAuthenticationProvider}
 * 에서는 이 기능을 우선으로 처리한다.
 *
 * @author JoonHo Son
 * @since 1.0.0
 */
public interface AuthenticationCustomComparePassword {
    /**
     * 입력된 비밀번호와 조회한 비밀번호를 비교하여 결과를 <strong>Boolean</strong>형태로 반환한다.
     *
     * @param enteredPassword  사용자가 입력한 비밀번호. 보통은 평문.
     * @param selectedPassword DB등에서 조회한 저장된 비밀번호
     *
     * @return 일치 여부
     */
    public Boolean comparePassword(String enteredPassword, String selectedPassword);
}
