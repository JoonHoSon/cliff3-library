package net.cliff3.maven.security;

import java.util.Base64;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;

import net.cliff3.maven.common.util.crypto.CryptoUtil;

/**
 * Spring security에서 사용하는 password encoder. SHA-256으로 처리.
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
public class DefaultPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return encodePassword(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String encodedRawPassword = encodePassword(rawPassword);

        return encodedPassword.equals(encodedRawPassword);
    }

    /**
     * 전달된 문자열을 {@link CryptoUtil#makeSHA256Hash(String)}을 이용하여 암호화 한 후
     * {@link  Base64.Encoder#encodeToString(byte[])}를 이용하여 base64 처리 후 반환
     *
     * @param rawPassword 평문 비밀번호
     *
     * @return 암호화된 결과
     */
    private String encodePassword(CharSequence rawPassword) {
        Optional<byte[]> result = CryptoUtil.makeSHA256Hash(String.valueOf(rawPassword));

        if (result.isPresent()) {
            return Base64.getEncoder().encodeToString(result.get());
        } else {
            throw new RuntimeException("비밀번호 인코딩 실패");
        }
    }
}
