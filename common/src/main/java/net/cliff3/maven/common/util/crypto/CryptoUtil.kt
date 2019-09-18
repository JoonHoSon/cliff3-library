package net.cliff3.maven.common.util.crypto

import net.cliff3.maven.common.util.CryptoException
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.spec.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * CryptoUtil
 *
 * @author JoonHo Son
 * @version 1.0.0 2019-09-09 최초 작성
 * @since 1.0.0
 */
class CryptoUtil {
    /**
     * 처리 가능한 Transformation
     */
    enum class Transformation(val value: String) {
        /**
         * DES/CBC/PKCS5Padding
         */
        DES_CBC_PKCS5PADDING("DES/CBC/PKCS5Padding"),
        /**
         * {@link #DES_CBC_PKCS5PADDING} 동일
         */
        DES(Transformation.DES_CBC_PKCS5PADDING.value),

        /**
         * DES/ECB/PKCS5Padding
         */
        DES_ECB_PKCS5PADDING("DES/ECB/PKCS5Padding"),

        /**
         * DES/CFB8/NoPadding
         */
        DES_CFB8_NOPADDING("DES/CFB8/NoPadding"),

        /**
         * DES/OFB32/PCKS5Padding
         */
        DES_OFB32_PKCS5PADDING("DES/OFB32/PKCS5Padding"),

        /**
         * RSA/ECB/PKCS1Padding
         */
        RSA_ECB_PKCS1PADDING("RSA/ECB/PKCS1Padding"),

        /**
         * {@link #RSA_ECB_PKCS1PADDING} 동일
         */
        RSA(Transformation.RSA_ECB_PKCS1PADDING.value);
    }

    /**
     * 공개키, 비밀키
     */
    enum class LoadKeyType {
        /**
         * 공개키
         */
        PUBLIC_KEY,

        /**
         * 비밀키
         */
        PRIVATE_KEY
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(CryptoUtil::class.java)

        private val UTF_8: Charset = StandardCharsets.UTF_8

        /**
         * Algorithm(DES)
         */
        const val ALGORITHM_DES = "DES"

        /**
         * Algorithm(RSA)
         */
        const val ALGORITHM_RSA = "RSA"

        /**
         * 운용모드(CBC)
         */
        const val OPERATION_MODE_CBC = "CBC"

        /**
         * 운용모드(ECB)
         */
        const val OPERATION_MODE_ECB = "ECB"

        /**
         * Padding 방식(PKCSS5Padding)
         */
        const val PADDING_PKCS5PADDING = "PKCS5Padding"

        /**
         * Padding 방식(PKCS1Padding)
         */
        const val PADDING_PKCS1PADDING = "PKCS1Padding"

        /**
         * Padding 방식(없음)
         */
        const val PADDING_NONE: String = "NoPadding"

        /**
         * 기본 반복 횟수
         */
        private const val DEFAULT_REPEAT_COUNT: Int = 1000

        /**
         * 128 키 사이즈
         */
        private const val KEY_128: Int = 128

        /**
         * 256 키 사이즈
         */
        private const val KEY_256: Int = 256

        /**
         * SHA256 hash 처리.
         * [java.io.UnsupportedEncodingException] 혹은 [java.security.NoSuchAlgorithmException] 예외가 발생할 수 있으며
         * 모든 예외는 [CryptoException]으로 변환하여 예외를 전달한다.
         *
         * @param target 대상 문자열
         * @param salt salt
         * @param repeatCount 반복 횟수
         *
         * @return SHA256 hash 처리 결과
         * @throws CryptoException 암호화 처리 오류 발생
         */
        @JvmStatic
        @Throws(CryptoException::class, IllegalArgumentException::class)
        fun makeSHA256Hash(target: String, salt: String?, repeatCount: Int = DEFAULT_REPEAT_COUNT): ByteArray {
            require(!target.isBlank()) { "SHA256 hashing 오류 : 대상 문자열 없음" }

            logger.debug("암호화 대상 문자열 : $target")

            try {
                val digest: MessageDigest = MessageDigest.getInstance("SHA-256")

                digest.reset()

                var convertedTarget: ByteArray = digest.digest(target.toByteArray(UTF_8))

                if (!salt.isNullOrBlank()) {
                    logger.debug("salt -> $salt")

                    digest.update(salt.toByteArray())

                    // 반복 횟수를 높여준다.
                    for (i in 0..repeatCount) {
                        digest.reset()
                        convertedTarget = digest.digest(convertedTarget)
                    }
                } else {
                    for (i in 0..repeatCount) {
                        digest.reset()

                        convertedTarget = digest.digest(convertedTarget)
                    }
                }

                return convertedTarget
            } catch (e: Exception) {
                logger.error("암호화 오류", e)

                throw CryptoException("암호화 오류", e)
            }
        }

