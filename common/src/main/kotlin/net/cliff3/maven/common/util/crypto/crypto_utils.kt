package net.cliff3.maven.common.util.crypto

import net.cliff3.maven.common.topLogger
import org.apache.commons.lang3.StringUtils
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.spec.*
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * 암호화 관련 함수 모음.
 *
 * @author JoonHo Son
 * @since 0.3.0
 */

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
    RSA(RSA_ECB_PKCS1PADDING.transformation)
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

/**
 * 기본 인코딩/디코딩 [StandardCharsets]
 */
private val CHAR_SET: Charset = StandardCharsets.UTF_8

/**
 * Algorithm(RSA)
 */
private const val ALGORITHM_RSA = "RSA"

/**
 * Algorithm(SHA-256)
 */
private const val ALGORITHM_SHA_256 = "SHA-256"

/**
 * Algorithm(AES)
 */
private const val ALGORITHM_AES = "AES"

/**
 * Algorithm SecretKeySpec
 */
private const val ALGORITHM_SECRET_FACTORY = "PBKDF2WithHmacSHA1"

/**
 * 운용모드(CBC)
 */
private const val OPERATION_MODE_CBC = "CBC"

/**
 * 운용모드(ECB)
 */
private const val OPERATION_MODE_ECB = "ECB"

/**
 * Padding 방식(PKCS5Padding)
 */
private const val PADDING_PKCS5PADDING = "PKCS5Padding"

/**
 * Padding 방식(PKCS1Padding)
 */
private const val PADDING_PKCS1PADDING = "PKCS1Padding"

/**
 * Padding 방식(없음)
 */
private const val PADDING_NONE = "NoPadding"

/**
 * 기본 반복 횟수
 */
const val DEFAULT_REPEAT_COUNT = 1_000

/**
 * 128 키 사이즈
 */
private const val KEY_128: Int = 128

/**
 * 256 키 사이즈
 */
private const val KEY_256 = 256

/**
 * RSA 키 사이즈
 */
private const val RSA_KEY_SIZE = 2048

// -----------------------------------------------------------------------------------------------------------------
// SHA-256
// -----------------------------------------------------------------------------------------------------------------

/**
 * SHA256 hash 처리. 대상 문자열이 없을 경우 `null`을 반환하며 [NoSuchAlgorithmException]
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
fun makeSHA256Hash(target: String?, salt: ByteArray?, repeatCount: Int = DEFAULT_REPEAT_COUNT): ByteArray? {
    try {
        if (StringUtils.isBlank(target)) {
            topLogger.warn("Hash 대상 존재하지 않음 : target [{}]", target)
            return null
        }

        val digest: MessageDigest = MessageDigest.getInstance(ALGORITHM_SHA_256)

        digest.reset()

        var convertedTarget: ByteArray = digest.digest(target?.toByteArray(CHAR_SET))

        if (salt != null) {
            digest.update(salt)
        }

        for (i in 0..<repeatCount) {
            digest.reset()

            convertedTarget = digest.digest(convertedTarget)
        }

        return convertedTarget
    } catch (e: Throwable) {
        topLogger.error("SHA256 hash error", e)

        throw CryptoException("SHA256 Hash 처리 실패", e)
    }
}

/**
 * [makeSHA256Hash] 참고. 반복횟수는 [DEFAULT_REPEAT_COUNT]로 설정된다.
 *
 * 반복 횟수는 [DEFAULT_REPEAT_COUNT]로 설정되며, `salt`는 `null`로 설정함.
 *
 * @param target Hash 처리 대상 문자열
 *
 * @return Hash 처리된 결과
 * @throws CryptoException [NoSuchAlgorithmException] 예외 발생시
 * @see [makeSHA256Hash]
 */
@Throws(CryptoException::class)
fun makeSHA256Hash(target: String?): ByteArray? {
    return makeSHA256Hash(target, null, DEFAULT_REPEAT_COUNT)
}

/**
 * [makeSHA256Hash] 참고. 반복횟수는 [DEFAULT_REPEAT_COUNT]로 설정된다.
 *
 * 반복 횟수는 [DEFAULT_REPEAT_COUNT]로 설정함.
 *
 * @param target Hash 처리 대상 문자열
 * @param salt salt
 *
 * @return Hash 처리된 결과
 * @throws CryptoException [NoSuchAlgorithmException] 예외 발생시
 * @see [makeSHA256Hash]
 */
