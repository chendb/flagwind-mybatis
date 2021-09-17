package com.flagwind.mybatis.definition.scripting.exception;

/**
 * 绑定参数不存在异常
 */
public class BindingScriptException extends ScriptException {

    public BindingScriptException(String message) {
        super(message);
    }
}
