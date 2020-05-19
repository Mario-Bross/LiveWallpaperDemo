package mcsoft.com.livewallpaperdemo.activity;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
//import io.reactivex.subjects.Subject;
import mcsoft.com.livewallpaperdemo.R;
import mcsoft.com.livewallpaperdemo.data.DataItem;
import mcsoft.com.livewallpaperdemo.data.StringMessageToken;
import mcsoft.com.livewallpaperdemo.data.WallpaperInfoRequestToken;
import mcsoft.com.livewallpaperdemo.data.WallpaperResourceImageToken;
import mcsoft.com.livewallpaperdemo.data.WallpaperUriToken;
import mcsoft.com.livewallpaperdemo.scheduler.LiveWallpaperScheduler;
import mcsoft.com.livewallpaperdemo.service.LiveWallpaperService;
import mcsoft.com.livewallpaperdemo.utils.GlideApp;
import mcsoft.com.livewallpaperdemo.utils.PermissionUtils;
import mcsoft.com.livewallpaperdemo.utils.RxDataBus;
import mcsoft.com.livewallpaperdemo.utils.LiveWallpaperUtils;
import mcsoft.com.livewallpaperdemo.utils.PermissionDialogBuilder;

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

    @BindView(R.id.scheduler_spinner)
    Spinner scheduler_spinner;

    private PermissionDialogBuilder permissionDialogBuilder;
    private Disposable dialogDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_wallpaper);
        ButterKnife.bind(this);
        versionType.setText("version: API 17");
        PermissionUtils.checkReadExternalStoragePermission(this);

        permissionDialogBuilder = PermissionUtils.checkWritePermission(this);
        if (permissionDialogBuilder != null) {
            dialogDisposable = PermissionUtils.setWriteSettingsObserver(this);
            permissionDialogBuilder.show(getSupportFragmentManager());
        }
        setListeners();
        subscribe();
        RxDataBus.getInstance().doNext(new WallpaperInfoRequestToken(WallpaperInfoRequestToken.GET_CURRENT_WALLPAPER));
        setEnableWallpaerButtonTitle();
        setEnableSchedulerButtonTitle();
        createSpinner();
    }


    private void createSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.scheduler_spinner_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scheduler_spinner.setAdapter(adapter);
        scheduler_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int[] values = getResources().getIntArray(R.array.scheduler_spinner_values_int);
                int val = values[position];
                if (val < 15) {
                    String selected = "Period changed to: " + String.valueOf(val) + " minutes. Custom scheduler";
                    Toast.makeText(parent.getContext(),selected,Toast.LENGTH_LONG).show();
                } else {
                    String selected = "Period changed to: " + String.valueOf(val) + " minutes. Work Manager";
                    Toast.makeText(parent.getContext(),selected,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        PermissionUtils.onRequestPermissionsResult(getApplicationContext(), requestCode, permissions, grantResults);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (dialogDisposable != null && dialogDisposable.isDisposed() == false) {
            dialogDisposable.dispose();
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        if (permissionDialogBuilder != null) {
            permissionDialogBuilder.dismiss();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (disposable != null && disposable.isDisposed() == false) {
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
        observable = RxDataBus.getInstance().listenToObservable();
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
                        RxDataBus.getInstance().doNext(new WallpaperResourceImageToken(R.drawable.wallpaper1));
                    if (id == R.id.wallpaper2)
                        RxDataBus.getInstance().doNext(new WallpaperResourceImageToken(R.drawable.wallpaper2));
                    if (id == R.id.wallpaper3)
                        RxDataBus.getInstance().doNext(new WallpaperResourceImageToken(R.drawable.wallpaper3));
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
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == CODE_WRITE_SETTINGS_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (dialogDisposable.isDisposed() == false)
                    dialogDisposable.dispose();
                permissionDialogBuilder = new PermissionDialogBuilder.Build()
                        .with(this)
                        .listener(new PermissionDialogBuilder.WriteSettingsErrorListener(this))
                        .title("Information")
                        .message("Application will restarted to apply new settings.")
                        .get();
                if (permissionDialogBuilder != null) {
                    dialogDisposable = PermissionUtils.setFinishActivityObserver(this);
                    permissionDialogBuilder.show(getSupportFragmentManager());
                }
            }
        }

        if (requestCode == CHANGE_WALLPAPER_STATUS_CODE && LiveWallpaperUtils.isWallpaperActive(getApplicationContext())) {
            Log.d(LiveWallpaperUtils.TAG, "onActivityResult code OK");
            subscribe();
            setEnableWallpaerButtonTitle();
            RxDataBus.getInstance().doNext(new WallpaperResourceImageToken(R.drawable.wallpaper1));
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
