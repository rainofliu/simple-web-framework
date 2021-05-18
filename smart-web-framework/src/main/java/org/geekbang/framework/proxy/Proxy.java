package org.geekbang.framework.proxy;

/**
 * 代理接口
 *
 * @author ajin
 **/
public interface Proxy {

    /**
     * 执行链式代理：将多个代理通过一个链子串起来，一个个地去执行，
     * 执行顺序取决于添加到链上的顺序
     */
    Object doProxy(ProxyChain proxyChain) throws Throwable;
}
