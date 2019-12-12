package net.cliff3.maven.common.util.crypto;

import static org.testng.Assert.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

/**
 * CryptoUtilTest
 *
 * @author JoonHo Son
 * @version 1.0.0 2019-09-09 최초 작성
 * @since 1.0.0
 */
public class CryptoUtilTest {
    private Logger logger = LoggerFactory.getLogger(CryptoUtilTest.class);

    private final String source = "한글 입력값abc123&";

    private final String password = "비밀번호123";

    private final String salt = "이건 salt";

    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    @Test
    public void sha256Test() throws CryptoException {
        final String result = new String(CryptoUtil.makeSHA256Hash("123"));

        logger.debug("hash result : {}", result);

        assertNotNull(result, "SHA256 hashing 실패");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void sha256ExceptionTest() throws CryptoException {
        final String result = new String(CryptoUtil.makeSHA256Hash(""));

        assertNotNull(result, "SHA256 hashing 실패");
    }

    @Test
    public void aes128Test() throws CryptoException {
        AESCrypto result = CryptoUtil.makeAES128Encrypt(source, password);

        assertNotNull(result, "암호화 결과 반환 실패");

        String decryptResult = new String(CryptoUtil.decryptAES128(result.getEncrypted(),
                                                                   password,
                                                                   result.getIv(),
                                                                   result.getSalt()));

        assertNotNull(decryptResult, "복호화 실패");
        assertEquals(decryptResult, source, "복호화 결과 불일치");

        logger.debug("복호화 문자열 : {}", decryptResult);

        result = CryptoUtil.makeAES128Encrypt(source, password, salt.getBytes());

        assertNotNull(result, "암호화 결과 반환 실패(사용자 정의 salt)");

        decryptResult = new String(CryptoUtil.decryptAES128(result.getEncrypted(),
                                                            password,
                                                            result.getIv(),
                                                            salt.getBytes()));

        assertNotNull(decryptResult, "복호화 실패(사용자 정의 salt)");
        assertEquals(decryptResult, source, "복호화 결과 불일치(사용자 정의 salt)");

        logger.debug("복호화 문자열 : {}", decryptResult);
    }

    @Test
    public void aes256Test() throws CryptoException {
        AESCrypto result = CryptoUtil.makeAES256Encrypt(source, password);

        assertNotNull(result, "암호화 결과 반환 실패");

        String decryptResult = new String(CryptoUtil.decryptAES256(result.getEncrypted(),
                                                                   password,
                                                                   result.getIv(),
                                                                   result.getSalt()));

        assertNotNull(decryptResult, "복호화 실패");
        assertEquals(decryptResult, source, "복호화 결과 불일치");

        logger.debug("복호화 문자열 : {}", decryptResult);

        result = CryptoUtil.makeAES256Encrypt(source, password, salt.getBytes());

        assertNotNull(result, "암호화 결과 반환 실패(사용자 정의 salt)");

        decryptResult = new String(CryptoUtil.decryptAES256(result.getEncrypted(),
                                                            password,
                                                            result.getIv(),
                                                            salt.getBytes()));

        assertNotNull(decryptResult, "복호화 실패(사용자 정의 salt)");
        assertEquals(decryptResult, source, "복호화 결과 불일치(사용자 정의 salt)");

        logger.debug("복호화 문자열 : {}", decryptResult);

        result = CryptoUtil.makeAES256Encrypt(source, password, salt);

        assertNotNull(result, "암호화 결과 반환 실패(사용자 정의 salt)");

        decryptResult = new String(CryptoUtil.decryptAES256(result.getEncrypted(),
                                                            password,
                                                            result.getIv(),
                                                            salt.getBytes()));

        assertNotNull(decryptResult, "복호화 실패(사용자 정의 salt)");
        assertEquals(decryptResult, source, "복호화 결과 불일치(사용자 정의 salt)");

        logger.debug("복호화 문자열 : {}", decryptResult);
    }

    @Test
    public void rsaTest() throws CryptoException {
        final String _source = "테스트 문자열12!@%^&%";
        RSAKeySet _result = CryptoUtil.encryptDataByRSA(_source.getBytes(UTF_8), true);

        assertNotNull(_result, "RSA 암호화 결과 없음");
        assertNotNull(_result.getEncryptedValue(), "RSA 암호화 결과 오류(encryptedValue)");
        assertNotNull(_result.getPrivateKey(), "RSA 암호화 결과 오류(privateKey)");
        assertNotNull(_result.getPrivateKeyExponent(), "RSA 암호화 결과 오류(privateKeyExponent)");
        assertNotNull(_result.getPrivateKeyModulus(), "RSA 암호화 결과 오류(privateKeyModulus)");
        assertNotNull(_result.getPublicKey(), "RSA 암호화 결과 오류(publicKey)");
        assertNotNull(_result.getPublicKeyExponent(), "RSA 암호화 오류(publicKeyExponent)");
        assertNotNull(_result.getPublicKeyModulus(), "RSA 암호화 오류(publicKeyModulus)");
        assertTrue(_result.getToStringKey() != null && _result.getToStringKey(), "RSA 암호화 오류(toStringKey)");

        byte[] _decryptedSource = CryptoUtil.decryptDataByRSA(_result.getEncryptedValue(), _result);

        assertNotNull(_decryptedSource, "RSA 복호화 실패");

        String _convertedResult = new String(_decryptedSource, UTF_8);

        assertNotNull(_convertedResult, "RSA 복호화 오류(문자열 변환 실패)");
        assertEquals(_convertedResult,_source,"RSA 복호화 오류(복호화 결과 불일치)");

    }

    @Test
    public void rsaTestWithKeySets() throws CryptoException {
        final String _source = "월급루팡. 일 안하고 너는 하루 종일 뭐하니?";
        RSAKeySet _result = CryptoUtil.encryptDataByRSA(_source.getBytes(UTF_8), true);
        final byte[] _rsaResults = CryptoUtil.encryptDataByRSA(_source.getBytes(UTF_8), Base64.decodeBase64(_result.getPublicKeyString()));

        final String decrypt1 = new String(CryptoUtil.decryptDataByRSA(Objects.requireNonNull(_result.getEncryptedValue()), _result));
        final String decrypt2 = new String(CryptoUtil.decryptDataByRSA(_rsaResults, _result));

        assertEquals(decrypt1, decrypt2, "암호화 결과 불일치");
    }
}
