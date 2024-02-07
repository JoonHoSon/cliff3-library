package net.cliff3.maven.common.util.crypto

/**
 * AESResult
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
class AESResult(val salt: ByteArray, val result: ByteArray, val iv: ByteArray) {
}