package cy.com.morefan.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

/**
 * 使用lru算法的Bitmap内存缓存池<br>
 * 
 * <b>创建时间</b> 2014-7-11
 * 
 * @author kymjs (https://github.com/kymjs)
 * @version 1.0
 */
public final class BitmapMemoryCache {

    private MemoryLruCache<String, Bitmap> cache;

    public BitmapMemoryCache() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory());
        init(maxMemory / 4);
    }

    /**
     * @param maxSize
     *            使用内存缓存的内存大小，单位：kb
     */
    public BitmapMemoryCache(int maxSize) {
        init(maxSize / 4);
    }

    /**
     * @param maxSize
     *            使用内存缓存的内存大小，单位：kb
     */
    @SuppressLint("NewApi")
    private void init(int maxSize) {
        cache = new MemoryLruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                super.sizeOf(key, value);
                if (SystemTools.getSDKVersion() >= 12) {
                    return value.getByteCount();
                } else {
                    return value.getRowBytes() * value.getHeight();
                }
            }
        };
    }

    public void put(String key, Bitmap bitmap) {
        if (this.get(key) == null) {
            cache.put(key, bitmap);
        }
    }

    public Bitmap get(String key) {
        return cache.get(key);
    }

    public void remove(String key) {
        cache.remove(key);
    }

    public void removeAll() {
        cache.removeAll();
    }
}
