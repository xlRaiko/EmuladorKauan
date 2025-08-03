/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Locale;
import org.jsoup.UncheckedIOException;
import org.jsoup.helper.Validate;

public final class CharacterReader {
    static final char EOF = '\uffff';
    private static final int maxStringCacheLen = 12;
    static final int maxBufferLen = 32768;
    static final int readAheadLimit = 24576;
    private static final int minReadAheadLen = 1024;
    private char[] charBuf;
    private Reader reader;
    private int bufLength;
    private int bufSplitPoint;
    private int bufPos;
    private int readerPos;
    private int bufMark = -1;
    private static final int stringCacheSize = 512;
    private String[] stringCache = new String[512];
    private boolean readFully;

    public CharacterReader(Reader input, int sz) {
        Validate.notNull(input);
        Validate.isTrue(input.markSupported());
        this.reader = input;
        this.charBuf = new char[sz > 32768 ? 32768 : sz];
        this.bufferUp();
    }

    public CharacterReader(Reader input) {
        this(input, 32768);
    }

    public CharacterReader(String input) {
        this(new StringReader(input), input.length());
    }

    public void close() {
        if (this.reader == null) {
            return;
        }
        try {
            this.reader.close();
        }
        catch (IOException iOException) {
        }
        finally {
            this.reader = null;
            this.charBuf = null;
            this.stringCache = null;
        }
    }

