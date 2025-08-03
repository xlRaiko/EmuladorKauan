/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.ContextUtil;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Properties;

public class ActionUtil {
    public static Scope stringToScope(String scopeStr) {
        if (Scope.SYSTEM.toString().equalsIgnoreCase(scopeStr)) {
            return Scope.SYSTEM;
        }
        if (Scope.CONTEXT.toString().equalsIgnoreCase(scopeStr)) {
            return Scope.CONTEXT;
        }
        return Scope.LOCAL;
    }

    public static void setProperty(InterpretationContext ic, String key, String value, Scope scope) {
        switch (scope) {
            case LOCAL: {
                ic.addSubstitutionProperty(key, value);
                break;
            }
            case CONTEXT: {
                ic.getContext().putProperty(key, value);
                break;
            }
            case SYSTEM: {
                OptionHelper.setSystemProperty(ic, key, value);
            }
        }
    }

    public static void setProperties(InterpretationContext ic, Properties props, Scope scope) {
        switch (scope) {
            case LOCAL: {
                ic.addSubstitutionProperties(props);
                break;
            }
            case CONTEXT: {
                ContextUtil cu = new ContextUtil(ic.getContext());
                cu.addProperties(props);
                break;
            }
            case SYSTEM: {
                OptionHelper.setSystemProperties(ic, props);
            }
        }
    }

    public static enum Scope {
        LOCAL,
        CONTEXT,
        SYSTEM;

    }
}

