package net.cliff3.maven.security;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import net.cliff3.maven.common.util.crypto.CryptoUtil;
import net.cliff3.maven.security.model.AbstractUser;
import org.apache.commons.lang3.StringUtils;

/**
 * 기본적으로 제공하는 사용자 관련 service method.<br>
 * 이를 상속받아 구현하는 UserDetailManager를 Spring security 인증 처리 로직에서 사용함.
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
public abstract class AbstractUserDetailService {
    /**
     * 사용자 일련번호를 이용하여 조회
     *
     * @param sequence 사용자 일련번호
     *
     * @return 사용자 DTO
     * @see AbstractUser
     */
    public abstract AbstractUser getUser(Long sequence);

    /**
     * 사용자 아이디를 이용하여 조회
     *
     * @param id 사용자 아이디
     *
     * @return 사용자 DTO
     * @see AbstractUser
     */
    public abstract AbstractUser getUser(String id);

    /**
     * * 아이디/비밀번호를 이용하여 조회
     *
     * @param id       사용자 아이디
     * @param password 사용자 비밀번호
     *
     * @return 사용자 DTO
     * @throws UsernameNotFoundException {@link UsernameNotFoundException} 참고
     */
    public abstract AbstractUser getUser(String id, String password) throws UsernameNotFoundException;

    /**
     * 평문 비밀번호를 암호화 하여 반환한다.
     *
     * @param password 대상 비밀번호 문자열
     *
     * @return 암호화된 비밀번호
     * @throws UnsupportedEncodingException 인코딩 오류
     * @throws NoSuchAlgorithmException     알고리즘 오류
     * @see CryptoUtil#makeSHA256Hash(String)
     */
    public final String encryptPassword(String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if (StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("암호화 대상 비밀번호가 없음");
        }

        Optional<byte[]> result = CryptoUtil.makeSHA256Hash(password);

        if (result.isPresent()) {
            return new String(result.get(), StandardCharsets.UTF_8);
        } else {
            throw new UnsupportedEncodingException("암호화 처리 오류");
        }
    }

    /**
     * 평문 비밀번호를 암호화 하여 반환한다.
     * <p>
     * 주어진 반복 횟수를 이용하여 암호화 후 반환한다.
     * </p>
     *
     * @param password    대상 비밀번호 문자열
     * @param repeatCount 반복 횟수
     *
     * @return 암호화된 비밀번호
     * @throws UnsupportedEncodingException 인코딩 오류
     * @throws NoSuchAlgorithmException     알고리즘 오류
     * @see CryptoUtil#makeSHA256Hash(String, byte[], int)
     */
    public final String encryptPassword(String password, int repeatCount)
        throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if (StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("암호화 대상 비밀번호가 없음");
        }

        Optional<byte[]> result = CryptoUtil.makeSHA256Hash(password, null, repeatCount);

        if (result.isPresent()) {
            return new String(result.get(), StandardCharsets.UTF_8);
        } else {
            throw new UnsupportedEncodingException("암호화 처리 오류");
        }
    }
}
