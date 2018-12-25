package khacchung.com.danhlamthangcanh1.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import khacchung.com.danhlamthangcanh1.R;
import khacchung.com.danhlamthangcanh1.model.FindArea;
import khacchung.com.danhlamthangcanh1.model.ItemData;
import khacchung.com.danhlamthangcanh1.model.ItemDataRepository;
import khacchung.com.danhlamthangcanh1.view.DetalActivity;

public class MyService extends Service implements LifecycleOwner {
    private static final int FLAG = 0;
    private Location myLocation;
    private ArrayList<ItemData> arrayList;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private boolean collider = false;
    private boolean tempCollider = false;
    private ItemData tempData;
    private LifecycleRegistry lifecycleRegistry;
    private LiveData<List<ItemData>> listLiveData;

    public MyService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        lifecycleRegistry.markState(Lifecycle.State.STARTED);
        Log.e("SERVICE", "onStartCommand");
//        arrayList = new ArrayList<>();
//        arrayList = (ArrayList<ItemData>) intent.getSerializableExtra(MainActivity.STRINGSEND);
        ItemDataRepository itemDataRepository = new ItemDataRepository(this, true);
        listLiveData = itemDataRepository.getListLiveData();

        arrayList = new ArrayList<>();
        listLiveData.observe(this, new Observer<List<ItemData>>() {
            @Override
            public void onChanged(@Nullable List<ItemData> itemData) {
                if (itemData.size() > 0) {
                    arrayList = (ArrayList<ItemData>) itemData;
                }
            }
        });

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLocation = location;
                ItemData itemData = checkCollider();
                if (!tempCollider && collider && itemData != null) {
                    sendNotification(true);
                } else if (tempCollider && !collider) {
                    sendNotification(false);
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return START_NOT_STICKY;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        return START_STICKY;
    }

    /**
     * Xử lý va chạm với khu vực:
     * - Nếu trước đó là flase mà sau là true => đi vào vùng chọn
     * - Nếu trước là true mà sau là false => đi ra khỏi vùng đó
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("SERVICE", "onCreate");
        lifecycleRegistry = new LifecycleRegistry(this);
        lifecycleRegistry.markState(Lifecycle.State.CREATED);

    }

    /**
     * Không dùng notification mà bật luôn sang màn hình Detail,
     * còn lúc out khỏi vùng thì dùng notification là ok
     */
    private void sendNotification(boolean b) {
        if (tempData == null) {
            return;
        }
        Intent intent = new Intent(this, DetalActivity.class);
        intent.putExtra(DetalActivity.SENDDATA, tempData.getId());
        @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(this,
                (int) System.currentTimeMillis(), intent, FLAG);
        Notification notification;
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (b) {
            if (checkActivityRun(tempData.getId())) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            notification = new Notification.Builder(this)
                    .setContentTitle("Bạn đã vào khu vực")
                    .setContentText("Chào mừng đến với " + tempData.getName())
                    .setSmallIcon(R.drawable.ic_map_black_24dp)
                    .setAutoCancel(true)
                    .build();
            assert notificationManager != null;
            notificationManager.notify(FLAG, notification);
        } else {
            notification = new Notification.Builder(this)
                    .setContentTitle("Bạn đã ra khỏi khu vực")
                    .setContentText("Hẹn gặp bạn lần sau")
                    .setSmallIcon(R.drawable.ic_map_black_24dp)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();
            assert notificationManager != null;
            notificationManager.notify(FLAG, notification);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Nullable
    private ItemData checkCollider() {
        tempCollider = collider;
        for (ItemData itemData : arrayList) {
            if (FindArea.getAreaMap(itemData.getBounds(),
                    new LatLng(myLocation.getLatitude(),
                            myLocation.getLongitude())) <= itemData.getAreaMap()) {
                Log.e("Collider", "Đã trùng nhau rồi, với thằng " + itemData.getName());
                collider = true;
                tempData = itemData;
                return itemData;
            }
        }
        collider = false;
        return null;
    }

    private boolean checkActivityRun(String id) {
        return (!DetalActivity.activityRun || !id.equals(DetalActivity.idRun));
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }
}