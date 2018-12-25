package khacchung.com.danhlamthangcanh1.model;

import java.util.List;

public class ListBound {
    private List<ItemBounds> bounds;

    public ListBound() {
    }

    public ListBound(List<ItemBounds> bounds) {

        this.bounds = bounds;
    }

    public List<ItemBounds> getBounds() {
        return bounds;
    }

    public void setBounds(List<ItemBounds> bounds) {
        this.bounds = bounds;
    }
}
