package khacchung.com.danhlamthangcanh1.model;

import java.io.Serializable;

public class ItemBounds implements Serializable {
    private Double lat;
    private Double lng;

    public ItemBounds() {
    }

    public ItemBounds(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        if (lat == null) {
            return 0.0;
        }
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        if (lng == null) {
            return 0.0;
        }
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
