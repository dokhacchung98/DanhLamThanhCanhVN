package khacchung.com.danhlamthangcanh1.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.List;

public class FindArea {
    public static Double getAreaTriagular(LatLng l1, LatLng l2, LatLng l3) {
        Double a = getDistance(l1, l2);
        Double b = getDistance(l2, l3);
        Double c = getDistance(l1, l3);
        Double p = (a + b + c) / 2;
        return Math.sqrt(p * (p - a) * (p - b) * (p - c));
    }

    private static Double getDistance(LatLng l1, LatLng l2) {
        return SphericalUtil.computeDistanceBetween(l1, l2);
    }

    public static Double getAreaMap(List<ItemBounds> latLngs, LatLng myLatLng) {
        Double s = 0.0;
        for (int i = 1; i < latLngs.size() - 1; i++) {
            s += getAreaTriagular(myLatLng,
                    new LatLng(latLngs.get(i).getLat(), latLngs.get(i).getLng()),
                    new LatLng(latLngs.get(i + 1).getLat(), latLngs.get(i + 1).getLng()));
        }
        s += getAreaTriagular(myLatLng,
                new LatLng(latLngs.get(1).getLat(), latLngs.get(1).getLng()),
                new LatLng(latLngs.get(latLngs.size() - 1).getLat(), latLngs.get(latLngs.size() - 1).getLng()));
        return s;
    }
}
