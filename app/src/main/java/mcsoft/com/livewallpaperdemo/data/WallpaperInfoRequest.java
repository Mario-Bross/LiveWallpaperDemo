package mcsoft.com.livewallpaperdemo.data;

import android.net.Uri;

public class WallpaperInfoRequest implements DataItem {
    public final static int GET_CURRENT_WALLPAPER = 1;

    public int requestId;
    public Uri res;

    public WallpaperInfoRequest(int id) {
        this.requestId = id;
        this.res = res;
    }
}
