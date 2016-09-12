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
}
