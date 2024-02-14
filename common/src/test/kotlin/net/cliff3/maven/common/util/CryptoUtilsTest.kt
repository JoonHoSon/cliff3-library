package net.cliff3.maven.common.util

import net.cliff3.maven.common.topLogger
import net.cliff3.maven.common.util.crypto.*
import org.apache.commons.lang3.StringUtils
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.InvalidAlgorithmParameterException
import java.security.KeyPair
import javax.crypto.BadPaddingException

@TestMethodOrder(MethodOrderer.MethodName::class)
class CryptoUtilsTest {
    @Test
    @Order(1)
    @DisplayName("SHA-256 test")
    fun sha256Test() {
        topLogger.debug("Start SHA-256 test")

        val plainText = "This 이것 That 저것"
        val result1 = makeSHA256Hash(plainText)
        val result2 = makeSHA256Hash(plainText, "salt1".toByteArray())
        val result3 = makeSHA256Hash(plainText, "salt2".toByteArray(), 50)
        val result4 = makeSHA256Hash("")

        topLogger.debug("result1 : {}", toHexString(result1) ?: "fail")
        topLogger.debug("result2 : {}", toHexString(result2) ?: "fail")
        topLogger.debug("result3 : {}", toHexString(result3) ?: "fail")
        topLogger.debug("result4 : {}", toHexString(result4) ?: "null!!")

        assertNotNull(result1, "SHA-256(salt 없음. 기본 반복) 오류")
        assertNotNull(result2, "SHA-256(기본 반복) 오류")
        assertNotNull(result3, "SHA-256(salt 지정, 지정 반복) 오류")
        assertNull(result4, "공백 hash 처리 오류")
    }

    @Test
    @Order(2)
    @DisplayName("AES test")
    fun aesTest() {
        topLogger.debug("Start AES encrypt/decrypt test")

        val plainText = "This 이것 That 저것"
        val secret = "SeCrEt"
        val result1: AESResult? = encryptAES128(plainText, secret)
        val repeatCount = 33

        assertNotNull(result1, "AES128 암호화 실패")
        assertNotNull(result1?.result, "AES128 암호화 실패(결과 없음)")

        topLogger.debug("result1 : {}", toHexString(result1?.result))

        val result2 = encryptAES128("", secret)

        assertNull(result2, "공백 확인 처리 오류")

        val decrypted1 = decryptAES128(result1?.result!!, secret, result1.iv, result1.salt, result1.repeatCount)

        assertNotNull(decrypted1, "AES128 복호화 실패")
        assertEquals(String(decrypted1!!, StandardCharsets.UTF_8), plainText, "AES128 복호화 실패(일치하지 않음)")

        topLogger.debug("AES128 decrypted result : {}", String(decrypted1, StandardCharsets.UTF_8))

        val result3: AESResult? = encryptAES256(plainText, secret, repeatCount = repeatCount)

        assertNotNull(result3, "AES256 암호화 실패")
        assertNotNull(result3?.result, "AES256 암호화 실패(결과 없음)")

        topLogger.debug("result3 : {}", toHexString(result3?.result))

        // repeatCount 불일치
        assertThrows(CryptoException::class.java, {
            decryptAES256(result3?.result!!, secret, result3.iv, result3.salt, DEFAULT_REPEAT_COUNT)
        }, "반복 횟수 불일치에 따른 오류 발생하지 않음")

        // secret 불일치
        assertThrows(CryptoException::class.java, {
            decryptAES256(
                result3?.result!!, "otherSecret",
                result3.iv, result3.salt, repeatCount
            )
        }, "Secret 불일치에 따른 오류 발생하지 않음")

        // iv 불일치
        assertThrows(CryptoException::class.java, {
            decryptAES256(result3?.result!!, secret, generateSalt(), result3.salt, repeatCount)
        }, "IV 불일치에 따른 오류 발생하지 않음")

        try {
            decryptAES256(result3?.result!!, secret, generateSalt(), result3.salt, repeatCount)
        } catch (e: CryptoException) {
            assertTrue(InvalidAlgorithmParameterException::class.java == e.cause!!::class.java)
        }

        // salt 불일치
        assertThrows(CryptoException::class.java, {
            decryptAES256(result3?.result!!, secret, result3.iv, generateSalt(), repeatCount)
        }, "Salt 불일치에 따른 오류 발생하지 않음")

        try {
            decryptAES256(result3?.result!!, secret, result3.iv, generateSalt(), repeatCount)
        } catch (e: CryptoException) {
            assertTrue(BadPaddingException::class.java == e.cause!!::class.java)
        }

        val decrypted2 = decryptAES256(result3?.result, secret, result3?.iv!!, result3.salt, result3.repeatCount)

        assertNotNull(decrypted2, "AES256 복호화 실패")
        assertEquals(String(decrypted2!!, StandardCharsets.UTF_8), plainText, "AES256 복호화 실패(일치하지 않음)")
    }

