package com.cuthead.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cuthead.controller.GetFragment;
import com.cuthead.controller.TimeUtil;
import com.cuthead.models.MyTimeMark;

import java.util.ArrayList;

/**
 * Created by shixu on 2014/7/28.
 */
public class NBTimeFragment extends Fragment {
    private View mView;
    private NumberPicker hour_Picker;
    private NumberPicker minute_Picker;
    private TextView tv_showtime;
    String commitTime;
    String []hour;
    private Button btn_next;
    boolean isChooseTime = false;
    private ViewGroup indicatorLayout;
    private TextView dot1;
    private TextView dot2;
    private ImageView bar;
    String tempsum ;
    String orderID;
    String hairstyle;
    String remark;
    String date;
    String barphone;
    String distance;
    String barberName;
    GetFragment getFragment;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_nb_time, container, false);
        tv_showtime = (TextView) mView.findViewById(R.id.tv_showtime);

        indicatorLayout = (RelativeLayout)mView.findViewById(R.id.indicator3);
        bar = (ImageView)indicatorLayout.findViewById(R.id.phase1_bar);
        bar.setImageResource(R.drawable.progress_indicate_bar);
        dot1 = (TextView)indicatorLayout.findViewById(R.id.phase1_dot);
        dot1.setBackgroundResource(R.drawable.progress_bar_mark);
        dot2 = (TextView)indicatorLayout.findViewById(R.id.phase1_dot);
        dot2.setBackgroundResource(R.drawable.progress_bar_mark);

        getFragment.getNumber(3);

        Bundle bundleget = getArguments();
        final String getTime = bundleget.getString("time");                                        // time string need to be analyzed
        barphone = bundleget.getString("barphone");
        orderID = bundleget.getString("orderID");
        hairstyle = bundleget.getString("hairstyle");
        remark = bundleget.getString("remark");
        final String sex = bundleget.getString("sex");
        date = bundleget.getString("date");
        distance = bundleget.getString("distance");
        final String address = bundleget.getString("address");
        barberName = bundleget.getString("barberName");

        //final String getTime = "6:20-6:40-7:40-12:00-15:40-16:20-18:40-20:20";
        //final String date = "20140808";

        final ArrayList<MyTimeMark> time = new ArrayList<MyTimeMark>(TimeUtil.getAvailableTime(TimeUtil.pharseTimeString(getTime)));
        MyTimeMark myTimeMark;                                                                 //  store bianliang
        hour = new String[time.size()];                                                        //get String for hour_picker
        for(int i = 0;i<time.size();i++)
        {
            hour[i] =Integer.toString(time.get(i).getHour());
        }
        minute_Picker = (NumberPicker) mView.findViewById(R.id.minute_picker);                   // minute_picker
        hour_Picker = (NumberPicker) mView.findViewById(R.id.hour_picker);                       //hour_picker
        hour_Picker.setDisplayedValues(hour);
        hour_Picker.setMaxValue(hour.length-1);
        hour_Picker.setMinValue(0);

        hour_Picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, final int newVal) {                     //newVal is index
                isChooseTime = true;
                ArrayList<MyTimeMark> timeInner = new ArrayList<MyTimeMark>(TimeUtil.getAvailableTime(TimeUtil.pharseTimeString(getTime)));
                int zeroMark = timeInner.get(newVal).getZeroMark();
                int twentyMark = timeInner.get(newVal).getTwentyMark();                          //监听器的参数都是数组下标所以要通过下标来确定所选时间
                int fourtyMark = timeInner.get(newVal).getFourtyMark();                          //对于小时可从上面的hour[]数组获得  即hour[newhour]
                int sum = zeroMark*0+twentyMark*20+fourtyMark*40;
                final int newhour = newVal;
                switch (zeroMark+twentyMark+fourtyMark)
                {
                    case 1: if(sum == 0)
                        tempsum = sum+"0";
                        String minute1 [] = {sum+""};
                        minute_Picker.setMaxValue(0);                                         //只有一种分钟选择的时候  不可能检测到分钟改变所以 直接以默认分钟为所选分钟
                        minute_Picker.setDisplayedValues(minute1);
                        minute_Picker.setMinValue(0);
                        tv_showtime.setText("已选择时间"+date+" "+ hour[newhour]+"时"+sum+"分");
                        //commitTime = date+";"+hour[newhour]+"."+sum;
                        commitTime = correctTime(date,hour[newhour],sum+"");
                        isChooseTime = true;
                        //getFinalTime.getFinalTime(commitTimw);                                 //接口传值
                        break;
                    case 2: String []minute2 = new String[2];                                     //两种和三种分钟情 的情况差不多    分钟从 minute[]数组里面取  但是内部类访问要求数组为final
                        switch (sum)                                                         //所以吧minute[]数组赋给拎一个 final数组 从而取值
                        {
                            case 20: minute2[0] = "00";minute2[1] = "20";break;
                            case 40: minute2[0] = "00";minute2[1] = "40";break;
                            case 60: minute2[0] = "20";minute2[1] = "40";break;
                        }
                        minute_Picker.setDisplayedValues(minute2);
                        minute_Picker.setMaxValue(1);
                        minute_Picker.setMinValue(0);
                        final String getMinute2[] = minute2;
                        String initminute2 = getMinute2[0];                                    //如果用户不选择分钟那么默认的分钟时 数组的第0个
                        //commitTime = date+";"+hour[newhour]+"."+initminute2;
                        commitTime = correctTime(date,hour[newhour],initminute2);
                        //getFinalTime.getFinalTime(commitTimw);                                 //以上三行就是设置默认时间的
                        tv_showtime.setText("已选择时间"+date+" " + hour[newhour] + "时" + initminute2 + "分");
                        minute_Picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker picker, int moldVal, int mnewVal) {
                                tv_showtime.setText("已选择时间"+date+" " + hour[newhour] + "时" + getMinute2[mnewVal] + "分");
                                //commitTime = date+";"+hour[newhour]+"."+getMinute2[mnewVal];                 //最后的提交带有日期和时间的  最终时间
                                commitTime = correctTime(date,hour[newhour],getMinute2[mnewVal]);
                                isChooseTime = true;
                                //getFinalTime.getFinalTime(commitTimw);
                            }
                        });break;
                    case 3: String minute3 [] = {"00","20","40"};
                        minute_Picker.setDisplayedValues(minute3);
                        minute_Picker.setMaxValue(2);
                        minute_Picker.setMinValue(0);
                        final String getMinute3[] = minute3;
                        String initminute3 = getMinute3[0];                                    //如果用户不选择分钟那么默认的分钟时 数组的第0个
                        //commitTime = date+";"+hour[newhour]+"."+initminute3;
                        commitTime = correctTime(date,hour[newhour],initminute3);
                        //getFinalTime.getFinalTime(commitTimw);                                 //以上三行就是设置默认时间的
                        tv_showtime.setText("已选择时间" +date+" "+ hour[newhour] + "时" + initminute3 + "分");
                        minute_Picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker picker, int moldVal, int mnewVal) {
                                tv_showtime.setText("已选择时间"+date + " "+ hour[newhour]+"时"+getMinute3[mnewVal]+"分");
                                //commitTime = date+";"+hour[newhour]+"."+getMinute3[mnewVal];                 //最后的提交带有日期和时间的  最终时间
                                commitTime = correctTime(date,hour[newhour],getMinute3[mnewVal]);
                                isChooseTime = true;
                                //getFinalTime.getFinalTime(commitTimw);
                            }
                        });
                }
            }
        });


        btn_next = (Button) mView.findViewById(R.id.btn_time_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isChooseTime == false)
                {
                    Toast toast = Toast.makeText(getActivity(),"您还未选择时间呢",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    return;
                }

                FragmentManager fragmentManager = getFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putString("time",commitTime);
                bundle.putInt("flag", 1);
                bundle.putString("oederID", orderID);
                bundle.putString("barphone",barphone);
                bundle.putString("hairstyle",hairstyle);
                bundle.putString("remark",remark);
                bundle.putString("distance",distance);
                bundle.putString("address",address);
                bundle.putString("sex",sex);
                bundle.putString("barberName",barberName);
                Fragment fragment = new SubmitFragment();
                fragment.setArguments(bundle);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container,fragment).addToBackStack(null).commit();

            }
        });

        return mView;
    }

    public void onAttach(Activity activity){
        super.onAttach(activity);
        if (!(activity instanceof GetFragment)) {
            throw new IllegalStateException("Fragment所在的Activity必须实现GetNumber接口");
        }
        getFragment=(GetFragment) activity;
    }

    public String correctTime(String date,String hour,String minute){
        if(Integer.parseInt(hour)<10)
            hour = "0"+hour;
        if(minute == "0")
            minute = "00";
        return date+";"+hour+":"+minute;
    }

}