        /**
         * [makeSHA256Hash] 참고
         */
        @JvmStatic
        @Throws(CryptoException::class, IllegalArgumentException::class)
        fun makeSHA256Hash(target: String, salt: String?): ByteArray {
            return makeSHA256Hash(target, salt, DEFAULT_REPEAT_COUNT)
        }

        /**
         * [makeSHA256Hash] 참고
         */
        @JvmStatic
        @Throws(CryptoException::class, IllegalArgumentException::class)
        fun makeSHA256Hash(target: String): ByteArray {
            return makeSHA256Hash(target, null, DEFAULT_REPEAT_COUNT)
        }

        /**
         * AES128 암호화 처리. [makeAESEncrypt] 참고
         */
        @JvmStatic
        @Throws(CryptoException::class)
        fun makeAES128Encrypt(target: String, secret: String): AESCrypto {
            return makeAESEncrypt(target, secret, 128, generateSalt())
        }

        /**
         * AES128 암호화 처리. [makeAESEncrypt] 참고
         */
        @JvmStatic
        @Throws(CryptoException::class)
        fun makeAES128Encrypt(target: String, secret: String, salt: String): AESCrypto {
            return makeAESEncrypt(target, secret, 128, salt.toByteArray())
        }

        /**
         * AES128 암호화 처리. [makeAESEncrypt] 참고
         */
        @JvmStatic
        @Throws(CryptoException::class)
        fun makeAES128Encrypt(target: String, secret: String, salt: ByteArray): AESCrypto {
            return makeAESEncrypt(target, secret, 128, salt)
        }

        /**
         * AES256 암호화 처리. [makeAESEncrypt] 참고
         */
        @JvmStatic
        @Throws(CryptoException::class)
        fun makeAES256Encrypt(target: String, secret: String): AESCrypto {
            return makeAESEncrypt(target, secret, 256, generateSalt())
        }

        /**
         * AES256 암호화 처리. [makeAESEncrypt] 참고
         */
        @JvmStatic
        @Throws(CryptoException::class)
        fun makeAES256Encrypt(target: String, secret: String, salt: String): AESCrypto {
            return makeAESEncrypt(target, secret, 256, salt.toByteArray())
        }

        /**
         * AES256 암호화 처리. [makeAESEncrypt] 참고
         */
        @JvmStatic
        @Throws(CryptoException::class)
        fun makeAES256Encrypt(target: String, secret: String, salt: ByteArray): AESCrypto {
            return makeAESEncrypt(target, secret, 256, salt)
        }

        /**
         * AES128 복호화 처리. [decryptAES] 참고
         */
        @JvmStatic
        @Throws(CryptoException::class)
        fun decryptAES128(target: ByteArray, secret: String, iv: ByteArray, salt: ByteArray): ByteArray {
            return decryptAES(target, secret, 128, iv, salt)
        }

        /**
         * AES256 복호화 처리. [decryptAES] 참고.
         */
        @JvmStatic
        @Throws(CryptoException::class)
        fun decryptAES256(target: ByteArray, secret: String, iv: ByteArray, salt: ByteArray): ByteArray {
            return decryptAES(target, secret, 256, iv, salt)
        }

        fun encryptDataByRSA(target: ByteArray, toStringKey: Boolean = true): RSAKeySet {
            val keySize: Int = 2048
            val cipher: Cipher = Cipher.getInstance(Transformation.RSA_ECB_PKCS1PADDING.value)
            val random: SecureRandom = SecureRandom()
            val generator: KeyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_RSA)

            generator.initialize(keySize, random)

            val keyPair: KeyPair = generator.generateKeyPair()
            val publicKey: Key = keyPair.public
            val privateKey: Key = keyPair.private

            cipher.init(Cipher.ENCRYPT_MODE, publicKey)

            val encrypted: ByteArray = cipher.doFinal(target)
            val result: RSAKeySet = RSAKeySet(publicKey = publicKey, privateKey = privateKey, toStringKey = toStringKey)

            result.encryptedValue = encrypted

            return result
        }

        fun decryptDataByRSA(target: ByteArray, keySet: RSAKeySet): ByteArray {
            val cipher: Cipher = Cipher.getInstance(Transformation.RSA_ECB_PKCS1PADDING.value)

            cipher.init(Cipher.DECRYPT_MODE, keySet.privateKey)

            return cipher.doFinal(target)
        }

        //--------------------------------------------------------------------------------------------------------------
        // private function
        //--------------------------------------------------------------------------------------------------------------

