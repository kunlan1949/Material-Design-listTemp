package com.kunlan.zidingyi.myapplication;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {

    private String N;
    private String P;
    private static final String TAG = "LoginActivity";
    private int status;
    private String AccessToken;
    private String msg;
    private long exitTime=0;
//    private TextView responseText;
    private BroadcastReceiver recvExit;
    private String responseData;
    @BindView(R.id.name) EditText name;
    @BindView(R.id.password) EditText password;
    @BindView(R.id.login) Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        final Button login=findViewById(R.id.login);
//        responseText=findViewById(R.id.responseText);

//        recvExit = new BroadcastReceiver() {//方式2 新的方式-广播
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (intent.getAction().equals("EXIT_APP")){
//                    finish();
//                }
//            }
//        };
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("EXIT_APP");
//        registerReceiver(recvExit, filter);


        CheckBox rp=null;
        ButterKnife.bind(this);
//        EditText name=findViewById(R.id.name);
//        EditText password=findViewById(R.id.password);
        N=name.getText().toString();
        P=password.getText().toString();
//        rp=findViewById(R.id.checkbox);
//        rp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                // TODO Auto-generated method stub
//                if(isChecked){
//                    showResponse(responseData);
//                    Toast.makeText(Login.this,"记住密码",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Login");
                sendRequestWithOkHttp(N,P);
                login.setEnabled(false);
                final ProgressDialog progressDialog = new ProgressDialog(Login.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("登录中...");
                progressDialog.show();
                if(status==0) {
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    // On complete call either onLoginSuccess or onLoginFailed
                                    login.setEnabled(true);
                                    // onLoginFailed();
                                    Intent intent= new Intent();
                                    intent.setClass(Login.this,MainActivity.class);
                                    Bundle bundle=new Bundle();
                                    bundle.putString("AccessToken","14828F291178E3F9E9B58426FF4C8B0E4287E402D451CE060B19FAC16DCB606A7DA04B114ACE8E3EADFC82B013607D7D09E12612D0DD79C47BD3C14B93FD43006D1F10B3CDCED3A9AD51D9C95E408CF003D80385D9D03D7CBF0FF28C0D2B9FC3E370C8D923DF7CF89FC1A9D7BE6C312FB63F4E5471E03CBBF663B1FF07D60C6766F3F81CCC6B780014615FD1FFC393255BAA4441B23C63EA808A4B4D4A5788F841E34B08BD70C3B8DA2D7C19FC7EE9DE5E542C4FDE106B693ACF767EEFAEAF70DDA41139BB96EF8F14E72D82F74B25E056AE19B7FA776929607364FF46AFB1B0924D2823CDC744821ED4F002EA0824D1");//压入数据    
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    Toast.makeText(Login.this, "登陆成功!", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            }, 3000);
                } else {
                    login.setEnabled(true);
                    Toast.makeText(Login.this, "登陆失败,请检查用户名和密码!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }



    /************************************************向服务器发送登录请求**************************************************************/
    public void sendRequestWithOkHttp(final String Account, final String password) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();   //定义一个OKHttpClient实例
                    RequestBody requestBody = new FormBody.Builder()
                            .add("Account", Account)
                            .add("Password", password)
                            .add("IsRememberMe", "true")
                            .build();
                    Log.d("Account", Account);
                    Log.d("Password", password);
                    //实例化一个Respon对象，用于发送HTTP请求
                    Request request = new Request.Builder()
                            .url("http://api.nlecloud.com/Users/Login")             //设置目标网址
                            .post(requestBody)
                            .build();
                    Response response = okHttpClient.newCall(request).execute();  //获取服务器返回的数据
                    if (response.body() != null) {
                        responseData = response.body().string();//存储服务器返回的数据
                        Log.d("data", responseData);
                        parseJSONWithGSON(responseData);


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

//    private void showResponse(final String response) {
//        //在子线程中更新UI
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                // 在这里进行UI操作，将结果显示到界面上
//                responseText.setText(response);
//            }
//        });
//    }
    private void parseJSONWithGSON(String json) {
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<App>() {}.getType();
        App app = gson.fromJson(json, type);
        status = app.getStatus(); // 获取登录状态
        AccessToken = app.getResultObj().getAccessToken(); //获取返回的确定设备标识的字符串

        msg = app.getMsg();
        Log.d("AccessToken_data",AccessToken);
        Log.d("status", String.valueOf(status));
    }



    public static class App {
        private Login.App.ResultObj ResultObj;
        private int Status;
        private int StatusCode;
        private String Msg;
        private Object ErrorObj;
        public static class ResultObj{
            private  int UserID;
            private String UserName;
            private String Email;
            private String Telphone;
            private Boolean Gender;
            private int CollegeID;
            private String CollegeName;
            private String RoleName;
            private int RoleID;
            private String AccessToken;
            private String ReturnUrl;
            private String DataToken;

            public String getAccessToken() {
                return AccessToken;
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

        public void setStatus(int status) {
            Status = status;
        }

        public void setMsg(String msg) {
            Msg = msg;
        }

        public Object getErrorObj() {
            return ErrorObj;
        }

        public Login.App.ResultObj getResultObj() {
            return ResultObj;
        }
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(recvExit);//方式2
        }catch (Exception e){
        }
        finish();
        super.onDestroy();
    }

    public void exit(){
        if((System.currentTimeMillis()-exitTime)>2000){
            Toast.makeText(Login.this,"再按一次推出程序!",Toast.LENGTH_SHORT).show();
            exitTime=System.currentTimeMillis();
        }else {
            finish();
            System.exit(0);  //销毁强制退出
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
