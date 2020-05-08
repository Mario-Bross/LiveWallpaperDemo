package mcsoft.com.livewallpaperdemo.activity;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.function.Consumer;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
//import io.reactivex.subjects.Subject;
import mcsoft.com.livewallpaperdemo.R;
import mcsoft.com.livewallpaperdemo.data.DataItem;
import mcsoft.com.livewallpaperdemo.data.DialogWriteSettingInfo;
import mcsoft.com.livewallpaperdemo.data.StringMessageToken;
import mcsoft.com.livewallpaperdemo.data.WallpaperInfoRequestToken;
import mcsoft.com.livewallpaperdemo.data.WallpaperResourceImageToken;
import mcsoft.com.livewallpaperdemo.data.WallpaperUriToken;
import mcsoft.com.livewallpaperdemo.scheduler.LiveWallpaperScheduler;
import mcsoft.com.livewallpaperdemo.service.LiveWallpaperService;
import mcsoft.com.livewallpaperdemo.utils.GlideApp;
import mcsoft.com.livewallpaperdemo.utils.LiveWallpaperObservable;
import mcsoft.com.livewallpaperdemo.utils.LiveWallpaperUtils;
import mcsoft.com.livewallpaperdemo.utils.PermissionDialogBuilder;
import mcsoft.com.livewallpaperdemo.utils.RxDataBus;

public class LiveWallpaperActivity extends LiveWallpaperActivityLifecycle {

    private final static int CHANGE_WALLPAPER_STATUS_CODE = 1;
    private final static String TAG = LiveWallpaperActivity.class.getCanonicalName();
    public final static int CODE_WRITE_SETTINGS_PERMISSION = 111;
    public final static int CODE_READ_EXTERNAL_STORE_PERMISSION = 112;

    private Observable<DataItem> observable;
    private Observer<DataItem> observer;
//    private Subject<DataItem> subject;
    private Disposable disposable;

    @BindView(R.id.enable_wallpaper_button)
    Button enableButton;

    @BindView(R.id.enable_scheduler)
    Button enableScheduler;

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
        checkReadExternalStoragePermission();
        checkWritePermission();
        setListeners();
        subscribe();
        LiveWallpaperObservable.getInstance().doNext(new WallpaperInfoRequestToken(WallpaperInfoRequestToken.GET_CURRENT_WALLPAPER));
        setEnableWallpaerButtonTitle();
        setEnableSchedulerButtonTitle();
    }

    // TODO: Add dialog for explanation
    private void checkWritePermission() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this) == false) {
                showWriteSettingsActivityObserver();
                new PermissionDialogBuilder.Build()
                    .with(this)
                    .listener(new PermissionDialogBuilder.WriteSettingsInfoListener(this))
                    .title("Ask for permission")
                    .message("In order to use this application you have to allow to modify system settings by this application.")
                    .show(getSupportFragmentManager());
            }
        }
    }

    private void showWriteSettingsActivityObserver() {

        Observer<DialogWriteSettingInfo> observer = new Observer<DialogWriteSettingInfo>() {
            Disposable disposable;
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(DialogWriteSettingInfo dialogWriteSettingInfo) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, LiveWallpaperActivity.CODE_WRITE_SETTINGS_PERMISSION);
                disposable.dispose();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        RxDataBus.getInstance().register(DialogWriteSettingInfo.class, observer);

//        SingleObserver<DialogWriteSettingInfo> single = new SingleObserver<DialogWriteSettingInfo>() {
//            Disposable disposable;
//            @Override
//            public void onSubscribe(Disposable d) {
//                disposable = d;
//            }
//
//            @Override
//            public void onSuccess(DialogWriteSettingInfo dialogWriteSettingInfo) {
//                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
//                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivityForResult(intent, LiveWallpaperActivity.CODE_WRITE_SETTINGS_PERMISSION);
//                disposable.dispose();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//        };
//        RxDataBus.getInstance().register(DialogWriteSettingInfo.class, single);
    }

    private void checkReadExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CODE_READ_EXTERNAL_STORE_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case CODE_READ_EXTERNAL_STORE_PERMISSION: {
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "READ EXTERNAL STORE Permission granted", Toast.LENGTH_LONG).show();
                }
                break;
            }

            case CODE_WRITE_SETTINGS_PERMISSION: {
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "WRITE SETTINGS Permission granted", Toast.LENGTH_LONG).show();
                }
                break;
            }

            default:
                break;
        }
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
        enableScheduler.setOnClickListener(enableSchedulerListener());
        enableWallpaper1.setOnClickListener(setWallpaper());
        enableWallpaper2.setOnClickListener(setWallpaper());
        enableWallpaper3.setOnClickListener(setWallpaper());
    }

    private void subscribe() {
        observer = getLocalObserver();
        observable = LiveWallpaperObservable.getInstance().listenToObservable();
        observable.subscribe(observer);
    }

