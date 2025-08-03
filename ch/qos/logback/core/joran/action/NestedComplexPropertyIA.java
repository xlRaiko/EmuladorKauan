/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.action.IADataForComplexProperty;
import ch.qos.logback.core.joran.action.ImplicitAction;
import ch.qos.logback.core.joran.spi.ElementPath;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.spi.NoAutoStartUtil;
import ch.qos.logback.core.joran.util.PropertySetter;
import ch.qos.logback.core.joran.util.beans.BeanDescriptionCache;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.AggregationType;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Stack;
import org.xml.sax.Attributes;

public class NestedComplexPropertyIA
extends ImplicitAction {
    Stack<IADataForComplexProperty> actionDataStack = new Stack();
    private final BeanDescriptionCache beanDescriptionCache;

    public NestedComplexPropertyIA(BeanDescriptionCache beanDescriptionCache) {
        this.beanDescriptionCache = beanDescriptionCache;
    }

    @Override
    public boolean isApplicable(ElementPath elementPath, Attributes attributes, InterpretationContext ic) {
        String nestedElementTagName = elementPath.peekLast();
        if (ic.isEmpty()) {
            return false;
        }
        Object o = ic.peekObject();
        PropertySetter parentBean = new PropertySetter(this.beanDescriptionCache, o);
        parentBean.setContext(this.context);
        AggregationType aggregationType = parentBean.computeAggregationType(nestedElementTagName);
        switch (aggregationType) {
            case NOT_FOUND: 
            case AS_BASIC_PROPERTY: 
            case AS_BASIC_PROPERTY_COLLECTION: {
                return false;
            }
            case AS_COMPLEX_PROPERTY_COLLECTION: 
            case AS_COMPLEX_PROPERTY: {
                IADataForComplexProperty ad = new IADataForComplexProperty(parentBean, aggregationType, nestedElementTagName);
                this.actionDataStack.push(ad);
                return true;
            }
        }
        this.addError("PropertySetter.computeAggregationType returned " + (Object)((Object)aggregationType));
        return false;
    }

    @Override
    public void begin(InterpretationContext ec, String localName, Attributes attributes) {
        IADataForComplexProperty actionData = this.actionDataStack.peek();
        String className = attributes.getValue("class");
        className = ec.subst(className);
        Class<?> componentClass = null;
        try {
            if (!OptionHelper.isEmpty(className)) {
                componentClass = Loader.loadClass(className, this.context);
            } else {
                PropertySetter parentBean = actionData.parentBean;
                componentClass = parentBean.getClassNameViaImplicitRules(actionData.getComplexPropertyName(), actionData.getAggregationType(), ec.getDefaultNestedComponentRegistry());
            }
            if (componentClass == null) {
                actionData.inError = true;
                String errMsg = "Could not find an appropriate class for property [" + localName + "]";
                this.addError(errMsg);
                return;
            }
            if (OptionHelper.isEmpty(className)) {
                this.addInfo("Assuming default type [" + componentClass.getName() + "] for [" + localName + "] property");
            }
            actionData.setNestedComplexProperty(componentClass.newInstance());
            if (actionData.getNestedComplexProperty() instanceof ContextAware) {
                ((ContextAware)actionData.getNestedComplexProperty()).setContext(this.context);
            }
            ec.pushObject(actionData.getNestedComplexProperty());
        }
        catch (Exception oops) {
            actionData.inError = true;
            String msg = "Could not create component [" + localName + "] of type [" + className + "]";
            this.addError(msg, oops);
        }
    }

    @Override
    public void end(InterpretationContext ec, String tagName) {
        Object o;
        Object nestedComplexProperty;
        IADataForComplexProperty actionData = this.actionDataStack.pop();
        if (actionData.inError) {
            return;
        }
        PropertySetter nestedBean = new PropertySetter(this.beanDescriptionCache, actionData.getNestedComplexProperty());
        nestedBean.setContext(this.context);
        if (nestedBean.computeAggregationType("parent") == AggregationType.AS_COMPLEX_PROPERTY) {
            nestedBean.setComplexProperty("parent", actionData.parentBean.getObj());
        }
        if ((nestedComplexProperty = actionData.getNestedComplexProperty()) instanceof LifeCycle && NoAutoStartUtil.notMarkedWithNoAutoStart(nestedComplexProperty)) {
            ((LifeCycle)nestedComplexProperty).start();
        }
        if ((o = ec.peekObject()) != actionData.getNestedComplexProperty()) {
            this.addError("The object on the top the of the stack is not the component pushed earlier.");
        } else {
            ec.popObject();
            switch (actionData.aggregationType) {
                case AS_COMPLEX_PROPERTY: {
                    actionData.parentBean.setComplexProperty(tagName, actionData.getNestedComplexProperty());
                    break;
                }
                case AS_COMPLEX_PROPERTY_COLLECTION: {
                    actionData.parentBean.addComplexProperty(tagName, actionData.getNestedComplexProperty());
                    break;
                }
                default: {
                    this.addError("Unexpected aggregationType " + (Object)((Object)actionData.aggregationType));
                }
            }
        }
    }
}

