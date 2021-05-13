package org.geekbang.framework.bean;

import org.geekbang.framework.utils.CastUtil;

import java.util.Map;

/**
 * 请求参数对象
 */
public class Param {
    // 参数名-> 参数值
    private Map<String, Object> paramMap;

    public Param(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    /**
     * 根据参数名获取long型参数值
     */
    public long getLong(String name) {
        return CastUtil.castLong(paramMap.get(name));
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }
}
