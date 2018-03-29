package com.flagwind.mybatis.exceptions;

/**
 * @author chendb
 */
public class MapperException extends RuntimeException {
    
    private static final long serialVersionUID = 9205621714275918529L;

	public MapperException() {
        super();
    }

    public MapperException(String message) {
        super(message);
    }

    public MapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapperException(Throwable cause) {
        super(cause);
    }

}