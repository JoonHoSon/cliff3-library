package net.cliff3.maven.common.util.crypto;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * CryptoUtilTest
 *
 * @author JoonHo Son
 * @since 0.1.0
 */
@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
public class CryptoUtilTest {
    private static final String SOURCE_KOREAN = "한글 원본 입니다.!@#%%23334!!@";

    private static final String SECRET = "this_is_password!@#$%";

    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    @Test
    @Order(1)
    @DisplayName("SHA-256 test")
    public void testSHA256Hash() {
        Optional<byte[]> _result = CryptoUtil.makeSHA256Hash(SOURCE_KOREAN);

        assertTrue(_result.isPresent(), "SHA256 암호화 실패");

        log.debug("encrypted result : {}", Base64.encodeBase64String(_result.get()));
        log.debug("encrypted result length : {}", Base64.encodeBase64String(_result.get()).length());
    }

    @Test
    @Order(2)
    @DisplayName("AES-128 test")
    public void testAES128Encrypt() {
        Optional<AESCrypto> _result = CryptoUtil.encryptAES128(SOURCE_KOREAN, SECRET);

        assertTrue(_result.isPresent(), "AES128 암호화 결과 반환값 없음");

        log.debug("aes crypto encrypted {}", _result.get().getEncrypted());

        AESCrypto _aesCrypto = _result.get();

        final Optional<byte[]> _decrypted = CryptoUtil.decryptAES128(_aesCrypto.getEncrypted(),
                                                                     SECRET,
                                                                     _aesCrypto.getIv(),
                                                                     _aesCrypto.getSalt());

        assertTrue(_decrypted.isPresent(), "복호화 결과 없음");
        assertEquals(new String(_decrypted.get(), UTF_8), SOURCE_KOREAN, "복호화 실패(결과 불일치)");
    }

    @Test
    @Order(3)
    @DisplayName("AES-128 암호화 결과를 base64로 변경 테스트")
    public void testAES128EncryptByBase64() {
        Optional<AESCrypto> _result = CryptoUtil.encryptAES128(SOURCE_KOREAN, SECRET);

        assertTrue(_result.isPresent(), "AES128 암호화 결과 반환값 없음");

        String _encoded = Base64.encodeBase64String(_result.get().getEncrypted());

        Optional<byte[]> _decrypted = CryptoUtil.decryptAES128ByBase64(_encoded,
                                                                       SECRET,
                                                                       _result.get().getIv(),
                                                                       _result.get().getSalt());

        assertTrue(_decrypted.isPresent(), "복호화 결과 없음(normal base64 string)");
        assertEquals(new String(_decrypted.get(), UTF_8), SOURCE_KOREAN, "복호화 실패(normal base64 string: 결과 불일치)");

        log.debug("decrypted string : {}", new String(_decrypted.get(), UTF_8));

        _encoded = Base64.encodeBase64URLSafeString(_result.get().getEncrypted());

        _decrypted = CryptoUtil.decryptAES128ByBase64(_encoded, SECRET, _result.get().getIv(), _result.get().getSalt());

        assertTrue(_decrypted.isPresent(), "복호화 결과 없음(url safe base64 string)");
        assertEquals(new String(_decrypted.get(), UTF_8), SOURCE_KOREAN, "복호화 실패(url safe base64 string: 결과 불일치)");
    }

    @Test
    @Order(4)
    @DisplayName("AES-256 test")
    public void testAES256Encrypt() {
        Optional<AESCrypto> _result = CryptoUtil.encryptAES256(SOURCE_KOREAN, SECRET);

        assertTrue(_result.isPresent(), "AES256 암호화 결과 반환값 없음");

        log.debug("aes crypto encrypted {}", _result.get().getEncrypted());

        AESCrypto _aesCrypto = _result.get();

        final Optional<byte[]> _decrypted = CryptoUtil.decryptAES256(_aesCrypto.getEncrypted(),
                                                                     SECRET,
                                                                     _aesCrypto.getIv(),
                                                                     _aesCrypto.getSalt());

        assertTrue(_decrypted.isPresent(), "복호화 결과 없음");
        assertEquals(new String(_decrypted.get(), UTF_8), SOURCE_KOREAN, "복호화 실패(결과 불일치)");
    }