@Throws(CryptoException::class)
fun makeSHA256Hash(target: String?, salt: ByteArray?): ByteArray? {
    return makeSHA256Hash(target, salt, DEFAULT_REPEAT_COUNT)
}

// -----------------------------------------------------------------------------------------------------------------
// AES
// -----------------------------------------------------------------------------------------------------------------

/**
 * AES 암호화 처리. 인자로 지정된 keySize 기반으로 암호화 처리를 하며, 해당 결과는 [AESResult] 인스턴스로 반환한다.
 * 대상 문자열이 존재하지 않을 경우 `null`을 반환한다.
 *
 * @param target  대상 문자열
 * @param secret  암호화키
 * @param keySize 키 사이즈
 * @param salt    salt
 * @param repeatCount 반복 횟수
 *
 * @return 처리 결과
 * @throws CryptoException 암호화 처리 중 발생
 * @see AESResult
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
            topLogger.warn("AES 암호화 대상 존재하지 않음 : target [{}]", target)

            return null
        }

        val factory: SecretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM_SECRET_FACTORY)
        val keySpec: KeySpec = PBEKeySpec(secret.toCharArray(), salt, repeatCount, keySize)
        val secretKey: SecretKey = factory.generateSecret(keySpec)
        val secretKeySpec: SecretKeySpec = SecretKeySpec(secretKey.encoded, ALGORITHM_AES)
        val cipher: Cipher = Cipher.getInstance(Transformation.AES_CBC_PKCS5PADDING.transformation)

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)

        val parameter: AlgorithmParameters = cipher.parameters
        val ivBytes: ByteArray = parameter.getParameterSpec(IvParameterSpec::class.java).iv
        val encrypted: ByteArray = cipher.doFinal(target.toByteArray(CHAR_SET))

        return AESResult(salt, encrypted, ivBytes, repeatCount)
    } catch (e: Throwable) {
        topLogger.error("Encrypt AES error", e)

        throw CryptoException("AES 암호화 오류[keySize: $keySize]", e)
    }
}

/**
 * AES 복호화 처리. 복호화 결과를 {@code byte[]} 형태로 반환한다.
 * 다음의 경우 내부적으로 [javax.crypto.BadPaddingException]이 발생하여 호출하는 함수에 [CryptoException] 예외를 발생 시킨다.
 *
 * * 암호화시 지정한 `repeatCount`와 복호화시 값이 불일치 할 경우
 * * 암호화시 저정한 `secret`과 복호화시 지정한 값이 불일치 할 경우
 * * 암호화시 지정한 `salt`와 복호화시 지정한 값이 불일치 할 경우
 *
 * 다음의 경우 내부적으로 [java.security.InvalidAlgorithmParameterException]이 발생하여 호출하는 함수에 [CryptoException]
 * 예외를 발생시킨다.
 *
 * * 암호화시 지정한 `iv`와 복호화시 지정한 값이 불일치 할 경우
 *
 * @param target  복호화 대상
 * @param secret  암호화키
 * @param iv initial vector
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
@Throws(CryptoException::class)
private fun decryptAES(
    target: ByteArray,
    secret: String,
    keySize: Int,
    iv: ByteArray,
    salt: ByteArray,
    repeatCount: Int = DEFAULT_REPEAT_COUNT
): ByteArray {
    try {
        val factory: SecretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM_SECRET_FACTORY)
        val spec: PBEKeySpec = PBEKeySpec(secret.toCharArray(), salt, repeatCount, keySize)
        val secretKey: SecretKey = factory.generateSecret(spec)
        val secretKeySpec: SecretKeySpec = SecretKeySpec(secretKey.encoded, ALGORITHM_AES)
        val cipher: Cipher = Cipher.getInstance(Transformation.AES_CBC_PKCS5PADDING.transformation)

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, IvParameterSpec(iv))

        return cipher.doFinal(target)
    } catch (e: Throwable) {
        topLogger.error("Decrypt AES error", e)

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
@Throws(CryptoException::class)
fun encryptAES128(
    target: String?,
    secret: String,
    salt: ByteArray = generateSalt(),
    repeatCount: Int = DEFAULT_REPEAT_COUNT
): AESResult? {
    return target?.let {
        return encryptAES(target, secret, KEY_128, salt, repeatCount)
    }
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
@Throws(CryptoException::class)
fun encryptAES256(
    target: String?,
    secret: String,
    salt: ByteArray = generateSalt(),
    repeatCount: Int = DEFAULT_REPEAT_COUNT
): AESResult? {
    return target?.let {
        return encryptAES(target, secret, KEY_256, salt, repeatCount)
    }
}

/**
 * `AES128`로 복호화. [decryptAES] 참고.
 * 다음의 경우 내부적으로 [javax.crypto.BadPaddingException]이 발생하여 호출하는 함수에 [CryptoException] 예외를 발생 시킨다.
 *
 * * 암호화시 지정한 `repeatCount`와 복호화시 값이 불일치 할 경우
 * * 암호화시 저정한 `secret`과 복호화시 지정한 값이 불일치 할 경우
 * * 암호화시 지정한 `salt`와 복호화시 지정한 값이 불일치 할 경우
 *
 * 다음의 경우 내부적으로 [java.security.InvalidAlgorithmParameterException]이 발생하여 호출하는 함수에 [CryptoException]
 * 예외를 발생시킨다.
 *
 * * 암호화시 지정한 `iv`와 복호화시 지정한 값이 불일치 할 경우
 *
 * @param target 복호화 대상
 * @param secret 암호화키
 * @param iv Initial vector
 * @param salt salt
 * @param repeatCount 반복 횟수
 * @see [decryptAES]
 * @return 복화화된 평문 바이트 배열
 */
