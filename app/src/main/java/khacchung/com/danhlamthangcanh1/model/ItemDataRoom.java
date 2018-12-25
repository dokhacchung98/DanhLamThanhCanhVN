package khacchung.com.danhlamthangcanh1.model;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.net.InetAddress;

@Database(entities = {ItemData.class}, version = 1)
public abstract class ItemDataRoom extends RoomDatabase {
    public abstract ItemDataDao itemDataDao();

    private static ItemDataRoom itemDataRoom;

    private static boolean checkService = false;


    static ItemDataRoom getDatabase(Context context, boolean checkService) {
        ItemDataRoom.checkService = checkService;
        if (itemDataRoom == null) {
            synchronized (ItemData.class) {
                itemDataRoom = Room.databaseBuilder(context.getApplicationContext(), ItemDataRoom.class, "itemdata").addCallback(callback).build();
            }
        }
        return itemDataRoom;
    }

    private static Callback callback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            if (isInternetAvailable()) {
                Log.e("CHECK INTERNET", "Đang có mạng nè " + checkService);
                if (!checkService)
                    new MyAsyn(itemDataRoom.itemDataDao()).execute();
            } else {
                Log.e("CHECK INTERNET", "Mất mạng chán ghê :'(");
            }
        }
    };

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

    public static class MyAsyn extends AsyncTask<Void, Void, Void> {

        private ItemDataDao itemDataDao;

        public MyAsyn(ItemDataDao itemDataDao) {
            this.itemDataDao = itemDataDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            itemDataDao.deleteData();
            return null;
        }
    }
}
