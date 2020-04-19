package mcsoft.com.livewallpaperdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import mcsoft.com.livewallpaperdemo.utils.LiveWallpaperUtils;

public class LiveWallpaperActivityLifecycle extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperActivityLifecycle::onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperActivityLifecycle::onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperActivityLifecycle::onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperActivityLifecycle::onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperActivityLifecycle::onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperActivityLifecycle::onDestroy");
        super.onDestroy();
    }

}