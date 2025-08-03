/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.ConverterUtil;
import ch.qos.logback.core.pattern.LiteralConverter;
import ch.qos.logback.core.pattern.parser.Node;
import ch.qos.logback.core.pattern.parser.Parser;
import ch.qos.logback.core.pattern.util.AlmostAsIsEscapeUtil;
import ch.qos.logback.core.rolling.helper.DateTokenConverter;
import ch.qos.logback.core.rolling.helper.FileFilterUtil;
import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import ch.qos.logback.core.rolling.helper.MonoTypedConverter;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.ScanException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FileNamePattern
extends ContextAwareBase {
    static final Map<String, String> CONVERTER_MAP = new HashMap<String, String>();
    String pattern;
    Converter<Object> headTokenConverter;

    public FileNamePattern(String patternArg, Context contextArg) {
        this.setPattern(FileFilterUtil.slashify(patternArg));
        this.setContext(contextArg);
        this.parse();
        ConverterUtil.startConverters(this.headTokenConverter);
    }

    void parse() {
        try {
            String patternForParsing = this.escapeRightParantesis(this.pattern);
            Parser p = new Parser(patternForParsing, new AlmostAsIsEscapeUtil());
            p.setContext(this.context);
            Node t = p.parse();
            this.headTokenConverter = p.compile(t, CONVERTER_MAP);
        }
        catch (ScanException sce) {
            this.addError("Failed to parse pattern \"" + this.pattern + "\".", sce);
        }
    }

    String escapeRightParantesis(String in) {
        return this.pattern.replace(")", "\\)");
    }

    public String toString() {
        return this.pattern;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.pattern == null ? 0 : this.pattern.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        FileNamePattern other = (FileNamePattern)obj;
        return !(this.pattern == null ? other.pattern != null : !this.pattern.equals(other.pattern));
    }

    public DateTokenConverter<Object> getPrimaryDateTokenConverter() {
        for (Converter<Object> p = this.headTokenConverter; p != null; p = p.getNext()) {
            DateTokenConverter dtc;
            if (!(p instanceof DateTokenConverter) || !(dtc = (DateTokenConverter)p).isPrimary()) continue;
            return dtc;
        }
        return null;
    }

    public IntegerTokenConverter getIntegerTokenConverter() {
        for (Converter<Object> p = this.headTokenConverter; p != null; p = p.getNext()) {
            if (!(p instanceof IntegerTokenConverter)) continue;
            return (IntegerTokenConverter)p;
        }
        return null;
    }

    public boolean hasIntegerTokenCOnverter() {
        IntegerTokenConverter itc = this.getIntegerTokenConverter();
        return itc != null;
    }

    public String convertMultipleArguments(Object ... objectList) {
        StringBuilder buf = new StringBuilder();
        for (Converter<Object> c = this.headTokenConverter; c != null; c = c.getNext()) {
            if (c instanceof MonoTypedConverter) {
                MonoTypedConverter monoTyped = (MonoTypedConverter)((Object)c);
                for (Object o : objectList) {
                    if (!monoTyped.isApplicable(o)) continue;
                    buf.append(c.convert(o));
                }
                continue;
            }
            buf.append(c.convert(objectList));
        }
        return buf.toString();
    }

    public String convert(Object o) {
        StringBuilder buf = new StringBuilder();
        for (Converter<Object> p = this.headTokenConverter; p != null; p = p.getNext()) {
            buf.append(p.convert(o));
        }
        return buf.toString();
    }

    public String convertInt(int i) {
        return this.convert(i);
    }

    public void setPattern(String pattern) {
        if (pattern != null) {
            this.pattern = pattern.trim();
        }
    }

    public String getPattern() {
        return this.pattern;
    }

    public String toRegexForFixedDate(Date date) {
        StringBuilder buf = new StringBuilder();
        for (Converter<Object> p = this.headTokenConverter; p != null; p = p.getNext()) {
            if (p instanceof LiteralConverter) {
                buf.append(p.convert(null));
                continue;
            }
            if (p instanceof IntegerTokenConverter) {
                buf.append("(\\d{1,3})");
                continue;
            }
            if (!(p instanceof DateTokenConverter)) continue;
            buf.append(p.convert(date));
        }
        return buf.toString();
    }

    public String toRegex() {
        StringBuilder buf = new StringBuilder();
        for (Converter<Object> p = this.headTokenConverter; p != null; p = p.getNext()) {
            if (p instanceof LiteralConverter) {
                buf.append(p.convert(null));
                continue;
            }
            if (p instanceof IntegerTokenConverter) {
                buf.append("\\d{1,2}");
                continue;
            }
            if (!(p instanceof DateTokenConverter)) continue;
            DateTokenConverter dtc = (DateTokenConverter)p;
            buf.append(dtc.toRegex());
        }
        return buf.toString();
    }

    static {
        CONVERTER_MAP.put("i", IntegerTokenConverter.class.getName());
        CONVERTER_MAP.put("d", DateTokenConverter.class.getName());
    }
}

