package com.cuthead.app;



import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.cuthead.controller.CustomRequest;
import com.cuthead.controller.NetworkUtil;
import com.cuthead.controller.ProgressWheel;
import com.cuthead.models.OrderAccept;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class QBProgressWheelFragment extends Fragment {
    final String url = null;
    private RequestQueue mRequestQueue;
    double longitude =0.0;
    double latitude = 0.0;
    String name;
    String phone;
    ProgressWheel pw;



    public QBProgressWheelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qbprogress_wheel, container, false);

        pw = (ProgressWheel)view.findViewById(R.id.qb_progress_wheel);
        pw.spin();
        // Inflate the layout for this fragment
        mRequestQueue = Volley.newRequestQueue(getActivity());
        // 需添加地理位置信息
        getLocation();
        Log.d("lon", Double.toString(longitude));
        Log.d("lat",Double.toString(latitude));


        SharedPreferences sp = getActivity().getSharedPreferences("com.cuthead.app.sp", Context.MODE_PRIVATE);
        name = sp.getString("phone",null);
        phone = sp.getString("name",null);

        Map<String, String> paras = new HashMap<String, String>();
       // paras.put("longitude",);
        paras.put("longitude",Double.toString(longitude));
        paras.put("latitude",Double.toString(latitude));
        paras.put("name",name);
        paras.put("phone",phone);


        CustomRequest req = new CustomRequest(Request.Method.POST, url, paras, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {

                OrderAccept order = NetworkUtil.phraseOrderAcceptJson(json);
                Bundle bundle = new Bundle();
                bundle.putParcelable("order",order);
                pw.stopSpinning();
                Fragment OrderSuccess = new OrderSuccessFragment();
                OrderSuccess.setArguments(bundle);

                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.qb_container,OrderSuccess).commit();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        mRequestQueue.add(req);
        return view;
    }
    public void getLocation()
    {
        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null){
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }else{
            LocationListener locationListener = new LocationListener() {

                // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                // Provider被enable时触发此函数，比如GPS被打开
                @Override
                public void onProviderEnabled(String provider) {
                    Toast toast = Toast.makeText(getActivity(),"GPS已打开",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
                // Provider被disable时触发此函数，比如GPS被关闭
                @Override
                public void onProviderDisabled(String provider) {
                    Toast toast = Toast.makeText(getActivity(),"GPS已关闭",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
                //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
                @Override
                public void onLocationChanged(Location location) {
                }
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000, 0,locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location != null){
                latitude = location.getLatitude(); //经度
                longitude = location.getLongitude(); //纬度
            }
        }
    }


}