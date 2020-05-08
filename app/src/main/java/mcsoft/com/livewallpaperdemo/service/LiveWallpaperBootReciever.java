package mcsoft.com.livewallpaperdemo.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import mcsoft.com.livewallpaperdemo.R;
import mcsoft.com.livewallpaperdemo.data.WallpaperResourceImageToken;
import mcsoft.com.livewallpaperdemo.utils.LiveWallpaperObservable;
import mcsoft.com.livewallpaperdemo.utils.LiveWallpaperUtils;

public class LiveWallpaperBootReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperBootReciever action: " + action);
        if (LiveWallpaperUtils.isWallpaperActive(context) == true) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_pref_name), Context.MODE_PRIVATE);
            int resId = sharedPreferences.getInt(context.getString(R.string.shared_pref_resource_id),0);
            if (resId != 0) {
                LiveWallpaperObservable.getInstance().doNext(new WallpaperResourceImageToken(resId));
            } else {
                LiveWallpaperObservable.getInstance().doNext(new WallpaperResourceImageToken(R.drawable.wallpaper1));
            }
        }
    }

}
