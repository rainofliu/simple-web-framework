package org.geekbang.framework.helper;

import org.geekbang.framework.annotation.Action;
import org.geekbang.framework.bean.Handler;
import org.geekbang.framework.bean.Request;
import org.geekbang.framework.utils.ArrayUtil;
import org.geekbang.framework.utils.CollectionUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 控制器助手类
 *
 * @author ajin
 */
public final class ControllerHelper {

    private static final Map<Request, Handler> ACTION_MAP = new HashMap<>();

    static {
        Set<Class<?>> controllerClassSet = ClassHelper.getControllerClassSet();
        if (CollectionUtil.isNotEmpty(controllerClassSet)) {
            for (Class<?> controllerClass : controllerClassSet) {
                Method[] methods = controllerClass.getDeclaredMethods();

                if (ArrayUtil.isNotEmpty(methods)) {
                    for (Method handlerMethod : methods) {
                        if (handlerMethod.isAnnotationPresent(Action.class)) {
                            Action action = handlerMethod.getAnnotation(Action.class);

                            String mapping = action.value();

                            // 验证URL映射规则
                            if (mapping.matches("\\w+:/\\w*")) {
                                String[] array = mapping.split(":");

                                if (ArrayUtil.isNotEmpty(array) && array.length == 2) {
                                    // 获取请求方法和请求路径
                                    String requestMethod = array[0];
                                    String requestPath   = array[1];

                                    Request request = new Request(requestMethod, requestPath);

                                    Handler handler = new Handler(controllerClass, handlerMethod);

                                    ACTION_MAP.put(request, handler);
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    /**
     * 获取{@link Handler}
     */
    public static Handler getHandler(String requestMethod, String requestPath) {
        Request request = new Request(requestMethod, requestPath);
        return ACTION_MAP.get(request);
    }
}
