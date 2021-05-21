package org.geekbang.framework.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet助手类
 */
public final class ServletHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServletHelper.class);

    private static final ThreadLocal<ServletHelper> SERVLET_HELPER_HOLDER = new ThreadLocal<>();

    private HttpServletRequest request;

    private HttpServletResponse response;

    public ServletHelper(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public static void init(HttpServletRequest request, HttpServletResponse response) {
        SERVLET_HELPER_HOLDER.set(new ServletHelper(request, response));
    }

    public static void destroy() {
        SERVLET_HELPER_HOLDER.remove();
    }

    public static HttpServletRequest getRequest() {
        return SERVLET_HELPER_HOLDER.get().request;
    }

    public static HttpServletResponse getResponse() {
        return SERVLET_HELPER_HOLDER.get().response;
    }

    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    public static ServletContext getServletContext() {
        return getRequest().getServletContext();
    }

    public static void setRequestAttribute(String key, Object value) {
        getRequest().setAttribute(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getRequestAttribute(String key) {
        return (T)getRequest().getAttribute(key);
    }

    public static void removeRequestAttribute(String key) {
        getRequest().removeAttribute(key);
    }


    /**
     * 发送重定向响应
     * @param location 重定向地址
     * */
    public static void sendRedirect(String location){
        try {
            getResponse().sendRedirect(location);
        } catch (IOException e) {
            LOGGER.error("send redirect failure",e);
            throw new RuntimeException(e);
        }
    }

    public static void setSessionAttribute(String key, Object value) {
        getSession().setAttribute(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getSessionAttribute(String key) {
        return (T)getSession().getAttribute(key);
    }

    public static void removeSessionAttribute(String key) {
        getSession().removeAttribute(key);
    }

    public static void invalidateSession(){
        getSession().invalidate();
    }


}
