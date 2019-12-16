package net.cliff3.maven.common.util.crypto;

import lombok.Getter;

/**
 * AES 암호화 결과
 *
 * @author JoonHo Son
 * @version 1.0.0 2019-12-13 최초 작성
 * @since 1.0.0
 */
public class AESCrypto {
    /**
     * Salt
     */
    @Getter
    private byte[] salt;

    /**
     * 암호화된 결과
     */
    @Getter
    private byte[] encrypted;

    /**
     * Initialize vector
     */
    @Getter
    private byte[] iv;

    /**
     * Default constructor
     *
     * @param salt      {@link #salt}
     * @param encrypted {@link #encrypted}
     * @param iv        {@link #iv}
     */
    public AESCrypto(byte[] salt, byte[] encrypted, byte[] iv) {
        this.salt = salt;
        this.encrypted = encrypted;
        this.iv = iv;
    }
}
