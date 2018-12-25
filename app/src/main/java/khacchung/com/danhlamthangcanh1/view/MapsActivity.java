package khacchung.com.danhlamthangcanh1.view;

import khacchung.com.danhlamthangcanh1.R;
import khacchung.com.danhlamthangcanh1.model.FindArea;
import khacchung.com.danhlamthangcanh1.model.ItemData;
import khacchung.com.danhlamthangcanh1.viewmodel.DataModel;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<ItemData> arrayList;
    private List<Polyline> polylines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        polylines = new ArrayList<>();
    }

    private void getData() {
        arrayList = new ArrayList<>();
        DataModel dataModel = ViewModelProviders.of(MapsActivity.this).get(DataModel.class);
        dataModel.getListLiveData().observe(MapsActivity.this, new Observer<List<ItemData>>() {
            @Override
            public void onChanged(@Nullable List<ItemData> itemData) {
                arrayList = (ArrayList<ItemData>) itemData;
                drawPolygon();
                moveCam();
            }
        });
    }

    private void moveCam() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (ItemData itemData : arrayList) {
            builder.include(itemData.getLatLngBound().northeast);
            builder.include(itemData.getLatLngBound().southwest);
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),
                getResources().getDisplayMetrics().widthPixels + 20,
                getResources().getDisplayMetrics().heightPixels + 20,
                (int) (getResources().getDisplayMetrics().widthPixels * 0.10)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera
        getData();
    }

    private void drawPolygon() {
        mMap.clear();
        polylines.clear();

        for (ItemData itemData : arrayList) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.parseColor("#0000ff"));
            polylineOptions.width(5.0f);
            for (int i = 1; i < itemData.getBounds().size(); i++) {
                polylineOptions.add(new LatLng(itemData.getBounds().get(i).getLat()
                        , itemData.getBounds().get(i).getLng()));
            }
            //findArea(itemData);
            polylines.add(mMap.addPolyline(polylineOptions));
        }
    }

    /**
     * Vị trí thử nghiệm: 21.029606, 105.852536 giữa hồ hoàn kiếm
     */
    private void findArea(ItemData itemData) {
        LatLng temp = new LatLng(21.029606, 105.852536);
        Double s = 0.0;
        for (int i = 1; i < itemData.getBounds().size() - 1; i++) {
            s += FindArea.getAreaTriagular(
                    new LatLng(itemData.getBounds().get(i).getLat(), itemData.getBounds().get(i).getLng()),
                    new LatLng(itemData.getBounds().get(i + 1).getLat(), itemData.getBounds().get(i + 1).getLng())
                    , temp);
        }
        s += FindArea.getAreaTriagular(
                new LatLng(itemData.getBounds().get(itemData.getBounds().size() - 1).getLat(), itemData.getBounds().get(itemData.getBounds().size() - 1).getLng()),
                new LatLng(itemData.getBounds().get(1).getLat(), itemData.getBounds().get(1).getLng())
                , temp);
        Log.e("Diện Tích", itemData.getName() + "Có diện tích thật= " +
                itemData.getAreaMap() + ", và có diện tích tính= " + s);

    }
}
