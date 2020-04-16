package mcsoft.com.livewallpaperdemo.utils;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.util.Log;

import mcsoft.com.livewallpaperdemo.service.LiveWallpaperService;

import static android.content.Context.WALLPAPER_SERVICE;

public class LiveWallpaperUtils {

    public final static String TAG = "LiveWallpaper";

    public static boolean isWallpaperActive(Context context) {
        boolean result = false;

        WallpaperManager wpm = (WallpaperManager) context.getSystemService(WALLPAPER_SERVICE);
        WallpaperInfo info = wpm.getWallpaperInfo();

        if  (info != null) {
            ComponentName serviceComponent = info.getComponent();
            String serviceClassName = serviceComponent.getClassName();
            String liveWallpaperClassName = LiveWallpaperService.class.getName();
            if (serviceClassName.equals(liveWallpaperClassName)) {
                Log.d(LiveWallpaperUtils.TAG, "isWallpaperActive:: Live Wallpaper is already running");
                result = true;
            } else {
                Log.d(LiveWallpaperUtils.TAG, "isWallpaperActive:: Live Wallpaper is not running, this should be a preview");
            }
        } else {
        }
        return result;
    }

    public static final Uri getUriToResource(@NonNull Context context,
                                             @AnyRes int resId)
        throws Resources.NotFoundException {
        /** Return a Resources instance for your application's package. */
        Resources res = context.getResources();
        /**
         * Creates a Uri which parses the given encoded URI string.
         * @param uriString an RFC 2396-compliant, encoded URI
         * @throws NullPointerException if uriString is null
         * @return Uri for this given uri string
         */
        Uri resUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
            "://" + res.getResourcePackageName(resId)
            + '/' + res.getResourceTypeName(resId)
            + '/' + res.getResourceEntryName(resId));
        /** return uri */
        return resUri;
    }

}
