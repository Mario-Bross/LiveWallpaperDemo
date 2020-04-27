package mcsoft.com.livewallpaperdemo.scheduler;

import android.content.Context;
import android.util.Log;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import mcsoft.com.livewallpaperdemo.R;
import mcsoft.com.livewallpaperdemo.data.WallpaperResourceImage;
import mcsoft.com.livewallpaperdemo.utils.LiveWallpaperObservable;
import mcsoft.com.livewallpaperdemo.utils.LiveWallpaperUtils;


public class SchedulerWorker extends Worker {

    public SchedulerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i(LiveWallpaperUtils.TAG, "***********************************+");
        Log.i(LiveWallpaperUtils.TAG, "SchedulerWorker::doWork");
        Log.i(LiveWallpaperUtils.TAG, "***********************************+");

        if (LiveWallpaperObservable.getInstance() != null) {
            Random random = new Random();
            int randomInteger = random.nextInt(9)  + 1;

            Log.i(LiveWallpaperUtils.TAG, "wallpaper" + Integer.toString(randomInteger));
            int resId = getApplicationContext().
                getResources().
                getIdentifier("wallpaper" + Integer.toString(randomInteger), "drawable", getApplicationContext().getPackageName());
            LiveWallpaperObservable.getInstance().doNext(new WallpaperResourceImage(resId));
        }
        return Result.success();
    }
}
