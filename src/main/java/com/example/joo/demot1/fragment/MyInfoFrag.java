package com.example.joo.demot1.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.joo.demot1.CertificationInUnitAct;
import com.example.joo.demot1.CommitPurchaseAct;
import com.example.joo.demot1.Constant;
import com.example.joo.demot1.LoginAct;
import com.example.joo.demot1.R;
import com.example.joo.demot1.ThemeAct;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyInfoFrag extends Fragment {
    TextView tvAccount;
    TextView tvCutOffTime;
    TextView tvTimeRemain;
    Button btnLogOut;
    ListView lvMyInfo;
    private String[] data = { "订单查询", "实名认证","单位注册申请"};


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_info, container, false);


    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListViewOut();
        InItButton();
    }
    public void ListViewOut(){
        lvMyInfo = (ListView)getActivity().findViewById(R.id.lv_info_contro);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),android.R.layout.simple_list_item_1, data);
        lvMyInfo.setAdapter(adapter);

        lvMyInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String logAccount = getActivity().getIntent().getStringExtra("loginAccount");
                switch (position){
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        Intent intentUnitCert = new Intent();
                        intentUnitCert.setClass(getActivity().getApplicationContext(), CertificationInUnitAct.class);
                        intentUnitCert.putExtra("loginAccount",logAccount);
                        startActivity(intentUnitCert);
                        break;


                }
            }
        });
    }
    public void InItButton() {
        tvAccount = (TextView) getActivity().findViewById(R.id.tv_my_info_account);
        tvCutOffTime = (TextView) getActivity().findViewById(R.id.tv_my_info_cut_off_time);
        tvTimeRemain = (TextView) getActivity().findViewById(R.id.tv_my_info_last_time);

        btnLogOut = (Button)getActivity().findViewById(R.id.btn_log_out);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity().getApplicationContext(),LoginAct.class);
                startActivity(intent);
            }
        });

        String userAccount = getActivity().getIntent().getStringExtra("loginAccount");
        tvAccount.setText(userAccount);
        CheckTimeURL(userAccount);
    }


    public void CheckTimeURL(String userAccount) {
        final String checkTimeUrlStr = Constant.URL_CheckTime + "/" + userAccount;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(checkTimeUrlStr);
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
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Long timesTamp =Long.valueOf(msg.obj.toString());
                System.out.println(msg.obj);
                System.out.println(msg.obj.toString());
                System.out.println(timesTamp);
                Long cur = System.currentTimeMillis();
                Long valueOfDif = timesTamp - cur;
                System.out.println(valueOfDif);
                if (valueOfDif > 0l) {
                    Date date = new Date(timesTamp);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                    String str = sdf.format(date);
                    tvCutOffTime.setText("使用权限至" + str);
                    tvTimeRemain.setText(RemainTrans(valueOfDif));
                }else {
                    tvCutOffTime.setText("无使用权限");
                    tvTimeRemain.setText("");
                }
            }
        }
    };

    public String RemainTrans(Long timeRemain){
        Long Res = timeRemain/(3600*1000);
        if (Res < 72l){
            return "剩余" + (int)Math.floor((double)Res) + "小时";
        }else {
            return "剩余" + (int)Math.ceil(Res/(24)) + "天";
        }
    }
        @Override
        public void onStart() {
            super.onStart();
        }

}

