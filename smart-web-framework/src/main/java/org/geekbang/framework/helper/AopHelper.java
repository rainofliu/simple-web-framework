package org.geekbang.framework.helper;

import org.geekbang.framework.annotation.Aspect;
import org.geekbang.framework.proxy.AspectProxy;
import org.geekbang.framework.proxy.Proxy;
import org.geekbang.framework.proxy.ProxyManagement;
import org.geekbang.framework.proxy.TransactionProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * AOP助手类
 *
 * @author ajin
 */
public final class AopHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AopHelper.class);

    /**
     * 使用静态代码块 初始化AOP框架
     * */
    static {
        try {
            Map<Class<?>, Set<Class<?>>> proxyMap  = createProxyMap();
            Map<Class<?>, List<Proxy>>   targetMap = createTargetMap(proxyMap);
            for (Map.Entry<Class<?>, List<Proxy>> targetEntry : targetMap.entrySet()) {
                // 目标类 ：  XXXController.class
                Class<?> targetClass = targetEntry.getKey();
                // AspectProxy的子类
                List<Proxy> proxyList = targetEntry.getValue();

                // 创建真正的代理对象
                Object proxy = ProxyManagement.createProxy(targetClass, proxyList);

                // 类似于Spring中注册 代理Bean
                BeanHelper.setBean(targetClass, proxy);

            }

        } catch (Exception e) {
            LOGGER.error("load aop failure", e);
        }
    }

    // /**
    //  * 拦截{@link Controller}所有方法
    //  */
    // @Aspect(Controller.class)
    // public class ControllerAspect extends AspectProxy {

    /**
     * @param aspect 标注在类上的{@link Aspect}注解（包含属性值：value() ）
     */
    private static Set<Class<?>> createTargetClassSet(Aspect aspect) throws Exception {
        Set<Class<?>> targetClassSet = new HashSet<>();

        // 获取@Aspect注解中 设置的 注解类 @XXX(Annotation) -> @Controller
        Class<? extends Annotation> annotation = aspect.value();

        // 如果注解类 不是Aspect类
        if (!annotation.equals(Aspect.class)) {
            // 获取该注解 ( @Controller )标注的类，并将类添加到目标类Set中
            targetClassSet.addAll(ClassHelper.getClassSetByAnnotation(annotation));
        }
        return targetClassSet;
    }

    /**
     * 创建代理类  和 目标类Set 的key value 映射关系
     */
    private static Map<Class<?>, Set<Class<?>>> createProxyMap() throws Exception {
        Map<Class<?>, Set<Class<?>>> proxyMap = new HashMap<>();
        addAspectProxy(proxyMap);
        createTransactionProxy(proxyMap);

        return proxyMap;
    }

    private static void addAspectProxy(Map<Class<?>, Set<Class<?>>> proxyMap) throws Exception {
        //AspectProxy的子类  Set    ControllerAspect
        Set<Class<?>> proxyClassSet = ClassHelper.getClassSetBySuper(AspectProxy.class);

        for (Class<?> proxyClass : proxyClassSet) {
            if (proxyClass.isAnnotationPresent(Aspect.class)) {
                Aspect aspect = proxyClass.getAnnotation(Aspect.class);
                // /**
                //  * 拦截{@link Controller}所有方法
                //  */
                // @Aspect(Controller.class)
                // public class ControllerAspect extends AspectProxy {
                // 创建目标类Set  目标类就是Controller标注的类 XXXController
                Set<Class<?>> targetClassSet = createTargetClassSet(aspect);
                proxyMap.put(proxyClass, targetClassSet);
            }
        }
    }

    private static void createTransactionProxy(Map<Class<?>, Set<Class<?>>> proxyMap) throws Exception {
        Set<Class<?>> serviceClassSet = ClassHelper.getServiceClassSet();
        proxyMap.put(TransactionProxy.class, serviceClassSet);
    }

    /**
     * Map<Class<?>, List<Proxy>>  : 目标类  -> 代理对象列表
     */
    private static Map<Class<?>, List<Proxy>> createTargetMap(Map<Class<?>, Set<Class<?>>> proxyMap) throws Exception {
        // Map<Class<?>, Set<Class<?>>> proxyMap
        // Class<?> : ControllerAspect Class
        // Set<Class<?>>  :  AController Class/ BController Class  -> Set

        Map<Class<?>, List<Proxy>> targetMap = new HashMap<>();

        // 遍历proxyMap
        for (Map.Entry<Class<?>, Set<Class<?>>> proxyEntry : proxyMap.entrySet()) {
            // 代理类(Proxy) Class
            Class<?> proxyClass = proxyEntry.getKey();
            // 目标类  Set
            Set<Class<?>> targetClassSet = proxyEntry.getValue();

            for (Class<?> targetClass : targetClassSet) {
                // 通过反射创建代理类对象
                Proxy proxy = (Proxy)proxyClass.newInstance();

                if (targetMap.containsKey(targetClass)) {
                    targetMap.get(targetClass).add(proxy);
                } else {
                    List<Proxy> proxyList = new ArrayList<>();
                    proxyList.add(proxy);
                    targetMap.put(targetClass, proxyList);
                }
            }
        }
        //  Class : target Class  -> AController.class
        // List<Proxy>  : ControllerAspect/ XXXAspect
        return targetMap;
    }
}
