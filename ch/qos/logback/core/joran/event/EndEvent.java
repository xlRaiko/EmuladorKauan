/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.event;

import ch.qos.logback.core.joran.event.SaxEvent;
import org.xml.sax.Locator;

public class EndEvent
extends SaxEvent {
    EndEvent(String namespaceURI, String localName, String qName, Locator locator) {
        super(namespaceURI, localName, qName, locator);
    }

    public String toString() {
        return "  EndEvent(" + this.getQName() + ")  [" + this.locator.getLineNumber() + "," + this.locator.getColumnNumber() + "]";
    }
}

