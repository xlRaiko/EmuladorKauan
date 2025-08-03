/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.util;

import ch.qos.logback.core.util.Loader;
import java.util.Iterator;
import java.util.ServiceLoader;

public class EnvUtil {
    static ClassLoader testServiceLoaderClassLoader = null;

    public static boolean isGroovyAvailable() {
        ClassLoader classLoader = Loader.getClassLoaderOfClass(EnvUtil.class);
        try {
            Class<?> bindingClass = classLoader.loadClass("groovy.lang.Binding");
            return bindingClass != null;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static ClassLoader getServiceLoaderClassLoader() {
        return testServiceLoaderClassLoader == null ? Loader.getClassLoaderOfClass(EnvUtil.class) : testServiceLoaderClassLoader;
    }

    public static <T> T loadFromServiceLoader(Class<T> c) {
        ServiceLoader<T> loader = ServiceLoader.load(c, EnvUtil.getServiceLoaderClassLoader());
        Iterator<T> it = loader.iterator();
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }
}

