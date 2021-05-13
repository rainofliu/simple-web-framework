package org.geekbang.framework;

import org.apache.commons.lang3.StringUtils;
import org.geekbang.framework.bean.Data;
import org.geekbang.framework.bean.Handler;
import org.geekbang.framework.bean.Param;
import org.geekbang.framework.bean.View;
import org.geekbang.framework.helper.BeanHelper;
import org.geekbang.framework.helper.ConfigHelper;
import org.geekbang.framework.helper.ControllerHelper;
import org.geekbang.framework.utils.*;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求转发器
 */
@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 初始化Helper类
        HelperLoader.init();

        ServletContext servletContext = config.getServletContext();
        // 注册处理Jsp的Servlet
        ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        jspServlet.addMapping(ConfigHelper.getAppJspPath() + "*");

        // 注册处理静态资源的默认Servlet
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        defaultServlet.addMapping(ConfigHelper.getAppAssetPath() + "*");

    }

    @Override
    public final void service(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String requestMethod = request.getMethod().toLowerCase();
        String requestPath   = request.getPathInfo();

        // Handler : Controller 和 Action方法的封装
        Handler handler = ControllerHelper.getHandler(requestMethod, requestPath);

        if (handler != null) {
            Class<?> controllerClass = handler.getControllerClass();
            // 找到Controller Bean对象
            Object controllerBeanInstance = BeanHelper.getBean(controllerClass);

            // 封装请求参数
            Map<String, Object> paramMap = new HashMap<>();

            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                // 参数名
                String parameterName = parameterNames.nextElement();
                // 参数值
                String parameterValue = request.getParameter(parameterName);

                paramMap.put(parameterName, parameterValue);
            }

            // 封装请求体
            ServletInputStream inputStream       = request.getInputStream();
            String             requestBodyString = StreamUtil.getString(inputStream);
            String             body              = CodecUtil.decodeURL(requestBodyString);

            if (StringUtil.isNotEmpty(body)) {
                String[] params = StringUtils.split(body, "&");
                if (ArrayUtil.isNotEmpty(params)) {
                    for (String param : params) {
                        String[] array = StringUtils.split(param, "=");
                        if (ArrayUtil.isNotEmpty(array) && array.length == 2) {
                            String paramName  = array[0];
                            String paramValue = array[1];
                            paramMap.put(paramName, paramValue);
                        }
                    }
                }
            }

            Param param = new Param(paramMap);

            // Action 方法：真正处理请求的方法
            Method method = handler.getMethod();
            // 调用Action 方法
            Object result = ReflectionUtil.invokeMethod(controllerBeanInstance, method, param);

            // 传统MVC架构
            if (result instanceof View) {
                View view = (View)request;
                // 返回的视图
                String path = view.getPath();
                if (StringUtil.isNotEmpty(path)) {
                    if (path.startsWith("/")) {
                        // 重定向
                        response.sendRedirect(request.getContextPath() + path);
                    } else {
                        // 核心逻辑
                        Map<String, Object> model = view.getModel();
                        for (Map.Entry<String, Object> entry : model.entrySet()) {
                            request.setAttribute(entry.getKey(), entry.getValue());
                        }

                        request.getRequestDispatcher(ConfigHelper.getAppBasePackage() + path).forward(request,
                            response);
                    }
                }
            } else if (result instanceof Data) {
                // REST API
                Data   data  = (Data)result;
                Object model = data.getModel();
                if (null != model) {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");

                    PrintWriter writer = response.getWriter();
                    String      json   = JsonUtil.toJson(model);
                    writer.write(json);
                    writer.flush();
                    writer.close();
                }
            }
        }
    }
}
