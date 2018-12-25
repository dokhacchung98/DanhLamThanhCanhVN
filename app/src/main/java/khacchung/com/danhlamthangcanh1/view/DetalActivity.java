package khacchung.com.danhlamthangcanh1.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import khacchung.com.danhlamthangcanh1.R;
import khacchung.com.danhlamthangcanh1.model.ConvertNameVideo;
import khacchung.com.danhlamthangcanh1.model.ItemData;
import khacchung.com.danhlamthangcanh1.viewmodel.DataModel;

public class DetalActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String SENDDATA = "suuush";

    private VideoView videoView;
    private TextView txtTitle;
    private TextView txtDesception;
    private ImageView imgThumbnail;
    private String id;
    private MediaController mediaController;

    private GoogleMap map;
    private ItemData item;

    public static boolean activityRun = false;
    public static String idRun = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detal);

        Objects.requireNonNull(getSupportActionBar()).hide();

        videoView = findViewById(R.id.videoView);
        txtTitle = findViewById(R.id.txtTitle);
        txtDesception = findViewById(R.id.txtDescription);
        imgThumbnail = findViewById(R.id.imgThumbnail);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        id = intent.getStringExtra(SENDDATA);

        if (id != null && !id.isEmpty()) {
            data();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityRun = true;
        idRun = id;
    }

    private void data() {
        DataModel dataModel = ViewModelProviders.of(this).get(DataModel.class);
        dataModel.getItemData(id).observe(this, new Observer<ItemData>() {
            @SuppressLint("SdCardPath")
            @Override
            public void onChanged(@Nullable final ItemData itemData) {
                item = itemData;
                moveCam();
                if (itemData != null) {
                    txtDesception.setText(itemData.getDescription());
                    txtTitle.setText(itemData.getName());
                    Picasso.get().load(itemData.getImageUrl()).into(imgThumbnail);
                    mediaController = new MediaController(DetalActivity.this);
                    videoView.setMediaController(mediaController);
                    videoView.setVideoPath("/data/data/" + "khacchung.com.danhlamthangcanh1" + "/video" + "/" + ConvertNameVideo.convertNameVideo(itemData.getVideoUrl()) + ".mp4");
                    videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                            Log.e("LOAD VIDEO", "Loi tu uri");
                            videoView.setVideoPath(itemData.getVideoUrl());
                            videoView.start();
                            return false;
                        }
                    });
                    videoView.start();
                }
            }
        });
    }


    @Override
    public String[] fileList() {
        return super.fileList();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);
        }
    }

    private void moveCam() {
        if (item != null) {
            map.addMarker(new MarkerOptions().position(item.getCenterMap()).title(item.getName()));
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(item.getLatLngBound(),
                    getResources().getDisplayMetrics().widthPixels + 20,
                    getResources().getDisplayMetrics().heightPixels + 20,
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.10)));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityRun = false;
        idRun = "";
    }
}
