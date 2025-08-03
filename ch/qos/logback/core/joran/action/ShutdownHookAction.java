/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.hook.ShutdownHookBase;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

public class ShutdownHookAction
extends Action {
    ShutdownHookBase hook;
    private boolean inError;

    @Override
    public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
        this.hook = null;
        this.inError = false;
        String className = attributes.getValue("class");
        if (OptionHelper.isEmpty(className)) {
            this.addError("Missing class name for shutdown hook. Near [" + name + "] line " + this.getLineNumber(ic));
            this.inError = true;
            return;
        }
        try {
            this.addInfo("About to instantiate shutdown hook of type [" + className + "]");
            this.hook = (ShutdownHookBase)OptionHelper.instantiateByClassName(className, ShutdownHookBase.class, this.context);
            this.hook.setContext(this.context);
            ic.pushObject(this.hook);
        }
        catch (Exception e) {
            this.inError = true;
            this.addError("Could not create a shutdown hook of type [" + className + "].", e);
            throw new ActionException(e);
        }
    }

    @Override
    public void end(InterpretationContext ic, String name) throws ActionException {
        if (this.inError) {
            return;
        }
        Object o = ic.peekObject();
        if (o != this.hook) {
            this.addWarn("The object at the of the stack is not the hook pushed earlier.");
        } else {
            ic.popObject();
            Thread hookThread = new Thread((Runnable)this.hook, "Logback shutdown hook [" + this.context.getName() + "]");
            this.context.putObject("SHUTDOWN_HOOK", hookThread);
            Runtime.getRuntime().addShutdownHook(hookThread);
        }
    }
}

