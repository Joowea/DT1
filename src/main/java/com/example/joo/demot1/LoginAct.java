package com.example.joo.demot1;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.AutoCompleteTextView;
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

public class LoginAct extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private String Res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.et_email);
        mPasswordView = (EditText) findViewById(R.id.et_password);

        Button btnRegister = (Button) findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(LoginAct.this, InfoRegisterAct.class);
                LoginAct.this.startActivity(intent);
            }
        });

        Button btnEmailSignIn = (Button) findViewById(R.id.btn_email_sign_in);
        btnEmailSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtil.isEmpty(mEmailView.getText().toString()) && !StringUtil.isEmpty(mPasswordView.getText().toString())) {
                    login(mEmailView.getText().toString(), mPasswordView.getText().toString());
                } else {
                    Toast.makeText(LoginAct.this, "账号、密码均不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Res = msg.obj.toString();
                switch (Res){
                    case "LoginSuccess" : {
                        Intent intent=new Intent(LoginAct.this,ThemeAct.class);
                        intent.putExtra("loginAccount",mEmailView.getText().toString());
                        LoginAct.this.startActivity(intent);
                        break;
                    }
                    case "WrongPassword" : {
                        new AlertDialog.Builder(LoginAct.this)
                                .setMessage("密码输入错误")
                                .setPositiveButton("重新输入",null)
                                .show();
                        break;
                    }
                    case "Non-existentAccount" : {
                        new AlertDialog.Builder(LoginAct.this)
                                .setMessage("用户名不存在")
                                .setPositiveButton("重新输入",null)
                                .show();
                        break;
                    }
                }
            }
        }
    };
    private void login(String account, String password) {

        final String loginUrlStr = Constant.URL_Login + "/" + account + "&" + password;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(loginUrlStr);
                    connection = (HttpURLConnection) url.openConnection(); // 打开该URL连接
                    connection.setRequestMethod("GET"); // 设置请求方法
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
}

