package mcsoft.com.livewallpaperdemo.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.service.wallpaper.WallpaperService;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import mcsoft.com.livewallpaperdemo.R;
import mcsoft.com.livewallpaperdemo.data.DataItem;
import mcsoft.com.livewallpaperdemo.data.Message;
import mcsoft.com.livewallpaperdemo.data.WallpaperInfoRequest;
import mcsoft.com.livewallpaperdemo.data.WallpaperResourceImage;
import mcsoft.com.livewallpaperdemo.data.WallpaperInfoData;
import mcsoft.com.livewallpaperdemo.utils.GlideApp;
import mcsoft.com.livewallpaperdemo.utils.LiveWallpaperObservable;
import mcsoft.com.livewallpaperdemo.utils.LiveWallpaperUtils;

public class LiveWallpaperService extends WallpaperService {


    private final static String TAG = LiveWallpaperService.class.getCanonicalName();


    @Override
    public void onCreate() {
    }

    @Override
    public Engine onCreateEngine() {
        WallpaperEngine engine = new WallpaperEngine();
        return engine;
    }

    @Override
    public void onDestroy() {
    }

    class WallpaperEngine  extends  WallpaperService.Engine {

        private DataItem currentWallpaperResourceImage;
        private DataItem currentWallpaperInfoRequest;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            // TODO Handle initialization.
            // Preview
//            loadWallpaper(R.drawable.wallpaper1);
            if (isPreview() == false) {
                doSubscription();
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (isPreview() == false) {
                LiveWallpaperObservable.getInstance().doComplete();
            }
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
            // TODO Surface has been created,
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


        private void loadWallpaper(Integer res) {

            Context context = getApplicationContext();

            // TODO: Load image uri from Database
            Uri imageUri = LiveWallpaperUtils.getUriToResource(context, res.intValue());
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
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {

                    }

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

        private Single<Boolean> getWallpaperResourceImageObservable() {

            // Subject - source of all events
            Observable<DataItem> subjectObservable  = LiveWallpaperObservable.getInstance().getObservable();

            // Single emits true or false
            Single<Boolean> wallpaperResourceImageObservable = subjectObservable.any(new Predicate<DataItem>() {
                @Override
                public boolean test(DataItem dataItem) throws Exception {
                    if (dataItem instanceof WallpaperResourceImage) {
                        currentWallpaperResourceImage = dataItem;
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            return wallpaperResourceImageObservable;
        }



        private void observeWallpaperResourceImage() {

            SingleObserver<Boolean>  wallpaperResourceImageObserver = (new SingleObserver<Boolean>() {
                Disposable disposable;
                @Override
                public void onSubscribe(Disposable d) {
                    Log.i(LiveWallpaperUtils.TAG, "observeWallpaperResourceImage::SingleObserver::onSubscribe");
                    disposable = d;
                }

                @Override
                public void onSuccess(Boolean aBoolean) {
                    if (aBoolean == true) {
                        Log.i(LiveWallpaperUtils.TAG, "observeWallpaperResourceImage::SingleObserver::onSuccess");
                        loadWallpaper(((WallpaperResourceImage) currentWallpaperResourceImage).resId);
                        // Send Message
                        LiveWallpaperObservable.
                                getInstance().
                                publishData(new Message("Wallpaer changed succesfully"));
                        // Send WallpaperInfo
                        LiveWallpaperObservable.
                                getInstance().
                                publishData(new WallpaperInfoData(LiveWallpaperUtils.getUriToResource(getApplicationContext(), ((WallpaperResourceImage) currentWallpaperResourceImage).resId)));
                        saveWallpaperInSharedPref((WallpaperResourceImage) currentWallpaperResourceImage);
                        disposable.dispose();
                        getWallpaperResourceImageObservable().subscribe(this);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.i(LiveWallpaperUtils.TAG, "observeWallpaperResourceImage::SingleObserver::onError");
                }
            });
            getWallpaperResourceImageObservable().subscribe(wallpaperResourceImageObserver);
        }


        private Single<Boolean> getWallpaperInfoRequestObservable() {
            // Subject - source of all events
            Observable<DataItem> subjectObservable  = LiveWallpaperObservable.getInstance().getObservable();
            // Single emits true or false
            Single<Boolean> wallpaperInfoRequestObservable = subjectObservable.any(new Predicate<DataItem>() {
                @Override
                public boolean test(DataItem dataItem) throws Exception {
                    if (dataItem instanceof WallpaperInfoRequest) {
                        currentWallpaperInfoRequest = dataItem;
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            return wallpaperInfoRequestObservable;
        }

        private void observeWallpaperInfoRequest() {
            SingleObserver<Boolean>  wallpaperInfoRequestObserver = (new SingleObserver<Boolean>() {

                Disposable disposable;

                @Override
                public void onSubscribe(Disposable d) {
                    Log.i(LiveWallpaperUtils.TAG, "observeWallpaperInfoRequest::SingleObserver::onSubscribe");
                    disposable = d;
                }

                @Override
                public void onSuccess(Boolean aBoolean) {
                    if (aBoolean == true) {
                        Log.i(LiveWallpaperUtils.TAG, "observeWallpaperInfoRequest::onNext, WallpaperInfoRequest");
//                        if (currentWallpaperResourceImage != null) {
                            Log.i(LiveWallpaperUtils.TAG, "observeWallpaperInfoRequest::onSuccess, WallpaperInfoRequest currentWallpaer not null");
                            if (((WallpaperInfoRequest) currentWallpaperInfoRequest).requestId == WallpaperInfoRequest.GET_CURRENT_WALLPAPER) {
                                LiveWallpaperObservable.
                                        getInstance().
                                        publishData(new WallpaperInfoData(LiveWallpaperUtils.getUriToResource(getApplicationContext(), ((WallpaperResourceImage) currentWallpaperResourceImage).resId)));
                            }
//                        }
                        disposable.dispose();
                        getWallpaperInfoRequestObservable().subscribe(this);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.i(LiveWallpaperUtils.TAG, "observeWallpaperInfoRequest::SingleObserver::onError");
                }
            });
            getWallpaperInfoRequestObservable().subscribe(wallpaperInfoRequestObserver);
        }

        private void doSubscription() {
            if (isPreview() == false) {
                observeWallpaperResourceImage();
                observeWallpaperInfoRequest();
            }
        }

        private void saveWallpaperInSharedPref(WallpaperResourceImage res) {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(getString(R.string.shared_pref_resource_id), res.resId);
            editor.commit();
        }

        private Observer<DataItem> getLocalObserver() {

            Observer<DataItem> observer = new Observer<DataItem>() {
                Disposable dis;
                @Override
                public void onSubscribe(Disposable d) {
                    dis = d;
                }

                @Override
                public void onNext(final DataItem res) {
                    Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperService::onNext Setting wallpaper, disposable = " + dis.isDisposed());

//                    if (res instanceof WallpaperResourceImage) {
//
//                        Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperService::onNext, WallpaperResourceImage");
//                        loadWallpaper(((WallpaperResourceImage) res).resId);
//                        // Save wallpaper resID
//                        currentWallpaperResourceImage = new WallpaperResourceImage(((WallpaperResourceImage) res).resId);
//
//                        // Send Message
//                        LiveWallpaperObservable.
//                                getInstance().
//                                publishData(new Message("Wallpaer changed succesfully"));
//
//                        // Inform other services
//                        LiveWallpaperObservable.
//                                getInstance().
//                                publishData(new WallpaperInfoData(LiveWallpaperUtils.getUriToResource(getApplicationContext(), ((WallpaperResourceImage) res).resId)));
//
//                        // Save current wallpaper in SharedPref (used to restore after reboot )
//                        saveWallpaperInSharedPref((WallpaperResourceImage) res);
//                    }

//                    if (res instanceof WallpaperInfoRequest) {
//                        Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperService::onNext, WallpaperInfoRequest");
//                        if (currentWallpaper != null) {
//                            Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperService::onNext, WallpaperInfoRequest currentWallpaer not null");
//                            if (((WallpaperInfoRequest) res).requestId == WallpaperInfoRequest.GET_CURRENT_WALLPAPER) {
//                                LiveWallpaperObservable.
//                                        getInstance().
//                                        publishData(new WallpaperInfoData(LiveWallpaperUtils.getUriToResource(getApplicationContext(), ((WallpaperResourceImage) currentWallpaper).resId)));
//                            }
//                        }
//                    }

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {
                    dis.dispose();
                    Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperService:: onComplete Wallpaper completed, disposable = " + dis.isDisposed());
                }
            };
            return observer;
        }
    }
}

