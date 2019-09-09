package net.cliff3.maven.common.util.crypto

import org.apache.commons.codec.binary.Base64

/**
 * AES 암호화 결과. 모든 값은 [Base64.encodeBase64URLSafeString]로 인코딩 되어 문자열로 저장되며
 * 필요할 경우 해당 값을 [Base64.decodeBase64]로 변환하여야 한다.
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
     * Salt 문자열. [Base64.encodeBase64URLSafeString] 처리 됨.
     */
    lateinit var saltString: String
        private set

    /**
     * 암호화된 결과.
     */
    var encrypted: ByteArray = encrypted
        private set

    /**
     * 암호화된 결과 문자열. [Base64.encodeBase64URLSafeString] 처리 됨.
     */
    lateinit var encryptedString: String
        private set

    /**
     * Initialize vector
     */
    var iv: ByteArray = iv
        private set

    /**
     * Initialize vector 문자열. [Base64.encodeBase64URLSafeString] 처리 됨.
     */
    lateinit var ivString: String
        private set

    init {
        when {
            salt.isEmpty() -> throw NullPointerException("Empty salt")
            else -> {
                saltString = Base64.encodeBase64URLSafeString(salt)
            }
        }

        when {
            encrypted.isEmpty() -> throw NullPointerException("Empty encrypted result.")
            else -> {
                encryptedString = Base64.encodeBase64URLSafeString(encrypted)
            }
        }

        when {
            iv.isEmpty() -> throw NullPointerException("Empty iv key.")
            else -> {
                ivString = Base64.encodeBase64URLSafeString(iv)
            }
        }
    }
}