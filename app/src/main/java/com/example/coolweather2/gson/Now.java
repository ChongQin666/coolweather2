package com.example.coolweather2.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/5/30.
 */

public class Now
{
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;
    public class More
    {
        @SerializedName("text")
        public String info;
    }
}
