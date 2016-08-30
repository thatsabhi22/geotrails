package com.theleafapps.pro.geotrails.models.multiples;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.theleafapps.pro.geotrails.models.BaseRecord;
import com.theleafapps.pro.geotrails.models.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aviator on 29/08/16.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Images extends BaseRecord {

    @JsonProperty("resource")
    public List<Image> imageList = new ArrayList<>();


}
