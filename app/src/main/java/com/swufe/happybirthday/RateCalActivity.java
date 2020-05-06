package com.swufe.happybirthday;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RateCalActivity extends AppCompatActivity {
    float rate = 0f;
    String TAG = "rateCal";
    EditText inp2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_cal);
        String title = getIntent().getStringExtra("title");
        rate = getIntent().getFloatExtra("rate",0f);

        Log.i(TAG, "onCreate: title=" + title);
        Log.i(TAG, "onCreate: rate=" + rate);
        ((TextView)findViewById(R.id.title2)).setText(title);
        inp2 = (EditText)findViewById(R.id.inp2);
        inp2.addTextChangedListener(new TextWatcher() {//输入项添加一个文本感应监听器
            //监听用户输入改变，不用按键就可以做出反应
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TextView show = (TextView)RateCalActivity.this.findViewById(R.id.show2);
                if(s.length()>0){//有内容时
                    float val = Float.parseFloat(s.toString());
                    show.setText(val+"RMB==>"+(100/rate*val));
                }else{//无内容时
                    show.setText("");
                }
            }
        });
    }
}
