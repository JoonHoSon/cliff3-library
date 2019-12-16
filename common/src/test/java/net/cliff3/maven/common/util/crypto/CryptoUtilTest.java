package net.cliff3.maven.common.util.crypto;

import static org.testng.Assert.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.testng.annotations.Test;

/**
 * CryptoUtilTest
 *
 * @author JoonHo Son
 * @version 1.0.0 2019-12-16 최초 작성
 * @since 1.0.0
 */
@Slf4j
@Test(groups = "cryptoUtilTest")
public class CryptoUtilTest {
    private static final String SOURCE_KOREAN = "한글 원본 입니다.!@#%%23334!!@";

    private static final String SECRET = "this_is_password!@#$%";

    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    @Test
    public void testSHA256Hash() {
        Optional<byte[]> _result = CryptoUtil.makeSHA256Hash(SOURCE_KOREAN);

        assertTrue(_result.isPresent(), "SHA256 암호화 실패");
    }

    @Test
    public void testAES128Encrypt() {
        Optional<AESCrypto> _result = CryptoUtil.makeAES128Encrypt(SOURCE_KOREAN, SECRET);

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
    public void testAES128EncryptByBase64() {
        Optional<AESCrypto> _result = CryptoUtil.makeAES128Encrypt(SOURCE_KOREAN, SECRET);

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
    public void testAES256Encrypt() {
        Optional<AESCrypto> _result = CryptoUtil.makeAES256Encrypt(SOURCE_KOREAN, SECRET);

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
    public void testAES256EncryptByBase64() {
        Optional<AESCrypto> _result = CryptoUtil.makeAES256Encrypt(SOURCE_KOREAN, SECRET);

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
}
