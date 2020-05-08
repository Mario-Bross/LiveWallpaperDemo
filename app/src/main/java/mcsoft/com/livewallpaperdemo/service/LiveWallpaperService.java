package mcsoft.com.livewallpaperdemo.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.service.wallpaper.WallpaperService;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import mcsoft.com.livewallpaperdemo.R;
import mcsoft.com.livewallpaperdemo.data.DataItem;
import mcsoft.com.livewallpaperdemo.data.StringMessageToken;
import mcsoft.com.livewallpaperdemo.data.WallpaperInfoRequestToken;
import mcsoft.com.livewallpaperdemo.data.WallpaperResourceImageToken;
import mcsoft.com.livewallpaperdemo.data.WallpaperUriToken;
import mcsoft.com.livewallpaperdemo.utils.GlideApp;
import mcsoft.com.livewallpaperdemo.utils.LiveWallpaperObservable;
import mcsoft.com.livewallpaperdemo.utils.LiveWallpaperUtils;

public class LiveWallpaperService extends WallpaperService {

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


        private synchronized void loadWallpaper(Integer res) {

            Context context = getApplicationContext();

            // TODO: Load image uri from Database
            Uri imageUri = LiveWallpaperUtils.getUriToResource(context, res.intValue());
            drawWallpaper(imageUri);

        }

