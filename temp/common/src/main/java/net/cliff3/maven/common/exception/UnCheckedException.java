package net.cliff3.maven.common.exception;

import lombok.Getter;

/**
 * UnCheckedException
 *
 * @author JoonHo Son
 * @since 0.1.0
 */
public class UnCheckedException extends RuntimeException {
    public UnCheckedException(Throwable cause) {
        super(cause);
    }
}
