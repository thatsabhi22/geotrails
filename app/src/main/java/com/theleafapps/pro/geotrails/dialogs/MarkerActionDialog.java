package com.theleafapps.pro.geotrails.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.theleafapps.pro.geotrails.R;
import com.theleafapps.pro.geotrails.ui.AddDataActivity;
import com.theleafapps.pro.geotrails.utils.DbHelper;

/**
 * Created by aviator on 18/01/16.
 */
public class MarkerActionDialog extends DialogFragment implements View.OnClickListener {
    String[] markerLongPressActionArray;
    Bundle bundle;
    Resources res;
    String ofl_loca_id;
    DbHelper dbHelper;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        res                         =   getResources();
        markerLongPressActionArray  =   res.getStringArray(R.array.markerLongPressAction);
        AlertDialog.Builder builder =   new AlertDialog.Builder(getActivity());

        bundle     = getArguments();
        if(bundle != null) {
            ofl_loca_id = bundle.getString("ofl_loca_id");

            builder.setItems(R.array.markerLongPressAction, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent;
                    Toast.makeText(getActivity(),
                            "Question Selected ->" + markerLongPressActionArray[which], Toast.LENGTH_LONG).show();

                    if (TextUtils.equals(markerLongPressActionArray[which], "Edit")) {
                        intent = new Intent(getActivity(), AddDataActivity.class);
                        intent.putExtra("ofl_loca_id",ofl_loca_id);
                        getActivity().startActivity(intent);
                    }

                    if (TextUtils.equals(markerLongPressActionArray[which], "Delete")) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                    }
                }
            });
        }
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        return dialog;
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    Dialog dialogI  = (Dialog) dialog;
                    Context context = dialogI.getContext();

                    dbHelper            =   new DbHelper(context);
                    SQLiteDatabase db   =   dbHelper.getWritableDatabase();
                    SQLiteStatement stmt = db.compileStatement("UPDATE marker SET is_deleted = 1, is_sync = 0 where ofl_loca_id = ?;");
                    stmt.bindString(1, String.valueOf(ofl_loca_id));
                    stmt.execute();

                    Toast.makeText(context,"The location is deleted successfully",Toast.LENGTH_LONG).show();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    @Override
    public void onClick(View view) {

    }
}