    @Test
    @Order(3)
    @DisplayName("RSA test")
    fun rsaTest() {
        topLogger.debug("Start RSA encrypt/decrypt test")

        val plainText = "This 이것 That 저것"
        val keyPair: KeyPair = generateRSAKeyPair()
        val result1: ByteArray? = encryptRSA(plainText.toByteArray(StandardCharsets.UTF_8), keyPair.public.encoded)

        assertNotNull(result1, "RSA 암호화 실패")

        val decrypted1: ByteArray? = decryptRSA(result1, keyPair.private.encoded)

        assertNotNull(decrypted1, "RSA 복호화 실패")
        assertEquals(String(decrypted1!!, StandardCharsets.UTF_8), plainText, "RSA 복호화 실패(일치하지 않음)")

        topLogger.debug("decrypted1 : {}", String(decrypted1, StandardCharsets.UTF_8))

        val result2: RSAKeySet? = encryptRSA(plainText.toByteArray(StandardCharsets.UTF_8), makeExtra = true)

        assertNotNull(result2, "RSA 암호화 실패")
        assertNotNull(result2!!.publicKey, "RSA 공개키 저장 실패")
        assertTrue(StringUtils.isNotBlank(result2.publicKeyString), "RSA 공개키 base64 문자열 저장 실패")
        assertTrue(StringUtils.isNotBlank(result2.publicKeyExponent), "RSA 공개키 지수 저장 실패")
        assertTrue(StringUtils.isNotBlank(result2.publicKeyModulus), "RSA 공개키 계수 저장 실패")
        assertNotNull(result2.privateKey, "RSA 개인키 저장 실패")
        assertTrue(StringUtils.isNotBlank(result2.privateKeyString))
        assertTrue(StringUtils.isNotBlank(result2.privateKeyExponent), "RSA 개인키 base64 문자열 저장 실패")
        assertTrue(StringUtils.isNotBlank(result2.privateKeyModulus), "RSA 개인키 계수 저장 실패")

        topLogger.debug("public key string : {}", result2.publicKeyString)
        topLogger.debug("public key exponent : {}", result2.publicKeyExponent)
        topLogger.debug("public key exponent(int) : {}", BigInteger(result2.publicKeyExponent, 16))
        topLogger.debug("public key modulus : {}", result2.publicKeyModulus)
        topLogger.debug("private key string : {}", result2.privateKeyString)
        topLogger.debug("private key exponent : {}", result2.privateKeyExponent)
        topLogger.debug("private key modulus : {}", result2.privateKeyModulus)

        val keySet: RSAKeySet = RSAKeySet(
            publicKeyModulus = result2.publicKeyModulus!!,
            publicKeyExponent = result2.publicKeyExponent!!,
            privateKeyModulus = result2.privateKeyModulus!!,
            privateKeyExponent = result2.privateKeyExponent!!,
            makeEncodedString = true
        )

        assertNotNull(keySet, "RSA 지수/계수를 이용한 인스턴스 생성 실패")
        assertNotNull(keySet.privateKey, "RSA 지수/계수를 이용한 인스턴스 생성시 개인키 지정 실패")
        assertTrue(StringUtils.isNotBlank(keySet.privateKeyString), "RSA 지수/계수를 이용한 인스턴스 생성시 개인키 base64 문자열 지정 실패")
        assertNotNull(keySet.publicKey, "RSA 지수/계수를 이용한 인스턴스 생성시 공개키 지정 실패")
        assertTrue(StringUtils.isNotBlank(keySet.publicKeyString), "RSA 지수/계수를 이용한 인스턴스 생성시 공개키 base64 문자열 지정 실패")
    }
}