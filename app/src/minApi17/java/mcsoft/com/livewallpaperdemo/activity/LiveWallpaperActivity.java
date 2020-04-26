package mcsoft.com.livewallpaperdemo.activity;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;


import androidx.work.WorkManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
//import io.reactivex.subjects.Subject;
import mcsoft.com.livewallpaperdemo.R;
import mcsoft.com.livewallpaperdemo.data.DataItem;
import mcsoft.com.livewallpaperdemo.data.Message;
import mcsoft.com.livewallpaperdemo.data.WallpaperInfoRequest;
import mcsoft.com.livewallpaperdemo.data.WallpaperResourceImage;
import mcsoft.com.livewallpaperdemo.data.WallpaperInfoData;
import mcsoft.com.livewallpaperdemo.scheduler.LiveWallpaperScheduler;
import mcsoft.com.livewallpaperdemo.service.LiveWallpaperService;
import mcsoft.com.livewallpaperdemo.utils.GlideApp;
import mcsoft.com.livewallpaperdemo.utils.LiveWallpaperObservable;
import mcsoft.com.livewallpaperdemo.utils.LiveWallpaperUtils;

public class LiveWallpaperActivity extends LiveWallpaperActivityLifecycle {

    private final static int CHANGE_WALLPAPER_STATUS_CODE = 1;
    private final static String TAG = LiveWallpaperActivity.class.getCanonicalName();

    private Observable<DataItem> observable;
    private Observer<DataItem> observer;
//    private Subject<DataItem> subject;
    private Disposable disposable;

    @BindView(R.id.enable_wallpaper_button)
    Button enableButton;

    @BindView(R.id.wallpaper1)
    Button enableWallpaper1;

    @BindView(R.id.wallpaper2)
    Button enableWallpaper2;

    @BindView(R.id.wallpaper3)
    Button enableWallpaper3;

    @BindView(R.id.tv_version_type)
    TextView versionType;

    @BindView(R.id.wallpaperPreview)
    ImageView wallpaperPreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_wallpaper);
        ButterKnife.bind(this);
        versionType.setText("version: API 17");
        setListeners();
        subscribe();
        LiveWallpaperObservable.getInstance().doNext(new WallpaperInfoRequest(WallpaperInfoRequest.GET_CURRENT_WALLPAPER));
        setButtonTitle();
    }

    @Override
    protected void onDestroy() {
        if (disposable.isDisposed() == false) {
            disposable.dispose();
        }
        super.onDestroy();
    }

    private void setListeners() {
        enableButton.setOnClickListener(enableWallpaperListener());
        enableWallpaper1.setOnClickListener(setWallpaper());
        enableWallpaper2.setOnClickListener(setWallpaper());
        enableWallpaper3.setOnClickListener(setWallpaper());
    }

    private void subscribe() {
        observer = getLocalObserver();
        observable = LiveWallpaperObservable.getInstance().listenToObservable();
        observable.subscribe(observer);
    }

    private Observer<DataItem> getLocalObserver() {

        Observer<DataItem> observer = new Observer<DataItem>() {
            Disposable dis;
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(DataItem res) {
 //               Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperActivity::observer::onNext, Token type: " + res.getClass().getName());
                if (res instanceof Message) {
                    Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperActivity::Message Token");
                }
                if (res instanceof WallpaperInfoData) {
                    Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperActivity::WallpaperInfoData Token");
                    loadPreview((WallpaperInfoData) res);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperActivity::observer::onError");
            }

            @Override
            public void onComplete() {
                Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperActivity::observer::onComplete");
                disposable.dispose();
            }
        };
        return observer;
    }

    private void loadPreview (WallpaperInfoData wallpaper) {

        Log.i(LiveWallpaperUtils.TAG, "Loading preview");
        GlideApp.
                with(getApplicationContext()).
                asBitmap().
                load(wallpaper.imageUri).
                centerInside().
                into(wallpaperPreview);
    }

    private void loadStaticWallpaperPreview() {
        Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperActivity::loadStaticWallpaperPreview");
        WallpaperManager wpm = (WallpaperManager) getSystemService(WALLPAPER_SERVICE);
        Drawable wallpaperDrawable = wpm.getDrawable();
        GlideApp
                .with(getApplicationContext())
                .asDrawable()
                .load(wallpaperDrawable)
                .centerInside()
                .into(wallpaperPreview);
    }

    private View.OnClickListener enableWallpaperListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LiveWallpaperUtils.TAG, "enableWallpaperListener");
                if (LiveWallpaperUtils.isWallpaperActive(getApplicationContext()) == true) {
                    disableWallpaper();
                } else {
                    setWallpaperActive();
                }
            }
        };
    }

    private View.OnClickListener setWallpaper() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LiveWallpaperUtils.TAG, "OnClickListener setWallpaper");
                if (LiveWallpaperUtils.isWallpaperActive(getApplicationContext()) == true) {
                    int id = v.getId();
                    if (id == R.id.wallpaper1)
                        LiveWallpaperObservable.getInstance().doNext(new WallpaperResourceImage(R.drawable.wallpaper1));
                    if (id == R.id.wallpaper2)
                        LiveWallpaperObservable.getInstance().doNext(new WallpaperResourceImage(R.drawable.wallpaper2));
                    if (id == R.id.wallpaper3)
                        LiveWallpaperObservable.getInstance().doNext(new WallpaperResourceImage(R.drawable.wallpaper3));
                }
            }
        };
    }

    private void setButtonTitle() {

        Log.i(LiveWallpaperUtils.TAG, "setButtonTitle");
        if (LiveWallpaperUtils.isWallpaperActive(getApplicationContext()) == true) {
            enableButton.setText("Disable Wallpaper");

        } else {
            enableButton.setText("Enable Wallpaper");
            loadStaticWallpaperPreview();
        }
    }


    private void setWallpaperActive() {
        Log.d(LiveWallpaperUtils.TAG, "setWallpaperActive");
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
            new ComponentName(this, LiveWallpaperService.class));
        startActivityForResult(intent, CHANGE_WALLPAPER_STATUS_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LiveWallpaperUtils.TAG, "onActivityResult");
        if (requestCode == CHANGE_WALLPAPER_STATUS_CODE && LiveWallpaperUtils.isWallpaperActive(getApplicationContext())) {
            Log.d(LiveWallpaperUtils.TAG, "onActivityResult code OK");
            subscribe();
            setButtonTitle();
            LiveWallpaperObservable.getInstance().doNext(new WallpaperResourceImage(R.drawable.wallpaper1));
            enableWallpaperScheduler();
        }
    }


    private void disableWallpaper() {
        WallpaperManager wpm = (WallpaperManager) getSystemService(WALLPAPER_SERVICE);
        try {
            wpm.clear();
            setButtonTitle();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(LiveWallpaperUtils.TAG, "Live Wallpaper cannot be disabled");
        }
    }

    private void enableWallpaperScheduler() {
        boolean isWorking = LiveWallpaperScheduler.getInstance().isSchedulerRunning(getApplicationContext());
        if (isWorking == true) {
            Log.d(LiveWallpaperUtils.TAG, "Scheduler is working");
        } else {
            Log.d(LiveWallpaperUtils.TAG, "Scheduler is not working");
        }
    }

    private void disableWallpaperScheduler() {
        WorkManager workManager = WorkManager.getInstance(this);
    }

}
