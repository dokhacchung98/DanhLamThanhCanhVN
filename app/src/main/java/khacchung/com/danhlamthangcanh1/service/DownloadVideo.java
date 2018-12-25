package khacchung.com.danhlamthangcanh1.service;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.ArrayList;

import khacchung.com.danhlamthangcanh1.model.ItemDataRoom;
import khacchung.com.danhlamthangcanh1.model.ItemDown;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DownloadVideo {
    private ArrayList<ItemDown> listUrl = new ArrayList<>();
    private Context context;
    private ProgressDialog progressDialog;
    private boolean isUsing = false;
    private boolean add = false;

    public void addUrl(ItemDown itemDown) {
        for (ItemDown item : listUrl) {
            if (item.getId().equals(itemDown.getId())) {
                return;
            }
        }
        @SuppressLint("SdCardPath") File file = new File("/data/data/" + "khacchung.com.danhlamthangcanh1" + "/video" + "/" + itemDown.getId() + ".mp4");
        if (!file.exists()) {
            Log.e("ADDURL", "add them : " + itemDown.getId());
            listUrl.add(itemDown);
        } else {
            add = true;
        }
        if (!isUsing) {
            downFile();
        }
    }

    public DownloadVideo(Context context) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Vui lòng chờ tải xong dữ liệu ");
        progressDialog.setCancelable(false);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Log.e("INTERNET", isInternetAvailable() + "");
        if (!isInternetAvailable()) {
            if (progressDialog.isShowing()) {
                progressDialog.cancel();
            }
        } else
            downFile();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private void downFile() {
        if (add) {
            cancleDialog();
        }
        if (listUrl.size() <= 0) {
            return;
        }
        isUsing = true;
        final ItemDown itemDown = listUrl.get(0);
        Log.e("DOWNLOAD", "bat dau down: " + itemDown.getId());
        OkHttpClient.Builder http = new OkHttpClient.Builder();
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl("https://firebasestorage.googleapis.com/v0/b/khacchung-firebase.appspot.com/o/");
        Retrofit retrofit = builder.client(http.build()).build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<ResponseBody> responseBodyCall = apiService.downloadFile(itemDown.getUrl());
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                new AsyncTask<ItemDown, Void, Void>() {
                    @Override
                    protected Void doInBackground(ItemDown... itemDowns) {
                        check = writeResponseBodyToDisk(response.body(), itemDowns[0].getId());
                        Log.e("DOWNLOAD VE", check + "");
                        if (check) {
                            Log.e("DOWNLOAD", "Tai ve thanh cong: " + itemDowns[0].getId());
                            listUrl.remove(0);
                            cancleDialog();
                        }
                        isUsing = false;
                        downFile();
                        return null;
                    }

                    private boolean check = false;
                }.execute(itemDown);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                Log.e("DOWNLOAD ERROR", "error: " + t);
            }
        });
    }

    private void cancleDialog() {
        if (listUrl.size() <= 0) {
            if (progressDialog.isShowing()) {
                progressDialog.cancel();
                //progressDialog.dismiss();
            }
        } else {
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        }
    }

    @SuppressLint("SdCardPath")
    private boolean writeResponseBodyToDisk(ResponseBody body, String name) {
        try {
            new File("/data/data/" + "khacchung.com.danhlamthangcanh1" + "/video").mkdir();
            File futureStudioIconFile = new File("/data/data/" + "khacchung.com.danhlamthangcanh1" + "/video" + "/" + name + ".mp4");

            byte[] fileReader = new byte[4096];
            try (InputStream inputStream = body.byteStream(); OutputStream outputStream = new FileOutputStream(futureStudioIconFile)) {

//                long fileSize = body.contentLength();
//                long fileSizeDownloaded = 0;

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
//                    fileSizeDownloaded += read;
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

}