    @Test
    @Order(5)
    @DisplayName("AES-256 암호화 결과를 base64로 변경 테스트")
    public void testAES256EncryptByBase64() {
        Optional<AESCrypto> _result = CryptoUtil.encryptAES256(SOURCE_KOREAN, SECRET);

        assertTrue(_result.isPresent(), "AES128 암호화 결과 반환값 없음");

        String _encoded = Base64.encodeBase64String(_result.get().getEncrypted());

        Optional<byte[]> _decrypted = CryptoUtil.decryptAES256ByBase64(_encoded,
                                                                       SECRET,
                                                                       _result.get().getIv(),
                                                                       _result.get().getSalt());

        assertTrue(_decrypted.isPresent(), "복호화 결과 없음(normal base64 string)");
        assertEquals(new String(_decrypted.get(), UTF_8), SOURCE_KOREAN, "복호화 실패(normal base64 string: 결과 불일치)");

        _encoded = Base64.encodeBase64URLSafeString(_result.get().getEncrypted());

        _decrypted = CryptoUtil.decryptAES256ByBase64(_encoded, SECRET, _result.get().getIv(), _result.get().getSalt());

        assertTrue(_decrypted.isPresent(), "복호화 결과 없음(url safe base64 string)");
        assertEquals(new String(_decrypted.get(), UTF_8), SOURCE_KOREAN, "복호화 실패(url safe base64 string: 결과 불일치)");
    }

    @Test
    @Order(6)
    @DisplayName("RSA encrypt test")
    public void testRSAEncrypt() {
        KeyPair _keyPair = CryptoUtil.generateRSAKeyPair();
        Optional<byte[]> _encrypted = CryptoUtil.encryptRSA(SOURCE_KOREAN.getBytes(UTF_8),
                                                            _keyPair.getPublic().getEncoded());

        assertTrue(_encrypted.isPresent(), "RSA 암호화 결과 반환값 없음");

        Optional<byte[]> _decrypted = CryptoUtil.decryptRSA(_encrypted.get(), _keyPair.getPrivate().getEncoded());

        assertTrue(_decrypted.isPresent(), "복호화 결과 반환값 없음");

        String _decryptedString = new String(_decrypted.get(), UTF_8);

        assertEquals(_decryptedString, SOURCE_KOREAN, "RSA 복호화 결과 불일치");
    }

    @Test
    @Order(7)
    @DisplayName("RSAKeySet을 이용한 복호화 테스트")
    public void testRSAWithKeySet() {
        Optional<RSAKeySet> _encrypted = CryptoUtil.encryptRSA(SOURCE_KOREAN.getBytes(UTF_8), true);

        assertTrue(_encrypted.isPresent(), "RSA 암호화 결과 반환값 없음");

        RSAKeySet _rsaKeySet = _encrypted.get();

        assertTrue(StringUtils.isNotBlank(_rsaKeySet.getPublicKeyModulus()), "RSA 공개키 계수값 없음");
        assertTrue(StringUtils.isNotBlank(_rsaKeySet.getPublicKeyExponent()), "RSA 공개키 지수값 없음");
        assertTrue(StringUtils.isNotBlank(_rsaKeySet.getPublicKeyString()), "RSA 공개키 base64 문자열 없음");
        assertTrue(StringUtils.isNotBlank(_rsaKeySet.getPrivateKeyModulus()), "RSA 비밀키 계수값 없음");
        assertTrue(StringUtils.isNotBlank(_rsaKeySet.getPrivateKeyExponent()), "RSA 비밀키 지수값 없음");
        assertTrue(StringUtils.isNotBlank(_rsaKeySet.getPrivateKeyString()), "RSA 비밀키 base64 문자열 없음");

        Optional<byte[]> _decrypted = CryptoUtil.decryptRSA(_encrypted.get().getEncryptedValue(), _encrypted.get());

        assertTrue(_decrypted.isPresent(), "RSA 복호화 결과 반환값 없음");

        String _decryptedString = new String(_decrypted.get(), UTF_8);

        assertEquals(_decryptedString, SOURCE_KOREAN, "RSA 복호화 결과 불일치");
    }
}
