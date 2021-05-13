package org.geekbang.framework.utils;

import org.apache.commons.lang3.ArrayUtils;

public final class ArrayUtil {

    public static boolean isEmpty(Object[] array) {
        return ArrayUtils.isNotEmpty(array);
    }

    public static boolean isNotEmpty(Object[] array){
        return !isEmpty(array);

    }
}
