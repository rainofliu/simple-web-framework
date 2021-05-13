package org.geekbang.framework.bean;

import org.geekbang.framework.annotation.Action;
import org.geekbang.framework.annotation.Controller;

import java.lang.reflect.Method;

/**
 * 封装{@link Action}信息
 */
public class Handler {
    /**
     * {@link Controller}类
     */
    private Class<?> controllerClass;
    /**
     * Action方法
     */
    private Method   method;

    public Handler(Class<?> controllerClass, Method method) {

        this.controllerClass = controllerClass;
        this.method = method;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public Method getMethod() {
        return method;
    }
}
