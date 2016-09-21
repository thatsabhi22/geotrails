package com.theleafapps.pro.geotrails.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.theleafapps.pro.geotrails.R;
import com.theleafapps.pro.geotrails.models.Mark;
import com.theleafapps.pro.geotrails.models.multiples.Marks;
import com.theleafapps.pro.geotrails.utils.DbHelper;
import com.theleafapps.pro.geotrails.utils.MySingleton;

/**
 * Created by aviator on 29/08/16.
 */
public class LocationListAdapter extends
        RecyclerView.Adapter<LocationListAdapter.MyViewHolder> implements View.OnClickListener{

    DbHelper dbHelper;
    Context mContext;
    LayoutInflater inflater;
    Marks markers;
    Mark current;
    ImageLoader mImageLoader;

    public LocationListAdapter(Context context, Marks markers){
        inflater                =   LayoutInflater.from(context);
        this.mContext           =   context;
        this.markers            =   markers;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  inflater.inflate(R.layout.single_row_location_list,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        current = markers.markerList.get(position);
        mImageLoader = MySingleton.getInstance(mContext).getImageLoader();

        holder.locationImageView.setImageUrl("http://dummyimage.com/54x54/000/fff&text=0",mImageLoader);

        if (!TextUtils.isEmpty(current.loca_title)) {
            if (current.loca_title.length() > 10) {
                current.loca_title = current.loca_title.substring(0, 10).concat("...");
            }
        }

        if (!TextUtils.isEmpty(current.user_add)) {
            if (current.user_add.length() > 20) {
                current.user_add = current.user_add.substring(0, 20).concat("...");
            }
        }

        if (!TextUtils.isEmpty(current.loca_desc)) {
            if (current.loca_desc.length() > 25) {
                current.loca_desc = current.loca_desc.substring(0, 25).concat("...");
            }
        }

        holder.location_title_tv.setText(current.loca_title);
        holder.location_user_add_tv.setText(current.user_add);
        holder.location_user_desc_tv.setText(current.loca_desc);

        if(current.is_star == 0){
            holder.fav_image_view.setImageResource(R.drawable.heart_empty_28);
        }else{
            holder.fav_image_view.setImageResource(R.drawable.heart_fill_28);
        }

        if(current.is_sync == 1){
            holder.sync_image_view.setBackgroundColor(Color.parseColor("#388E3C"));
        }else{
            holder.sync_image_view.setBackgroundColor(Color.RED);
        }

    }

    @Override
    public int getItemCount() {
        if(markers!=null && markers.markerList.size() != 0) {
            return markers.markerList.size();
        }
        return 0;
    }

    @Override
    public void onClick(View view) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        NetworkImageView locationImageView;
        TextView location_title_tv,location_user_add_tv,location_user_desc_tv;
        ImageView fav_image_view,sync_image_view;

        public MyViewHolder(View itemView) {
            super(itemView);

            locationImageView       = (NetworkImageView) itemView.findViewById(R.id.locationImageView);
            location_title_tv       = (TextView) itemView.findViewById(R.id.location_title_tv);
            location_user_add_tv    = (TextView) itemView.findViewById(R.id.location_user_add_tv);
            location_user_desc_tv   = (TextView) itemView.findViewById(R.id.location_user_desc_tv);
            fav_image_view          = (ImageView) itemView.findViewById(R.id.fav_image_view);
            sync_image_view         = (ImageView) itemView.findViewById(R.id.sync_indicator);

            fav_image_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext,"Heart, sweet heart",Toast.LENGTH_SHORT).show();
                    View parentRow      =   (View) view.getParent();
                    CardView cardView   =   (CardView) parentRow.getParent();
                    RecyclerView rv     =   (RecyclerView) cardView.getParent();
                    int position        =   rv.getChildLayoutPosition(cardView);

                    if(markers.markerList.get(position).is_star == 0){
                        fav_image_view.setImageResource(R.drawable.heart_fill_28);
                        markers.markerList.get(position).is_star = 1;
                        updateFav(markers.markerList.get(position).loca_id,1);
                    }else{
                        fav_image_view.setImageResource(R.drawable.heart_empty_28);
                        markers.markerList.get(position).is_star = 0;
                        updateFav(markers.markerList.get(position).loca_id,0);
                    }
                    Log.d("Tangho", "onClick: this is sit");
                }
            });
        }
    }

    private void updateFav(int loca_id, int is_star) {
        dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteStatement stmt = db.compileStatement("UPDATE marker SET is_star = ? where loca_id = ?;");
        stmt.bindString(1, String.valueOf(is_star));
        stmt.bindString(2, String.valueOf(loca_id));
        stmt.execute();
    }
}
