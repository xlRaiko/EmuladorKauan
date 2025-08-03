/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.util;

import ch.qos.logback.core.spi.ContextAware;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

public class StringToObjectConverter {
    private static final Class<?>[] STING_CLASS_PARAMETER = new Class[]{String.class};

    public static boolean canBeBuiltFromSimpleString(Class<?> parameterClass) {
        Package p = parameterClass.getPackage();
        if (parameterClass.isPrimitive()) {
            return true;
        }
        if (p != null && "java.lang".equals(p.getName())) {
            return true;
        }
        if (StringToObjectConverter.followsTheValueOfConvention(parameterClass)) {
            return true;
        }
        if (parameterClass.isEnum()) {
            return true;
        }
        return StringToObjectConverter.isOfTypeCharset(parameterClass);
    }

    public static Object convertArg(ContextAware ca, String val, Class<?> type) {
        if (val == null) {
            return null;
        }
        String v = val.trim();
        if (String.class.isAssignableFrom(type)) {
            return v;
        }
        if (Integer.TYPE.isAssignableFrom(type)) {
            return new Integer(v);
        }
        if (Long.TYPE.isAssignableFrom(type)) {
            return new Long(v);
        }
        if (Float.TYPE.isAssignableFrom(type)) {
            return new Float(v);
        }
        if (Double.TYPE.isAssignableFrom(type)) {
            return new Double(v);
        }
        if (Boolean.TYPE.isAssignableFrom(type)) {
            if ("true".equalsIgnoreCase(v)) {
                return Boolean.TRUE;
            }
            if ("false".equalsIgnoreCase(v)) {
                return Boolean.FALSE;
            }
        } else {
            if (type.isEnum()) {
                return StringToObjectConverter.convertToEnum(ca, v, type);
            }
            if (StringToObjectConverter.followsTheValueOfConvention(type)) {
                return StringToObjectConverter.convertByValueOfMethod(ca, type, v);
            }
            if (StringToObjectConverter.isOfTypeCharset(type)) {
                return StringToObjectConverter.convertToCharset(ca, val);
            }
        }
        return null;
    }

    private static boolean isOfTypeCharset(Class<?> type) {
        return Charset.class.isAssignableFrom(type);
    }

    private static Charset convertToCharset(ContextAware ca, String val) {
        try {
            return Charset.forName(val);
        }
        catch (UnsupportedCharsetException e) {
            ca.addError("Failed to get charset [" + val + "]", e);
            return null;
        }
    }

    public static Method getValueOfMethod(Class<?> type) {
        try {
            return type.getMethod("valueOf", STING_CLASS_PARAMETER);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
        catch (SecurityException e) {
            return null;
        }
    }

    private static boolean followsTheValueOfConvention(Class<?> parameterClass) {
        Method valueOfMethod = StringToObjectConverter.getValueOfMethod(parameterClass);
        if (valueOfMethod == null) {
            return false;
        }
        int mod = valueOfMethod.getModifiers();
        return Modifier.isStatic(mod);
    }

    private static Object convertByValueOfMethod(ContextAware ca, Class<?> type, String val) {
        try {
            Method valueOfMethod = type.getMethod("valueOf", STING_CLASS_PARAMETER);
            return valueOfMethod.invoke(null, val);
        }
        catch (Exception e) {
            ca.addError("Failed to invoke valueOf{} method in class [" + type.getName() + "] with value [" + val + "]");
            return null;
        }
    }

    private static Object convertToEnum(ContextAware ca, String val, Class<? extends Enum> enumType) {
        return Enum.valueOf(enumType, val);
    }

    boolean isBuildableFromSimpleString() {
        return false;
    }
}

