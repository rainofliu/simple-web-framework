package org.geekbang.framework.helper;

import org.geekbang.framework.bean.FormParam;
import org.geekbang.framework.bean.Param;
import org.geekbang.framework.utils.ArrayUtil;
import org.geekbang.framework.utils.CodecUtil;
import org.geekbang.framework.utils.StreamUtil;
import org.geekbang.framework.utils.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 请求助手类
 */
public final class RequestHelper {

    /**
     * 创建请求参数对象
     */
    public static Param createParam(HttpServletRequest request) throws IOException {
        List<FormParam> formParamList = new ArrayList<>();
        formParamList.addAll(parseParameterNames(request));
        formParamList.addAll(parseInputStream(request));

        return new Param(formParamList);

    }

    private static List<FormParam> parseParameterNames(HttpServletRequest request) {
        List<FormParam> formParamList = new ArrayList<>();

        Enumeration<String> enumeration = request.getParameterNames();

        while (enumeration.hasMoreElements()) {
            String fieldName = enumeration.nextElement();
            // String parameterValue = request.getParameter(parameterName);
            // formParamList.add(new FormParam(parameterName, parameterValue));

            String[] fieldValues = request.getParameterValues(fieldName);

            if (ArrayUtil.isNotEmpty(fieldValues)) {
                Object fieldValue;
                if (fieldValues.length == 1) {
                    fieldValue = fieldValues[0];
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < fieldValues.length; i++) {
                        stringBuilder.append(fieldValues[i]);
                        if (i != fieldValues.length - 1) {
                            stringBuilder.append(StringUtil.SEPARATOR);
                        }
                    }
                    fieldValue = stringBuilder.toString();
                }

                formParamList.add(new FormParam(fieldName, fieldValue));
            }

        }

        return formParamList;
    }

    public static List<FormParam> parseInputStream(HttpServletRequest request) throws IOException {
        List<FormParam> formParamList = new ArrayList<>();

        String body = CodecUtil.decodeURL(StreamUtil.getString(request.getInputStream()));
        if (StringUtil.isNotEmpty(body)) {
            String[] kvs = StringUtil.splitString(body, "&");

            if (ArrayUtil.isNotEmpty(kvs)) {
                for (String kv : kvs) {
                    String[] array = StringUtil.splitString(kv, "=");
                    if (ArrayUtil.isNotEmpty(array) && array.length == 2) {
                        String fieldName  = array[0];
                        String fieldValue = array[1];
                        formParamList.add(new FormParam(fieldName, fieldValue));
                    }
                }
            }
        }

        return formParamList;
    }
}
