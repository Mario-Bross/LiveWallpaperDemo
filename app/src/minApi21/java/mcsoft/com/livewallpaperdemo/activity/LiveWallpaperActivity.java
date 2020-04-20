package mcsoft.com.livewallpaperdemo.activity;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import mcsoft.com.livewallpaperdemo.R;
import mcsoft.com.livewallpaperdemo.service.LiveWallpaperService;

public class LiveWallpaperActivity extends AppCompatActivity {

    private final static int CHANGE_WALLPAPER_STATUS_CODE = 1;
    private final static String TAG = LiveWallpaperActivity.class.getCanonicalName();

    @BindView(R.id.enable_wallpaper_button)
    Button button;
    @BindView(R.id.tv_version_type)
    TextView versionType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_wallpaper);
        ButterKnife.bind(this);
        setButtonTitle();
        versionType.setText("version: API 21");
        button.setOnClickListener(getOnClickListener());
    }


    private View.OnClickListener getOnClickListener() {
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


    private void setButtonTitle() {

        if (isWallpaperActive() == true) {
            button.setText("Disable Wallpaper");
        } else {
            button.setText("Enable Wallpaper");
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
        if (requestCode == CHANGE_WALLPAPER_STATUS_CODE) {
            setButtonTitle();
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
