/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.conditional;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.conditional.IfAction;
import ch.qos.logback.core.joran.conditional.ThenActionState;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import java.util.List;
import java.util.Stack;
import org.xml.sax.Attributes;

public abstract class ThenOrElseActionBase
extends Action {
    Stack<ThenActionState> stateStack = new Stack();

    @Override
    public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
        if (!this.weAreActive(ic)) {
            return;
        }
        ThenActionState state = new ThenActionState();
        if (ic.isListenerListEmpty()) {
            ic.addInPlayListener(state);
            state.isRegistered = true;
        }
        this.stateStack.push(state);
    }

    boolean weAreActive(InterpretationContext ic) {
        Object o = ic.peekObject();
        if (!(o instanceof IfAction)) {
            return false;
        }
        IfAction ifAction = (IfAction)o;
        return ifAction.isActive();
    }

    @Override
    public void end(InterpretationContext ic, String name) throws ActionException {
        if (!this.weAreActive(ic)) {
            return;
        }
        ThenActionState state = this.stateStack.pop();
        if (state.isRegistered) {
            ic.removeInPlayListener(state);
            Object o = ic.peekObject();
            if (o instanceof IfAction) {
                IfAction ifAction = (IfAction)o;
                this.removeFirstAndLastFromList(state.eventList);
                this.registerEventList(ifAction, state.eventList);
            } else {
                throw new IllegalStateException("Missing IfAction on top of stack");
            }
        }
    }

    abstract void registerEventList(IfAction var1, List<SaxEvent> var2);

    void removeFirstAndLastFromList(List<SaxEvent> eventList) {
        eventList.remove(0);
        eventList.remove(eventList.size() - 1);
    }
}

