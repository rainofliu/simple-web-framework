package org.geekbang.framework.helper;

import org.geekbang.framework.annotation.Controller;
import org.geekbang.framework.annotation.Service;
import org.geekbang.framework.utils.ClassUtil;

import java.lang.annotation.Annotation;
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
    public static final Set<Class<?>> CLASS_SET;

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

    /**
     * 获取某应用包下某父类（或接口）的所有子类（或实现类）
     */
    public static Set<Class<?>> getClassSetBySuper(Class<?> superClass) {
        Set<Class<?>> classSet = new HashSet<>();
        // CLASS_SET:应用包下所有的类
        for (Class<?> cls : CLASS_SET) {
            // 判断子类或接口的实现类
            if (superClass.isAssignableFrom(cls) && !superClass.equals(cls)) {
                classSet.add(cls);
            }
        }

        return classSet;
    }

    /**
     * 获取应用包下带有某注解的所有类
     */
    public static Set<Class<?>> getClassSetByAnnotation(Class<? extends Annotation> annotationClass) {
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> cls : CLASS_SET) {
            if (cls.isAnnotationPresent(annotationClass)) {
                classSet.add(cls);
            }
        }

        return classSet;
    }

}
