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
import mcsoft.com.livewallpaperdemo.data.DialogWriteSettingInfo;
import mcsoft.com.livewallpaperdemo.data.DialogWriteSettingsError;

public class PermissionDialogBuilder extends DialogFragment {

    private String mTitle;
    private String mMessage;
    private Context builderContext;
    private DialogInterface.OnClickListener mListener;

    public static class WriteSettingsInfoListener implements DialogInterface.OnClickListener {

        private Context mContext;

        public WriteSettingsInfoListener(Context context) {
            this.mContext = context;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            RxDataBus.getInstance().post(new DialogWriteSettingInfo());
        }
    }

    public static class WriteSettingsErrorListener implements DialogInterface.OnClickListener {

        private Context mContext;

        public WriteSettingsErrorListener(Context context) {
            this.mContext = context;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            RxDataBus.getInstance().post(new DialogWriteSettingsError());
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

        Dialog dialog =  new AlertDialog.Builder(getActivity())
            .setTitle(mTitle)
            .setMessage(mMessage)
            .setPositiveButton("OK", mListener)
            .create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
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
