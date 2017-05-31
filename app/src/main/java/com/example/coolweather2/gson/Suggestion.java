package com.example.coolweather2.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/5/30.
 */

public class Suggestion
{
    @SerializedName("comf")
    public Comfort comfort;
    public class Comfort
    {
        public String info;
    }

    @SerializedName("cw")
    public Carwash carWash;
    public class Carwash{
        public String info;
    }

    @SerializedName("sport")
    public Sport sport;
    public class Sport{
        public String info;
    }

}
