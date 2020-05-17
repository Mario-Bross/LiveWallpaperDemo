package mcsoft.com.livewallpaperdemo.utils;

import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.SingleSubject;
import io.reactivex.functions.Consumer;
import mcsoft.com.livewallpaperdemo.data.DataItem;
import mcsoft.com.livewallpaperdemo.data.DialogWriteSettingInfo;

public class SingleRxDataBus {

    SingleSubject<DataItem> single;

    public SingleRxDataBus() {
    }

    public void post(DataItem event) {
        if (single != null) {
            single.onSuccess(event);
        }
    }

    public <TYPE> void register(Class<TYPE>  aClass, Consumer<TYPE> action) {
        single = SingleSubject.create();
        single
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
                .subscribe((action));
    }

}
