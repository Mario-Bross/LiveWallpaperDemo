package mcsoft.com.livewallpaperdemo.scheduler;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import mcsoft.com.livewallpaperdemo.utils.LiveWallpaperUtils;

public class SchedulerWorker extends Worker {

    public SchedulerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i(LiveWallpaperUtils.TAG, "SchedulerWorker::doWork");
        return Result.success();
    }
}
