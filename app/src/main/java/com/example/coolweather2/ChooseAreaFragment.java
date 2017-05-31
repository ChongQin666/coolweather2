package com.example.coolweather2;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolweather2.db.City;
import com.example.coolweather2.db.County;
import com.example.coolweather2.db.Province;
import com.example.coolweather2.util.HttpUtil;
import com.example.coolweather2.util.Utility;

import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/4.
 */
//使用碎片来呈现省市县的数据
public class ChooseAreaFragment extends Fragment
{
    //定义三个固定值用于在点击事件判断要加载什么数据
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;
    private ProgressDialog progressDialog;//定义进度条
    private TextView titleText;//定义头布局的标题
    private Button backButton;//定义返回按钮
    private ListView listView;//定义数据列表
    private ArrayAdapter<String> adapter;//定义适配器
    private List<String> dataList=new ArrayList<>();//定义放入ListView的数据列表

    //定义省列表，用于把从数据库中读到的数据传入，并在listView中显示出来
    private List<Province> provinceList;
    //定义市列表，用于把从数据库中读到的数据传入，并在listView中显示出来
    private List<City> cityList;
    //定义县列表，用于把从数据库中读到的数据传入，并在listView中显示出来
    private List<County> countyList;
    //选中的省份
    private Province selectedProvince;
    //选中的城市
    private City selectedCity;
    //当前选中的级别
    private int currentLevel;
    //之所以要定义选中的省份和城市，是因为在后面向服务器取数据的时候需要选中城市或省份的id号，把网址和id号发送给服务器得到数据

    @Nullable
    @Override//为碎片创建视图（加载布局）的时候调用
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.choose_area,container,false);
        titleText=(TextView)view.findViewById(R.id.title_text);
        backButton=(Button)view.findViewById(R.id.back_button);
        listView=(ListView)view.findViewById(R.id.list_view);
        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override//确保与碎片关联的活动已经创建完毕的时候调用
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE)
                {
                    selectedProvince=provinceList.get(position);//后面可以通过selectedProvince.getID来得到省id，再和网址一起发送给服务器获取数据
                    queryCities();
                }
                else if(currentLevel==LEVEL_CITY)
                {
                    selectedCity=cityList.get(position);
                    queryCounties();
                }
                else if(currentLevel==LEVEL_COUNTY)
                {
                    String weatherId=countyList.get(position).getWeatherId();
                    Intent intent=new Intent(getActivity(),WeatherActivity.class);
                    intent.putExtra("weather_id",weatherId);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(currentLevel==LEVEL_COUNTY)
                {
                   queryCities();
                }
                else if (currentLevel==LEVEL_CITY)
                {
                    queryProvinces();
                }
            }
        });
        queryProvinces();//还没点击前就先加载省的数据，
    }

    //查询全国所有的省，优先从数据库查询，如果没有再从服务器中查询
    private void queryProvinces()
    {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);//省级最大，无返回按钮
        provinceList= DataSupport.findAll(Province.class);//使用Litepal查询数据的方法
        if(provinceList.size()>0)
        {
            dataList.clear();
            for(Province province:provinceList)
            {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        }
        else
        {
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    //查询全国所有的市，优先从数据库查询，如果没有再从服务器中查询
    private void queryCities()
    {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList=DataSupport.where("provinceid=?",String.valueOf(selectedProvince.getId())).find(City.class);
        if(cityList.size()>0)
        {
            dataList.clear();
            for(City city:cityList)
            {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;
        }
        else
        {
            int provinceCode=selectedProvince.getProvinceCode();
            String address="http://guolin.tech/api/china"+ provinceCode;
            queryFromServer(address,"city");

        }
    }

    private void queryCounties()
    {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList=DataSupport.where("cityid=?",String.valueOf(selectedCity.getId())).find(County.class);
        if(countyList.size()>0)
        {
            dataList.clear();
            for(County county:countyList)
            {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }
        else
        {
            int provinceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode();
            String address="http://guolin.tech/api/china"+ provinceCode+"/"+cityCode;
            queryFromServer(address, "county");
        }

    }

    //根据传入的地址和类型从服务器上查询省市县数据
    private void queryFromServer(String address, final String type)
    {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            //当发送网址后服务器返回的结果将会回传给这个方法里面
            public void onResponse(Call call, Response response) throws IOException
            {
                String responseText=response.body().string();
                boolean result=false;
                if(type.equals("province"))
                {
                    result= Utility.handleProvinceResponse(responseText);
                }
                else if("city".equals(type))
                {
                    result=Utility.handleCityResponse(responseText,selectedProvince.getId());
                }
                else if("county".equals(type))
                {
                    result=Utility.handleCityResponse(responseText,selectedCity.getId());
                }
                if(result)
                {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type))
                            {
                                queryProvinces();
                            }
                            else if ("city".equals(type))
                            {
                                queryCities();
                            }
                            else if ("county".equals(type))
                            {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e)
            {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }



    //显示进度对话框
    private void showProgressDialog()
    {
        if(progressDialog==null)
        {
           progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    //关闭进度对话框
    private void closeProgressDialog()
    {
        if (progressDialog!=null)
        {
            progressDialog.dismiss();
        }
    }

}