fun decryptAES128(
    target: ByteArray?,
    secret: String,
    iv: ByteArray,
    salt: ByteArray,
    repeatCount: Int?
): ByteArray? {
    return target?.let {
        return decryptAES(it, secret, KEY_128, iv, salt, repeatCount ?: DEFAULT_REPEAT_COUNT)
    }
}

/**
 * `AES256`으로 복호화. [decryptAES] 참고.
 * 다음의 경우 내부적으로 [javax.crypto.BadPaddingException]이 발생하여 호출하는 함수에 [CryptoException] 예외를 발생 시킨다.
 *
 * * 암호화시 지정한 `repeatCount`와 복호화시 값이 불일치 할 경우
 * * 암호화시 저정한 `secret`과 복호화시 지정한 값이 불일치 할 경우
 * * 암호화시 지정한 `salt`와 복호화시 지정한 값이 불일치 할 경우
 *
 * 다음의 경우 내부적으로 [java.security.InvalidAlgorithmParameterException]이 발생하여 호출하는 함수에 [CryptoException]
 * 예외를 발생시킨다.
 *
 * * 암호화시 지정한 `iv`와 복호화시 지정한 값이 불일치 할 경우
 *
 * @param target 복호화 대상
 * @param secret 암호화키
 * @param iv Initial vector
 * @param salt salt
 * @param repeatCount 반복 횟수
 * @see [decryptAES]
 * @return 복화화된 평문 바이트 배열
 */
fun decryptAES256(
    target: ByteArray?,
    secret: String,
    iv: ByteArray,
    salt: ByteArray,
    repeatCount: Int?
): ByteArray? {
    return target?.let {
        return decryptAES(it, secret, KEY_256, iv, salt, repeatCount ?: DEFAULT_REPEAT_COUNT)
    }
}

// -----------------------------------------------------------------------------------------------------------------
// RSA
// -----------------------------------------------------------------------------------------------------------------
/**
 * RSA key pair 생성
 *
 * @return [KeyPair]
 */
fun generateRSAKeyPair(): KeyPair {
    try {
        val generator: KeyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_RSA)

        generator.initialize(RSA_KEY_SIZE, SecureRandom())

        return generator.genKeyPair()
    } catch (e: Throwable) {
        topLogger.error("Generate RSA key pair error", e)

        throw CryptoException("RSA key pair 생성 실패", e)
    }
}

/**
 * 주어진 [ByteArray]을 [LoadKeyType]에 따라 해당 키로 변환하여 반환
 *
 * @param key Key 바이트 배열
 * @param keyType [LoadKeyType]
 * @return [Key]
 * @see [NoSuchAlgorithmException]
 * @see [InvalidKeySpecException]
 * @throws CryptoException 암호화 처리중 발생
 */
