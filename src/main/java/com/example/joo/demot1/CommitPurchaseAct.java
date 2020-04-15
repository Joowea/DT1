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
import android.widget.NumberPicker;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CommitPurchaseAct extends AppCompatActivity {
    public String Res;
    public NumberPicker npTime;
    public TextView tvTotalPrice;
    public TextView tvPriceOut;
    public TextView tvTimeOut;
    public Button btnPay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit_purchase);

        npTime = (NumberPicker) findViewById(R.id.np_time);
        tvPriceOut = (TextView) findViewById(R.id.tv_price_commit);
        tvTimeOut = (TextView) findViewById(R.id.tv_time_commit);
        tvTotalPrice = (TextView) findViewById(R.id.tv_total_price);
        btnPay = (Button)findViewById(R.id.btn_pay);

        tvPriceOut.setText(getIntent().getStringExtra("Price"));
        tvTimeOut.setText(getIntent().getStringExtra("Time"));

        npTime.setMinValue(1);
        npTime.setMaxValue(10);
        npTime.setValue(1);
        tvTotalPrice.setText(getIntent().getIntExtra("eachPrice",0)*npTime.getValue() + "元");
        System.out.println(tvTimeOut.getText().toString());

        npTime.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                TextView tvTotalPrice;
                tvTotalPrice = (TextView) findViewById(R.id.tv_total_price);
                int eachPric = getIntent().getIntExtra("eachPrice", 0);
                int time = newVal;
                int total = eachPric * time;
                tvTotalPrice.setText(total + "元");
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new AlertDialog.Builder(CommitPurchaseAct.this)
                        .setMessage("是否确认花费"+tvTotalPrice.getText()+"购买"+npTime.getValue()
                                +tvTimeOut.getText()+"的授权")
                        .setPositiveButton("确认",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                String loginAccount = getIntent().getStringExtra("loginAccount");
                                Long extraTim;
                                switch (tvTimeOut.getText().toString()){
                                    case "年":
                                        extraTim = PayForYear(npTime.getValue());
                                        AuthorizedUrl(loginAccount,extraTim);
                                        break;
                                    case "个月":
                                        extraTim = PayForMonth(npTime.getValue());
                                        AuthorizedUrl(loginAccount,extraTim);
                                        break;
                                    case "天":
                                        extraTim = PayForDay(npTime.getValue());
                                        AuthorizedUrl(loginAccount,extraTim);
                                        break;
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
//        Bundle bundle = getIntent().getExtras();
//        tvPriceOut.setText(bundle.getString("Price"));
//        tvTimeOut.setText(bundle.getString("Time"));
//        try {
//            Log.e("Test",bundle.getInt("eachPrice")+"");
//            tvTotalPrice.setText(bundle.getInt("eachPrice")+"");
//
//            //(getIntent().getIntExtra("eachPrice",0)*npCount.getValue()*npTime.getValue())
//        }
//        catch(Exception e){
//          e.printStackTrace();
    }
    public Long PayForYear(int yearCount){
        //转化年至毫秒
        Long oneYearTimestamp = 31536000000l;
        Long extraTime = oneYearTimestamp*yearCount;
        return extraTime;
    }
    public Long PayForMonth(int monthCount){
        //转化月至毫秒
        Long oneMonthTimestamo = 2678400000l;
        Long extraTime = monthCount*oneMonthTimestamo;
        return extraTime;
    }
    public Long PayForDay(int dayCount){
        //转化天至毫秒
        Long oneDayTimestamo = 86400000l;
        Long extraTime = dayCount*oneDayTimestamo;
        return extraTime;
    }

    public void AuthorizedUrl(String userAccount, Long extraTim){

        final String loginUrlStr = Constant.URL_Authorized + "/" + userAccount + "&" + extraTim;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(loginUrlStr);
                    connection = (HttpURLConnection) url.openConnection(); // 打开该URL连接
                    connection.setRequestMethod("PUT"); // 设置请求方法
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
                if (Res.equals("AuthorizedSuccess")){
                    new AlertDialog.Builder(CommitPurchaseAct.this)
                            .setMessage("已成功获得授权")
                            .setPositiveButton("确认",new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    Intent intent=new Intent(CommitPurchaseAct.this,ThemeAct.class);
                                    intent.putExtra("loginAccount",getIntent().getStringExtra("loginAccount"));
                                    CommitPurchaseAct.this.startActivity(intent);
                                }
                            })
                            .show();
                }else {
                    new AlertDialog.Builder(CommitPurchaseAct.this)
                        .setMessage("数据连接失败请，稍后重试")
                        .setPositiveButton("确认",null);
                }
            }
        }
    };
}
