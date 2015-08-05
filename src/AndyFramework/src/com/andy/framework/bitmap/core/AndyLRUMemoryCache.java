package com.andy.framework.bitmap.core;

import java.util.LinkedHashMap;
import java.util.Map;
import android.annotation.SuppressLint;

/**
 * @description: 最近最少使用算法缓存实现
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-12  下午2:41:39
 */
@SuppressLint("DefaultLocale")
public class AndyLRUMemoryCache<K, V> {

	private final LinkedHashMap<K, V> map;
	// 缓存大小
	private int size;
	// 最大大小
	private int maxSize;
	
	private int putCount; // put的数量
	private int createCount; // 创建value数量
	private int evictionCount; // 回收数量
	private int hitCount; // 命中数量
	private int missCount; // 错过数量
	
	public AndyLRUMemoryCache(int maxSize) {
		if (maxSize <= 0) {
			throw new IllegalArgumentException("AndyLRUMemoryCache ：illegal param maxSize <= 0");
		}
		this.maxSize = maxSize;
		map = new LinkedHashMap<K, V>();
	}
	
	/**
	 * 根据Key得到对应的value，如果存在则放到队列开头
	 * */
	public final V get(K key) {
		if (key == null) {
			throw new NullPointerException("key is null");
		}
		
		// 如果map中存在key-value则直接取出并返回
		V mapValue = null;
		synchronized (this) {
			mapValue = map.get(key);
			if (mapValue != null) {
				hitCount++;
				return mapValue;
			}
			missCount++;
		}
		
		/**
		 * map中不存在key-value
		 * 尝试去创建一个value，耗时操作，而且当create return 时map可能会改变
		 * 如果在create的时候有一个冲突的值add则需要释放加入的值
		 * */
		V createdValue = create(key);
		if (createdValue == null) {
			return null;
		}
		
		/**
		 * 尝试创建value成功
		 * */
		synchronized (this) {
			createCount++;
			mapValue = map.put(key, createdValue);
			if (mapValue != null) {
				// 这是一个冲突值，所以撤销最后一次设置的值重新放值
				map.put(key, mapValue);
			} else {
				size += safeSizeOf(key, createdValue);
			}
		}
		
		if (mapValue != null) {
			entryRemoved(false, key, createdValue, mapValue);
            return mapValue;
		} else {
			trimToSize(maxSize);
            return createdValue;
		}
	}
	
	/**
	 * 将value移动到队列开头
	 * */
	public final V put(K key, V value) {
		if (key == null) {
			throw new NullPointerException("key is null");
		}
		if (value == null) {
			throw new NullPointerException("value is null");
		}
		// 之前的值
		V previousValue = null;
		synchronized (this) {
			putCount++;
			size += safeSizeOf(key, value);
			previousValue = map.put(key, value);
			if (previousValue != null) {
				size -= safeSizeOf(key, previousValue);
			}
		}
		
		if (previousValue != null) {
            entryRemoved(false, key, previousValue, value);
        }

        trimToSize(maxSize);
        return previousValue;
	}
	
	/**
	 * 移除K-V
	 * */
	public final V remove(K key) {
		if (key == null) {
			throw new NullPointerException("key is null");
		}
		
		V previousValue;
		synchronized (this) {
			previousValue = map.remove(key);
			if (previousValue != null) {
				size -= safeSizeOf(key, previousValue);
			}
		}
		
		if (previousValue != null) {
			entryRemoved(false, key, previousValue, null);
		}
		return previousValue;
	}
	
	/**
     * Clear the cache, calling {@link #entryRemoved} on each removed entry.
     */
    public final void removeAll() {
        trimToSize(-1); // -1 will evict 0-sized elements
    }

    /**
     * For caches that do not override {@link #sizeOf}, this returns the number
     * of entries in the cache. For all other caches, this returns the sum of
     * the sizes of the entries in this cache.
     */
    public synchronized final int size() {
        return size;
    }

    /**
     * For caches that do not override {@link #sizeOf}, this returns the maximum
     * number of entries in the cache. For all other caches, this returns the
     * maximum sum of the sizes of the entries in this cache.
     */
    public synchronized final int maxSize() {
        return maxSize;
    }

