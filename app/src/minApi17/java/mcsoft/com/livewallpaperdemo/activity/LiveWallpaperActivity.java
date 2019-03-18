package mcsoft.com.livewallpaperdemo.activity;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import mcsoft.com.livewallpaperdemo.R;
import mcsoft.com.livewallpaperdemo.service.LiveWallpaperService;
import mcsoft.com.livewallpaperdemo.utils.LiveWallpaperObservable;

public class LiveWallpaperActivity extends AppCompatActivity {

    private final static int CHANGE_WALLPAPER_STATUS_CODE = 1;
    private final static String TAG = LiveWallpaperActivity.class.getCanonicalName();

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_wallpaper);
        ButterKnife.bind(this);
        setButtonTitle();
        versionType.setText("version: API 17");
        enableButton.setOnClickListener(enableWallpaperListener());
        enableWallpaper1.setOnClickListener(enableWallpaper());
        enableWallpaper2.setOnClickListener(enableWallpaper());
        enableWallpaper3.setOnClickListener(enableWallpaper());
    }


    private View.OnClickListener enableWallpaperListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWallpaperActive() == true) {
                    disableWallpaper();
                } else {
                    setWallpaperActive();
                }
            }
        };
    }

    private View.OnClickListener enableWallpaper() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWallpaperActive() == true) {
                    int id = v.getId();
                    if (id == R.id.wallpaper1) LiveWallpaperObservable.getInstance().changeWallpaper(R.drawable.wallpaper1);
                    if (id == R.id.wallpaper2) LiveWallpaperObservable.getInstance().changeWallpaper(R.drawable.wallpaper2);
                    if (id == R.id.wallpaper3) LiveWallpaperObservable.getInstance().changeWallpaper(R.drawable.wallpaper3);
                }
            }
        };
    }

    private void setButtonTitle() {

        if (isWallpaperActive() == true) {
            enableButton.setText("Disable Wallpaper");
        } else {
            enableButton.setText("Enable Wallpaper");
        }

    }


    private boolean isWallpaperActive() {
        boolean result = false;

        WallpaperManager wpm = (WallpaperManager) getSystemService(WALLPAPER_SERVICE);
        WallpaperInfo info = wpm.getWallpaperInfo();

        if  (info != null) {
            ComponentName serviceComponent = info.getComponent();
            String serviceClassName = serviceComponent.getClassName();
            String liveWallpaperClassName = LiveWallpaperService.class.getName();
            if (serviceClassName.equals(liveWallpaperClassName)) {
                Log.d(TAG, "Live Wallpaper is already running");
                result = true;
            } else {
                Log.d(TAG, "Live Wallpaper is not running, this should be a preview");
            }
        }
        return result;
    }


    private void setWallpaperActive() {
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
            new ComponentName(this, LiveWallpaperService.class));
        startActivityForResult(intent, CHANGE_WALLPAPER_STATUS_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHANGE_WALLPAPER_STATUS_CODE && isWallpaperActive()) {
            setButtonTitle();
            LiveWallpaperObservable.getInstance().changeWallpaper(R.drawable.wallpaper1);
        }
    }


    private void disableWallpaper() {
        WallpaperManager wpm = (WallpaperManager) getSystemService(WALLPAPER_SERVICE);
        try {
            wpm.clear();
            setButtonTitle();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Live Wallpaper cannot be disabled");
        }
    }

}
