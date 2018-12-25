package khacchung.com.danhlamthangcanh1.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.SphericalUtil;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "itemdata")
@TypeConverters(ItemData.ProductTypeConverter.class)
public class ItemData implements Serializable {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    @ColumnInfo(name = "video_url")
    private String videoUrl;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "bounds")
    private List<ItemBounds> bounds;

    public ItemData() {
    }

    public ItemData(String name, String imageUrl, String videoUrl, String description) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public ItemData(@NonNull String id, String name, String imageUrl, String videoUrl, String description) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.description = description;
    }

    public ItemData(@NonNull String id, String name, String imageUrl, String videoUrl, String description, List<ItemBounds> bounds) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.description = description;
        this.bounds = bounds;
    }

    public String getName() {
        if (name == null) {
            return "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        if (imageUrl == null) {
            return "https://www.rtpmh.com/wp-content/uploads/2017/07/faq-1-420x314c.png";
        }
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        if (videoUrl == null) {
            return "https://firebasestorage.googleapis.com/v0/b/khacchung-firebase.appspot.com/o/New%20intro%20for%20Mini.mp4?alt=media&token=588cf192-3de3-4b8f-ae99-4956ec627a43";
        }
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getDescription() {
        if (description == null) {
            return "";
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ItemBounds> getBounds() {
        return bounds;
    }

    public void setBounds(List<ItemBounds> bounds) {
        this.bounds = bounds;
    }

    public static class ProductTypeConverter {
        @TypeConverter
        public static List<ItemBounds> stringToMeasurements(String json) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<ItemBounds>>() {
            }.getType();
            return gson.fromJson(json, type);
        }

        @TypeConverter
        public static String measurementsToString(List<ItemBounds> list) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<ItemBounds>>() {
            }.getType();
            String json = gson.toJson(list, type);
            return json;
        }
    }

    public LatLngBounds getLatLngBound() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (getMinLngMaxLat() == null || getMaxLngMinLat() == null) {
            return null;
        }
        builder.include(getMinLngMaxLat());
        builder.include(getMaxLngMinLat());
        return builder.build();
    }

    private LatLng getMinLngMaxLat() {
        if (bounds == null) {
            return null;
        }
        Double tempLat = bounds.get(1).getLat();
        Double tempLng = bounds.get(1).getLng();
        for (int i = 2; i < bounds.size(); i++) {
            if (tempLat > bounds.get(i).getLat()) {
                tempLat = bounds.get(i).getLat();
            }
            if (tempLng < bounds.get(i).getLng()) {
                tempLng = bounds.get(i).getLng();
            }
        }
        return new LatLng(tempLat, tempLng);
    }

    private LatLng getMaxLngMinLat() {
        if (bounds == null) {
            return null;
        }
        Double tempLat = bounds.get(1).getLat();
        Double tempLng = bounds.get(1).getLng();
        for (int i = 2; i < bounds.size(); i++) {
            if (tempLat < bounds.get(i).getLat()) {
                tempLat = bounds.get(i).getLat();
            }
            if (tempLng > bounds.get(i).getLng()) {
                tempLng = bounds.get(i).getLng();
            }
        }
        return new LatLng(tempLat, tempLng);
    }

    /**
     * 10 là sai số
     */
    public Double getAreaMap() {
        List<LatLng> latLngs = new ArrayList<>();
        if (bounds == null) {
            return 0.0;
        }
        for (int i = 1; i < bounds.size(); i++) {
            latLngs.add(new LatLng(bounds.get(i).getLat(), bounds.get(i).getLng()));
        }
        return SphericalUtil.computeArea(latLngs) + 10;
    }

    public LatLng getCenterMap() {
        Double a = 0.0, b = 0.0;
        for (int i = 1; i < bounds.size(); i++) {
            a += bounds.get(i).getLat();
            b += bounds.get(i).getLng();
        }
        return new LatLng(a / (bounds.size() - 1), b / (bounds.size() - 1));
    }

}
