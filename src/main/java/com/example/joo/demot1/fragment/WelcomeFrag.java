package com.example.joo.demot1.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.example.joo.demot1.CommitPurchaseAct;
import com.example.joo.demot1.R;


public class WelcomeFrag extends Fragment {

    private Button btnWelPurByYear;
    private Button btnWelPurByMonth;
    private Button btnWelPurByDay;
    private TextView tvWelPriceByYear;
    private TextView tvWelPriceByMonth;
    private TextView tvWelPriceByDay;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        InItButton();
    }

    public void InItButton(){
        tvWelPriceByYear = (TextView)getActivity().findViewById(R.id.tv_price_year);
        tvWelPriceByMonth = (TextView)getActivity().findViewById(R.id.tv_price_month);
        tvWelPriceByDay = (TextView)getActivity().findViewById(R.id.tv_price_day);

        btnWelPurByYear = (Button)getActivity().findViewById(R.id.btn_purchase_by_year);
        btnWelPurByMonth = (Button)getActivity().findViewById(R.id.btn_purchase_by_month);
        btnWelPurByDay = (Button)getActivity().findViewById(R.id.btn_purchase_by_day);

        btnWelPurByYear.setOnClickListener(new ButtonClickListener());
        btnWelPurByMonth.setOnClickListener(new ButtonClickListener());
        btnWelPurByDay.setOnClickListener(new ButtonClickListener());
    }

    class ButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            String logAccount = getActivity().getIntent().getStringExtra("loginAccount");
            switch (v.getId()){
                case R.id.btn_purchase_by_year:
//                    Bundle bundleYear = new Bundle();
//                    bundleYear.putString("Price",tvWelPriceByYear.getText().toString());
//                    bundleYear.putString("Time","年");
//                    bundleYear.putInt("eachPrice",3600);
//                    intentYear.putExtras(bundleYear);
                    Intent intentYear = new Intent(getActivity().getApplicationContext(), CommitPurchaseAct.class);
                    intentYear.putExtra("Price",tvWelPriceByYear.getText().toString());
                    intentYear.putExtra("Time","年");
                    intentYear.putExtra("eachPrice",3600);
                    intentYear.putExtra("loginAccount",logAccount);
                    startActivity(intentYear);
                    break;
                case R.id.btn_purchase_by_month:
                    Intent intentMonth = new Intent(getActivity().getApplicationContext(), CommitPurchaseAct.class);
                    intentMonth.putExtra("Price",tvWelPriceByMonth.getText());
                    intentMonth.putExtra("Time","个月");
                    intentMonth.putExtra("eachPrice",400);
                    intentMonth.putExtra("loginAccount",logAccount);
                    startActivity(intentMonth);
                    break;
                case R.id.btn_purchase_by_day:
                    Intent intentDay = new Intent(getActivity().getApplicationContext(), CommitPurchaseAct.class);
                    intentDay.putExtra("Price",tvWelPriceByDay.getText());
                    intentDay.putExtra("Time","天");
                    intentDay.putExtra("eachPrice",15);
                    intentDay.putExtra("loginAccount",logAccount);
                    startActivity(intentDay);
                    break;
            }
        }
    }
    public void onStart() {
        super.onStart();
    }
}
