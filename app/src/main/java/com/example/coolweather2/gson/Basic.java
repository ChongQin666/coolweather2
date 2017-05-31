package com.example.coolweather2.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/5/30.
 */

public class Basic
{
    @SerializedName("city")//以注解的方式让JSON字段和java字段之间建立映射关系
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;
    public class Update
    {
       @SerializedName("loc")
        public String updateTime;
    }

}
