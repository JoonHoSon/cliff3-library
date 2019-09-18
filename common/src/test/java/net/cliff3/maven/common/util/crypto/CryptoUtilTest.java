package net.cliff3.maven.common.util.crypto;

import static org.testng.Assert.*;

import net.cliff3.maven.common.util.CryptoException;
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
}
