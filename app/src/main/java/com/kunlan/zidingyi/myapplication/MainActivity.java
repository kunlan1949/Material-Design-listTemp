package com.kunlan.zidingyi.myapplication;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.litepal.LitePal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 分页主页
 * @Project    App_Page
 * @Package    com.android.dividepage
 * @author     chenlin
 * @version    1.0
 * @Date       2012年6月2日
 * @Note       TODO
 */
public class MainActivity extends Activity {






    private float value;
    private String v;
    private String time;
    private String AccessToken;
    int dataOffset_main = 0;
    int dataNumber_main = 10;
    private Handler handler;
    private String url="http://api.nlecloud.com/devices/13067/Sensors/panao_temperature";
    String[] time_data_main = {" "," "," "," "," "," "," "," "," "," "};
    String[] temp_data_main = {" "," "," "," "," "," "," "," "," "," "};
    String[] id_data_main = {" "," "," "," "," "," "," "," "," "," "};
    String[] ii={"30","29","28","27","26","25","24","23","22","21","20"};
    NoScrollListview id_list_main;
    NoScrollListview temp_list_main;
    NoScrollListview time_list_main;
    Button button1;
    Button button2;
    Button button3;
    Button button4;




    private int iii=0;
    int isFristAddheader = 1;



    private Boolean yn=false;
    private Boolean sta=true;
    private Boolean sto=false;
    List<Datas> datasList;
//    private MyAdapter2 myAdapter2;
    private ListView listView;
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        initViews();
//        initDatas();
        Bundle bundle = getIntent().getExtras();                               //得到传过来的bundle  
        assert bundle != null;
        AccessToken = bundle.getString("AccessToken");                    //读出数据
        ini();
        button2.setEnabled(false);
        sendRequestWithOkHttp();
        final Button start_btn=findViewById(R.id.start_btn);
        final Button stop_btn=findViewById(R.id.stop_btn);
        start_btn.setEnabled(sta);
        stop_btn.setEnabled(sto);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yn=true;
                sta=false;
                sto=true;
                start_btn.setEnabled(sta);
                stop_btn.setEnabled(sto);
            }
        });
        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yn=false;
                sto=false;
                sta=true;
                stop_btn.setEnabled(sto);
                start_btn.setEnabled(sta);
            }
        });
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 4) {
                    /*** 写执行的代码*/
                    sendRequestWithOkHttp();
                    getNewTime();

                    if(yn) {
                        saveDataToDb();
                        iii += 1;
                        Log.d("iii", String.valueOf(iii));
                        Toast.makeText(MainActivity.this, "正在记录", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MainActivity.this, "已停止记录", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // (1) 使用handler发送消息
                Message message = new Message();
                message.what = 4;
                handler.sendMessage(message);
                getNewTime();
            }
        }, 0, 5000);


    }

    void saveDataToDb(){
        LitePal.getDatabase();
        Datas datas = new Datas();
        datas.setTimeData(getNewTime());
        datas.setTempData(v);
        datas.save();
        Log.d("Main","数据存储");
    }

    public String getNewTime(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
//        ti=simpleDateFormat.format(date);
        Log.d("当前日期时间",""+simpleDateFormat.format(date));
        return simpleDateFormat.format(date);
    }
    void getDataFromDb(int dataNumber,int dataOffset) {
     datasList = LitePal
                .select("id", "TempData", "TimeData")
                .order("id desc")
                .limit(dataNumber)
                .offset(dataOffset)
                .find(Datas.class);

//        myAdapter2 = new MyAdapter2();
//        listView = findViewById(R.id.page_list);
//        listView.setAdapter(myAdapter2);
//        myAdapter2.notifyDataSetChanged(); // 刷新界面
        int i= 0;
        for(Datas datas:datasList){
            id_data_main[i] = (i+1+dataOffset)+"";
            temp_data_main[i] = ""+datas.getTempData();
            time_data_main[i] = datas.getTimeData();
            Log.d("Main_id",id_data_main[i]);
            Log.d("Main_temp",temp_data_main[i]);
            Log.d("Main_time",time_data_main[i]);
            i++;
        }

        writeDataToListView(id_list_main ,id_data_main ,R.layout.header_id);
        writeDataToListView(temp_list_main ,temp_data_main ,R.layout.header_temp);
        writeDataToListView(time_list_main ,time_data_main ,R.layout.header_time);
    }



    void writeDataToListView(ListView listView, String[] dataStr, int headerviewid){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                MainActivity.this, R.layout.array_adapter,dataStr);
        listView.setAdapter(adapter);
        View headerView = View.inflate(this,headerviewid,null);
        if(isFristAddheader<=3){
            listView.addHeaderView(headerView);
            isFristAddheader++;
        }
    }
    public void ini(){
        id_list_main = findViewById(R.id.page_list);//数据列表
        temp_list_main = findViewById(R.id.page_list2);
        time_list_main = findViewById(R.id.page_list3);
         button1=findViewById(R.id.pre_page);    //首页
          button2=findViewById(R.id.pre_item);   //上一页
         button3=findViewById(R.id.next_page);    //末页
          button4=findViewById(R.id.next_item);   //下一页

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataOffset_main = 0;
                getDataFromDb(dataNumber_main,dataOffset_main);
                button2.setEnabled(false);
                button4.setEnabled(true);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataOffset_main-=10;
                getDataFromDb(dataNumber_main,dataOffset_main);
                button4.setEnabled(true);
                if(dataOffset_main<=0){
                    button2.setEnabled(false);
                }
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataOffset_main = 20;
                getDataFromDb(dataNumber_main,dataOffset_main);
                button4.setEnabled(false);
                button2.setEnabled(true);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataOffset_main+=10;
                getDataFromDb(dataNumber_main,dataOffset_main);
                button2.setEnabled(true);
                if(dataOffset_main>=20){
                    button4.setEnabled(false);
                }
            }
        });
    }

    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //在子线程中执行Http请求，并将最终的请求结果回调到okhttp3.Callback中
                HttpUtil.sendOkHttpRequest(url, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //在这里进行异常情况处理
                    }
                    @Override
                    public void onResponse(Call call, @NonNull Response response) throws IOException {
                        //得到服务器返回的具体内容
                        String responseData = response.body().string();
                        parseJSONWithGSON(responseData);
                        //显示UI界面，调用的showResponse方法
                        // showResponse(responseData);
                    }

                }, AccessToken);
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private void parseJSONWithGSON(String json) {
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<Data>() {}.getType();
        Data data = gson.fromJson(json, type);
        time=data.getResultObj().getRecordTime();
        value = (float) data.getResultObj().getValue();
        v=String.valueOf(value);
//        TextView textView=findViewById(R.id.temp);
//        TextView textView1=findViewById(R.id.time222);
//        textView.setText(v);
//        textView1.setText(time);
        Log.d("v",v);
        Log.d("value",time);
    }
    public static class Data {
        private ResultObj ResultObj;
        private int Status;
        private int StatusCode;
        private String Msg;
        private Object ErrorObj;
        public static class ResultObj{
            private String ApiTag;
            private byte Groups;
            private byte Protocol;
            private String Name;
            private String createData;
            private byte TransType;
            private byte DataType;
            private Object TypeAttrs;
            private int DevicesID;
            private String SensorType;
            private float Value;
            private String RecordTime;
            //传感器
            private String Unit;
            //执行器
//            private byte OperType;
//            private String OperTypeAttrs;


            public String getRecordTime() {
                return RecordTime;
            }

            public float getValue() {
                return Value;
            }
        }

        public int getStatus() {
            return Status;
        }

        public int getStatusCode() {
            return StatusCode;
        }

        public String getMsg() {
            return Msg;
        }

        public Object getErrorObj() {
            return ErrorObj;
        }

        public Data.ResultObj getResultObj() {
            return ResultObj;
        }
    }




    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            // 创建退出对话框
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            // 设置对话框标题
            isExit.setTitle("系统提示");
            // 设置对话框消息
            isExit.setMessage("确定要退出到登录界面吗?");
            // 添加选择按钮并注册监听
            isExit.setButton("确定", listener);
            isExit.setButton2("取消", listener);
            // 显示对话框
            isExit.show();

        }

        return false;

    }
    /**监听对话框里面的button点击事件*/
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
    {
        public void onClick(DialogInterface dialog, int which)
        {
            switch (which)
            {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序

//                    Intent intent2 = new Intent("EXIT_APP");
//                    sendBroadcast(intent2);
                    Intent intent=new Intent(MainActivity.this,Login.class);
                    startActivity(intent);
                    onDestroy();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };

}

