/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import org.xml.sax.Attributes;

public class LevelAction
extends Action {
    boolean inError = false;

    @Override
    public void begin(InterpretationContext ec, String name, Attributes attributes) {
        Object o = ec.peekObject();
        if (!(o instanceof Logger)) {
            this.inError = true;
            this.addError("For element <level>, could not find a logger at the top of execution stack.");
            return;
        }
        Logger l = (Logger)o;
        String loggerName = l.getName();
        String levelStr = ec.subst(attributes.getValue("value"));
        if ("INHERITED".equalsIgnoreCase(levelStr) || "NULL".equalsIgnoreCase(levelStr)) {
            l.setLevel(null);
        } else {
            l.setLevel(Level.toLevel(levelStr, Level.DEBUG));
        }
        this.addInfo(loggerName + " level set to " + l.getLevel());
    }

    public void finish(InterpretationContext ec) {
    }

    @Override
    public void end(InterpretationContext ec, String e) {
    }
}

