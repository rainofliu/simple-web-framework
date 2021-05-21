package org.geekbang.framework.helper;

import org.geekbang.framework.constants.ConfigConstants;
import org.geekbang.framework.utils.PropertiesUtil;

import java.util.Properties;

/**
 * 属性文件助手类
 *
 * @author ajin
 */
public class ConfigHelper {

    private static final Properties CONFIG_PROPERTIES = PropertiesUtil.loadProperties(ConfigConstants.CONFIG_FILE);

    /**
     * 获取jdbc驱动
     */
    public static String getJdbcDriver() {
        return PropertiesUtil.getString(CONFIG_PROPERTIES, ConfigConstants.JDBC_DRIVER);
    }

    public static String getJdbcUrl() {
        return PropertiesUtil.getString(CONFIG_PROPERTIES, ConfigConstants.JDBC_URL);
    }

    public static String getJdbcUserName() {
        return PropertiesUtil.getString(CONFIG_PROPERTIES, ConfigConstants.JDBC_USERNAME);
    }

    public static String getJdbcPassword() {
        return PropertiesUtil.getString(CONFIG_PROPERTIES, ConfigConstants.JDBC_PASSWORD);
    }

    public static String getAppBasePackage() {
        return PropertiesUtil.getString(CONFIG_PROPERTIES, ConfigConstants.APP_BASE_PACKAGE);
    }

    public static String getAppJspPath() {
        return PropertiesUtil.getString(CONFIG_PROPERTIES, ConfigConstants.APP_JSP_PATH, "/WEB-INF/view/");
    }

    public static String getAppAssetPath() {
        return PropertiesUtil.getString(CONFIG_PROPERTIES, ConfigConstants.APP_ASSET_PATH, "/asset/");
    }

    public static int getAppUploadLimit() {
        return PropertiesUtil.getInt(CONFIG_PROPERTIES, ConfigConstants.APP_UPLOAD_LIMIT, 10);

    }
}
