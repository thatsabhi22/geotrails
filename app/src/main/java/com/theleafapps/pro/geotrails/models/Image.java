package com.theleafapps.pro.geotrails.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by aviator on 29/08/16.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Image extends BaseRecord {

    @JsonProperty("image_id")
    public int image_id;

    @JsonProperty("user_id")
    public int user_id;

    @JsonProperty("loca_id")
    public int loca_id;

    @JsonProperty("image_name")
    public String image_name;

    @JsonProperty("image_desc")
    public String image_desc;
}
