package mcsoft.com.livewallpaperdemo.utils;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class LiveWallpaperObservable {

    private static LiveWallpaperObservable instance;
    public static LiveWallpaperObservable getInstance() {
        if (instance == null) {
            instance = new LiveWallpaperObservable();
        }
        return instance;
    }


    private PublishSubject<String> publisher;

    private LiveWallpaperObservable() {
    }

    public Observable<String> getObservable() {
        publisher = PublishSubject.create();
        return publisher;
    }

    public void changeWallpaper( String param) {
        publisher.onNext(param);
    }

    public void doComplete() {
        publisher.onComplete();
    }


}
