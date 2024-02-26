package net.cliff3.maven.common.exception

/**
 * 잘못된 인자
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
class InvalidArgumentException(val causeMessage: String? = null) : RuntimeException() {
}