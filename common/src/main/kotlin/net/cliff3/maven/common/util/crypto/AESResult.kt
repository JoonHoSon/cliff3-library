package net.cliff3.maven.common.util.crypto

/**
 * AESResult
 *
 * @author JoonHo Son
 * @since 0.3.0
 * @property salt salt
 * @property result AES 암호화 결과
 * @property iv Initialize vector
 * @property repeatCount 반복 횟수
 */
class AESResult(val salt: ByteArray, val result: ByteArray, val iv: ByteArray, val repeatCount: Int) {
}