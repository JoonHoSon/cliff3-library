package net.cliff3.maven.common.util

/**
 * CryptoException
 *
 * @author JoonHo Son
 * @version 1.0.0 2019-09-09 최초 작성
 * @since 1.0.0
 */
class CryptoException(message: String): Exception(message) {
    private var throwable: Throwable? = null

    constructor(message: String, throwable: Throwable): this(message) {
        this.throwable = throwable
    }
}