//    private void test() {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            LiveWallpaperObservable.getInstance().register(StringMessageToken.class, new Consumer<StringMessageToken>() {
//                @Override
//                public void accept(StringMessageToken stringMessageToken) {
//                    Toast.makeText(getApplicationContext(),"StringMessageToken",Toast.LENGTH_LONG).show();
//                }
//            });
//        }
//    }


    private Observer<DataItem> getLocalObserver() {

        Observer<DataItem> observer = new Observer<DataItem>() {
            Disposable dis;
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(DataItem res) {
                if (res instanceof StringMessageToken) {
                    Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperActivity::Message Token");
                    Log.i(LiveWallpaperUtils.TAG, "Message: " + ((StringMessageToken) res).msg);
                }
                else if (res instanceof WallpaperUriToken) {
                    Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperActivity::WallpaperInfoData Token");
                    Log.i(LiveWallpaperUtils.TAG, "URI: " + ((WallpaperUriToken) res).imageUri);
                    loadPreview((WallpaperUriToken) res);
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

    private void loadPreview (WallpaperUriToken wallpaper) {

        Log.i(LiveWallpaperUtils.TAG, "Loading preview");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GlideApp.
                    with(getApplicationContext()).
                    asBitmap().
                    load(wallpaper.imageUri).
                    fitCenter().
                    into(wallpaperPreview);
            }
        });
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

    private View.OnClickListener enableSchedulerListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LiveWallpaperUtils.TAG, "enableScheduler");
                boolean isWorking = LiveWallpaperScheduler.getInstance().isSchedulerRunning(getApplicationContext());
                if (LiveWallpaperUtils.isWallpaperActive(getApplicationContext()) == true) {
                    if (isWorking == true) {
                        disableWallpaperScheduler();
                    } else {
                        enableWallpaperScheduler();
                    }
                } else {
                    if (isWorking == true) {
                        disableWallpaperScheduler();                    }
                }
            }
        };
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
                        LiveWallpaperObservable.getInstance().doNext(new WallpaperResourceImageToken(R.drawable.wallpaper1));
                    if (id == R.id.wallpaper2)
                        LiveWallpaperObservable.getInstance().doNext(new WallpaperResourceImageToken(R.drawable.wallpaper2));
                    if (id == R.id.wallpaper3)
                        LiveWallpaperObservable.getInstance().doNext(new WallpaperResourceImageToken(R.drawable.wallpaper3));
                }
            }
        };
    }


    private void setEnableWallpaerButtonTitle() {
        Log.i(LiveWallpaperUtils.TAG, "setEnableWallpaerButtonTitle");
        if (LiveWallpaperUtils.isWallpaperActive(getApplicationContext()) == true) {
            enableButton.setText("Disable Wallpaper");

        } else {
            enableButton.setText("Enable Wallpaper");
            loadStaticWallpaperPreview();
        }
    }

    private void setEnableSchedulerButtonTitle() {
        Log.i(LiveWallpaperUtils.TAG, "setEnableSchedulerButtonTitle");
        boolean isWorking = LiveWallpaperScheduler.getInstance().isSchedulerRunning(getApplicationContext());
        if (isWorking == true) {
            enableScheduler.setText("Disable Scheduler");
        } else {
            enableScheduler.setText("Enable Scheduler");
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

        if (requestCode == CODE_WRITE_SETTINGS_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(this) == false) {
                    new PermissionDialogBuilder.Build()
                        .with(this)
                        .listener(new PermissionDialogBuilder.WriteSettingsErrorListener(this))
                        .title("Error")
                        .message("You have to allow to modify system settings by this application.")
                        .show(getSupportFragmentManager());
                }
            }
        }

        if (requestCode == CHANGE_WALLPAPER_STATUS_CODE && LiveWallpaperUtils.isWallpaperActive(getApplicationContext())) {
            Log.d(LiveWallpaperUtils.TAG, "onActivityResult code OK");
            subscribe();
            setEnableWallpaerButtonTitle();
            LiveWallpaperObservable.getInstance().doNext(new WallpaperResourceImageToken(R.drawable.wallpaper1));
        }

    }


    private void disableWallpaper() {
        WallpaperManager wpm = (WallpaperManager) getSystemService(WALLPAPER_SERVICE);
        try {
            wpm.clear();
            setEnableWallpaerButtonTitle();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(LiveWallpaperUtils.TAG, "Live Wallpaper cannot be disabled");
        }
    }

    private void enableWallpaperScheduler() {
        boolean isWorking = LiveWallpaperScheduler.getInstance().isSchedulerRunning(getApplicationContext());
        if (isWorking == false) {
            Log.d(LiveWallpaperUtils.TAG, "Scheduler is not working");
            LiveWallpaperScheduler.getInstance().startScheduler(getApplicationContext());
            setEnableSchedulerButtonTitle();
        }
    }

    private void disableWallpaperScheduler() {
        boolean isWorking = LiveWallpaperScheduler.getInstance().isSchedulerRunning(getApplicationContext());
        if (isWorking == true) {
            Log.d(LiveWallpaperUtils.TAG, "Scheduler is working");
            LiveWallpaperScheduler.getInstance().stopScheduler(getApplicationContext());
            setEnableSchedulerButtonTitle();
        }
    }

}
