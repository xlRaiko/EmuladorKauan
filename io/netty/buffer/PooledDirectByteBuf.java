/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBuf;
import io.netty.util.internal.ObjectPool;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

final class PooledDirectByteBuf
extends PooledByteBuf<ByteBuffer> {
    private static final ObjectPool<PooledDirectByteBuf> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<PooledDirectByteBuf>(){

        @Override
        public PooledDirectByteBuf newObject(ObjectPool.Handle<PooledDirectByteBuf> handle) {
            return new PooledDirectByteBuf(handle, 0);
        }
    });

    static PooledDirectByteBuf newInstance(int maxCapacity) {
        PooledDirectByteBuf buf = RECYCLER.get();
        buf.reuse(maxCapacity);
        return buf;
    }

    private PooledDirectByteBuf(ObjectPool.Handle<PooledDirectByteBuf> recyclerHandle, int maxCapacity) {
        super(recyclerHandle, maxCapacity);
    }

    @Override
    protected ByteBuffer newInternalNioBuffer(ByteBuffer memory) {
        return memory.duplicate();
    }

    @Override
    public boolean isDirect() {
        return true;
    }

    @Override
    protected byte _getByte(int index) {
        return ((ByteBuffer)this.memory).get(this.idx(index));
    }

    @Override
    protected short _getShort(int index) {
        return ((ByteBuffer)this.memory).getShort(this.idx(index));
    }

    @Override
    protected short _getShortLE(int index) {
        return ByteBufUtil.swapShort(this._getShort(index));
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        index = this.idx(index);
        return (((ByteBuffer)this.memory).get(index) & 0xFF) << 16 | (((ByteBuffer)this.memory).get(index + 1) & 0xFF) << 8 | ((ByteBuffer)this.memory).get(index + 2) & 0xFF;
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        index = this.idx(index);
        return ((ByteBuffer)this.memory).get(index) & 0xFF | (((ByteBuffer)this.memory).get(index + 1) & 0xFF) << 8 | (((ByteBuffer)this.memory).get(index + 2) & 0xFF) << 16;
    }

    @Override
    protected int _getInt(int index) {
        return ((ByteBuffer)this.memory).getInt(this.idx(index));
    }

    @Override
    protected int _getIntLE(int index) {
        return ByteBufUtil.swapInt(this._getInt(index));
    }

    @Override
    protected long _getLong(int index) {
        return ((ByteBuffer)this.memory).getLong(this.idx(index));
    }

    @Override
    protected long _getLongLE(int index) {
        return ByteBufUtil.swapLong(this._getLong(index));
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
        this.checkDstIndex(index, length, dstIndex, dst.length);
        this._internalNioBuffer(index, length, true).get(dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        this.checkDstIndex(length, dstIndex, dst.length);
        this._internalNioBuffer(this.readerIndex, length, false).get(dst, dstIndex, length);
        this.readerIndex += length;
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        dst.put(this.duplicateInternalNioBuffer(index, dst.remaining()));
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        int length = dst.remaining();
        this.checkReadableBytes(length);
        dst.put(this._internalNioBuffer(this.readerIndex, length, false));
        this.readerIndex += length;
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.getBytes(index, out, length, false);
        return this;
    }

    private void getBytes(int index, OutputStream out, int length, boolean internal) throws IOException {
        this.checkIndex(index, length);
        if (length == 0) {
            return;
        }
        ByteBufUtil.readBytes(this.alloc(), internal ? this.internalNioBuffer() : ((ByteBuffer)this.memory).duplicate(), this.idx(index), length, out);
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        this.checkReadableBytes(length);
        this.getBytes(this.readerIndex, out, length, true);
        this.readerIndex += length;
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        ((ByteBuffer)this.memory).put(this.idx(index), (byte)value);
    }

    @Override
    protected void _setShort(int index, int value) {
        ((ByteBuffer)this.memory).putShort(this.idx(index), (short)value);
    }

    @Override
    protected void _setShortLE(int index, int value) {
        this._setShort(index, ByteBufUtil.swapShort((short)value));
    }

    @Override
    protected void _setMedium(int index, int value) {
        index = this.idx(index);
        ((ByteBuffer)this.memory).put(index, (byte)(value >>> 16));
        ((ByteBuffer)this.memory).put(index + 1, (byte)(value >>> 8));
        ((ByteBuffer)this.memory).put(index + 2, (byte)value);
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        index = this.idx(index);
        ((ByteBuffer)this.memory).put(index, (byte)value);
        ((ByteBuffer)this.memory).put(index + 1, (byte)(value >>> 8));
        ((ByteBuffer)this.memory).put(index + 2, (byte)(value >>> 16));
    }

    @Override
    protected void _setInt(int index, int value) {
        ((ByteBuffer)this.memory).putInt(this.idx(index), value);
    }

    @Override
    protected void _setIntLE(int index, int value) {
        this._setInt(index, ByteBufUtil.swapInt(value));
    }

    @Override
    protected void _setLong(int index, long value) {
        ((ByteBuffer)this.memory).putLong(this.idx(index), value);
    }

    @Override
    protected void _setLongLE(int index, long value) {
        this._setLong(index, ByteBufUtil.swapLong(value));
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        this.checkSrcIndex(index, length, srcIndex, src.capacity());
        if (src.hasArray()) {
            this.setBytes(index, src.array(), src.arrayOffset() + srcIndex, length);
        } else if (src.nioBufferCount() > 0) {
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
        this._internalNioBuffer(index, length, false).put(src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        int length = src.remaining();
        this.checkIndex(index, length);
        ByteBuffer tmpBuf = this.internalNioBuffer();
        if (src == tmpBuf) {
            src = src.duplicate();
        }
        index = this.idx(index);
        tmpBuf.limit(index + length).position(index);
        tmpBuf.put(src);
        return this;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        this.checkIndex(index, length);
        byte[] tmp = ByteBufUtil.threadLocalTempArray(length);
        int readBytes = in.read(tmp, 0, length);
        if (readBytes <= 0) {
            return readBytes;
        }
        ByteBuffer tmpBuf = this.internalNioBuffer();
        tmpBuf.position(this.idx(index));
        tmpBuf.put(tmp, 0, readBytes);
        return readBytes;
    }

    @Override
    public ByteBuf copy(int index, int length) {
        this.checkIndex(index, length);
        ByteBuf copy = this.alloc().directBuffer(length, this.maxCapacity());
        return copy.writeBytes(this, index, length);
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
}

