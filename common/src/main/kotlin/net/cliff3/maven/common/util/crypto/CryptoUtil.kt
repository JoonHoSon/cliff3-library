package net.cliff3.maven.common.util.crypto

import org.apache.commons.lang3.StringUtils
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.AlgorithmParameters
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


enum class Transformation(val transformation: String) {
    /**
     * RSA/ECB/PKCS1Padding
     */
    RSA_ECB_PKCS1PADDING("RSA/ECB/PKCS1Padding"),

    /**
     * AES/CBC/PKCS5Padding
     */
    AES_CBC_PKCS5PADDING("AES/CBC/PKCS5Padding"),

    /**
     * [Transformation.RSA_ECB_PKCS1PADDING]와 동일
     */
    RSA(Transformation.RSA_ECB_PKCS1PADDING.transformation)
}

enum class LoadKeyType {
    /**
     * 공개키
     */
    PUBLIC_KEY,

    /**
     * 개인키
     */
    PRIVATE_KEY
}

class CryptoUtil {
    /**
     * 기본 인코딩/디코딩 [StandardCharsets]
     */
    private val CHAR_SET: Charset = StandardCharsets.UTF_8

    /**
     * Algorithm(RSA)
     */
    private val ALGORITHM_RSA = "RSA"

    /**
     * 운용모드(CBC)
     */
    private val OPERATION_MODE_CBC = "CBC"

    /**
     * 운용모드(ECB)
     */
    private val OPERATION_MODE_ECB = "ECB"

    /**
     * Padding 방식(PKCS5Padding)
     */
    private val PADDING_PKCS5PADDING = "PKCS5Padding"

    /**
     * Padding 방식(PKCS1Padding)
     */
    private val PADDING_PKCS1PADDING = "PKCS1Padding"

    /**
     * Padding 방식(없음)
     */
    private val PADDING_NONE = "NoPadding"

    /**
     * 기본 반복 횟수
     */
    private val DEFAULT_REPEAT_COUNT = 1_000

    /**
     * 128 키 사이즈
     */
    private val KEY_128: Int = 128

    /**
     * 256 키 사이즈
     */
    private val KEY_256 = 256

    /**
     * SHA256 hash 처리. 대상 문자열이 없을 경우 **null**을 반환하며 [NoSuchAlgorithmException]
     * 발생시 [CryptoException] 예외를 발생시킨다.
     *
     * @param target      Hash 처리 대상 문자열
     * @param salt        salt
     * @param repeatCount 반복 횟수. 기본값은 [DEFAULT_REPEAT_COUNT]
     *
     * @return Hash 처리된 결과
     * @throws CryptoException [NoSuchAlgorithmException] 예외 발생시
     */
    @Throws(CryptoException::class)
    fun makeSHA256Hash(target: String, salt: ByteArray?, repeatCount: Int = DEFAULT_REPEAT_COUNT): ByteArray? {
        try {
            if (StringUtils.isBlank(target)) {
                return null
            }
            val digest: MessageDigest = MessageDigest.getInstance("SHA-256")

            digest.reset()

            var convertedTarget: ByteArray = digest.digest(target.toByteArray(CHAR_SET))

            if (salt != null) {
                digest.update(salt)
            }

            for (i in 0..<repeatCount) {
                digest.reset()

                convertedTarget = digest.digest(convertedTarget)
            }

            return convertedTarget
        } catch (e: Throwable) {
            throw CryptoException("SHA256 Hash 처리 실패", e)
        }
    }

    /**
     * [makeSHA256Hash] 참고. 반복횟수는 [DEFAULT_REPEAT_COUNT]로 설정된다.
     *
     * @param target Hash 처리 대상 문자열
     *
     * @return Hash 처리된 결과
     * @throws CryptoException [NoSuchAlgorithmException] 예외 발생시
     * @see [makeSHA256Hash]
     */
    @Throws(CryptoException::class)
    fun makeSHA256Hash(target: String): ByteArray? {
        return makeSHA256Hash(target, null, DEFAULT_REPEAT_COUNT)
    }

    /**
     * [makeSHA256Hash] 참고. 반복횟수는 [DEFAULT_REPEAT_COUNT]로 설정된다.
     *
     * @param target Hash 처리 대상 문자열
     * @param salt salt
     *
     * @return Hash 처리된 결과
     * @throws CryptoException [NoSuchAlgorithmException] 예외 발생시
     * @see [makeSHA256Hash]
     */
    @Throws(CryptoException::class)
    fun makeSHA256Hash(target: String, salt: ByteArray): ByteArray? {
        return makeSHA256Hash(target, salt)
    }

