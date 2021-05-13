package org.geekbang.framework.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 视图对象
 *
 * @author ajin
 */
public class View {
    /**
     * 视图路径
     */
    private String              path;
    /**
     * 模型数据
     */
    private Map<String, Object> model;

    public View(String path) {
        this.path = path;
        this.model = new HashMap<>();
    }

    public View addModel(String key, Object value) {
        this.model.put(key, value);
        return this;
    }

    public String getPath() {
        return path;
    }

    public Map<String, Object> getModel() {
        return model;
    }
}
