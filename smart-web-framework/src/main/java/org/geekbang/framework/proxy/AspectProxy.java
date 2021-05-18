package org.geekbang.framework.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 切面代理类
 */
public abstract class AspectProxy implements Proxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(AspectProxy.class);

    @Override
    public Object doProxy(ProxyChain proxyChain) throws Throwable {
        Object result = null;

        // 目标类
        Class<?> targetClass = proxyChain.getTargetClass();
        // 目标方法
        Method targetMethod = proxyChain.getTargetMethod();
        // 方法参数 -> 对象数组
        Object[] methodParams = proxyChain.getMethodParams();

        begin();

        try {
            // 如果需要拦截
            if (intercept(targetClass, targetMethod, methodParams)) {
                before(targetClass, targetMethod, methodParams);
                // 代理链
                result = proxyChain.doProxyChain();
                after(targetClass, targetMethod, methodParams);
            } else {
                result = proxyChain.doProxyChain();

            }

        } catch (Throwable e) {
            LOGGER.error("proxy failure", e);

            error(targetClass, targetMethod, methodParams);
            // 抛出异常
            throw e;
        } finally {
            end();
        }

        return result;
    }
    // 钩子方法
    public void begin() {

    }

    /**
     * 是否要拦截
     */
    public boolean intercept(Class<?> targetClass, Method targetMethod, Object[] params) throws Throwable {
        return true;
    }

    // 钩子方法
    public void before(Class<?> targetClass, Method targetMethod, Object[] params) throws Throwable {

    }
    // 钩子方法
    public void after(Class<?> targetClass, Method targetMethod, Object[] params) throws Throwable {

    }
    // 钩子方法
    public void error(Class<?> targetClass, Method targetMethod, Object[] params) throws Throwable {

    }
    // 钩子方法
    public void end() {

    }
}
