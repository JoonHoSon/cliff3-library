package net.cliff3.maven.common.exception

/**
 * 조회 대상(데이터, 파일 등)이 존재하지 않을 경우 발생
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
open class NotExistException(val causeMessage: String? = null) : RuntimeException() {
}