package mcsoft.com.livewallpaperdemo.scheduler;

import android.content.Context;
import android.util.Log;


import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import mcsoft.com.livewallpaperdemo.utils.LiveWallpaperUtils;

import static mcsoft.com.livewallpaperdemo.utils.LiveWallpaperUtils.TAG;

public class LiveWallpaperScheduler {

    private static final String SCHEDULER_TAG = "live_wallpaper_worker_tag";

    private static LiveWallpaperScheduler mInstance;

    private LiveWallpaperScheduler() {
    }

    public static LiveWallpaperScheduler getInstance() {
        if (mInstance == null){
            mInstance = new LiveWallpaperScheduler();
        }
        return mInstance;
    }

    public void startScheduler(Context context) {
        PeriodicWorkRequest changeWallPaper =
            new PeriodicWorkRequest.Builder(SchedulerWorker.class, 15, TimeUnit.MINUTES)
                .addTag(SCHEDULER_TAG)
                .build();
        WorkManager wm = WorkManager.getInstance(context);
        wm.enqueueUniquePeriodicWork(SCHEDULER_TAG, ExistingPeriodicWorkPolicy.REPLACE, changeWallPaper);
        Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperScheduler::startScheduler");
    }

    public void stopScheduler(Context context) {
        WorkManager wm = WorkManager.getInstance(context);
        wm.cancelAllWorkByTag(SCHEDULER_TAG);
        Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperScheduler::stopScheduler");
    }

    public void getWorkRequestId(Context context) {
        WorkManager wm = WorkManager.getInstance(context);
        ListenableFuture<List<WorkInfo>> future = wm.getWorkInfosByTag(SCHEDULER_TAG);
    }


    public boolean isSchedulerRunning(Context context) {
        boolean isRunning = false;
        WorkManager wm = WorkManager.getInstance(context);
        ListenableFuture<List<WorkInfo>> future = wm.getWorkInfosByTag(SCHEDULER_TAG);
        try {
            
            List<WorkInfo> list = future.get();
            // start only if no such tasks present
            if((list == null) || (list.size() == 0)){
                Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperScheduler::isSchedulerRunning: WorkInfo count = " + list.size());
                isRunning = false;
            } else {
                Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperScheduler::isSchedulerRunning: WorkInfo count = " + list.size());
                isRunning = isRunning(list);
            }
            
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wm.pruneWork();
        return isRunning;
    }

    private boolean isRunning(List<WorkInfo> list) {
        boolean isRunning = false;
        for (WorkInfo work : list) {
            if (work.getState() == WorkInfo.State.RUNNING || work.getState() == WorkInfo.State.ENQUEUED ) {
                Log.i(LiveWallpaperUtils.TAG, "RUNNING or ENQUEUED");
                isRunning = true;
            }
        }
        return isRunning;
    }
}
