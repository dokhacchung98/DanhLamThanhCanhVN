package khacchung.com.danhlamthangcanh1.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import khacchung.com.danhlamthangcanh1.model.ItemData;
import khacchung.com.danhlamthangcanh1.model.ItemDataRepository;

public class DataModel extends AndroidViewModel {
    private ItemDataRepository itemDataRepository;
    private LiveData<List<ItemData>> listLiveData;
    private Application context;

    public DataModel(@NonNull Application application) {
        super(application);
        this.context = application;
        itemDataRepository = new ItemDataRepository(application, false);
        listLiveData = itemDataRepository.getListLiveData();
    }

    public LiveData<List<ItemData>> getListLiveData() {
        return listLiveData;
    }

    public void insertData(ItemData itemData) {
        itemDataRepository.insertData(itemData);
    }

    public LiveData<ItemData> getItemData(String id) {
        return itemDataRepository.getItemData(context, id);
    }

    public void deleteItem(String id) {
        itemDataRepository.deleteItem(id);
    }

    public void updateItem(ItemData itemData) {
        itemDataRepository.updateItem(itemData);
    }

}
