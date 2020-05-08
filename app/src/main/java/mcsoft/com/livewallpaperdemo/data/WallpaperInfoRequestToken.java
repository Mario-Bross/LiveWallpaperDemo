package mcsoft.com.livewallpaperdemo.data;

import android.net.Uri;

public class WallpaperInfoRequestToken implements DataItem {
    public final static int GET_CURRENT_WALLPAPER = 1;
    public final static int GET_WALLPAPER_ORIENTATION = 2;

    public int requestId;
//    public Uri res;

    public WallpaperInfoRequestToken(int id) {
        this.requestId = id;
//        this.res = res;
    }
}
