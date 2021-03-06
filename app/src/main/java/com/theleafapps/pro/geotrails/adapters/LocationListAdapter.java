package com.theleafapps.pro.geotrails.adapters;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.theleafapps.pro.geotrails.R;
import com.theleafapps.pro.geotrails.dialogs.MarkerActionDialog;
import com.theleafapps.pro.geotrails.models.Mark;
import com.theleafapps.pro.geotrails.models.multiples.Markers;
import com.theleafapps.pro.geotrails.tasks.UpdateMarkerIsStarTask;
import com.theleafapps.pro.geotrails.ui.HomeActivity;
import com.theleafapps.pro.geotrails.ui.LocationListActivity;
import com.theleafapps.pro.geotrails.utils.DbHelper;
import com.theleafapps.pro.geotrails.utils.MySingleton;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import eu.davidea.flipview.FlipView;

/**
 * Created by aviator on 29/08/16.
 */
public class LocationListAdapter extends
        RecyclerView.Adapter<LocationListAdapter.MyViewHolder> implements View.OnClickListener {

    DbHelper dbHelper;
    Context mContext;
    LayoutInflater inflater;
    Markers markers;
    Mark current;
    ImageLoader mImageLoader;
    String multiMarker;
    Set<String> multiMarkerList;
    FragmentManager fragmentManager;

    public LocationListAdapter(Context context, Markers markers, String multiMarker, FragmentManager fragmentManager) {
        inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.markers = markers;
        this.multiMarker = multiMarker;
        multiMarkerList = new LinkedHashSet<>();
        this.fragmentManager = fragmentManager;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.single_row_location_list, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        current = markers.markList.get(position);
        mImageLoader = MySingleton.getInstance(mContext).getImageLoader();

//        holder.locationImageView.setImageUrl("http://dummyimage.com/54x54/000/fff&text=0",mImageLoader);

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

        if (TextUtils.equals(current.is_star, "false")) {
            holder.fav_image_view.setImageResource(R.drawable.heart_empty_28);
        } else {
            holder.fav_image_view.setImageResource(R.drawable.heart_fill_28);
        }

        if (current.is_sync == 1) {
            holder.sync_image_view.setBackgroundColor(Color.parseColor("#388E3C"));
        } else {
            holder.sync_image_view.setBackgroundColor(Color.RED);
        }


    }

    @Override
    public int getItemCount() {
        if (markers != null && markers.markList.size() != 0) {
            return markers.markList.size();
        }
        return 0;
    }

    @Override
    public void onClick(View view) {

    }

    @NonNull
    private String getOflLocaIdByCardPosition(View view) {
        int position = getCardViewPositionWithParentRecycler(view);
        return String.valueOf(markers.markList.get(position).ofl_loca_id);
    }

    private int getCardViewPosition(View view) {
        View parentRow = (View) view.getParent();
        CardView cardView = (CardView) parentRow.getParent();
        RecyclerView rv = (RecyclerView) cardView.getParent();
        return rv.getChildLayoutPosition(cardView);
    }

    private int getCardViewPositionWithParentRecycler(View view) {
        RecyclerView rv = (RecyclerView) view.getParent();
        return rv.getChildLayoutPosition(view);
    }

    private void updateFav(int ofl_loca_id, int is_star, int is_sync) {
        try {
            dbHelper = new DbHelper(mContext);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            SQLiteStatement stmt = db.compileStatement(DbHelper.update_marker_star_sync_ofl);
            stmt.bindString(1, String.valueOf(is_star));
            stmt.bindString(2, String.valueOf(is_sync));
            stmt.bindString(3, String.valueOf(ofl_loca_id));
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCloudMark(Mark mark, String is_star) {
        try {
            mark.is_star = is_star;
            Markers markers = new Markers();
            markers.markList.add(mark);

            UpdateMarkerIsStarTask updateMarkerIsStarTask = new UpdateMarkerIsStarTask(mContext, markers);
            updateMarkerIsStarTask.execute().get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        FlipView locationFlipView;
        TextView location_title_tv, location_user_add_tv, location_user_desc_tv;
        ImageView fav_image_view, sync_image_view;

        public MyViewHolder(View itemView) {
            super(itemView);

            locationFlipView = (FlipView) itemView.findViewById(R.id.locationFlipView);
            location_title_tv = (TextView) itemView.findViewById(R.id.location_title_tv);
            location_user_add_tv = (TextView) itemView.findViewById(R.id.location_user_add_tv);
            location_user_desc_tv = (TextView) itemView.findViewById(R.id.location_user_desc_tv);
            fav_image_view = (ImageView) itemView.findViewById(R.id.fav_image_view);
            sync_image_view = (ImageView) itemView.findViewById(R.id.sync_indicator);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //###################################################
                    String ofl_loca_id = getOflLocaIdByCardPosition(view);

                    Intent intent = new Intent(mContext, HomeActivity.class);

                    intent.putExtra("multimarker", ofl_loca_id);
                    intent.putExtra("caller", "LocationListActivity");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(intent);
//                    Toast.makeText(mContext,"card clicked",Toast.LENGTH_SHORT).show();
                    //###################################################

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
//                    Toast.makeText(mContext,"card clicked long",Toast.LENGTH_SHORT).show();
                    String ofl_loca_id = getOflLocaIdByCardPosition(view);
                    Bundle bundle = new Bundle();
                    bundle.putString("ofl_loca_id", ofl_loca_id);
                    MarkerActionDialog ldc = new MarkerActionDialog();
                    ldc.setArguments(bundle);
                    ldc.show(fragmentManager, "securityQuestionDialog");
                    return false;
                }
            });

            locationFlipView.setOnFlippingListener(new FlipView.OnFlippingListener() {
                @Override
                public void onFlipped(FlipView flipView, boolean checked) {
                    int position = getCardViewPosition(flipView);
                    //Toast.makeText(mContext,"ofl_loca_id : "+markers.markList.get(position).ofl_loca_id,Toast.LENGTH_SHORT).show();
                    String ofl_loca_id = String.valueOf(markers.markList.get(position).ofl_loca_id);

                    if (checked)
                        multiMarkerList.add(ofl_loca_id);
                    else
                        multiMarkerList.remove(ofl_loca_id);

                    LocationListActivity.multiMarkerString = TextUtils.join(",", multiMarkerList);

//                  Toast.makeText(mContext,"multimarkerstr -> " + LocationListActivity.multiMarkerString,Toast.LENGTH_SHORT).show();
                }
            });

            fav_image_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(mContext,"Heart, sweet heart",Toast.LENGTH_SHORT).show();
                    int position = getCardViewPosition(view);

                    if (TextUtils.equals(markers.markList.get(position).is_star, "false")) {
                        fav_image_view.setImageResource(R.drawable.heart_fill_28);
                        sync_image_view.setBackgroundColor(Color.RED);
                        markers.markList.get(position).is_star = "true";
                        updateFav(markers.markList.get(position).ofl_loca_id, 1, 0);
                        //updateCloudMark(markers.markList.get(position),"true");
                    } else {
                        fav_image_view.setImageResource(R.drawable.heart_empty_28);
                        sync_image_view.setBackgroundColor(Color.RED);
                        markers.markList.get(position).is_star = "false";
                        updateFav(markers.markList.get(position).ofl_loca_id, 0, 0);
                        //updateCloudMark(markers.markList.get(position),"false");
                    }
                    Log.d("Tangho", "onClick: this is sit");
                }
            });
        }
    }
}
