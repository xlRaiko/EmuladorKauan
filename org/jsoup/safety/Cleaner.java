/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.safety;

import java.util.List;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.ParseErrorList;
import org.jsoup.parser.Parser;
import org.jsoup.parser.Tag;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

public class Cleaner {
    private Whitelist whitelist;

    public Cleaner(Whitelist whitelist) {
        Validate.notNull(whitelist);
        this.whitelist = whitelist;
    }

    public Document clean(Document dirtyDocument) {
        Validate.notNull(dirtyDocument);
        Document clean = Document.createShell(dirtyDocument.baseUri());
        if (dirtyDocument.body() != null) {
            this.copySafeNodes(dirtyDocument.body(), clean.body());
        }
        return clean;
    }

    public boolean isValid(Document dirtyDocument) {
        Validate.notNull(dirtyDocument);
        Document clean = Document.createShell(dirtyDocument.baseUri());
        int numDiscarded = this.copySafeNodes(dirtyDocument.body(), clean.body());
        return numDiscarded == 0 && dirtyDocument.head().childNodes().isEmpty();
    }

    public boolean isValidBodyHtml(String bodyHtml) {
        Document clean = Document.createShell("");
        Document dirty = Document.createShell("");
        ParseErrorList errorList = ParseErrorList.tracking(1);
        List<Node> nodes = Parser.parseFragment(bodyHtml, dirty.body(), "", errorList);
        dirty.body().insertChildren(0, nodes);
        int numDiscarded = this.copySafeNodes(dirty.body(), clean.body());
        return numDiscarded == 0 && errorList.isEmpty();
    }

    private int copySafeNodes(Element source, Element dest) {
        CleaningVisitor cleaningVisitor = new CleaningVisitor(source, dest);
        NodeTraversor.traverse((NodeVisitor)cleaningVisitor, source);
        return cleaningVisitor.numDiscarded;
    }

    private ElementMeta createSafeElement(Element sourceEl) {
        String sourceTag = sourceEl.tagName();
        Attributes destAttrs = new Attributes();
        Element dest = new Element(Tag.valueOf(sourceTag), sourceEl.baseUri(), destAttrs);
        int numDiscarded = 0;
        Attributes sourceAttrs = sourceEl.attributes();
        for (Attribute sourceAttr : sourceAttrs) {
            if (this.whitelist.isSafeAttribute(sourceTag, sourceEl, sourceAttr)) {
                destAttrs.put(sourceAttr);
                continue;
            }
            ++numDiscarded;
        }
        Attributes enforcedAttrs = this.whitelist.getEnforcedAttributes(sourceTag);
        destAttrs.addAll(enforcedAttrs);
        return new ElementMeta(dest, numDiscarded);
    }

    private static class ElementMeta {
        Element el;
        int numAttribsDiscarded;

        ElementMeta(Element el, int numAttribsDiscarded) {
            this.el = el;
            this.numAttribsDiscarded = numAttribsDiscarded;
        }
    }

    private final class CleaningVisitor
    implements NodeVisitor {
        private int numDiscarded = 0;
        private final Element root;
        private Element destination;

        private CleaningVisitor(Element root, Element destination) {
            this.root = root;
            this.destination = destination;
        }

        @Override
        public void head(Node source, int depth) {
            if (source instanceof Element) {
                Element sourceEl = (Element)source;
                if (Cleaner.this.whitelist.isSafeTag(sourceEl.normalName())) {
                    ElementMeta meta = Cleaner.this.createSafeElement(sourceEl);
                    Element destChild = meta.el;
                    this.destination.appendChild(destChild);
                    this.numDiscarded += meta.numAttribsDiscarded;
                    this.destination = destChild;
                } else if (source != this.root) {
                    ++this.numDiscarded;
                }
            } else if (source instanceof TextNode) {
                TextNode sourceText = (TextNode)source;
                TextNode destText = new TextNode(sourceText.getWholeText());
                this.destination.appendChild(destText);
            } else if (source instanceof DataNode && Cleaner.this.whitelist.isSafeTag(source.parent().nodeName())) {
                DataNode sourceData = (DataNode)source;
                DataNode destData = new DataNode(sourceData.getWholeData());
                this.destination.appendChild(destData);
            } else {
                ++this.numDiscarded;
            }
        }

        @Override
        public void tail(Node source, int depth) {
            if (source instanceof Element && Cleaner.this.whitelist.isSafeTag(source.nodeName())) {
                this.destination = this.destination.parent();
            }
        }
    }
}

