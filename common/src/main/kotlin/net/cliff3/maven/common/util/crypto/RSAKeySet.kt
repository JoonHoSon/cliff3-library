package net.cliff3.maven.common.util.crypto

import java.math.BigInteger
import java.security.Key
import java.security.KeyFactory
import java.security.spec.RSAPrivateKeySpec
import java.security.spec.RSAPublicKeySpec
import java.util.*

/**
 * RSAKeySet
 *
 * [encryptRSA]로 생성된 결과
 *
 * @author JoonHo Son
 * @since 0.3.0
 * @see encryptRSA
 */
class RSAKeySet {
    /**
     * Public key
     */
    val publicKey: Key

    /**
     * [Base64.Encoder.encodeToString] 처리된 [publicKey] 문자열
     */
    var publicKeyString: String? = null
        private set

    /**
     * 공개키 계수(modulus)
     */
    var publicKeyModulus: String? = null
        private set

    /**
     * 공개키 지수(exponent)
     */
    var publicKeyExponent: String? = null
        private set

    /**
     * Private key
     */
    val privateKey: Key

    /**
     * [Base64.Encoder.encodeToString] 처리된 [privateKey] 문자열
     */
    var privateKeyString: String? = null
        private set

    /**
     * 개인키 계수(modulus)
     */
    var privateKeyModulus: String? = null
        private set

    /**
     * 개인키 지수(exponent)
     */
    var privateKeyExponent: String? = null
        private set

    /**
     * 공개/개인키 및 각 계수(modulus) 및 지수(exponent)의 문자열(16진수 형태) 처리 여부. 해당 값이 `true`일 경우 16진수 형태의
     * 공개/개인키 및 각 계수 및 지수를 생성한다.
     */
    var isMakeExtra: Boolean = false
        private set

    /**
     * 암호회 처리된 결과
     */
    var encryptedValue: ByteArray? = null

    private val encoder = Base64.getEncoder()

    /**
     * Constructor
     *
     * @param publicKey 공개키
     * @param privateKey 개인키
     * @param 계수(modulus) 및 지수(exponent) 16진수 문자열 생성 여부
     */
    @Throws(CryptoException::class)
    constructor(publicKey: Key, privateKey: Key, makeExtra: Boolean = false) {
        this.publicKey = publicKey
        this.privateKey = privateKey
        this.isMakeExtra = makeExtra

        if (!makeExtra) {
            return
        }

        try {
            val factory: KeyFactory = KeyFactory.getInstance("RSA")

            // public key
            val publicSpec: RSAPublicKeySpec = factory.getKeySpec(publicKey, RSAPublicKeySpec::class.java)

            this.publicKeyModulus = publicSpec.modulus.toString(16)
            this.publicKeyExponent = publicSpec.publicExponent.toString(16)
            this.publicKeyString = encoder.encodeToString(publicKey.encoded)

            // private key
            val privateSpec: RSAPrivateKeySpec = factory.getKeySpec(privateKey, RSAPrivateKeySpec::class.java)

            this.privateKeyModulus = privateSpec.modulus.toString(16)
            this.privateKeyExponent = privateSpec.privateExponent.toString(16)
            this.privateKeyString = encoder.encodeToString(privateKey.encoded)
        } catch (e: Throwable) {
            throw CryptoException("계수 및 지수 생성 실패", e)
        }
    }

    /**
     * Constructor
     *
     * @param publicKeyModulus 16진수로 구성된 공개키 계수
     * @param publicKeyExponent 16진수로 구성된 공개키 지수
     * @param privateKeyModulus 16진수로 구성된 개인키 계수
     * @param privateKeyExponent 16진수로 구성된 개인키 계수
     */
    @Throws(CryptoException::class)
    constructor(
        publicKeyModulus: String,
        publicKeyExponent: String,
        privateKeyModulus: String,
        privateKeyExponent: String,
        makeEncodedString: Boolean = false
    ) {
        this.publicKeyModulus = publicKeyModulus
        this.publicKeyExponent = publicKeyExponent
        this.privateKeyModulus = privateKeyModulus
        this.privateKeyExponent = privateKeyExponent

        try {
            val factory: KeyFactory = KeyFactory.getInstance("RSA")

            // public key 생성
            var modulus: BigInteger = BigInteger(publicKeyModulus, 16)
            var exponent: BigInteger = BigInteger(publicKeyExponent, 16)
            val publicSpec: RSAPublicKeySpec = RSAPublicKeySpec(modulus, exponent)

            publicKey = factory.generatePublic(publicSpec)

            // private key 생성
            modulus = BigInteger(privateKeyModulus, 16)
            exponent = BigInteger(privateKeyExponent, 16)
            val privateSpec: RSAPrivateKeySpec = RSAPrivateKeySpec(modulus, exponent)

            privateKey = factory.generatePrivate(privateSpec)

            if (!makeEncodedString) {
                return
            }

            this.publicKeyString = encoder.encodeToString(publicKey.encoded)
            this.privateKeyString = encoder.encodeToString(privateKey.encoded)
        } catch (e: Throwable) {
            throw CryptoException("계수 및 지수로부터 키 생성 실패", e)
        }
    }
}