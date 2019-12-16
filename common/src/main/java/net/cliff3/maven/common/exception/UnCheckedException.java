package net.cliff3.maven.common.exception;

import lombok.Getter;

/**
 * UnCheckedException
 *
 * @author JoonHo Son
 * @version 1.0.0 2019-12-13 최초 작성
 * @since 1.0.0
 */
public class UnCheckedException extends RuntimeException {
    public UnCheckedException(Throwable cause) {
        super(cause);
    }
}
