package com.flagwind.mybatis.definition.scripting.exception;


public class ScriptException extends RuntimeException {

    public ScriptException(String message) {
        super(message);
    }

    public ScriptException(String message, Throwable cause) {
        super(message, cause);
    }

}
