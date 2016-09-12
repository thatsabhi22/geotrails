package com.theleafapps.pro.geotrails.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by aviator on 29/08/16.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class User extends BaseRecord {

    @JsonProperty("user_id")
    public int user_id;

    @JsonProperty("user_dev_id")
    public String user_dev_id;

    @JsonProperty("fb_id")
    public String fb_id;

    @JsonProperty("current_location")
    public String current_location;

    @JsonProperty("created_on")
    public String created_on;

    @JsonProperty("email")
    public String email;

    @JsonProperty("first_name")
    public String first_name;

    @JsonProperty("gender")
    public String gender;

    @JsonProperty("last_name")
    public String last_name;

    @JsonProperty("modified_on")
    public String modified_on;
}
