package net.cliff3.maven.common.util.crypto

/**
 * AES 암호화 결과.
 *
 * @author JoonHo Son
 * @version 1.0.0 2019-09-09 최초 작성
 * @since 1.0.0
 */
class AESCrypto(salt: ByteArray, encrypted: ByteArray, iv: ByteArray) {
    /**
     * Salt
     */
    var salt: ByteArray = salt
        private set

    /**
     * 암호화된 결과.
     */
    var encrypted: ByteArray = encrypted
        private set

    /**
     * Initialize vector
     */
    var iv: ByteArray = iv
        private set
}