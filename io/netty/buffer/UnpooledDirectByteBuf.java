/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public class UnpooledDirectByteBuf
extends AbstractReferenceCountedByteBuf {
    private final ByteBufAllocator alloc;
    ByteBuffer buffer;
    private ByteBuffer tmpNioBuf;
    private int capacity;
    private boolean doNotFree;

    public UnpooledDirectByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
        super(maxCapacity);
        ObjectUtil.checkNotNull(alloc, "alloc");
        ObjectUtil.checkPositiveOrZero(initialCapacity, "initialCapacity");
        ObjectUtil.checkPositiveOrZero(maxCapacity, "maxCapacity");
        if (initialCapacity > maxCapacity) {
            throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", initialCapacity, maxCapacity));
        }
        this.alloc = alloc;
        this.setByteBuffer(this.allocateDirect(initialCapacity), false);
    }

    protected UnpooledDirectByteBuf(ByteBufAllocator alloc, ByteBuffer initialBuffer, int maxCapacity) {
        this(alloc, initialBuffer, maxCapacity, false, true);
    }

    UnpooledDirectByteBuf(ByteBufAllocator alloc, ByteBuffer initialBuffer, int maxCapacity, boolean doFree, boolean slice) {
        super(maxCapacity);
        ObjectUtil.checkNotNull(alloc, "alloc");
        ObjectUtil.checkNotNull(initialBuffer, "initialBuffer");
        if (!initialBuffer.isDirect()) {
            throw new IllegalArgumentException("initialBuffer is not a direct buffer.");
        }
        if (initialBuffer.isReadOnly()) {
            throw new IllegalArgumentException("initialBuffer is a read-only buffer.");
        }
        int initialCapacity = initialBuffer.remaining();
        if (initialCapacity > maxCapacity) {
            throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", initialCapacity, maxCapacity));
        }
        this.alloc = alloc;
        this.doNotFree = !doFree;
        this.setByteBuffer((slice ? initialBuffer.slice() : initialBuffer).order(ByteOrder.BIG_ENDIAN), false);
        this.writerIndex(initialCapacity);
    }

    protected ByteBuffer allocateDirect(int initialCapacity) {
        return ByteBuffer.allocateDirect(initialCapacity);
    }

    protected void freeDirect(ByteBuffer buffer) {
        PlatformDependent.freeDirectBuffer(buffer);
    }

    void setByteBuffer(ByteBuffer buffer, boolean tryFree) {
        ByteBuffer oldBuffer;
        if (tryFree && (oldBuffer = this.buffer) != null) {
            if (this.doNotFree) {
                this.doNotFree = false;
            } else {
                this.freeDirect(oldBuffer);
            }
        }
        this.buffer = buffer;
        this.tmpNioBuf = null;
        this.capacity = buffer.remaining();
    }

    @Override
    public boolean isDirect() {
        return true;
    }

    @Override
    public int capacity() {
        return this.capacity;
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        int bytesToCopy;
        this.checkNewCapacity(newCapacity);
        int oldCapacity = this.capacity;
        if (newCapacity == oldCapacity) {
            return this;
        }
        if (newCapacity > oldCapacity) {
            bytesToCopy = oldCapacity;
        } else {
            this.trimIndicesToCapacity(newCapacity);
            bytesToCopy = newCapacity;
        }
        ByteBuffer oldBuffer = this.buffer;
        ByteBuffer newBuffer = this.allocateDirect(newCapacity);
        oldBuffer.position(0).limit(bytesToCopy);
        newBuffer.position(0).limit(bytesToCopy);
        newBuffer.put(oldBuffer).clear();
        this.setByteBuffer(newBuffer, true);
        return this;
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.alloc;
    }

    @Override
    public ByteOrder order() {
        return ByteOrder.BIG_ENDIAN;
    }

    @Override
    public boolean hasArray() {
        return false;
    }

    @Override
    public byte[] array() {
        throw new UnsupportedOperationException("direct buffer");
    }

    @Override
    public int arrayOffset() {
        throw new UnsupportedOperationException("direct buffer");
    }

    @Override
    public boolean hasMemoryAddress() {
        return false;
    }

    @Override
    public long memoryAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte getByte(int index) {
        this.ensureAccessible();
        return this._getByte(index);
    }

    @Override
    protected byte _getByte(int index) {
        return this.buffer.get(index);
    }

    @Override
    public short getShort(int index) {
        this.ensureAccessible();
        return this._getShort(index);
    }

    @Override
    protected short _getShort(int index) {
        return this.buffer.getShort(index);
    }

    @Override
    protected short _getShortLE(int index) {
        return ByteBufUtil.swapShort(this.buffer.getShort(index));
    }

    @Override
    public int getUnsignedMedium(int index) {
        this.ensureAccessible();
        return this._getUnsignedMedium(index);
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return (this.getByte(index) & 0xFF) << 16 | (this.getByte(index + 1) & 0xFF) << 8 | this.getByte(index + 2) & 0xFF;
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        return this.getByte(index) & 0xFF | (this.getByte(index + 1) & 0xFF) << 8 | (this.getByte(index + 2) & 0xFF) << 16;
    }

    @Override
    public int getInt(int index) {
        this.ensureAccessible();
        return this._getInt(index);
    }

    @Override
    protected int _getInt(int index) {
        return this.buffer.getInt(index);
    }

    @Override
    protected int _getIntLE(int index) {
        return ByteBufUtil.swapInt(this.buffer.getInt(index));
    }

    @Override
    public long getLong(int index) {
        this.ensureAccessible();
        return this._getLong(index);
    }

    @Override
    protected long _getLong(int index) {
        return this.buffer.getLong(index);
    }

    @Override
    protected long _getLongLE(int index) {
        return ByteBufUtil.swapLong(this.buffer.getLong(index));
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.checkDstIndex(index, length, dstIndex, dst.capacity());
        if (dst.hasArray()) {
            this.getBytes(index, dst.array(), dst.arrayOffset() + dstIndex, length);
        } else if (dst.nioBufferCount() > 0) {
            for (ByteBuffer bb : dst.nioBuffers(dstIndex, length)) {
                int bbLen = bb.remaining();
                this.getBytes(index, bb);
                index += bbLen;
            }
        } else {
            dst.setBytes(dstIndex, this, index, length);
        }
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.getBytes(index, dst, dstIndex, length, false);
        return this;
    }

    void getBytes(int index, byte[] dst, int dstIndex, int length, boolean internal) {
        this.checkDstIndex(index, length, dstIndex, dst.length);
        ByteBuffer tmpBuf = internal ? this.internalNioBuffer() : this.buffer.duplicate();
        tmpBuf.clear().position(index).limit(index + length);
        tmpBuf.get(dst, dstIndex, length);
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        this.checkReadableBytes(length);
        this.getBytes(this.readerIndex, dst, dstIndex, length, true);
        this.readerIndex += length;
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        this.getBytes(index, dst, false);
        return this;
    }

    void getBytes(int index, ByteBuffer dst, boolean internal) {
        this.checkIndex(index, dst.remaining());
        ByteBuffer tmpBuf = internal ? this.internalNioBuffer() : this.buffer.duplicate();
        tmpBuf.clear().position(index).limit(index + dst.remaining());
        dst.put(tmpBuf);
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        int length = dst.remaining();
        this.checkReadableBytes(length);
        this.getBytes(this.readerIndex, dst, true);
        this.readerIndex += length;
        return this;
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        this.ensureAccessible();
        this._setByte(index, value);
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        this.buffer.put(index, (byte)value);
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        this.ensureAccessible();
        this._setShort(index, value);
        return this;
    }

    @Override
    protected void _setShort(int index, int value) {
        this.buffer.putShort(index, (short)value);
    }

    @Override
    protected void _setShortLE(int index, int value) {
        this.buffer.putShort(index, ByteBufUtil.swapShort((short)value));
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        this.ensureAccessible();
        this._setMedium(index, value);
        return this;
    }

    @Override
    protected void _setMedium(int index, int value) {
        this.setByte(index, (byte)(value >>> 16));
        this.setByte(index + 1, (byte)(value >>> 8));
        this.setByte(index + 2, (byte)value);
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        this.setByte(index, (byte)value);
        this.setByte(index + 1, (byte)(value >>> 8));
        this.setByte(index + 2, (byte)(value >>> 16));
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        this.ensureAccessible();
        this._setInt(index, value);
        return this;
    }

    @Override
    protected void _setInt(int index, int value) {
        this.buffer.putInt(index, value);
    }

    @Override
    protected void _setIntLE(int index, int value) {
        this.buffer.putInt(index, ByteBufUtil.swapInt(value));
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        this.ensureAccessible();
        this._setLong(index, value);
        return this;
    }

    @Override
    protected void _setLong(int index, long value) {
        this.buffer.putLong(index, value);
    }

    @Override
    protected void _setLongLE(int index, long value) {
        this.buffer.putLong(index, ByteBufUtil.swapLong(value));
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        this.checkSrcIndex(index, length, srcIndex, src.capacity());
        if (src.nioBufferCount() > 0) {
            for (ByteBuffer bb : src.nioBuffers(srcIndex, length)) {
                int bbLen = bb.remaining();
                this.setBytes(index, bb);
                index += bbLen;
            }
        } else {
            src.getBytes(srcIndex, this, index, length);
        }
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        this.checkSrcIndex(index, length, srcIndex, src.length);
        ByteBuffer tmpBuf = this.internalNioBuffer();
        tmpBuf.clear().position(index).limit(index + length);
        tmpBuf.put(src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        this.ensureAccessible();
        ByteBuffer tmpBuf = this.internalNioBuffer();
        if (src == tmpBuf) {
            src = src.duplicate();
        }
        tmpBuf.clear().position(index).limit(index + src.remaining());
        tmpBuf.put(src);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.getBytes(index, out, length, false);
        return this;
    }

    void getBytes(int index, OutputStream out, int length, boolean internal) throws IOException {
        this.ensureAccessible();
        if (length == 0) {
            return;
        }
        ByteBufUtil.readBytes(this.alloc(), internal ? this.internalNioBuffer() : this.buffer.duplicate(), index, length, out);
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        this.checkReadableBytes(length);
        this.getBytes(this.readerIndex, out, length, true);
        this.readerIndex += length;
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return this.getBytes(index, out, length, false);
    }

    private int getBytes(int index, GatheringByteChannel out, int length, boolean internal) throws IOException {
        this.ensureAccessible();
        if (length == 0) {
            return 0;
        }
        ByteBuffer tmpBuf = internal ? this.internalNioBuffer() : this.buffer.duplicate();
        tmpBuf.clear().position(index).limit(index + length);
        return out.write(tmpBuf);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return this.getBytes(index, out, position, length, false);
    }

    private int getBytes(int index, FileChannel out, long position, int length, boolean internal) throws IOException {
        this.ensureAccessible();
        if (length == 0) {
            return 0;
        }
        ByteBuffer tmpBuf = internal ? this.internalNioBuffer() : this.buffer.duplicate();
        tmpBuf.clear().position(index).limit(index + length);
        return out.write(tmpBuf, position);
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        this.checkReadableBytes(length);
        int readBytes = this.getBytes(this.readerIndex, out, length, true);
        this.readerIndex += readBytes;
        return readBytes;
    }

    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        this.checkReadableBytes(length);
        int readBytes = this.getBytes(this.readerIndex, out, position, length, true);
        this.readerIndex += readBytes;
        return readBytes;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        this.ensureAccessible();
        if (this.buffer.hasArray()) {
            return in.read(this.buffer.array(), this.buffer.arrayOffset() + index, length);
        }
        byte[] tmp = ByteBufUtil.threadLocalTempArray(length);
        int readBytes = in.read(tmp, 0, length);
        if (readBytes <= 0) {
            return readBytes;
        }
        ByteBuffer tmpBuf = this.internalNioBuffer();
        tmpBuf.clear().position(index);
        tmpBuf.put(tmp, 0, readBytes);
        return readBytes;
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        this.ensureAccessible();
        ByteBuffer tmpBuf = this.internalNioBuffer();
        tmpBuf.clear().position(index).limit(index + length);
        try {
            return in.read(tmpBuf);
        }
        catch (ClosedChannelException ignored) {
            return -1;
        }
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        this.ensureAccessible();
        ByteBuffer tmpBuf = this.internalNioBuffer();
        tmpBuf.clear().position(index).limit(index + length);
        try {
            return in.read(tmpBuf, position);
        }
        catch (ClosedChannelException ignored) {
            return -1;
        }
    }

    @Override
    public int nioBufferCount() {
        return 1;
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return new ByteBuffer[]{this.nioBuffer(index, length)};
    }

    @Override
    public final boolean isContiguous() {
        return true;
    }

    @Override
    public ByteBuf copy(int index, int length) {
        ByteBuffer src;
        this.ensureAccessible();
        try {
            src = (ByteBuffer)this.buffer.duplicate().clear().position(index).limit(index + length);
        }
        catch (IllegalArgumentException ignored) {
            throw new IndexOutOfBoundsException("Too many bytes to read - Need " + (index + length));
        }
        return this.alloc().directBuffer(length, this.maxCapacity()).writeBytes(src);
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        this.checkIndex(index, length);
        return (ByteBuffer)this.internalNioBuffer().clear().position(index).limit(index + length);
    }

    private ByteBuffer internalNioBuffer() {
        ByteBuffer tmpNioBuf = this.tmpNioBuf;
        if (tmpNioBuf == null) {
            this.tmpNioBuf = tmpNioBuf = this.buffer.duplicate();
        }
        return tmpNioBuf;
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        this.checkIndex(index, length);
        return ((ByteBuffer)this.buffer.duplicate().position(index).limit(index + length)).slice();
    }

    @Override
    protected void deallocate() {
        ByteBuffer buffer = this.buffer;
        if (buffer == null) {
            return;
        }
        this.buffer = null;
        if (!this.doNotFree) {
            this.freeDirect(buffer);
        }
    }

    @Override
    public ByteBuf unwrap() {
        return null;
    }
}

