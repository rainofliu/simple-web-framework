package org.geekbang.framework.annotation;

import java.lang.annotation.*;

/**
 * 切面
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) // 定义在类上
public @interface Aspect {

    /**
     * 注解
     * */
    Class<? extends Annotation> value();
}
