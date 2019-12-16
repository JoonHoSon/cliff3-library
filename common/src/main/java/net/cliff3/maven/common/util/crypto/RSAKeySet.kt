package net.cliff3.maven.common.util.crypto

import org.apache.commons.codec.binary.Base64
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
 *
 * @constructor
 */
class RSAKeySet {
    /**
     * Public key
     */
    var publicKey: Key? = null
        private set

    /**
     * [Base64.encodeBase64URLSafeString]으로 변환된 Public key 문자열
     */
    var publicKeyString: String? = null
        private set

    /**
     * Private key
     */
    var privateKey: Key? = null
        private set

    /**
     * [Base64.encodeBase64URLSafeString]으로 변환된 Private key 문자열
     */
    var privateKeyString: String? = null
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
    var toStringKey: Boolean? = false
        private set

    /**
     * 공개/비공개키 문자열 정보를 이용한 생성자. 해당 문자열 정보들을 기반으로 [stringToKey] 함수를 통해
     * [publicKey] 및 [privateKey]를 복원한다.
     *
     * @param publicKeyModulus [RSAKeySet.publicKeyModulus] 대응
     * @param publicKeyExponent [RSAKeySet.publicKeyExponent] 대응
     * @param privateKeyModulus [RSAKeySet.privateKeyModulus] 대응
     * @param privateKeyExponent [RSAKeySet.privateKeyExponent] 대응
     *
     * @see stringToKey
     */
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

    /**
     * 공개/비공개키 정보를 이용한 생성자. [toStringKey] 인자가 `true` 경우 해당 키를 기반으로
     * [publicKeyModulus], [publicKeyExponent], [privateKeyModulus], [privateKeyExponent] 문자열을 할당한다.
     *
     * @param publicKey 공개키
     * @param privateKey 비공개키
     * @param toStringKey 공개/비공개키 정보의 문자열 데이터 생성 여부를 지정
     *
     * @see keyToString
     */
    constructor(publicKey: Key, privateKey: Key, toStringKey: Boolean = false) {
        this.publicKey = publicKey
        this.privateKey = privateKey
        this.toStringKey = this.toStringKey

        if (toStringKey) {
            this.toStringKey = toStringKey

            keyToString()
        }
    }

    /**
     * 생성자를 통해 전달된 공개/비공개키 문자열 정보를 이용하여 실제 공개/비공개키([publicKey]/[privateKey])로 복원한다.
     */
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

    /**
     * 공개/비공개키([publicKey]/[privateKey])를 문자열로 변환하여 보관한다.
     *
     * @see publicKeyModulus
     * @see publicKeyExponent
     * @see privateKeyModulus
     * @see privateKeyModulus
     */
    private fun keyToString() {
        val factory: KeyFactory = KeyFactory.getInstance("RSA")

        // public
        val publicSpec: RSAPublicKeySpec = factory.getKeySpec(publicKey, RSAPublicKeySpec::class.java)

        publicKeyModulus = publicSpec.modulus.toString(16)
        publicKeyExponent = publicSpec.publicExponent.toString(16)
        publicKeyString = Base64.encodeBase64URLSafeString(publicKey?.encoded)

        // private
        val privateSpec: RSAPrivateKeySpec = factory.getKeySpec(privateKey, RSAPrivateKeySpec::class.java)

        privateKeyModulus = privateSpec.modulus.toString(16)
        privateKeyExponent = privateSpec.privateExponent.toString(16)
        privateKeyString = Base64.encodeBase64URLSafeString(privateKey?.encoded)
    }
}