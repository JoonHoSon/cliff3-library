package net.cliff3.maven.common.util.crypto;

/**
 * CryptoException
 *
 * @author JoonHo Son
 * @version 1.0.0 2019-12-13 최초 작성
 * @since 1.0.0
 */
public class CryptoException extends RuntimeException {
    /**
     * Default constructor. 암호화 대상 문자열이 없을 경우 발생
     */
    public CryptoException() {
        super("암호화 대상 문자열이 없음");
    }

    /**
     * Constructor
     *
     * @param message 오류 내용
     */
    public CryptoException(String message) {
        super(message);
    }

    /**
     * Constructor
     *
     * @param message 오류 내용
     * @param cause   발생 예외
     */
    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }
}
