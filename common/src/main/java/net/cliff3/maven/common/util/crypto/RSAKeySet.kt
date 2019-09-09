package net.cliff3.maven.common.util.crypto

import java.math.BigInteger
import java.security.Key
import java.security.KeyFactory
import java.security.spec.RSAPrivateKeySpec
import java.security.spec.RSAPublicKeySpec

/**
 * RSAKeySet
 *
 * @author JoonHo Son
 * @version 1.0.0 2019-09-09 최초 작성
 * @since 1.0.0
 */
class RSAKeySet {
    /**
     * Public key
     */
    var publicKey: Key? = null
        private set

    /**
     * Private key
     */
    var privateKey: Key? = null
        private set

    /**
     * 암호화된 결과
     */
    var encryptedValue: ByteArray? = null

    /**
     * 공개키의 계수(modulus)
     */
    var publicKeyModulus: String? = null
        private set

    /**
     * 공개키의 지수(exponent)
     */
    var publicKeyExponent: String? = null
        private set

    /**
     * 비공개키의 계수(modulus)
     */
    var privateKeyModulus: String? = null
        private set

    /**
     * 비공개키의 지수(exponent)
     */
    var privateKeyExponent: String? = null
        private set

    /**
     * 공개/비공개 키의 계수(modulus) 및 지수(exponent)의 문자열 처리 여부. 해당 값이 **true**일 경우
     * 16진수의 공개/비공개키 문자열을 생성한다.<br>
     *
     * [publicKeyModulus], [publicKeyExponent], [privateKeyModulus], [privateKeyExponent] 참고
     *
     */
    var toStringKey: Boolean? = null
        private set

    constructor(publicKeyModulus: String,
                publicKeyExponent: String,
                privateKeyModulus: String,
                privateKeyExponent: String) {
        this.publicKeyModulus = publicKeyModulus
        this.publicKeyExponent = publicKeyExponent
        this.privateKeyModulus = privateKeyModulus
        this.privateKeyExponent = privateKeyExponent

        stringToKey()
    }

    constructor(publicKey: Key, privateKey: Key, toStringKey: Boolean = false) {
        this.publicKey = publicKey
        this.privateKey = privateKey
        this.toStringKey = this.toStringKey

        if (toStringKey) {
            keyToString()
        }
    }

    private fun stringToKey() {
        val factory: KeyFactory = KeyFactory.getInstance("RSA")

        // public
        val publicModulus: BigInteger = BigInteger(publicKeyModulus, 16)
        val publicExponent: BigInteger = BigInteger(publicKeyExponent, 16)
        val publicSpec: RSAPublicKeySpec = RSAPublicKeySpec(publicModulus, publicExponent)

        this.publicKey = factory.generatePublic(publicSpec)

        // private
        val privateModulus: BigInteger = BigInteger(privateKeyModulus, 16)
        val privateExponent: BigInteger = BigInteger(privateKeyExponent, 16)
        val privateSpec: RSAPrivateKeySpec = RSAPrivateKeySpec(privateModulus, privateExponent)

        this.privateKey = factory.generatePrivate(privateSpec)
    }

    private fun keyToString() {
        val factory: KeyFactory = KeyFactory.getInstance("RSA")

        // public
        val publicSpec: RSAPublicKeySpec = factory.getKeySpec(publicKey, RSAPublicKeySpec::class.java)

        this.publicKeyModulus = publicSpec.modulus.toString(16)
        this.publicKeyExponent = publicSpec.publicExponent.toString(16)

        // private
        val privateSpec: RSAPrivateKeySpec = factory.getKeySpec(privateKey, RSAPrivateKeySpec::class.java)

        privateKeyModulus = privateSpec.modulus.toString(16)
        privateKeyExponent = privateSpec.privateExponent.toString(16)
    }
}