        /**
         * AES 암호화 처리. 암호화 결과는 [AESCrypto] 인스턴스로 반환된다. salt 값이 없을 경우 [generateSalt]를 이용하여 생성한다.
         *
         * @param target 대상 문자열
         * @param secret 암호화 키
         * @param keySize 키 사이즈
         * @param salt salt
         * @return [AESCrypto]
         * @throws CryptoException 암호화 실패시 발생
         *
         * @see java.io.UnsupportedEncodingException
         * @see java.security.NoSuchAlgorithmException
         * @see java.security.InvalidKeyException
         * @see java.security.spec.InvalidKeySpecException
         * @see java.security.spec.InvalidParameterSpecException
         * @see javax.crypto.NoSuchPaddingException
         * @see javax.crypto.BadPaddingException
         * @see javax.crypto.IllegalBlockSizeException
         */
        @Throws(CryptoException::class)
        private fun makeAESEncrypt(target: String, secret: String, keySize: Int, salt: ByteArray): AESCrypto {
            require(StringUtils.isNotBlank(target)) { throw IllegalArgumentException("암호화 대상 문자열 없음") }
            require(StringUtils.isNotBlank(secret)) { throw IllegalArgumentException("암호화 키 없음") }

            logger.debug("암호화 대상 문자열 : $target")

            try {
                val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                val spec: KeySpec = PBEKeySpec(secret.toCharArray(), salt, DEFAULT_REPEAT_COUNT, keySize)
                val secretKey: SecretKey = factory.generateSecret(spec)
                val key: SecretKey = SecretKeySpec(secretKey.encoded, "AES")
                val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

                cipher.init(Cipher.ENCRYPT_MODE, key)

                val parameters: AlgorithmParameters = cipher.parameters
                val ivBytes: ByteArray = parameters.getParameterSpec(IvParameterSpec::class.java).iv
                val encryptedBytes: ByteArray = cipher.doFinal(target.toByteArray(UTF_8))

                return AESCrypto(salt, encryptedBytes, ivBytes)
            } catch (e: Exception) {
                logger.error("AES 암호화 실패", e)

                throw CryptoException("AES 암호화 실패", e)
            }
        }

        /**
         * AES 복호화 처리. 인자로 전달되는 [secret], [iv] 및 [salt]를 이용하여 대상 문자열을 복호화 한다.
         *
         * @param target 암호화된 문자열
         * @param secret 암호화 키
         * @param keySize 키 사이즈
         * @param iv initialize vector
         * @param salt salt
         * @return 복호화된 문자열
         *
         * @see java.io.UnsupportedEncodingException
         * @see java.security.NoSuchAlgorithmException
         * @see java.security.InvalidKeyException
         * @see java.security.InvalidAlgorithmParameterException
         * @see java.security.spec.InvalidKeySpecException
         * @see javax.crypto.NoSuchPaddingException
         * @see javax.crypto.BadPaddingException
         * @see javax.crypto.IllegalBlockSizeException
         */
        @Throws(CryptoException::class)
        private fun decryptAES(target: ByteArray,
                               secret: String,
                               keySize: Int,
                               iv: ByteArray,
                               salt: ByteArray): ByteArray {
            require(target.isNotEmpty()) { throw IllegalArgumentException("복호화 대상 없음") }
            require(StringUtils.isNotBlank(secret)) { throw IllegalArgumentException("암호화 키 없음") }

            try {
                val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                val spec: PBEKeySpec = PBEKeySpec(secret.toCharArray(), salt, DEFAULT_REPEAT_COUNT, keySize)
                val secretKey: SecretKey = factory.generateSecret(spec)
                val key: SecretKey = SecretKeySpec(secretKey.encoded, "AES")
                val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

                cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))

                return cipher.doFinal(target)
            } catch (e: Exception) {
                logger.error("AES 복호화 실패", e)

                throw CryptoException("AES 복호화 실패", e)
            }
        }

        /**
         * Salt 생성
         *
         * @return salt byte array
         */
        private fun generateSalt(): ByteArray {
            val random: SecureRandom = SecureRandom()
            val bytes: ByteArray = ByteArray(30)

            random.nextBytes(bytes)

            return bytes
        }

        /**
         * 공개키 및 비밀키를 반환한다.
         *
         * @param 암호화된 키
         * @param type [LoadKeyType]
         *
         * @return 공개/비밀키
         */
        @Throws(IOException::class, NoSuchAlgorithmException::class, InvalidKeySpecException::class)
        private fun loadKey(key: String, type: LoadKeyType): Key {
            require(key.isNotBlank()) { throw IllegalArgumentException("암호화 키가 없음") }

            val decodedKey: ByteArray = key.toByteArray()
            val factory: KeyFactory = KeyFactory.getInstance(ALGORITHM_RSA)
            val keySpec: EncodedKeySpec

            return if (LoadKeyType.PUBLIC_KEY == type) {
                keySpec = X509EncodedKeySpec(decodedKey)

                factory.generatePublic(keySpec)
            } else {
                keySpec = PKCS8EncodedKeySpec(decodedKey)

                factory.generatePrivate(keySpec)
            }
        }
    }
}