package com.example.joo.demot1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class InfoRegisterAct extends AppCompatActivity {
    private EditText etRegAccount;
    private EditText etRegPassword;
    private EditText etRegPasswordAgain;
    private EditText etRegName;
    private EditText etRegIdNumb;
    private EditText etRegPhone;
    private EditText etRegEmail;
    private String Res;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_register);

        etRegAccount = (EditText) findViewById(R.id.et_reAccount);
        etRegPassword = (EditText) findViewById(R.id.et_rePassword);
        etRegPasswordAgain = (EditText) findViewById(R.id.et_rePasswordAgain);
        etRegName = (EditText) findViewById(R.id.et_reName);
        etRegIdNumb = (EditText) findViewById(R.id.et_reIdNumb);
        etRegPhone = (EditText) findViewById(R.id.et_rePhone);
        etRegEmail = (EditText) findViewById(R.id.et_reEmail);

        Button btnRegisterUp = (Button) findViewById(R.id.btn_register_up);
        btnRegisterUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!StringUtil.isEmpty(etRegAccount.getText().toString())
                        &&!StringUtil.isEmpty(etRegPassword.getText().toString())
                        &&!StringUtil.isEmpty(etRegPasswordAgain.getText().toString())
                        &&!StringUtil.isEmpty(etRegName.getText().toString())
                        &&!StringUtil.isEmpty(etRegEmail.getText().toString())
                        &&!StringUtil.isEmpty(etRegPhone.getText().toString())) {
                    try {
                        register(etRegAccount.getText().toString(), etRegPassword.getText().toString(),
                                etRegPasswordAgain.getText().toString(), etRegName.getText().toString(),
                                etRegIdNumb.getText().toString(), Double.parseDouble(etRegPhone.getText().toString()),
                                etRegEmail.getText().toString());
                    }catch (Exception ex)
                    {
                        Toast.makeText(InfoRegisterAct.this, "输入信息有误！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(InfoRegisterAct.this, "注册信息均不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button regReturn = (Button) findViewById(R.id.btn_return);
        regReturn.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                Intent intent = new Intent();
                intent.setClass(InfoRegisterAct.this,LoginAct.class);
                InfoRegisterAct.this.startActivity(intent);
            }
        });
    }
    private void register(String account, String password, String passwordAgain, String name, String idNumb, Double phone,
                          String eMail) {
        final String registerUrlStr = Constant.URL_Register + "/" + account + "&" + password + "&"
                + passwordAgain + "&" + name + "&" + idNumb + "&" + phone + "&" + eMail;
        System.out.println(registerUrlStr);

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(registerUrlStr); // 声明一个URL,注意——如果用百度首页实验，请使用https
                    connection = (HttpURLConnection) url.openConnection(); // 打开该URL连接
                    connection.setRequestMethod("POST"); // 设置请求方法，“POST或GET”
                    connection.setConnectTimeout(8000); // 设置连接建立的超时时间
                    connection.setReadTimeout(8000); // 设置网络报文收发超时时间

                    InputStream in = connection.getInputStream();  // 通过连接的输入流获取下发报文，然后就是Java的流处理
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    //给handler发送消息
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = response.toString();
                    handler.sendMessage(msg);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Res = msg.obj.toString();
                switch (Res){
                    case "RegisterSuccess" : {
                        new AlertDialog.Builder(InfoRegisterAct.this)
                                .setMessage("注册成功")
                                .setPositiveButton("返回登陆", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent=new Intent(InfoRegisterAct.this,LoginAct.class);
                                        startActivity(intent);
                                    }
                                })
                                .show();
                        break;
                    }
                    case "DifferentPassword" : {
                        new AlertDialog.Builder(InfoRegisterAct.this)
                                .setMessage("两次密码输入内容不一致")
                                .setPositiveButton("重新输入",null)
                                .show();
                        break;
                    }
                    case "TheSameAccount" : {
                        new AlertDialog.Builder(InfoRegisterAct.this)
                                .setMessage("该用户名已存在")
                                .setPositiveButton("重新输入",null)
                                .show();
                        break;
                    }
                    case "SomeContextEmpty" : {
                        new AlertDialog.Builder(InfoRegisterAct.this)
                                .setMessage("注册信息输入不完整")
                                .setPositiveButton("重新输入",null)
                                .show();
                        break;
                    }
                }
            }
        }
    };
}
