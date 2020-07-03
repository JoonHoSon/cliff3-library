package net.cliff3.maven.common.util.crypto;

import lombok.Getter;

/**
 * AES 암호화 결과
 *
 * @author JoonHo Son
 * @since 0.1.0
 */
public class AESCrypto {
    /**
     * Salt
     */
    @Getter
    private final byte[] salt;

    /**
     * 암호화된 결과
     */
    @Getter
    private final byte[] encrypted;

    /**
     * Initialize vector
     */
    @Getter
    private final byte[] iv;

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
