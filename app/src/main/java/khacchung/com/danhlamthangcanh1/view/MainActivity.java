package khacchung.com.danhlamthangcanh1.view;

import android.app.ActivityManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import khacchung.com.danhlamthangcanh1.R;
import khacchung.com.danhlamthangcanh1.model.ConvertNameVideo;
import khacchung.com.danhlamthangcanh1.model.ItemData;
import khacchung.com.danhlamthangcanh1.model.ItemDown;
import khacchung.com.danhlamthangcanh1.service.DownloadVideo;
import khacchung.com.danhlamthangcanh1.service.MyService;
import khacchung.com.danhlamthangcanh1.viewmodel.DataModel;

public class MainActivity extends AppCompatActivity {

    String[] perms = {"android.permission.ACCESS_WIFI_STATE", "android.permission.ACCESS_NETWORK_STATE"
            , "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"
            , "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};

    public static final String STRINGSEND = "DataSend";
    private ArrayList<ItemData> arrayList;
    private ItemAdapter itemAdapter;
    private DataModel dataModel;
    private DownloadVideo downloadVideo;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.dltc);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permisson : perms) {
                if (checkSelfPermission(permisson) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(perms, 0);
                    return;
                }
            }
        }
        init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                finish();
                return;
            }
        }
        init();
    }

    private void init() {
        arrayList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recycle);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        itemAdapter = new ItemAdapter(arrayList, this);
        recyclerView.setAdapter(itemAdapter);


        data();
        getDataFirebase();
        downloadVideo = new DownloadVideo(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemMap:
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Lấy dữ liệu từ firebase
     */
    private void getDataFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                ItemData data = dataSnapshot.getValue(ItemData.class);
                assert data != null;
                if (data.getId().isEmpty()) {
                    data.setId(Objects.requireNonNull(dataSnapshot.getKey()));
                }
//                Log.e("DATA", "Tên: " + data.getName() + " - " + data.getBounds().size());
                downloadVideo.addUrl(new ItemDown(data.getVideoUrl(), ConvertNameVideo.convertNameVideo(data.getVideoUrl())));
                dataModel.insertData(data);
                itemAdapter.notifyDataSetChanged();
            }

            /**Khi item trên server thay đổi:
             *
             ItemData itemDataChange = dataSnapshot.getValue(ItemData.class);
             if (itemDataChange.getId().isEmpty()) {
             itemDataChange.setId(dataSnapshot.getRef().getKey());
             }
             dataModel.updateItem(itemDataChange);
             */
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            /**Khi item trên server bị xóa:
             *
             ItemData itemRemove = dataSnapshot.getValue(ItemData.class);
             if (itemRemove.getId().isEmpty()) {
             itemRemove.setId(dataSnapshot.getRef().getKey());
             }
             dataModel.deleteItem(itemRemove.getId());
             */
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Gọi data model để xử lý lấy dữ liệu trong database
     * Hàm onChange nhận biết sự kiện thay đổi của database
     */
    private void data() {
        dataModel = ViewModelProviders.of(this).get(DataModel.class);
        dataModel.getListLiveData().observe(this, new Observer<List<ItemData>>() {
            @Override
            public void onChanged(@Nullable List<ItemData> itemData) {
                arrayList = (ArrayList<ItemData>) itemData;
                itemAdapter.setArrayList(arrayList);
            }
        });
        Intent intent = new Intent(MainActivity.this, MyService.class);
        if (!isServiceRunning()) {
            startService(intent);
        }
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
