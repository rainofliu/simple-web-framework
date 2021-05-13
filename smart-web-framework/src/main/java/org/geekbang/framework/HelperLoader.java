package org.geekbang.framework;

import org.geekbang.framework.helper.BeanHelper;
import org.geekbang.framework.helper.ClassHelper;
import org.geekbang.framework.helper.ControllerHelper;
import org.geekbang.framework.helper.IocHelper;
import org.geekbang.framework.utils.ClassUtil;

/**
 * 助手类 加载器
 */
public class HelperLoader {

    public static void init() {
        Class<?>[] initClasses =
            new Class[] {ClassHelper.class, BeanHelper.class, IocHelper.class, ControllerHelper.class};

        for (Class<?> initClass : initClasses) {
            ClassUtil.loadClass(initClass.getName());
        }
    }
}
