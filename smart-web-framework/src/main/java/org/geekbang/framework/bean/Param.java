package org.geekbang.framework.bean;

import org.geekbang.framework.utils.CastUtil;
import org.geekbang.framework.utils.CollectionUtil;
import org.geekbang.framework.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求参数对象
 */
public class Param {

    /**
     * 表单参数列表
     */
    private List<FormParam> formParamList;

    /**
     * 文件参数列表
     */
    private List<FileParam> fileParamList;

    // 参数名-> 参数值
    private Map<String, Object> paramMap;

    public Param(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    /**
     * 只包含表单参数
     */
    public Param(List<FormParam> formParamList) {
        this.formParamList = formParamList;
    }

    public Param(List<FormParam> formParamList, List<FileParam> fileParamList) {
        this.formParamList = formParamList;
        this.fileParamList = fileParamList;
    }

    // /**
    //  * 根据参数名获取long型参数值
    //  */
    // public long getLong(String name) {
    //     return CastUtil.castLong(paramMap.get(name));
    // }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    // public boolean isEmpty() {
    //     return CollectionUtil.isEmpty(paramMap);
    // }

    /**
     * 获取请求参数映射
     */
    public Map<String, Object> getFieldMap() {
        Map<String, Object> fieldMap = new HashMap<>();

        if (CollectionUtil.isNotEmpty(formParamList)) {
            // 遍历表单参数
            for (FormParam formParam : formParamList) {
                String fieldName  = formParam.getFieldName();
                String fieldValue = formParam.getFieldValue();

                if (fieldMap.containsKey(fieldName)) {
                    fieldValue = fieldMap.get(fieldName) + StringUtil.SEPARATOR + fieldValue;
                }
                fieldMap.put(fieldName, fieldValue);
            }
        }

        return fieldMap;
    }

    /**
     * 获取上传文件映射
     */
    public Map<String, List<FileParam>> getFileMap() {
        Map<String, List<FileParam>> fileMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(fileParamList)) {
            // 遍历文件参数
            for (FileParam fileParam : fileParamList) {
                // 文件上传的字段名
                String fieldName = fileParam.getFieldName();

                List<FileParam> fileParams;
                if (!fileMap.containsKey(fieldName)) {
                    fileParams = new ArrayList<>();
                } else {
                    fileParams = fileMap.get(fieldName);
                }
                fileParams.add(fileParam);

                fileMap.put(fieldName, fileParams);
            }
        }

        return fileMap;
    }

    /**
     * 获取所有上传文件
     */
    public List<FileParam> getFileList(String fieldName) {
        return getFileMap().get(fieldName);
    }

    /**
     * 获取唯一上传文件
     */
    public FileParam getFile(String fieldName) {
        List<FileParam> fileParams = getFileList(fieldName);

        if (CollectionUtil.isNotEmpty(fileParams) && fileParams.size() == 1) {
            return fileParams.get(0);
        }

        return null;

    }

    /**
     * 验证参数是否为空
     */
    public boolean isEmpty() {
        return CollectionUtil.isEmpty(formParamList) && CollectionUtil.isNotEmpty(fileParamList);
    }

    /**
     * 根据参数名 获取String类型 参数值
     */
    public String getString(String name) {
        return CastUtil.castString(getFieldMap().get(name));
    }

    /**
     * 根据参数名 获取Double类型 参数值
     */
    public double getDouble(String name) {
        return CastUtil.castDouble(getFieldMap().get(name));
    }

    /**
     * 根据参数名 获取long类型 参数值
     */
    public long getLong(String name) {
        return CastUtil.castLong(getFieldMap().get(name));
    }

    /**
     * 根据参数名 获取int类型 参数值
     */
    public int getInt(String name) {
        return CastUtil.castInt(getFieldMap().get(name));
    }

    /**
     * 根据参数名 获取Boolean类型 参数值
     */
    public boolean getBoolean(String name) {
        return CastUtil.castBoolean(getFieldMap().get(name));
    }

}
