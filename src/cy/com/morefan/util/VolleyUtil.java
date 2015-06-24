package cy.com.morefan.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleyUtil
{
    private static RequestQueue requestQueue = null;

    private static ImageLoader imageLoader = null;

    public static void init(Context context)
    {
        requestQueue = Volley.newRequestQueue(context);
        imageLoader = new ImageLoader(requestQueue, LruImageCache.instance() );
    }
    
    public static RequestQueue getRequestQueue() {
        if (requestQueue != null) {
            return requestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }
    public static void addRequest(Request<?> request, Object tag) {
        if (tag != null) {
            request.setTag(tag);
        }
        requestQueue.add(request);
    }
    public static void cancelAll(Object tag) {
        requestQueue.cancelAll(tag);
    }
    public static ImageLoader getImageLoader(Context context) {
        if (null == imageLoader) 
        {
            init(context);
        } 
        return imageLoader;
    }
}
