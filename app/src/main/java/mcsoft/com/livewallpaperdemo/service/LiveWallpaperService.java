package mcsoft.com.livewallpaperdemo.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.service.wallpaper.WallpaperService;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mcsoft.com.livewallpaperdemo.R;
import mcsoft.com.livewallpaperdemo.activity.LiveWallpaperActivity;
import mcsoft.com.livewallpaperdemo.utils.GlideApp;
import mcsoft.com.livewallpaperdemo.utils.LiveWallpaperObservable;
import mcsoft.com.livewallpaperdemo.utils.LiveWallpaperUtils;

public class LiveWallpaperService extends WallpaperService {

    private final static String TAG = LiveWallpaperService.class.getCanonicalName();

    @Override
    public Engine onCreateEngine() {
        WallpaperEngine engine = new WallpaperEngine();
        return engine;
    }



    class WallpaperEngine  extends  WallpaperService.Engine {

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            // TODO Handle initialization.
            // Preview
            loadWallpaper();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            LiveWallpaperObservable.getInstance().doComplete();
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep,
                                     float yOffsetStep, int xPixelOffset, int yPixelOffset) {

            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
            // TODO Handle homescreen offset events.

        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            // TODO Handle touch and motion events.
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            // TODO Surface has been created, run the Thread that will
            // update the display. Draw wallpaper here
            loadWallpaper();
            doSubscription();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
            } else {
            }
        }

        @Override
        public void onSurfaceChanged (SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder,format,width,height);
        }


        private void loadWallpaper() {

            Context context = getApplicationContext();

            // TODO: Load image uri from Database
            Uri imageUri = LiveWallpaperUtils.getUriToResource(context, R.drawable.wallpaper1);
            drawWallpaper(imageUri);

        }

        private void drawWallpaper(Uri imageUri) {
            GlideApp.
                with(getApplicationContext()).
                asBitmap().
                override(getDesiredMinimumWidth(),getDesiredMinimumHeight()).
                load(imageUri).
                centerInside().
                into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        final SurfaceHolder holder = getSurfaceHolder();
                        Canvas canvas = null;  // canvas

                        try {
                            canvas = holder.lockCanvas();  //get the canvas
                            if (canvas != null) {
                                // draw something
                                // my draw code
                                canvas.drawBitmap(resource,0,0, null);

                            }
                        } finally {
                            if (canvas != null)
                                holder.unlockCanvasAndPost(canvas);
                        }

                    }
                });

        }

        private void doSubscription() {
            if (isPreview() == false) {
                Observer<String> observer = new Observer<String>() {
                    Disposable dis;
                    @Override
                    public void onSubscribe(Disposable d) {
                        dis = d;
                    }

                    @Override
                    public void onNext(String s) {
                        Log.i(TAG, s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "Wallpaper completed");
                    }
                };
                LiveWallpaperObservable.getInstance().getObservable().subscribe(observer);
            }
        }

    }
}

