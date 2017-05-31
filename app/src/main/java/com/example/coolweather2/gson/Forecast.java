package com.example.coolweather2.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/5/30.
 */

public class Forecast
{
    public String date;
    @SerializedName("cond")
    public More more;
    @SerializedName("tmp")
    public Temperature temperature;
    public class More
    {
        @SerializedName("txt_d")
        public String info;
    }
    public class Temperature
    {
        public String max;
        public String min;
    }
}
