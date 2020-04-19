package mcsoft.com.livewallpaperdemo.data;


import android.net.Uri;

public class WallpaperInfoData implements DataItem {
    public Uri imageUri;
    public WallpaperInfoData(Uri uri) {
        this.imageUri = uri;
    }
}
