package com.theleafapps.pro.geotrails.utils;

import com.facebook.AccessToken;

/**
 * Created by aviator on 02/09/16.
 */
public class Commons {

    public static AccessToken accessT;

    public static String insert_usr_st = "INSERT INTO gt_user "+
                "(user_dev_id,fb_id,first_name,last_name,gender,email,current_location) values (?,?,?,?,?,?,?);";

    public static String get_usr_by_fb_id_st = "SELECT user_id from gt_user where fb_id = ?";


    public static String update_usr_st =  "UPDATE gt_user SET user_dev_id = ?,fb_id = ?,first_name = ?,last_name = ?,gender = ?," +
            "email = ?,current_location = ? WHERE user_id = ?";

    public static String insert_marker_st = "INSERT INTO marker (user_lat,user_long,user_id,user_add,loca_title," +
            "loca_desc,geocode_add,is_star,is_sync) values (?,?,?,?,?,?,?,?,?);";

    public static String update_marker_sync = "UPDATE marker SET is_sync=1";
}
