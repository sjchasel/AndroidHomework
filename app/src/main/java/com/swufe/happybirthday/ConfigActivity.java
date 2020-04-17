package com.swufe.happybirthday;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ConfigActivity extends AppCompatActivity {
    public final String TAG = "ConfigActivity";

    EditText dollarText;
    EditText euroText;
    EditText wonText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Intent intent = getIntent();
        float dollar2 = intent.getFloatExtra("dollar_rate_key",0.0f);//第一个参数指明要取哪个数据，如果第一个参数写错了，那么就会以第二个参数（默认值）生成
        Log.i(TAG,"onCreate:dollar2=" + dollar2);
        float euro2 = intent.getFloatExtra("euro_rate_key",0.0f);
        Log.i(TAG,"onCreate:euro2=" + euro2);
        float won2 = intent.getFloatExtra("won_rate_key",0.0f);
        Log.i(TAG,"onCreate:won2=" + won2);

        dollarText = (EditText) findViewById(R.id.dollar_rate);
        euroText = findViewById(R.id.euro_rate);
        wonText = findViewById(R.id.won_rate);

        //显示数据到控件
        dollarText.setText(String.valueOf(dollar2));
        euroText.setText(String.valueOf(euro2));
        wonText.setText(String.valueOf(won2));
    }

    //指定一个方法，用于保存数据
    public void save(View btn){

        Log.i(TAG,"save");

        //获取新的输入数据
        float newDollar = Float.parseFloat(dollarText.getText().toString());
        float newEuro = Float.parseFloat(euroText.getText().toString());
        float newWon = Float.parseFloat(wonText.getText().toString());

        Log.i(TAG,"save获取到新的值");
        Log.i(TAG,"save:newDollar=" + newDollar);
        Log.i(TAG,"save:newEuro=" + newEuro);
        Log.i(TAG,"save:newWon=" + newWon);

        //保存到Bundle或放入Extra(把数据打包放入bdl，再把bdl放入intent)
        Intent intent = getIntent();
        Bundle bdl = new Bundle();
        bdl.putFloat("key_dollar",newDollar);
        bdl.putFloat("key_euro",newEuro);
        bdl.putFloat("key_won",newWon);
        intent.putExtras(bdl);
        setResult(2,intent);

        //返回到调用页面
        finish();//结束当前页面，回到上一级页面
    }
}
