package khacchung.com.danhlamthangcanh1.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao()
public interface ItemDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertData(ItemData itemData);

    @Query("select * from itemdata")
    public LiveData<List<ItemData>> getArrData();

    @Query("select * from itemdata where id =:id")
    public LiveData<ItemData> getItemData(String id);

    @Query("delete from itemdata")
    public void deleteData();

    @Query("delete from itemdata where id=:id")
    public void deleteItem(String id);

    @Query("update itemdata set name=:name,image_url=:image_url, video_url=:video_url, description=:description where id=:id")
    public void updateItem(String id, String name, String image_url, String video_url, String description);
}
