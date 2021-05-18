package org.geekbang.framework.proxy;

import org.geekbang.framework.annotation.Aspect;
import org.geekbang.framework.annotation.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 拦截{@link Controller}所有方法
 */
@Aspect(Controller.class)
public class ControllerAspect extends AspectProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerAspect.class);

    private long begin;

    @Override
    public void begin() {

        super.begin();
    }

    @Override
    public void before(Class<?> targetClass, Method targetMethod, Object[] params) throws Throwable {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("begin-----------");
            LOGGER.debug(String.format("class: %s", targetClass.getName()));
            LOGGER.debug(String.format("method: %s", targetMethod.getName()));
            // 开始的时间戳
            begin = System.currentTimeMillis();

        }
    }

    @Override
    public void after(Class<?> targetClass, Method targetMethod, Object[] params) throws Throwable {
        if (LOGGER.isDebugEnabled()) {
            // 打印执行的时间长度
            LOGGER.debug(String.format("execute time: %dms", System.currentTimeMillis() - begin));
            LOGGER.debug("end-----------");

        }
    }

    @Override
    public void error(Class<?> targetClass, Method targetMethod, Object[] params) throws Throwable {
        super.error(targetClass, targetMethod, params);
    }

    @Override
    public void end() {
        super.end();
    }
}
