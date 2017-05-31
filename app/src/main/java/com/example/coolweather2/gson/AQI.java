package com.example.coolweather2.gson;

/**
 * Created by Administrator on 2017/5/30.
 */

public class AQI
{
    public AQICity city;
    public class AQICity
    {
        public String aqi;
        public String pm25;
    }
}
