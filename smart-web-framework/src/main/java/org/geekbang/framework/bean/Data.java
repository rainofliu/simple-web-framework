package org.geekbang.framework.bean;

/**
 * 数据对象
 */
public class Data {
    /**
     * 模型数据
     * */
    private Object model;

    public Object getModel() {
        return model;
    }

    public Data(Object model) {

        this.model = model;
    }
}
