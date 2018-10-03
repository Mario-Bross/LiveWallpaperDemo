package mcsoft.com.livewallpaperdemo.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;

public class LiveWallpaperUtils {

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
