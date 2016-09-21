package com.theleafapps.pro.geotrails.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by aviator on 29/08/16.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Mark extends BaseRecord {

    @JsonProperty("loca_id")
    public int loca_id;

    @JsonProperty("user_id")
    public int user_id;

    @JsonProperty("loca_title")
    public String loca_title;

    @JsonProperty("loca_desc")
    public String loca_desc;

    @JsonProperty("geo_code_add")
    public String geo_code_add;

    @JsonProperty("user_add")
    public String user_add;

    @JsonProperty("user_lat")
    public double user_lat;

    @JsonProperty("user_long")
    public double user_long;

    @JsonProperty("created_on")
    public String created_on;

    @JsonProperty("modified_on")
    public String modified_on;

    @JsonProperty("is_star")
    public int is_star;

    @JsonProperty("is_sync")
    public int is_sync;
}
