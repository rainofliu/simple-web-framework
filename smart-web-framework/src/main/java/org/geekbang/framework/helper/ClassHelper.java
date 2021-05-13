package org.geekbang.framework.helper;

import org.geekbang.framework.annotation.Controller;
import org.geekbang.framework.annotation.Service;
import org.geekbang.framework.utils.ClassUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 类操作助手类
 */
public final class ClassHelper {
    /**
     * 用于存放所加载的类
     */
    private static final Set<Class<?>> CLASS_SET;

    static {
        String appBasePackage = ConfigHelper.getAppBasePackage();
        CLASS_SET = ClassUtil.getClassSet(appBasePackage);
    }

    public static Set<Class<?>> getClassSet() {
        return CLASS_SET;
    }

    /**
     * 获取应用包下所有的{@link Service}类
     */
    public static Set<Class<?>> getServiceClassSet() {
        return CLASS_SET.stream().filter(klass -> klass.isAnnotationPresent(Service.class)).collect(Collectors.toSet());
    }

    /**
     * 获取应用包下所有的{@link Controller}类
     */
    public static Set<Class<?>> getControllerClassSet() {
        return CLASS_SET.stream().filter(klass -> klass.isAnnotationPresent(Controller.class)).collect(
            Collectors.toSet());
    }

    /**
     * 获取应用包下所有的Bean Class
     */
    public static Set<Class<?>> getBeanClassSet() {
        Set<Class<?>> beanClassSet       = new HashSet<>();
        Set<Class<?>> serviceClassSet    = getServiceClassSet();
        Set<Class<?>> controllerClassSet = getControllerClassSet();

        beanClassSet.addAll(serviceClassSet);
        beanClassSet.addAll(controllerClassSet);

        return beanClassSet;
    }

}
