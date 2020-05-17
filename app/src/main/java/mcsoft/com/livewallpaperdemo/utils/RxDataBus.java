package mcsoft.com.livewallpaperdemo.utils;

import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.SingleSubject;
import io.reactivex.subjects.Subject;
import mcsoft.com.livewallpaperdemo.data.DataItem;

public class RxDataBus {

    private static RxDataBus rxDataBus;
    private Subject<DataItem> mBusSubject =  PublishSubject.create();

    public static synchronized RxDataBus getInstance() {
        if (rxDataBus == null){
            rxDataBus = new RxDataBus();
        }
        return rxDataBus;
    }

    /* As Observable */
    public Observable<DataItem> listenToObservable() {
        if (mBusSubject == null)
            mBusSubject = PublishSubject.create();
        return mBusSubject;
    }

    /* As Observer */
    public void doNext(DataItem res) {
        if (mBusSubject != null) {
            Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperObservable::publishData res = " + res.getClass().getName());
            mBusSubject.onNext(res);
        }
    }


    public void doComplete() {
        if (mBusSubject != null) {
            Log.i(LiveWallpaperUtils.TAG, "LiveWallpaperObservable::onComplete");
            mBusSubject.onComplete();
            mBusSubject = null;
        }
    }

    // ---------------------------- New implementation
    public void post(DataItem event) {
        if (mBusSubject != null) {
            mBusSubject.onNext(event);
        }
    }

    // Register Observer
    public <TYPE> void register(Class<TYPE>  aClass, Observer<TYPE> action) {
        if (mBusSubject != null) {
            mBusSubject
                    .filter(new Predicate<DataItem>() {
                        @Override
                        public boolean test(DataItem event) throws Exception {
                            String aClassStr = aClass.toString();
                            String eventStr = event.getClass().toString();
                            // Sprawdz czy instancja eventu zgadza sie z aClass
                            boolean val = event.getClass().equals(aClass);
                            return val;
                        }
                    })
                    .map(new Function<DataItem, TYPE>() {
                        public TYPE apply(DataItem dataItem) {
                            return (TYPE) dataItem;
                        }
                    })
                    .subscribe(action);
        }
    }


    public <TYPE> Disposable register(Class<TYPE>  aClass, Consumer<TYPE> action) {
       Disposable disposable =  mBusSubject
                .filter(new Predicate<DataItem>() {
                    @Override
                    public boolean test(DataItem event) throws Exception {
                        String aClassStr = aClass.toString();
                        String eventStr = event.getClass().toString();
                        // Sprawdz czy instancja eventu zgadza sie z aClass
                        boolean val = event.getClass().equals(aClass);
                        return val;
                    }
                })
                .map(new Function<DataItem, TYPE>() {
                    public TYPE apply(DataItem dataItem) {
                        return (TYPE) dataItem;
                    }
                })
                .subscribe(action);
       return disposable;
    };

}


