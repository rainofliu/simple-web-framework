package org.geekbang.framework.proxy;

import org.geekbang.framework.annotation.Transaction;
import org.geekbang.framework.helper.DatabaseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 事务代理
 */
public class TransactionProxy implements Proxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionProxy.class);

    private static final ThreadLocal<Boolean> FLAG_HOLDER = ThreadLocal.withInitial(() -> Boolean.FALSE);

    @Override
    public Object doProxy(ProxyChain proxyChain) throws Throwable {
        Object result;

        Boolean flag = FLAG_HOLDER.get();
        // 目标方法
        Method targetMethod = proxyChain.getTargetMethod();

        if (!flag && targetMethod.isAnnotationPresent(Transaction.class)) {
            FLAG_HOLDER.set(true);

            try {
                DatabaseHelper.beginTransaction();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("begin transaction");
                }
                result = proxyChain.doProxyChain();

                DatabaseHelper.commitTransaction();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("commit transaction");
                }
            } catch (Exception e) {

                DatabaseHelper.rollBackTransaction();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("rollback transaction");
                }
                throw e;
            } finally {
                FLAG_HOLDER.remove();
            }
        } else {
            result = proxyChain.doProxyChain();
        }

        return result;
    }
}
