package net.cliff3.maven.common.util

import net.cliff3.maven.common.topLogger
import net.cliff3.maven.common.util.crypto.makeSHA256Hash
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull

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
}