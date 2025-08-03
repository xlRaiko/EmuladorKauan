/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.spi;

import java.io.IOException;
import java.io.OutputStream;

public enum ConsoleTarget {
    SystemOut("System.out", new OutputStream(){

        @Override
        public void write(int b) throws IOException {
            System.out.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            System.out.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            System.out.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            System.out.flush();
        }
    }),
    SystemErr("System.err", new OutputStream(){

        @Override
        public void write(int b) throws IOException {
            System.err.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            System.err.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            System.err.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            System.err.flush();
        }
    });

    private final String name;
    private final OutputStream stream;

    public static ConsoleTarget findByName(String name) {
        for (ConsoleTarget target : ConsoleTarget.values()) {
            if (!target.name.equalsIgnoreCase(name)) continue;
            return target;
        }
        return null;
    }

    private ConsoleTarget(String name, OutputStream stream) {
        this.name = name;
        this.stream = stream;
    }

    public String getName() {
        return this.name;
    }

    public OutputStream getStream() {
        return this.stream;
    }

    public String toString() {
        return this.name;
    }
}

