package mcsoft.com.livewallpaperdemo.utils;

import java.util.function.Consumer;

import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import mcsoft.com.livewallpaperdemo.data.DataItem;

public class RxDataBus {

    private static RxDataBus rxDataBus;
    private final Subject<DataItem> mBusSubject =  PublishSubject.create();

    public static synchronized RxDataBus getInstance() {
        if (rxDataBus == null){
            rxDataBus = new RxDataBus();
        }
        return rxDataBus;
    }

    public void post(DataItem event) {
        mBusSubject.onNext(event);
    }

    // Create and register Observer
    public <TYPE> void register(Class<TYPE>  aClass, Observer<TYPE> action) {
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

//    public <TYPE> void register(Class<TYPE>  aClass, SingleObserver<TYPE> action) {
//        mBusSubject
//            .filter(new Predicate<DataItem>() {
//                @Override
//                public boolean test(DataItem event) throws Exception {
//                    return event.getClass().equals(aClass.toString());
//                }
//            })
//            .map(new Function<DataItem, TYPE>() {
//                public TYPE apply(DataItem dataItem) {
//                    return (TYPE) dataItem;
//                }
//            })
//            .sin;
//    }

}
