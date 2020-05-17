package mcsoft.com.livewallpaperdemo.scheduler;

import android.content.Context;
import android.util.Log;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import mcsoft.com.livewallpaperdemo.data.WallpaperResourceImageToken;
import mcsoft.com.livewallpaperdemo.utils.RxDataBus;
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

        if (RxDataBus.getInstance() != null) {
            Random random = new Random();
            int randomInteger = random.nextInt(9)  + 1;

            Log.i(LiveWallpaperUtils.TAG, "wallpaper" + Integer.toString(randomInteger));
            int resId = getApplicationContext().
                getResources().
                getIdentifier("wallpaper" + Integer.toString(randomInteger), "drawable", getApplicationContext().getPackageName());
            RxDataBus.getInstance().doNext(new WallpaperResourceImageToken(resId));
        }
        return Result.success();
    }
}
