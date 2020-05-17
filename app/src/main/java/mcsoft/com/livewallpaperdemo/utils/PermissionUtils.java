package mcsoft.com.livewallpaperdemo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import mcsoft.com.livewallpaperdemo.activity.LiveWallpaperActivity;
import mcsoft.com.livewallpaperdemo.data.DialogWriteSettingInfo;
import mcsoft.com.livewallpaperdemo.data.DialogWriteSettingsError;

public class PermissionUtils {

    public final static int CODE_WRITE_SETTINGS_PERMISSION = 111;
    public final static int CODE_READ_EXTERNAL_STORE_PERMISSION = 112;

    // ----------------------------------------------------------
    // Check for READ_EXTERNAL_STORAGE permission
    public static void checkReadExternalStoragePermission(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CODE_READ_EXTERNAL_STORE_PERMISSION);
            }
        }
    }
    // ----------------------------------------------------------

    // ----------------------------------------------------------
    public static void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case CODE_READ_EXTERNAL_STORE_PERMISSION: {
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(context, "READ EXTERNAL STORE Permission granted", Toast.LENGTH_LONG).show();
                }
                break;
            }
            default:
                break;
        }
    }
    // ----------------------------------------------------------

    // ----------------------------------------------------------
    public static PermissionDialogBuilder checkWritePermission(AppCompatActivity context) {
        PermissionDialogBuilder dialogBuilder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(context) == false) {
                dialogBuilder = new PermissionDialogBuilder.Build()
                        .with(context)
                        .listener(new PermissionDialogBuilder.WriteSettingsInfoListener(context))
                        .title("Ask for permission")
                        .message("In order to use this application you have to allow to modify system settings by this application.")
                        .get();
            }
        }
        return dialogBuilder;
    }
    // ----------------------------------------------------------

    public static Disposable setWriteSettingsObserver(AppCompatActivity context) {

        Consumer<DialogWriteSettingInfo> consumer = new Consumer<DialogWriteSettingInfo>() {
            @Override
            public void accept(DialogWriteSettingInfo dialogWriteSettingInfo) throws Exception {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));

//              Note: This flag (Intent.FLAG_ACTIVITY_NEW_TASK) can not be used when the
//              caller is requesting a result from the activity being launched.
//              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivityForResult(intent, LiveWallpaperActivity.CODE_WRITE_SETTINGS_PERMISSION);
            }
        };
        Disposable disposable = RxDataBus.getInstance().register(DialogWriteSettingInfo.class, consumer);
        return disposable;

    }

    public static Disposable setFinishActivityObserver(AppCompatActivity context) {

        Consumer<DialogWriteSettingsError> consumer = new Consumer<DialogWriteSettingsError>() {
            @Override
            public void accept(DialogWriteSettingsError dialogWriteSettingsError) throws Exception {
                Intent intent = context.getIntent();
                context.finish();
                context.startActivity(intent);
            }
        };
        Disposable disposable = RxDataBus.getInstance().register(DialogWriteSettingsError.class, consumer);
        return disposable;
    }

}
