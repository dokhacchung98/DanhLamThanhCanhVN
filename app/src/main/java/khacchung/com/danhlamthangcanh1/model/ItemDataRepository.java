package khacchung.com.danhlamthangcanh1.model;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

public class ItemDataRepository {
    private ItemDataDao itemDataDao;
    private LiveData<List<ItemData>> listLiveData;
    private LiveData<ItemData> itemData;
    private boolean check = false;

    public ItemDataRepository(Context context, boolean check) {
        this.check = check;
        ItemDataRoom itemDataRoom = ItemDataRoom.getDatabase(context, check);
        itemDataDao = itemDataRoom.itemDataDao();
        listLiveData = itemDataDao.getArrData();
    }

    public LiveData<List<ItemData>> getListLiveData() {
        return listLiveData;
    }

    public LiveData<ItemData> getItemData(Context context, String id) {
        ItemDataRoom itemDataRoom = ItemDataRoom.getDatabase(context, check);
        itemDataDao = itemDataRoom.itemDataDao();
        itemData = itemDataDao.getItemData(id);
        return itemData;
    }

    public void insertData(ItemData itemData) {
        new MyAsynTaskPush(itemDataDao).execute(itemData);
    }

    public void updateItem(ItemData itemData) {
        new UpdateAsyn(itemDataDao).execute(itemData);
    }

    public void deleteItem(String id) {
        new DeleteAsyn(itemDataDao).execute(id);
    }

    public static class UpdateAsyn extends AsyncTask<ItemData, Void, Void> {

        private ItemDataDao itemDataDao;

        public UpdateAsyn(ItemDataDao itemDataDao) {
            this.itemDataDao = itemDataDao;
        }

        @Override
        protected Void doInBackground(ItemData... itemData) {
            itemDataDao.updateItem(itemData[0].getId(), itemData[0].getName(), itemData[0].getImageUrl(), itemData[0].getVideoUrl(), itemData[0].getDescription());
            return null;
        }
    }

    public static class DeleteAsyn extends AsyncTask<String, Void, Void> {
        private ItemDataDao itemDataDao;

        public DeleteAsyn(ItemDataDao itemDataDao) {
            this.itemDataDao = itemDataDao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            itemDataDao.deleteItem(strings[0]);
            return null;
        }
    }

    private static class MyAsynTaskPush extends AsyncTask<ItemData, Void, Void> {

        private ItemDataDao itemDataDao;

        public MyAsynTaskPush(ItemDataDao itemDataDao) {
            this.itemDataDao = itemDataDao;
        }

        @Override
        protected Void doInBackground(ItemData... itemData) {
            if (itemData.length > 0)
                itemDataDao.insertData(itemData[0]);
            return null;
        }
    }
}