        private void drawWallpaper(Uri imageUri) {
            GlideApp.
                with(getApplication()).
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

        private void dispatcher() {
            // Subject - source of all events
            //
            Observable<DataItem> subjectObservable  = LiveWallpaperObservable.getInstance().listenToObservable();
            subjectObservable.subscribe(new Consumer<DataItem>() {
                @Override
                public void accept(DataItem dataItem) throws Exception {
                    if (dataItem instanceof WallpaperInfoRequestToken) {
                        Single.just((WallpaperInfoRequestToken)dataItem).subscribe(observeWallpaperInfoRequestToken());
                    } else if (dataItem instanceof WallpaperResourceImageToken) {
                        Single.just((WallpaperResourceImageToken)dataItem).subscribe(observeWallpaperResourceImageToken());
                    }
                }
            });
        }

        private SingleObserver<WallpaperInfoRequestToken> observeWallpaperInfoRequestToken() {
            SingleObserver<WallpaperInfoRequestToken> singleObserver = new SingleObserver<WallpaperInfoRequestToken>() {

                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onSuccess(WallpaperInfoRequestToken wallpaperInfoRequestToken) {
                    Log.i(LiveWallpaperUtils.TAG, "observeWallpaperInfoRequest::onNext, WallpaperInfoRequest");
                    Log.i(LiveWallpaperUtils.TAG, "observeWallpaperInfoRequest::onSuccess, WallpaperInfoRequest currentWallpaer not null");
                    if (wallpaperInfoRequestToken.requestId == WallpaperInfoRequestToken.GET_CURRENT_WALLPAPER) {
                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.shared_pref_name), Context.MODE_PRIVATE);
                        int resId = sharedPreferences.getInt(getApplicationContext().getString(R.string.shared_pref_resource_id),0);
                        if (resId != 0) {
                            LiveWallpaperObservable.getInstance().
                                doNext(new WallpaperUriToken(LiveWallpaperUtils.getUriToResource(getApplicationContext(),resId)));
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                }
            };
            return singleObserver;
        }


        private SingleObserver<WallpaperResourceImageToken> observeWallpaperResourceImageToken() {
            SingleObserver<WallpaperResourceImageToken> singleObserver = new SingleObserver<WallpaperResourceImageToken>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onSuccess(WallpaperResourceImageToken wallpaperResourceImageToken) {
                    Log.i(LiveWallpaperUtils.TAG, "observeWallpaperResourceImage::SingleObserver::onSuccess");
                    loadWallpaper(wallpaperResourceImageToken.resId);
                    // Send Message
                    LiveWallpaperObservable.getInstance().
                        doNext(new StringMessageToken("Wallpaer changed succesfully"));

                    LiveWallpaperObservable.getInstance().
                        doNext(new WallpaperUriToken(LiveWallpaperUtils.getUriToResource(getApplicationContext(), wallpaperResourceImageToken.resId)));

                    // Send WallpaperInfo
                    saveWallpaperInSharedPref(wallpaperResourceImageToken);
                }

                @Override
                public void onError(Throwable e) {

                }
            };
            return singleObserver;
        }







//        private Single<Boolean> getWallpaperResourceImageObservable() {
//
//            // Subject - source of all events
//            Observable<DataItem> subjectObservable  = LiveWallpaperObservable.getInstance().listenToObservable();
//
//            // Single emits true or false
//            Single<Boolean> wallpaperResourceImageObservable = subjectObservable.any(new Predicate<DataItem>() {
//                @Override
//                public boolean test(DataItem dataItem) throws Exception {
//                    if (dataItem instanceof WallpaperResourceImageToken) {
//                        currentWallpaperResourceImage = dataItem;
//                        return true;
//                    } else {
//                        return false;
//                    }
//                }
//            });
//            return wallpaperResourceImageObservable;
//        }
//
//
//
//        private void observeWallpaperResourceImage() {
//
//            SingleObserver<Boolean>  wallpaperResourceImageObserver = (new SingleObserver<Boolean>() {
//                Disposable disposable;
//                @Override
//                public void onSubscribe(Disposable d) {
//                    Log.i(LiveWallpaperUtils.TAG, "observeWallpaperResourceImage::SingleObserver::onSubscribe");
//                    disposable = d;
//                }
//
//                @Override
//                public void onSuccess(Boolean aBoolean) {
//                    if (aBoolean == true) {
//                        Log.i(LiveWallpaperUtils.TAG, "observeWallpaperResourceImage::SingleObserver::onSuccess");
//                        loadWallpaper(((WallpaperResourceImageToken) currentWallpaperResourceImage).resId);
//                        // Send Message
//                        LiveWallpaperObservable.getInstance().
//                            doNext(new StringMessageToken("Wallpaer changed succesfully"));
//                        // Send WallpaperInfo
//                        LiveWallpaperObservable.getInstance().
//                            doNext(new WallpaperUriToken(LiveWallpaperUtils.getUriToResource(getApplicationContext(), ((WallpaperResourceImageToken) currentWallpaperResourceImage).resId)));
//                        saveWallpaperInSharedPref((WallpaperResourceImageToken) currentWallpaperResourceImage);
//                        disposable.dispose();
//                        getWallpaperResourceImageObservable().subscribe(this);
//                    }
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    Log.i(LiveWallpaperUtils.TAG, "observeWallpaperResourceImage::SingleObserver::onError");
//                }
//            });
//            getWallpaperResourceImageObservable().subscribe(wallpaperResourceImageObserver);
//        }
//
//        private Single<Boolean> getWallpaperInfoRequestObservable() {
//            // Subject - source of all events
//            Observable<DataItem> subjectObservable  = LiveWallpaperObservable.getInstance().listenToObservable();
//            // Single emits true or false
//            Single<Boolean> wallpaperInfoRequestObservable = subjectObservable.any(new Predicate<DataItem>() {
//                @Override
//                public boolean test(DataItem dataItem) throws Exception {
//                    if (dataItem instanceof WallpaperInfoRequestToken) {
//                        currentWallpaperInfoRequest = dataItem;
//                        return true;
//                    } else {
//                        return false;
//                    }
//                }
//            });
//            return wallpaperInfoRequestObservable;
//        }
//
//        // Move to separate class
//        private void observeWallpaperInfoRequest() {
//            SingleObserver<Boolean>  wallpaperInfoRequestObserver = (new SingleObserver<Boolean>() {
//
//                Disposable disposable;
//
//                @Override
//                public void onSubscribe(Disposable d) {
//                    Log.i(LiveWallpaperUtils.TAG, "observeWallpaperInfoRequest::SingleObserver::onSubscribe");
//                    disposable = d;
//                }
//
//                @Override
//                public void onSuccess(Boolean aBoolean) {
//                    if (aBoolean == true) {
//                        Log.i(LiveWallpaperUtils.TAG, "observeWallpaperInfoRequest::onNext, WallpaperInfoRequest");
//                        Log.i(LiveWallpaperUtils.TAG, "observeWallpaperInfoRequest::onSuccess, WallpaperInfoRequest currentWallpaer not null");
//                        if (((WallpaperInfoRequestToken) currentWallpaperInfoRequest).requestId == WallpaperInfoRequestToken.GET_CURRENT_WALLPAPER) {
//                            LiveWallpaperObservable.getInstance().
//                                doNext(new WallpaperUriToken(LiveWallpaperUtils.getUriToResource(getApplicationContext(), ((WallpaperResourceImageToken) currentWallpaperResourceImage).resId)));
//                        }
//                        disposable.dispose();
//                        getWallpaperInfoRequestObservable().subscribe(this);
//                    }
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    Log.i(LiveWallpaperUtils.TAG, "observeWallpaperInfoRequest::SingleObserver::onError");
//                }
//            });
//            getWallpaperInfoRequestObservable().subscribe(wallpaperInfoRequestObserver);
//        }

        private void doSubscription() {
            if (isPreview() == false) {
                dispatcher();
//                observeWallpaperResourceImage();
//                observeWallpaperInfoRequest();
            }
        }

        private void saveWallpaperInSharedPref(WallpaperResourceImageToken res) {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(getString(R.string.shared_pref_resource_id), res.resId);
            editor.commit();
        }

    }
}

