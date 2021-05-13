package org.geekbang.framework.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类操作工具类
 *
 * @author ajin
 */
public  class ClassUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassUtil.class);

    /**
     * 获取类加载器
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }


    public static Class<?> loadClass(String className){
        return loadClass(className,true);
    }
    /**
     * 加载类
     */
    public static Class<?> loadClass(String className, boolean isInitialized) {
        Class<?> cls;
        try {
            cls = Class.forName(className, isInitialized, getClassLoader());
        } catch (ClassNotFoundException e) {
            LOGGER.error("load class failure!", e);
            throw new RuntimeException(e);
        }
        return cls;
    }



    /**
     * 获取指定包下的所有类
     */
    public static Set<Class<?>> getClassSet(String packageName) {
        Set<Class<?>> classSet = new HashSet<>();

        ClassLoader classLoader = getClassLoader();
        try {
            Enumeration<URL> enumeration = classLoader.getResources(packageName.replace(".", "/"));
            while (enumeration.hasMoreElements()) {
                URL url = enumeration.nextElement();
                if (url != null) {
                    String protocol = url.getProtocol();
                    if ("file".equals(protocol)) {
                        String packagePath = url.getPath().replaceAll("%20", "");
                        addClass(classSet, packagePath, packageName);
                    } else if ("jar".equals(protocol)) {
                        JarURLConnection connection = (JarURLConnection)url.openConnection();
                        if (connection != null) {
                            JarFile jarFile = connection.getJarFile();
                            if (null != jarFile) {
                                Enumeration<JarEntry> entries = jarFile.entries();
                                while (entries.hasMoreElements()) {
                                    JarEntry jarEntry     = entries.nextElement();
                                    String   jarEntryName = jarEntry.getName();
                                    if (jarEntryName.endsWith(".class")) {
                                        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf("."))
                                            .replaceAll("/", ".");
                                        doAdd(classSet, className);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("get class set failure!", e);
        }
        return classSet;
    }

    private static void addClass(Set<Class<?>> classSet, String packagePath, String packageName) {
        // 对某一个包名下的文件或者路径先列举出来，然后再过滤
        File[] files = new File(packagePath).listFiles(file -> (file.isFile() && file.getName().endsWith(".class"))
            || file.isDirectory());
        for (File file : files) {
            String fileName = file.getName();
            if (file.isFile()) {
                // 获取类名称
                String className = fileName.substring(0, fileName.lastIndexOf("."));
                if (StringUtil.isNotEmpty(packageName)) {
                    // 获取全类名
                    className = packageName + "." + className;
                }
                doAdd(classSet, className);
            } else {
                String subPackagePath = fileName;
                if (StringUtil.isNotEmpty(subPackagePath)) {
                    subPackagePath = packagePath + "/" + subPackagePath;
                }

                String subPackageName = fileName;
                if (StringUtil.isNotEmpty(subPackageName)) {
                    subPackageName = packageName + "." + subPackageName;
                }
                addClass(classSet, subPackagePath, subPackageName);
            }
        }
    }

    private static void doAdd(Set<Class<?>> classSet, String className) {
        Class<?> cls = loadClass(className, false);
        classSet.add(cls);
    }

}
