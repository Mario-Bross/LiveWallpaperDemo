package mcsoft.com.livewallpaperdemo.utils;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import mcsoft.com.livewallpaperdemo.data.DataItem;
import mcsoft.com.livewallpaperdemo.data.Message;
import mcsoft.com.livewallpaperdemo.service.LiveWallpaperService;

// TODO: Rename this class to LiveWallpaperDataBus
public class LiveWallpaperObservable {

    private static LiveWallpaperObservable instance;

    public static synchronized LiveWallpaperObservable getInstance() {
        if (instance == null) {
            instance = new LiveWallpaperObservable();
        }
        return instance;
    }

    private PublishSubject<DataItem> publisher;

    private LiveWallpaperObservable() {
        publisher = PublishSubject.create();
    }

    /* As Observable */
    public Observable<DataItem> listenToObservable() {
        if (publisher == null) {
            publisher = PublishSubject.create();
        }
        return publisher;
    }

    /* As Observer */
    public void doNext(DataItem res) {
        if (publisher != null) {
            Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperObservable::publishData res = " + res.getClass().getName());
            publisher.onNext(res);
        }
    }

    public void doComplete() {
        Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperObservable::onComplete");
        publisher.onComplete();
        publisher = null;
    }


    private void test() {
        publisher.single(new Message(""));
    }

}
