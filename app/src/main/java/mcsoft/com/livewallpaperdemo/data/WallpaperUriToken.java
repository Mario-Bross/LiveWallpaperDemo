package mcsoft.com.livewallpaperdemo.data;


import android.net.Uri;

public class WallpaperUriToken implements DataItem {
    public Uri imageUri;
    public WallpaperUriToken(Uri uri) {
        this.imageUri = uri;
    }
}
