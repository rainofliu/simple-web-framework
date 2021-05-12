package org.geekbang.utils;

/**
 * 简单的类型转换工具类
 *
 * @author ajin
 */
public final class CastUtil {

    private static final String EMPTY_STRING = "";

    public static String castString(Object source) {
        return castString(source, EMPTY_STRING);
    }

    public static String castString(Object source, String defaultValue) {
        return source != null ? String.valueOf(source) : defaultValue;
    }

    public static double castDouble(Object source) {
        return castDouble(source, 0D);
    }

    public static double castDouble(Object source, double defaultValue) {
        double doubleValue = defaultValue;
        if (source != null) {
            String strValue = castString(source);
            if (StringUtil.isNotEmpty(strValue)) {
                try {
                    doubleValue = Double.parseDouble(strValue);
                } catch (NumberFormatException e) {
                    doubleValue = defaultValue;
                }
            }
        }
        return doubleValue;
    }

    public static long castLong(Object source) {
        return castLong(source, 0L);
    }

    public static long castLong(Object source, long defaultValue) {
        long longValue = defaultValue;
        if (null != source) {
            String strValue = castString(source);
            if (StringUtil.isNotEmpty(strValue)) {
                try {
                    longValue = Long.parseLong(strValue);
                } catch (NumberFormatException e) {
                    longValue = defaultValue;
                }
            }
        }
        return longValue;
    }

    public static int castInt(Object source) {
        return castInt(source, 0);
    }

    // Object -> String -> int
    public static int castInt(Object source, int defaultValue) {
        int intValue = defaultValue;
        if (null != source) {
            String strValue = castString(source);
            if (StringUtil.isNotEmpty(strValue)) {
                try {
                    intValue = Integer.parseInt(strValue);
                } catch (NumberFormatException e) {
                    intValue = defaultValue;
                }
            }
        }
        return intValue;
    }

    public static boolean castBoolean(Object source) {
        return castBoolean(source, false);
    }

    public static boolean castBoolean(Object source, boolean defaultValue) {
        boolean booleanValue = defaultValue;
        if (null != source) {
            String strValue = castString(source);
            if (StringUtil.isNotEmpty(strValue)) {
                try {
                    booleanValue = Boolean.parseBoolean(strValue);
                } catch (Exception e) {
                    booleanValue = defaultValue;
                }
            }
        }

        return booleanValue;
    }

}
