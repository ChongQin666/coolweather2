package com.example.coolweather2.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Administrator on 2017/5/3.
 */
//工具类：专用于发送http请求
//因为我们所有的省市县的数据都是从服务端获取得到的，因此这里和服务器的交互，向服务器发送请求的操作封装在这里，以后直接用
public class HttpUtil
{
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback)
            //这里一个传入要访问的网址，一个传入okhttp3.Callback参数，这是okhttp库中自带的一个回调接口。
    {
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
        //Okhttp在enqueue（）方法中开好子线程并执行Http请求，并将最终的请求结果回调到okhttp3.Callback中
    }
    //这个类的作用是访问天气网址并得到网址传来的数据，这一步完成之后下一步就要解析从服务器传来的数据，把结果传给Utility类中的解析方法
}