    private void bufferUp() {
        int offset;
        int pos;
        if (this.readFully || this.bufPos < this.bufSplitPoint) {
            return;
        }
        if (this.bufMark != -1) {
            pos = this.bufMark;
            offset = this.bufPos - this.bufMark;
        } else {
            pos = this.bufPos;
            offset = 0;
        }
        try {
            int read;
            int thisRead;
            long skipped = this.reader.skip(pos);
            this.reader.mark(32768);
            for (read = 0; read <= 1024; read += thisRead) {
                thisRead = this.reader.read(this.charBuf, read, this.charBuf.length - read);
                if (thisRead == -1) {
                    this.readFully = true;
                }
                if (thisRead <= 0) break;
            }
            this.reader.reset();
            if (read > 0) {
                Validate.isTrue(skipped == (long)pos);
                this.bufLength = read;
                this.readerPos += pos;
                this.bufPos = offset;
                if (this.bufMark != -1) {
                    this.bufMark = 0;
                }
                this.bufSplitPoint = this.bufLength > 24576 ? 24576 : this.bufLength;
            }
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public int pos() {
        return this.readerPos + this.bufPos;
    }

    public boolean isEmpty() {
        this.bufferUp();
        return this.bufPos >= this.bufLength;
    }

    private boolean isEmptyNoBufferUp() {
        return this.bufPos >= this.bufLength;
    }

    public char current() {
        this.bufferUp();
        return this.isEmptyNoBufferUp() ? (char)'\uffff' : this.charBuf[this.bufPos];
    }

    char consume() {
        this.bufferUp();
        char val = this.isEmptyNoBufferUp() ? (char)'\uffff' : this.charBuf[this.bufPos];
        ++this.bufPos;
        return val;
    }

    void unconsume() {
        if (this.bufPos < 1) {
            throw new UncheckedIOException(new IOException("No buffer left to unconsume"));
        }
        --this.bufPos;
    }

    public void advance() {
        ++this.bufPos;
    }

    void mark() {
        if (this.bufLength - this.bufPos < 1024) {
            this.bufSplitPoint = 0;
        }
        this.bufferUp();
        this.bufMark = this.bufPos;
    }

    void unmark() {
        this.bufMark = -1;
    }

    void rewindToMark() {
        if (this.bufMark == -1) {
            throw new UncheckedIOException(new IOException("Mark invalid"));
        }
        this.bufPos = this.bufMark;
        this.unmark();
    }

    int nextIndexOf(char c) {
        this.bufferUp();
        for (int i = this.bufPos; i < this.bufLength; ++i) {
            if (c != this.charBuf[i]) continue;
            return i - this.bufPos;
        }
        return -1;
    }

    int nextIndexOf(CharSequence seq) {
        this.bufferUp();
        char startChar = seq.charAt(0);
        for (int offset = this.bufPos; offset < this.bufLength; ++offset) {
            if (startChar != this.charBuf[offset]) {
                while (++offset < this.bufLength && startChar != this.charBuf[offset]) {
                }
            }
            int i = offset + 1;
            int last = i + seq.length() - 1;
            if (offset >= this.bufLength || last > this.bufLength) continue;
            int j = 1;
            while (i < last && seq.charAt(j) == this.charBuf[i]) {
                ++i;
                ++j;
            }
            if (i != last) continue;
            return offset - this.bufPos;
        }
        return -1;
    }

    public String consumeTo(char c) {
        int offset = this.nextIndexOf(c);
        if (offset != -1) {
            String consumed = CharacterReader.cacheString(this.charBuf, this.stringCache, this.bufPos, offset);
            this.bufPos += offset;
            return consumed;
        }
        return this.consumeToEnd();
    }

    String consumeTo(String seq) {
        int offset = this.nextIndexOf(seq);
        if (offset != -1) {
            String consumed = CharacterReader.cacheString(this.charBuf, this.stringCache, this.bufPos, offset);
            this.bufPos += offset;
            return consumed;
        }
        if (this.bufLength - this.bufPos < seq.length()) {
            return this.consumeToEnd();
        }
        int endPos = this.bufLength - seq.length() + 1;
        String consumed = CharacterReader.cacheString(this.charBuf, this.stringCache, this.bufPos, endPos - this.bufPos);
        this.bufPos = endPos;
        return consumed;
    }

    public String consumeToAny(char ... chars) {
        int pos;
        this.bufferUp();
        int start = pos = this.bufPos;
        int remaining = this.bufLength;
        char[] val = this.charBuf;
        int charLen = chars.length;
        block0: while (pos < remaining) {
            for (int i = 0; i < charLen; ++i) {
                if (val[pos] == chars[i]) break block0;
            }
            ++pos;
        }
        this.bufPos = pos;
        return pos > start ? CharacterReader.cacheString(this.charBuf, this.stringCache, start, pos - start) : "";
    }

    String consumeToAnySorted(char ... chars) {
        int pos;
        this.bufferUp();
        int start = pos = this.bufPos;
        int remaining = this.bufLength;
        char[] val = this.charBuf;
        while (pos < remaining && Arrays.binarySearch(chars, val[pos]) < 0) {
            ++pos;
        }
        this.bufPos = pos;
        return this.bufPos > start ? CharacterReader.cacheString(this.charBuf, this.stringCache, start, pos - start) : "";
    }

    String consumeData() {
        int pos;
        int start = pos = this.bufPos;
        int remaining = this.bufLength;
        char[] val = this.charBuf;
        block3: while (pos < remaining) {
            switch (val[pos]) {
                case '\u0000': 
                case '&': 
                case '<': {
                    break block3;
                }
                default: {
                    ++pos;
                    continue block3;
                }
            }
        }
        this.bufPos = pos;
        return pos > start ? CharacterReader.cacheString(this.charBuf, this.stringCache, start, pos - start) : "";
    }

    String consumeRawData() {
        int pos;
        int start = pos = this.bufPos;
        int remaining = this.bufLength;
        char[] val = this.charBuf;
        block3: while (pos < remaining) {
            switch (val[pos]) {
                case '\u0000': 
                case '<': {
                    break block3;
                }
                default: {
                    ++pos;
                    continue block3;
                }
            }
        }
        this.bufPos = pos;
        return pos > start ? CharacterReader.cacheString(this.charBuf, this.stringCache, start, pos - start) : "";
    }

    String consumeTagName() {
        int pos;
        this.bufferUp();
        int start = pos = this.bufPos;
        int remaining = this.bufLength;
        char[] val = this.charBuf;
        block3: while (pos < remaining) {
            switch (val[pos]) {
                case '\u0000': 
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': 
                case '/': 
                case '<': 
                case '>': {
                    break block3;
                }
                default: {
                    ++pos;
                    continue block3;
                }
            }
        }
        this.bufPos = pos;
        return pos > start ? CharacterReader.cacheString(this.charBuf, this.stringCache, start, pos - start) : "";
    }

    String consumeToEnd() {
        this.bufferUp();
        String data = CharacterReader.cacheString(this.charBuf, this.stringCache, this.bufPos, this.bufLength - this.bufPos);
        this.bufPos = this.bufLength;
        return data;
    }

    String consumeLetterSequence() {
        char c;
        this.bufferUp();
        int start = this.bufPos;
        while (this.bufPos < this.bufLength && ((c = this.charBuf[this.bufPos]) >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || Character.isLetter(c))) {
            ++this.bufPos;
        }
        return CharacterReader.cacheString(this.charBuf, this.stringCache, start, this.bufPos - start);
    }

    String consumeLetterThenDigitSequence() {
        char c;
        this.bufferUp();
        int start = this.bufPos;
        while (this.bufPos < this.bufLength && ((c = this.charBuf[this.bufPos]) >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || Character.isLetter(c))) {
            ++this.bufPos;
        }
        while (!this.isEmptyNoBufferUp() && (c = this.charBuf[this.bufPos]) >= '0' && c <= '9') {
            ++this.bufPos;
        }
        return CharacterReader.cacheString(this.charBuf, this.stringCache, start, this.bufPos - start);
    }

    String consumeHexSequence() {
        char c;
        this.bufferUp();
        int start = this.bufPos;
        while (this.bufPos < this.bufLength && ((c = this.charBuf[this.bufPos]) >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f')) {
            ++this.bufPos;
        }
        return CharacterReader.cacheString(this.charBuf, this.stringCache, start, this.bufPos - start);
    }

    String consumeDigitSequence() {
        char c;
        this.bufferUp();
        int start = this.bufPos;
        while (this.bufPos < this.bufLength && (c = this.charBuf[this.bufPos]) >= '0' && c <= '9') {
            ++this.bufPos;
        }
        return CharacterReader.cacheString(this.charBuf, this.stringCache, start, this.bufPos - start);
    }

    boolean matches(char c) {
        return !this.isEmpty() && this.charBuf[this.bufPos] == c;
    }

    boolean matches(String seq) {
        this.bufferUp();
        int scanLength = seq.length();
        if (scanLength > this.bufLength - this.bufPos) {
            return false;
        }
        for (int offset = 0; offset < scanLength; ++offset) {
            if (seq.charAt(offset) == this.charBuf[this.bufPos + offset]) continue;
            return false;
        }
        return true;
    }

    boolean matchesIgnoreCase(String seq) {
        this.bufferUp();
        int scanLength = seq.length();
        if (scanLength > this.bufLength - this.bufPos) {
            return false;
        }
        for (int offset = 0; offset < scanLength; ++offset) {
            char upTarget;
            char upScan = Character.toUpperCase(seq.charAt(offset));
            if (upScan == (upTarget = Character.toUpperCase(this.charBuf[this.bufPos + offset]))) continue;
            return false;
        }
        return true;
    }

    boolean matchesAny(char ... seq) {
        if (this.isEmpty()) {
            return false;
        }
        this.bufferUp();
        char c = this.charBuf[this.bufPos];
        for (char seek : seq) {
            if (seek != c) continue;
            return true;
        }
        return false;
    }

    boolean matchesAnySorted(char[] seq) {
        this.bufferUp();
        return !this.isEmpty() && Arrays.binarySearch(seq, this.charBuf[this.bufPos]) >= 0;
    }

    boolean matchesLetter() {
        if (this.isEmpty()) {
            return false;
        }
        char c = this.charBuf[this.bufPos];
        return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || Character.isLetter(c);
    }

    boolean matchesDigit() {
        if (this.isEmpty()) {
            return false;
        }
        char c = this.charBuf[this.bufPos];
        return c >= '0' && c <= '9';
    }

    boolean matchConsume(String seq) {
        this.bufferUp();
        if (this.matches(seq)) {
            this.bufPos += seq.length();
            return true;
        }
        return false;
    }

    boolean matchConsumeIgnoreCase(String seq) {
        if (this.matchesIgnoreCase(seq)) {
            this.bufPos += seq.length();
            return true;
        }
        return false;
    }

    boolean containsIgnoreCase(String seq) {
        String loScan = seq.toLowerCase(Locale.ENGLISH);
        String hiScan = seq.toUpperCase(Locale.ENGLISH);
        return this.nextIndexOf(loScan) > -1 || this.nextIndexOf(hiScan) > -1;
    }

    public String toString() {
        if (this.bufLength - this.bufPos < 0) {
            return "";
        }
        return new String(this.charBuf, this.bufPos, this.bufLength - this.bufPos);
    }

    private static String cacheString(char[] charBuf, String[] stringCache, int start, int count) {
        if (count > 12) {
            return new String(charBuf, start, count);
        }
        if (count < 1) {
            return "";
        }
        int hash = 31 * count;
        int offset = start;
        for (int i = 0; i < count; ++i) {
            hash = 31 * hash + charBuf[offset++];
        }
        int index = hash & 0x1FF;
        String cached = stringCache[index];
        if (cached == null) {
            stringCache[index] = cached = new String(charBuf, start, count);
        } else {
            if (CharacterReader.rangeEquals(charBuf, start, count, cached)) {
                return cached;
            }
            stringCache[index] = cached = new String(charBuf, start, count);
        }
        return cached;
    }

    static boolean rangeEquals(char[] charBuf, int start, int count, String cached) {
        if (count == cached.length()) {
            int i = start;
            int j = 0;
            while (count-- != 0) {
                if (charBuf[i++] == cached.charAt(j++)) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    boolean rangeEquals(int start, int count, String cached) {
        return CharacterReader.rangeEquals(this.charBuf, start, count, cached);
    }
}

