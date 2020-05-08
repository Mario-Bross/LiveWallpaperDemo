package mcsoft.com.livewallpaperdemo.data;

// ResID of image that will be set as wallpaper
public class WallpaperResourceImageToken implements DataItem {
    public int resId;
    public WallpaperResourceImageToken(int resId) {
        this.resId = resId;
    }
}
