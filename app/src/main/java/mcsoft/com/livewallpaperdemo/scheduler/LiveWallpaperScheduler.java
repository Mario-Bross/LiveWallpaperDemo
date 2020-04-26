package mcsoft.com.livewallpaperdemo.scheduler;

import android.content.Context;


import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import static mcsoft.com.livewallpaperdemo.utils.LiveWallpaperUtils.TAG;

public class LiveWallpaperScheduler {

    private static final String SCHEDULER_TAG = "live_wallpaper_worker_tag";

    private static LiveWallpaperScheduler mInstance;

//    private Context context;

    private LiveWallpaperScheduler() {

    }

    public static LiveWallpaperScheduler getInstance() {
        if (mInstance == null){
            mInstance = new LiveWallpaperScheduler();
        }
        return mInstance;
    }


    public boolean isSchedulerRunning(Context context) {
        boolean isRunning = false;
        WorkManager wm = WorkManager.getInstance(context);
        ListenableFuture<List<WorkInfo>> future = wm.getWorkInfosByTag(SCHEDULER_TAG);
        try {
            
            List<WorkInfo> list = future.get();
            // start only if no such tasks present
            if((list == null) || (list.size() == 0)){
                isRunning = false;
            } else {
                isRunning = true;
            }
            
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return isRunning;
    }
}
