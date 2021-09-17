package com.flagwind.mybatis.definition.scripting.exception;

/**
 * 注册公式异常
 *
 * @author 奔波儿灞
 * @since 1.0
 */
public class RegistryScriptException extends ScriptException {

    public RegistryScriptException(String message) {
        super(message);
    }

    public RegistryScriptException(String message, Throwable cause) {
        super(message, cause);
    }

}
