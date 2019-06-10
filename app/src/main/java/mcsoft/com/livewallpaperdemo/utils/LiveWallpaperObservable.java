package mcsoft.com.livewallpaperdemo.utils;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;
import mcsoft.com.livewallpaperdemo.service.LiveWallpaperService;

public class LiveWallpaperObservable {

    private static LiveWallpaperObservable instance;
    public static LiveWallpaperObservable getInstance() {
        if (instance == null) {
            instance = new LiveWallpaperObservable();
        }
        return instance;
    }


    private PublishSubject<Integer> publisher;

    private LiveWallpaperObservable() {
        publisher = PublishSubject.create();
    }

    public Observable<Integer> getObservable() {
        return publisher;
    }

    public void changeWallpaper(Integer res) {
        if (publisher != null) {
            publisher.onNext(res);
        }
    }

    public void doComplete() {
        publisher.onComplete();
        publisher = null;
    }


    private void test() {
    }
}
