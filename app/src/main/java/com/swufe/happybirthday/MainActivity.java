package com.swufe.happybirthday;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    TextView out;
    EditText inp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inp = findViewById(R.id.input);
        out = findViewById(R.id.hello);

        Button ftoc = (Button)findViewById(R.id.FtoC);
        Button ctof = (Button)findViewById(R.id.CtoF);
        ftoc.setOnClickListener(this);
        ctof.setOnClickListener(this);

    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.FtoC:
                ftocClick(view);
                break;
            case R.id.CtoF:
                ctofClick(view);
                break;
            default:
                break;
        }
    }

    public void ftocClick(android.view.View v){
        Log.i("main","进行华氏转摄氏");
        try{
            String str = inp.getText().toString();
            long l = Long.parseLong(str);
            long ll = 5*(l - 32)/9;
            out.setText("转换后的摄氏温度是"+ll+"°C");
        }catch(Exception e){
            out.setText("请输入数字");
        }
    }
    public void ctofClick(android.view.View v) {
        Log.i("main", "进行摄氏转华氏");
        try{
            String str = inp.getText().toString();
            long l = Long.parseLong(str);
            long ll = l*9/5+32;
            out.setText("转换后的华氏温度是"+ll+"°F");
        }catch(Exception e){
            out.setText("请输入数字");
        }
    }
}
