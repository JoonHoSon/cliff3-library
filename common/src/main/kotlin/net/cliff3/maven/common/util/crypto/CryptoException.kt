package net.cliff3.maven.common.util.crypto

/**
 * CryptoException
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
class CryptoException(val causeMessage: String = "암호화 대상 문자열이 없음", override val cause: Throwable?) :
    RuntimeException(causeMessage, cause) {
}