@Throws(CryptoException::class)
private fun loadKey(key: ByteArray, keyType: LoadKeyType): Key {
    try {
        val factory: KeyFactory = KeyFactory.getInstance(ALGORITHM_RSA)
        val spec: EncodedKeySpec

        if (LoadKeyType.PUBLIC_KEY == keyType) {
            spec = X509EncodedKeySpec(key)

            return factory.generatePublic(spec)
        } else {
            spec = PKCS8EncodedKeySpec(key)

            return factory.generatePrivate(spec)
        }
    } catch (e: Throwable) {
        topLogger.error("Load key error", e)

        throw CryptoException("LoadKeyType을 이용한 key 생성 실패", e)
    }
}

/**
 * RSA 암호화 처리
 *
 * @param target 암호화 대상
 * @param publicKey 공개키
 * @return 암호화 결과(바이트 배열)
 * @throws CryptoException 암호화 처리 중 발생
 * @see [NoSuchAlgorithmException]
 * @see [javax.crypto.NoSuchPaddingException]
 * @see [InvalidKeySpecException]
 * @see [InvalidKeyException]
 * @see [javax.crypto.IllegalBlockSizeException]
 * @see [javax.crypto.BadPaddingException]
 */
@Throws(CryptoException::class)
fun encryptRSA(target: ByteArray?, publicKey: ByteArray): ByteArray? {
    try {
        return target?.let {
            val cipher: Cipher = Cipher.getInstance(Transformation.RSA.transformation)

            cipher.init(Cipher.ENCRYPT_MODE, loadKey(publicKey, LoadKeyType.PUBLIC_KEY))

            return cipher.doFinal(it)
        }
    } catch (e: Throwable) {
        topLogger.error("Encrypt RSA error", e)

        throw CryptoException("RSA 암호화 오류", e)
    }
}

/**
 * 공개/개인키를 자체 생성하여 RSA 암호화 처리 후 [RSAKeySet]을 반환. [encryptRSA] 참고.
 *
 * @param target 암호화 대상
 * @param makeExtra 공개/비공개 키의 계수, 지수 및 [Base64.Encoder.encodeToString] 생성 여부
 *
 * @return 암호화 결과
 * @see [RSAKeySet]
 * @see [generateRSAKeyPair]
 * @see [encryptRSA]
 */
@Throws(CryptoException::class)
fun encryptRSA(target: ByteArray?, makeExtra: Boolean = true): RSAKeySet? {
    val keyPair: KeyPair = generateRSAKeyPair()
    val encrypted: ByteArray? = encryptRSA(target, keyPair.public.encoded)

    return encrypted?.let {
        val result: RSAKeySet = RSAKeySet(keyPair.public, keyPair.private, makeExtra)

        result.encryptedValue = it

        return result
    }
}

/**
 * RSA 복호화 처리
 *
 * @param target     복호화 대상
 * @param privateKey 비밀키
 *
 * @return 복호화 결과
 * @throws CryptoException 복호화 실패시
 * @see [NoSuchAlgorithmException]
 * @see [javax.crypto.NoSuchPaddingException]
 * @see [InvalidKeySpecException]
 * @see [InvalidKeyException]
 * @see [javax.crypto.IllegalBlockSizeException]
 * @see [javax.crypto.BadPaddingException]
 */
@Throws(CryptoException::class)
fun decryptRSA(target: ByteArray?, privateKey: ByteArray): ByteArray? {
    try {
        return target?.let {
            val cipher: Cipher = Cipher.getInstance(Transformation.RSA.transformation)
            val key: Key = loadKey(privateKey, LoadKeyType.PRIVATE_KEY)

            cipher.init(Cipher.DECRYPT_MODE, key)

            return cipher.doFinal(target)
        }
    } catch (e: Throwable) {
        topLogger.error("Decrypt RSA error", e)

        throw CryptoException("RSA 복호화 오류", e)
    }
}

/**
 * [decryptRSA] 참고
 *
 * @param target 복호화 대상
 * @param keySet [RSAKeySet]
 *
 * @return 복호화 결과
 * @see [RSAKeySet]
 * @see [decryptRSA]
 */
fun decryptRSA(target: ByteArray?, keySet: RSAKeySet): ByteArray? {
    return decryptRSA(target, keySet.privateKey.encoded)
}