package mcsoft.com.livewallpaperdemo.utils;

import android.util.Log;

import java.util.function.Consumer;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import mcsoft.com.livewallpaperdemo.data.DataItem;
import mcsoft.com.livewallpaperdemo.data.StringMessageToken;

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
        publisher.single(new StringMessageToken(""));
    }

//    public <T> void register(Class<T> eventClass, Consumer<T> action) {
//        publisher
//            .filter(new Predicate<Class>() {
//                @Override
//                public boolean test(Class aClass) throws Exception {
//                    return aClass.getClass().equals(eventClass);
//                }
//            })


//                @Override
//                public boolean test(DataItem dataItem) throws Exception {
//                    return dataItem.getClass().equals(eventClass);
//                }
//            })

//            .filter(event -> event.getClass().equals(eventClass))
//            .map (obj -> (T)obj)
//            .subscribe((Observer<? super T>) action);



}