    /**
     * AES 암호화 처리. 인자로 지정된 keySize 기반으로 암호화 처리를 하며, 해당 결과는 [AESResult] 인스턴스로 반환한다.
     * 대상 문자열이 존재하지 않을 경우 null을 반환한다.
     *
     * @param target  대상 문자열
     * @param secret  암호화키
     * @param keySize 키 사이즈
     * @param salt    salt
     * @param repeatCount 반복 횟수
     *
     * @return 처리 결과
     * @throws CryptoException 암호화 처리 중 발생
     * @see AESCrypto
     * @see [java.io.UnsupportedEncodingException]
     * @see [NoSuchAlgorithmException]
     * @see [java.security.spec.InvalidKeySpecException]
     * @see [javax.crypto.NoSuchPaddingException]
     * @see [java.security.InvalidKeyException]
     * @see [java.security.spec.InvalidParameterSpecException]
     * @see [javax.crypto.BadPaddingException]
     * @see [javax.crypto.IllegalBlockSizeException]
     */
    @Throws(CryptoException::class)
    private fun encryptAES(
        target: String,
        secret: String,
        keySize: Int,
        salt: ByteArray,
        repeatCount: Int = DEFAULT_REPEAT_COUNT
    ): AESResult? {
        try {
            if (StringUtils.isBlank(target)) {
                return null
            }

            val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val keySpec: KeySpec = PBEKeySpec(secret.toCharArray(), salt, repeatCount, keySize)
            val secretKey: SecretKey = factory.generateSecret(keySpec)
            val secretKeySpec: SecretKeySpec = SecretKeySpec(secretKey.encoded, "AES")
            val cipher: Cipher = Cipher.getInstance(Transformation.AES_CBC_PKCS5PADDING.transformation)

            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)

            val parameter: AlgorithmParameters = cipher.parameters
            val ivBytes: ByteArray = parameter.getParameterSpec(IvParameterSpec::class.java).iv
            val encrypted: ByteArray = cipher.doFinal(target.toByteArray(CHAR_SET))

            return AESResult(salt, encrypted, ivBytes, repeatCount)
        } catch (e: Throwable) {
            throw CryptoException("AES 암호화 오류[keySize: $keySize]", e)
        }
    }

    /**
     * AES 복호화 처리. 복호화 결과를 {@code byte[]} 형태로 반환한다.
     *
     * @param target  복호화 대상
     * @param secret  암호화키
     * @param ivBytes initial vector
     * @param salt    salt
     * @param keySize 키 사이즈
     * @param repeatCount 반복 횟수
     *
     * @return 처리 결과
     * @throws CryptoException 복호화 처리시
     * @see [NoSuchAlgorithmException]
     * @see [java.security.spec.InvalidKeySpecException]
     * @see [javax.crypto.NoSuchPaddingException]
     * @see [java.security.InvalidKeyException]
     * @see [java.security.spec.InvalidParameterSpecException]
     */
    private fun decryptAES(
        target: ByteArray,
        secret: String,
        keySize: Int,
        iv: ByteArray,
        salt: ByteArray,
        repeatCount: Int = DEFAULT_REPEAT_COUNT
    ): ByteArray {
        try {
            val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec: PBEKeySpec = PBEKeySpec(secret.toCharArray(), salt, repeatCount, keySize)
            val secretKey: SecretKey = factory.generateSecret(spec)
            val secretKeySpec: SecretKeySpec = SecretKeySpec(secretKey.encoded, "AES")
            val cipher: Cipher = Cipher.getInstance(Transformation.AES_CBC_PKCS5PADDING.transformation)

            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, IvParameterSpec(iv))

            return cipher.doFinal(target)
        } catch (e: Throwable) {
            throw CryptoException("AES 복호화 오류[keySize: $keySize]", e)
        }
    }

    /**
     * 임의의  salt 생성
     *
     * @return salt
     */
    fun generateSalt(): ByteArray {
        val result = ByteArray(30)

        SecureRandom().nextBytes(result)

        return result
    }

    /**
     * AES 128 암호화. [encryptAES] 참고.
     *
     * @param target 대상 문자열
     * @param secret 암호화키
     * @param salt salt
     * @param repeatCount 반복 횟수
     *
     * @return 처리 결과
     * @throws CryptoException 암호화 처리 중 발생
     * @see [encryptAES]
     */
    fun encryptAES128(
        target: String,
        secret: String,
        salt: ByteArray = generateSalt(),
        repeatCount: Int = DEFAULT_REPEAT_COUNT
    ): AESResult? {
        return encryptAES(target, secret, KEY_128, salt, repeatCount)
    }

    /**
     * AES 256 암호화. [encryptAES] 참고.
     *
     * @param target 대상 문자열
     * @param secret 암호화키
     * @param salt salt
     * @param repeatCount 반복 횟수
     *
     * @return 처리 결과
     * @throws CryptoException 암호화 처리 중 발생
     * @see [encryptAES]
     */
    fun encryptAES256(
        target: String,
        secret: String,
        salt: ByteArray = generateSalt(),
        repeatCount: Int = DEFAULT_REPEAT_COUNT
    ): AESResult? {
        return encryptAES(target, secret, KEY_256, salt, repeatCount)
    }
}