    /**
     * Returns the number of times {@link #get} returned a value.
     */
    public synchronized final int hitCount() {
        return hitCount;
    }

    /**
     * Returns the number of times {@link #get} returned null or required a new
     * value to be created.
     */
    public synchronized final int missCount() {
        return missCount;
    }

    /**
     * Returns the number of times {@link #create(Object)} returned a value.
     */
    public synchronized final int createCount() {
        return createCount;
    }

    /**
     * Returns the number of times {@link #put} was called.
     */
    public synchronized final int putCount() {
        return putCount;
    }

    /**
     * Returns the number of values that have been evicted.
     */
    public synchronized final int evictionCount() {
        return evictionCount;
    }

    /**
     * Returns a copy of the current contents of the cache, ordered from least
     * recently accessed to most recently accessed.
     */
    public synchronized final Map<K, V> snapshot() {
        return new LinkedHashMap<K, V>(map);
    }

	public synchronized final String toString() {
        int accesses = hitCount + missCount;
        int hitPercent = accesses != 0 ? (100 * hitCount / accesses) : 0;
        return String.format("LruMemoryCache[maxSize=%d,hits=%d,misses=%d,hitRate=%d%%]",maxSize, hitCount, missCount, hitPercent);
    }
	
	/**
     * Called after a cache miss to compute a value for the corresponding key.
     * Returns the computed value or null if no value can be computed. The
     * default implementation returns null.
     *
     * <p>The method is called without synchronization: other threads may
     * access the cache while this method is executing.
     *
     * <p>If a value for {@code key} exists in the cache when this method
     * returns, the created value will be released with {@link #entryRemoved}
     * and discarded. This can occur when multiple threads request the same key
     * at the same time (causing multiple values to be created), or when one
     * thread calls {@link #put} while another is creating a value for the same
     * key.
     */
	protected V create(K key) {
		return null;
	}
	
	/**
     * Returns the size of the entry for {@code key} and {@code value} in
     * user-defined units.  The default implementation returns 1 so that size
     * is the number of entries and max size is the maximum number of entries.
     *
     * <p>An entry's size must not change while it is in the cache.
     */
    protected int sizeOf(K key, V value) {
        return 1;
    }
    
    /**
     * Called for entries that have been evicted or removed. This method is
     * invoked when a value is evicted to make space, removed by a call to
     * {@link #remove}, or replaced by a call to {@link #put}. The default
     * implementation does nothing.
     *
     * <p>The method is called without synchronization: other threads may
     * access the cache while this method is executing.
     *
     * @param evicted true if the entry is being removed to make space, false
     *     if the removal was caused by a {@link #put} or {@link #remove}.
     * @param newValue the new value for {@code key}, if it exists. If non-null,
     *     this removal was caused by a {@link #put}. Otherwise it was caused by
     *     an eviction or a {@link #remove}.
     */
    protected void entryRemoved(boolean evicted, K key, V oldValue, V newValue) {}
	
    
    
/************************************** private method  *********************************************/	
	private int safeSizeOf(K key, V value) {
		int result = sizeOf(key, value);
        if (result < 0) {
            throw new IllegalStateException("Negative size: " + key + " = " + value);
        }
        return result;
	}
	
	/**
	 * 清理map直到size小于等于maxSize
	 * */
	private void trimToSize(int maxSize) {
		while (true) {
            K key;
            V value;
            synchronized (this) {
                if (size < 0 || (map.isEmpty() && size != 0)) {
                    throw new IllegalStateException(getClass().getName()
                            + ".sizeOf() is reporting inconsistent results!");
                }

                if (size <= maxSize || map.isEmpty()) {
                    break;
                }

                Map.Entry<K, V> toEvict = map.entrySet().iterator().next();
                key = toEvict.getKey();
                value = toEvict.getValue();
                map.remove(key);
                size -= safeSizeOf(key, value);
                evictionCount++;
            }
            entryRemoved(true, key, value, null);
        }
	}
}
