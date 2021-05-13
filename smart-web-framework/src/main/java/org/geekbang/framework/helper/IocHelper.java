package org.geekbang.framework.helper;

import org.geekbang.framework.annotation.Inject;
import org.geekbang.framework.utils.ArrayUtil;
import org.geekbang.framework.utils.CollectionUtil;
import org.geekbang.framework.utils.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * IOC助手类
 */
public final class IocHelper {

    static {
        Map<Class<?>, Object> beanClassMap = BeanHelper.getBeanMap();
        if (CollectionUtil.isNotEmpty(beanClassMap)) {
            // 遍历Bean Map
            for (Map.Entry<Class<?>, Object> entry : beanClassMap.entrySet()) {
                Class<?> beanClass = entry.getKey();
                // Bean实例对象
                Object beanInstance = entry.getValue();

                Field[] declaredFields = beanClass.getDeclaredFields();

                if (ArrayUtil.isNotEmpty(declaredFields)) {
                    for (Field beanField : declaredFields) {
                        if (beanField.isAnnotationPresent(Inject.class)) {

                            Class<?> beanFieldClass = beanField.getType();

                            Object fieldBeanInstance = beanClassMap.get(beanFieldClass);
                            if (null != fieldBeanInstance) {
                                //利用反射初始化 field Value
                                ReflectionUtil.setField(beanInstance, beanField, fieldBeanInstance);
                            }
                        }
                    }
                }
            }
        }
    }
}
