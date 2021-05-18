package org.geekbang.framework;

import org.geekbang.framework.helper.*;
import org.geekbang.framework.utils.ClassUtil;

/**
 * 助手类 加载器
 */
public class HelperLoader {

    public static void init() {
        Class<?>[] initClasses =
            new Class[] {ClassHelper.class, BeanHelper.class, IocHelper.class, ControllerHelper.class, AopHelper.class};

        for (Class<?> initClass : initClasses) {
            ClassUtil.loadClass(initClass.getName());
        }
    }
}
