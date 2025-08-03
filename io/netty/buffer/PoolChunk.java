/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.PoolArena;
import io.netty.buffer.PoolChunkList;
import io.netty.buffer.PoolChunkMetric;
import io.netty.buffer.PoolSubpage;
import io.netty.buffer.PoolThreadCache;
import io.netty.buffer.PooledByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

final class PoolChunk<T>
implements PoolChunkMetric {
    private static final int INTEGER_SIZE_MINUS_ONE = 31;
    final PoolArena<T> arena;
    final T memory;
    final boolean unpooled;
    final int offset;
    private final byte[] memoryMap;
    private final byte[] depthMap;
    private final PoolSubpage<T>[] subpages;
    private final int subpageOverflowMask;
    private final int pageSize;
    private final int pageShifts;
    private final int maxOrder;
    private final int chunkSize;
    private final int log2ChunkSize;
    private final int maxSubpageAllocs;
    private final byte unusable;
    private final Deque<ByteBuffer> cachedNioBuffers;
    int freeBytes;
    PoolChunkList<T> parent;
    PoolChunk<T> prev;
    PoolChunk<T> next;

    PoolChunk(PoolArena<T> arena, T memory, int pageSize, int maxOrder, int pageShifts, int chunkSize, int offset) {
        this.unpooled = false;
        this.arena = arena;
        this.memory = memory;
        this.pageSize = pageSize;
        this.pageShifts = pageShifts;
        this.maxOrder = maxOrder;
        this.chunkSize = chunkSize;
        this.offset = offset;
        this.unusable = (byte)(maxOrder + 1);
        this.log2ChunkSize = PoolChunk.log2(chunkSize);
        this.subpageOverflowMask = ~(pageSize - 1);
        this.freeBytes = chunkSize;
        assert (maxOrder < 30) : "maxOrder should be < 30, but is: " + maxOrder;
        this.maxSubpageAllocs = 1 << maxOrder;
        this.memoryMap = new byte[this.maxSubpageAllocs << 1];
        this.depthMap = new byte[this.memoryMap.length];
        int memoryMapIndex = 1;
        for (int d = 0; d <= maxOrder; ++d) {
            int depth = 1 << d;
            for (int p = 0; p < depth; ++p) {
                this.memoryMap[memoryMapIndex] = (byte)d;
                this.depthMap[memoryMapIndex] = (byte)d;
                ++memoryMapIndex;
            }
        }
        this.subpages = this.newSubpageArray(this.maxSubpageAllocs);
        this.cachedNioBuffers = new ArrayDeque<ByteBuffer>(8);
    }

    PoolChunk(PoolArena<T> arena, T memory, int size, int offset) {
        this.unpooled = true;
        this.arena = arena;
        this.memory = memory;
        this.offset = offset;
        this.memoryMap = null;
        this.depthMap = null;
        this.subpages = null;
        this.subpageOverflowMask = 0;
        this.pageSize = 0;
        this.pageShifts = 0;
        this.maxOrder = 0;
        this.unusable = (byte)(this.maxOrder + 1);
        this.chunkSize = size;
        this.log2ChunkSize = PoolChunk.log2(this.chunkSize);
        this.maxSubpageAllocs = 0;
        this.cachedNioBuffers = null;
    }

    private PoolSubpage<T>[] newSubpageArray(int size) {
        return new PoolSubpage[size];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int usage() {
        int freeBytes;
        PoolArena<T> poolArena = this.arena;
        synchronized (poolArena) {
            freeBytes = this.freeBytes;
        }
        return this.usage(freeBytes);
    }

    private int usage(int freeBytes) {
        if (freeBytes == 0) {
            return 100;
        }
        int freePercentage = (int)((long)freeBytes * 100L / (long)this.chunkSize);
        if (freePercentage == 0) {
            return 99;
        }
        return 100 - freePercentage;
    }

    boolean allocate(PooledByteBuf<T> buf, int reqCapacity, int normCapacity, PoolThreadCache threadCache) {
        long handle = (normCapacity & this.subpageOverflowMask) != 0 ? this.allocateRun(normCapacity) : this.allocateSubpage(normCapacity);
        if (handle < 0L) {
            return false;
        }
        ByteBuffer nioBuffer = this.cachedNioBuffers != null ? this.cachedNioBuffers.pollLast() : null;
        this.initBuf(buf, nioBuffer, handle, reqCapacity, threadCache);
        return true;
    }

    private void updateParentsAlloc(int id) {
        while (id > 1) {
            byte val2;
            int parentId = id >>> 1;
            byte val1 = this.value(id);
            byte val = val1 < (val2 = this.value(id ^ 1)) ? val1 : val2;
            this.setValue(parentId, val);
            id = parentId;
        }
    }

    private void updateParentsFree(int id) {
        int logChild = this.depth(id) + 1;
        while (id > 1) {
            int parentId = id >>> 1;
            byte val1 = this.value(id);
            byte val2 = this.value(id ^ 1);
            if (val1 == --logChild && val2 == logChild) {
                this.setValue(parentId, (byte)(logChild - 1));
            } else {
                byte val = val1 < val2 ? val1 : val2;
                this.setValue(parentId, val);
            }
            id = parentId;
        }
    }

    private int allocateNode(int d) {
        int id = 1;
        int initial = -(1 << d);
        byte val = this.value(id);
        if (val > d) {
            return -1;
        }
        while (val < d || (id & initial) == 0) {
            val = this.value(id <<= 1);
            if (val <= d) continue;
            val = this.value(id ^= 1);
        }
        byte value = this.value(id);
        assert (value == d && (id & initial) == 1 << d) : String.format("val = %d, id & initial = %d, d = %d", value, id & initial, d);
        this.setValue(id, this.unusable);
        this.updateParentsAlloc(id);
        return id;
    }

    private long allocateRun(int normCapacity) {
        int d = this.maxOrder - (PoolChunk.log2(normCapacity) - this.pageShifts);
        int id = this.allocateNode(d);
        if (id < 0) {
            return id;
        }
        this.freeBytes -= this.runLength(id);
        return id;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private long allocateSubpage(int normCapacity) {
        PoolSubpage<T> head = this.arena.findSubpagePoolHead(normCapacity);
        int d = this.maxOrder;
        PoolSubpage<T> poolSubpage = head;
        synchronized (poolSubpage) {
            int id = this.allocateNode(d);
            if (id < 0) {
                return id;
            }
            PoolSubpage<T>[] subpages = this.subpages;
            int pageSize = this.pageSize;
            this.freeBytes -= pageSize;
            int subpageIdx = this.subpageIdx(id);
            PoolSubpage<T> subpage = subpages[subpageIdx];
            if (subpage == null) {
                subpage = new PoolSubpage<T>(head, this, id, this.runOffset(id), pageSize, normCapacity);
                subpages[subpageIdx] = subpage;
            } else {
                subpage.init(head, normCapacity);
            }
            return subpage.allocate();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void free(long handle, ByteBuffer nioBuffer) {
        int memoryMapIdx = PoolChunk.memoryMapIdx(handle);
        int bitmapIdx = PoolChunk.bitmapIdx(handle);
        if (bitmapIdx != 0) {
            PoolSubpage<T> head;
            PoolSubpage<T> subpage = this.subpages[this.subpageIdx(memoryMapIdx)];
            assert (subpage != null && subpage.doNotDestroy);
            PoolSubpage<T> poolSubpage = head = this.arena.findSubpagePoolHead(subpage.elemSize);
            synchronized (poolSubpage) {
                if (subpage.free(head, bitmapIdx & 0x3FFFFFFF)) {
                    return;
                }
            }
        }
        this.freeBytes += this.runLength(memoryMapIdx);
        this.setValue(memoryMapIdx, this.depth(memoryMapIdx));
        this.updateParentsFree(memoryMapIdx);
        if (nioBuffer != null && this.cachedNioBuffers != null && this.cachedNioBuffers.size() < PooledByteBufAllocator.DEFAULT_MAX_CACHED_BYTEBUFFERS_PER_CHUNK) {
            this.cachedNioBuffers.offer(nioBuffer);
        }
    }

    void initBuf(PooledByteBuf<T> buf, ByteBuffer nioBuffer, long handle, int reqCapacity, PoolThreadCache threadCache) {
        int memoryMapIdx = PoolChunk.memoryMapIdx(handle);
        int bitmapIdx = PoolChunk.bitmapIdx(handle);
        if (bitmapIdx == 0) {
            byte val = this.value(memoryMapIdx);
            assert (val == this.unusable) : String.valueOf(val);
            buf.init(this, nioBuffer, handle, this.runOffset(memoryMapIdx) + this.offset, reqCapacity, this.runLength(memoryMapIdx), threadCache);
        } else {
            this.initBufWithSubpage(buf, nioBuffer, handle, bitmapIdx, reqCapacity, threadCache);
        }
    }

    void initBufWithSubpage(PooledByteBuf<T> buf, ByteBuffer nioBuffer, long handle, int reqCapacity, PoolThreadCache threadCache) {
        this.initBufWithSubpage(buf, nioBuffer, handle, PoolChunk.bitmapIdx(handle), reqCapacity, threadCache);
    }

    private void initBufWithSubpage(PooledByteBuf<T> buf, ByteBuffer nioBuffer, long handle, int bitmapIdx, int reqCapacity, PoolThreadCache threadCache) {
        assert (bitmapIdx != 0);
        int memoryMapIdx = PoolChunk.memoryMapIdx(handle);
        PoolSubpage<T> subpage = this.subpages[this.subpageIdx(memoryMapIdx)];
        assert (subpage.doNotDestroy);
        assert (reqCapacity <= subpage.elemSize);
        buf.init(this, nioBuffer, handle, this.runOffset(memoryMapIdx) + (bitmapIdx & 0x3FFFFFFF) * subpage.elemSize + this.offset, reqCapacity, subpage.elemSize, threadCache);
    }

    private byte value(int id) {
        return this.memoryMap[id];
    }

    private void setValue(int id, byte val) {
        this.memoryMap[id] = val;
    }

    private byte depth(int id) {
        return this.depthMap[id];
    }

    private static int log2(int val) {
        return 31 - Integer.numberOfLeadingZeros(val);
    }

    private int runLength(int id) {
        return 1 << this.log2ChunkSize - this.depth(id);
    }

    private int runOffset(int id) {
        int shift = id ^ 1 << this.depth(id);
        return shift * this.runLength(id);
    }

    private int subpageIdx(int memoryMapIdx) {
        return memoryMapIdx ^ this.maxSubpageAllocs;
    }

    private static int memoryMapIdx(long handle) {
        return (int)handle;
    }

    private static int bitmapIdx(long handle) {
        return (int)(handle >>> 32);
    }

    @Override
    public int chunkSize() {
        return this.chunkSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int freeBytes() {
        PoolArena<T> poolArena = this.arena;
        synchronized (poolArena) {
            return this.freeBytes;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString() {
        int freeBytes;
        PoolArena<T> poolArena = this.arena;
        synchronized (poolArena) {
            freeBytes = this.freeBytes;
        }
        return "Chunk(" + Integer.toHexString(System.identityHashCode(this)) + ": " + this.usage(freeBytes) + "%, " + (this.chunkSize - freeBytes) + '/' + this.chunkSize + ')';
    }

    void destroy() {
        this.arena.destroyChunk(this);
    }
}

