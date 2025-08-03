/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.select;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import org.jsoup.helper.Validate;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Evaluator;
import org.jsoup.select.NodeFilter;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import org.jsoup.select.QueryParser;
import org.jsoup.select.Selector;

public class Elements
extends ArrayList<Element> {
    public Elements() {
    }

    public Elements(int initialCapacity) {
        super(initialCapacity);
    }

    public Elements(Collection<Element> elements) {
        super(elements);
    }

    public Elements(List<Element> elements) {
        super(elements);
    }

    public Elements(Element ... elements) {
        super(Arrays.asList(elements));
    }

    @Override
    public Elements clone() {
        Elements clone = new Elements(this.size());
        for (Element e : this) {
            clone.add(e.clone());
        }
        return clone;
    }

    public String attr(String attributeKey) {
        for (Element element : this) {
            if (!element.hasAttr(attributeKey)) continue;
            return element.attr(attributeKey);
        }
        return "";
    }

    public boolean hasAttr(String attributeKey) {
        for (Element element : this) {
            if (!element.hasAttr(attributeKey)) continue;
            return true;
        }
        return false;
    }

    public List<String> eachAttr(String attributeKey) {
        ArrayList<String> attrs = new ArrayList<String>(this.size());
        for (Element element : this) {
            if (!element.hasAttr(attributeKey)) continue;
            attrs.add(element.attr(attributeKey));
        }
        return attrs;
    }

    public Elements attr(String attributeKey, String attributeValue) {
        for (Element element : this) {
            element.attr(attributeKey, attributeValue);
        }
        return this;
    }

    public Elements removeAttr(String attributeKey) {
        for (Element element : this) {
            element.removeAttr(attributeKey);
        }
        return this;
    }

    public Elements addClass(String className) {
        for (Element element : this) {
            element.addClass(className);
        }
        return this;
    }

    public Elements removeClass(String className) {
        for (Element element : this) {
            element.removeClass(className);
        }
        return this;
    }

    public Elements toggleClass(String className) {
        for (Element element : this) {
            element.toggleClass(className);
        }
        return this;
    }

    public boolean hasClass(String className) {
        for (Element element : this) {
            if (!element.hasClass(className)) continue;
            return true;
        }
        return false;
    }

    public String val() {
        if (this.size() > 0) {
            return this.first().val();
        }
        return "";
    }

    public Elements val(String value) {
        for (Element element : this) {
            element.val(value);
        }
        return this;
    }

    public String text() {
        StringBuilder sb = StringUtil.borrowBuilder();
        for (Element element : this) {
            if (sb.length() != 0) {
                sb.append(" ");
            }
            sb.append(element.text());
        }
        return StringUtil.releaseBuilder(sb);
    }

    public boolean hasText() {
        for (Element element : this) {
            if (!element.hasText()) continue;
            return true;
        }
        return false;
    }

    public List<String> eachText() {
        ArrayList<String> texts = new ArrayList<String>(this.size());
        for (Element el : this) {
            if (!el.hasText()) continue;
            texts.add(el.text());
        }
        return texts;
    }

    public String html() {
        StringBuilder sb = StringUtil.borrowBuilder();
        for (Element element : this) {
            if (sb.length() != 0) {
                sb.append("\n");
            }
            sb.append(element.html());
        }
        return StringUtil.releaseBuilder(sb);
    }

    public String outerHtml() {
        StringBuilder sb = StringUtil.borrowBuilder();
        for (Element element : this) {
            if (sb.length() != 0) {
                sb.append("\n");
            }
            sb.append(element.outerHtml());
        }
        return StringUtil.releaseBuilder(sb);
    }

    @Override
    public String toString() {
        return this.outerHtml();
    }

    public Elements tagName(String tagName) {
        for (Element element : this) {
            element.tagName(tagName);
        }
        return this;
    }

    public Elements html(String html) {
        for (Element element : this) {
            element.html(html);
        }
        return this;
    }

    public Elements prepend(String html) {
        for (Element element : this) {
            element.prepend(html);
        }
        return this;
    }

    public Elements append(String html) {
        for (Element element : this) {
            element.append(html);
        }
        return this;
    }

    public Elements before(String html) {
        for (Element element : this) {
            element.before(html);
        }
        return this;
    }

    public Elements after(String html) {
        for (Element element : this) {
            element.after(html);
        }
        return this;
    }

    public Elements wrap(String html) {
        Validate.notEmpty(html);
        for (Element element : this) {
            element.wrap(html);
        }
        return this;
    }

    public Elements unwrap() {
        for (Element element : this) {
            element.unwrap();
        }
        return this;
    }

    public Elements empty() {
        for (Element element : this) {
            element.empty();
        }
        return this;
    }

    public Elements remove() {
        for (Element element : this) {
            element.remove();
        }
        return this;
    }

    public Elements select(String query) {
        return Selector.select(query, this);
    }

    public Elements not(String query) {
        Elements out = Selector.select(query, this);
        return Selector.filterOut(this, out);
    }

    public Elements eq(int index) {
        return this.size() > index ? new Elements((Element)this.get(index)) : new Elements();
    }

    public boolean is(String query) {
        Evaluator eval = QueryParser.parse(query);
        for (Element e : this) {
            if (!e.is(eval)) continue;
            return true;
        }
        return false;
    }

    public Elements next() {
        return this.siblings(null, true, false);
    }

    public Elements next(String query) {
        return this.siblings(query, true, false);
    }

    public Elements nextAll() {
        return this.siblings(null, true, true);
    }

    public Elements nextAll(String query) {
        return this.siblings(query, true, true);
    }

    public Elements prev() {
        return this.siblings(null, false, false);
    }

    public Elements prev(String query) {
        return this.siblings(query, false, false);
    }

    public Elements prevAll() {
        return this.siblings(null, false, true);
    }

    public Elements prevAll(String query) {
        return this.siblings(query, false, true);
    }

    private Elements siblings(String query, boolean next, boolean all) {
        Elements els = new Elements();
        Evaluator eval = query != null ? QueryParser.parse(query) : null;
        block0: for (Element e : this) {
            Element sib;
            while ((sib = next ? e.nextElementSibling() : e.previousElementSibling()) != null) {
                if (eval == null) {
                    els.add(sib);
                } else if (sib.is(eval)) {
                    els.add(sib);
                }
                e = sib;
                if (all) continue;
                continue block0;
            }
        }
        return els;
    }

    public Elements parents() {
        LinkedHashSet<Element> combo = new LinkedHashSet<Element>();
        for (Element e : this) {
            combo.addAll(e.parents());
        }
        return new Elements((Collection<Element>)combo);
    }

    public Element first() {
        return this.isEmpty() ? null : (Element)this.get(0);
    }

    public Element last() {
        return this.isEmpty() ? null : (Element)this.get(this.size() - 1);
    }

    public Elements traverse(NodeVisitor nodeVisitor) {
        NodeTraversor.traverse(nodeVisitor, this);
        return this;
    }

    public Elements filter(NodeFilter nodeFilter) {
        NodeTraversor.filter(nodeFilter, this);
        return this;
    }

    public List<FormElement> forms() {
        return this.nodesOfType(FormElement.class);
    }

    public List<Comment> comments() {
        return this.nodesOfType(Comment.class);
    }

    public List<TextNode> textNodes() {
        return this.nodesOfType(TextNode.class);
    }

    public List<DataNode> dataNodes() {
        return this.nodesOfType(DataNode.class);
    }

    private <T extends Node> List<T> nodesOfType(Class<T> tClass) {
        ArrayList<T> nodes = new ArrayList<T>();
        for (Element el : this) {
            if (el.getClass().isInstance(tClass)) {
                nodes.add(tClass.cast(el));
                continue;
            }
            if (!Node.class.isAssignableFrom(tClass)) continue;
            for (int i = 0; i < el.childNodeSize(); ++i) {
                Node node = el.childNode(i);
                if (!tClass.isInstance(node)) continue;
                nodes.add(tClass.cast(node));
            }
        }
        return nodes;
    }
}

