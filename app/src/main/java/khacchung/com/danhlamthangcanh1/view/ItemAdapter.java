package khacchung.com.danhlamthangcanh1.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import khacchung.com.danhlamthangcanh1.R;
import khacchung.com.danhlamthangcanh1.model.ItemData;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHodel> {

    private ArrayList<ItemData> arrayList;
    private Context context;

    public ItemAdapter(ArrayList<ItemData> arrayList, Context context) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public void setArrayList(ArrayList<ItemData> arrayList) {
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHodel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_recycleview, parent, false);
        return new MyViewHodel(view);
    }

    /**
     * Không share title và description
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHodel holder, int position) {
        final ItemData data = arrayList.get(position);
        String name = data.getName();
        if (name.length() > 12) {
            name = name.substring(0, 12) + "...";
        }
        holder.txtName.setText(name);
        String des = data.getDescription();
        if (des.length() > 150) {
            des = des.substring(0, 150) + " ...";
        }
        holder.txtDescription.setText(des);
        Picasso.get().load(data.getImageUrl()).fit().centerCrop().into(holder.imgThubnail);

        holder.cardView.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        holder.cardView.setCardBackgroundColor(Color.parseColor("#d217df"));
                        break;
                    case MotionEvent.ACTION_UP:
                        holder.cardView.setCardBackgroundColor(Color.parseColor("#1771df"));
                        Intent intent = new Intent(context, DetalActivity.class);
                        intent.putExtra(DetalActivity.SENDDATA, data.getId());

                        Pair[] pair = new Pair[1];
                        pair[0] = new Pair(holder.imgThubnail, "imgShare");
//                        pair[1] = new Pair(holder.txtName, "titleShare");
//                        pair[2] = new Pair(holder.txtDescription, "desShare");

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pair);
                        context.startActivity(intent, options.toBundle());
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public static class MyViewHodel extends RecyclerView.ViewHolder {
        public ImageView imgThubnail;
        public TextView txtName;
        public TextView txtDescription;
        private CardView cardView;

        public MyViewHodel(View itemView) {
            super(itemView);
            imgThubnail = itemView.findViewById(R.id.imgThumbnail);
            txtName = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            cardView = itemView.findViewById(R.id.cardClick);
        }
    }
}
