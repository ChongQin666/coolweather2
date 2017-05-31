package com.example.coolweather2.util;

import android.text.TextUtils;

import com.example.coolweather2.db.City;
import com.example.coolweather2.db.County;
import com.example.coolweather2.db.Province;
import com.example.coolweather2.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/5/4.
 */
//工具类：专用于数据的解析，可以通过不同的方法选择使用JSONObject解析省市县数据还是用GSON来解析天气信息
//由于服务器返回的省市县数据都是JSON格式的，这里提供一个工具类来解析和处理这种数据
    //我们把省级，市级和县级的数据分别通过JSONArray和JSONObject解析出来，组装成实体类对象，然后通过save（）方法将数据保存到数据库中
public class Utility
{
    /*
    * 解析和处理服务器返回的省级数据
    * */
    public static boolean handleProvinceResponse(String response)
    //传入服务器返回的response数据进行解析
    {
        if(!TextUtils.isEmpty(response))//TextUtils类是安卓对于字符串处理的一个类，这里表示，判断response是否为空
        {
            try {
                JSONArray allProvinces=new JSONArray(response);//创建JSONAray数组并把response放到数组里面
                for(int i=0;i<allProvinces.length();i++)
                {
                    JSONObject provinceObject=allProvinces.getJSONObject(i);//从数组中循环取出对象
                    Province province=new Province();//我们建的表对象
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();//把数据存储到数据库的表中，这里的数据库指的是安卓手机内置的SQLite数据库，我们使用LitePal这个ORM框架来操作数据库
                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

        /*
    * 解析和处理服务器返回的市级数据
    * */
        public static boolean handleCityResponse(String response,int provinceId)
        //传入服务器返回的response数据进行解析
        {
            if(!TextUtils.isEmpty(response))
            {
                try {
                    JSONArray allCities=new JSONArray(response);
                    for(int i=0;i<allCities.length();i++)
                    {
                       JSONObject cityObject=allCities.getJSONObject(i);
                        City city=new City();
                        city.setCityName(cityObject.getString("name"));
                        city.setCityCode(cityObject.getInt("id"));
                        city.setProvinceId(provinceId);
                        city.save();
                    }
                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

       /*
    * 解析和处理服务器返回的县级数据
    * */
    public static boolean handleCountyResponse(String response,int cityId)
    //传入服务器返回的response数据进行解析
    {
        if(!TextUtils.isEmpty(response))
        {
            try {
                JSONArray allCounties=new JSONArray(response);//创建JSONAray数组并把response放到数组里面
                for(int i=0;i<allCounties.length();i++)
                {
                    JSONObject countyObject=allCounties.getJSONObject(i);//从数组中循环取出对象
                    County county=new County();//我们建的表对象
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();//把数据存储到数据库中
                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Weather handleWeatherResponse(String response)
    {
        try {
            JSONObject jsonObject=new JSONObject(response);//把Heweather这个类中的数据取出来
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");//根据键取值
            String weatherContent=jsonArray.getJSONObject(0).toString();//因为每次点击一个城市的数据，所以就取数据里的第一个对象
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
