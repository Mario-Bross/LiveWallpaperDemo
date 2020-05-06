package mcsoft.com.livewallpaperdemo.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import mcsoft.com.livewallpaperdemo.activity.LiveWallpaperActivity;

public class PermissionDialogBuilder extends DialogFragment {

    private String mTitle;
    private String mMessage;
    private Context builderContext;
    private DialogInterface.OnClickListener mListener;

    public static class OnClickListener1 implements DialogInterface.OnClickListener {

        private Context mContext;

        public OnClickListener1(Context context) {
            this.mContext = context;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + this.mContext.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ((LiveWallpaperActivity)this.mContext).startActivityForResult(intent, LiveWallpaperActivity.CODE_WRITE_SETTINGS_PERMISSION);
        }
    }

    public static class OnClickListener2 implements DialogInterface.OnClickListener {

        private Context mContext;

        public OnClickListener2(Context context) {
            this.mContext = context;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            ((LiveWallpaperActivity)this.mContext).finish();
        }
    }


    private PermissionDialogBuilder( Build builder) {
        this.mTitle = builder.mTitle;
        this.mMessage = builder.mMessage;
        this.builderContext = builder.mContext;
        this.mListener = builder.listener;
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, "Fragment TAG");
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity())
            .setTitle(mTitle)
            .setMessage(mMessage)
            .setPositiveButton("OK", mListener)
            .create();
    };

    public static final class Build {

        private String mTitle;
        private String mMessage;
        private Context mContext;
        private DialogInterface.OnClickListener listener;

        public void show(FragmentManager fragmentManager) {
            new PermissionDialogBuilder(this).show(fragmentManager);
        }

        public PermissionDialogBuilder get() {
            return new PermissionDialogBuilder(this);
        }

        public Build with(Context context) {
            this.mContext = context;
            return this;
        }

        public Build title(String title){
            this.mTitle = title;
            return this;
        }

        public Build message(String message) {
            this.mMessage = message;
            return this;
        }

        public Build listener(DialogInterface.OnClickListener listener) {
            this.listener = listener;
            return this;
        }


    }


}
