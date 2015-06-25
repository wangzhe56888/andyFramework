package com.andy.framework.bitmap.core;

import java.util.ArrayList;

public class AndyBytesBufferPool {
    public static class AndyBytesBuffer {
        public byte[] data;
        public int offset;
        public int length;

        private AndyBytesBuffer(int capacity) {
            this.data = new byte[capacity];
        }
    }

    private final int mPoolSize;
    private final int mBufferSize;
    private final ArrayList<AndyBytesBuffer> mList;

    public AndyBytesBufferPool(int poolSize, int bufferSize) {
        mList = new ArrayList<AndyBytesBuffer>(poolSize);
        mPoolSize = poolSize;
        mBufferSize = bufferSize;
    }

    public synchronized AndyBytesBuffer get() {
        int n = mList.size();
        return n > 0 ? mList.remove(n - 1) : new AndyBytesBuffer(mBufferSize);
    }

    public synchronized void recycle(AndyBytesBuffer buffer) {
        if (buffer.data.length != mBufferSize) return;
        if (mList.size() < mPoolSize) {
            buffer.offset = 0;
            buffer.length = 0;
            mList.add(buffer);
        }
    }

    public synchronized void clear() {
        mList.clear();
    }
}
