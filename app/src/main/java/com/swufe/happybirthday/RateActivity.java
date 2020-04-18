package com.swufe.happybirthday;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RateActivity extends AppCompatActivity implements Runnable{

    private final String TAG = "Rate";
    private float dollarRate = 0.1f;
    private float euroRate = 0.2f;
    private float wonRate = 0.3f;
    String nowDate;

    EditText rmb;
    TextView show;
    Handler handler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        rmb = findViewById(R.id.rmb);
        show = findViewById(R.id.showOut);



        //获取SP里保存的数据
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        //还可以用:(推荐使用)
        //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        //第一个参数是文件名，第二个参数是权限。此时表示对它的访问是私有访问，只有当前的APP才能读写文件
        dollarRate = sharedPreferences.getFloat("dollar_rate",0.0f);//第二个参数是默认值
        euroRate = sharedPreferences.getFloat("euro_rate",0.0f);
        wonRate = sharedPreferences.getFloat("won_rate",0.0f);

        Log.i(TAG,"onCreate:sp dollarRate="+dollarRate);
        Log.i(TAG,"onCreate:sp wonrRate="+wonRate);
        Log.i(TAG,"onCreate:sp euroRate="+euroRate);

        //获取当前时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate =  new Date(System.currentTimeMillis());
        nowDate = formatter.format(curDate);
        Log.i(TAG,"现在的时间是："+nowDate);
        String lastDate = sharedPreferences.getString("lastDate"," ");//获得sp里的时间
        Log.i(TAG,"上次储存的时间是："+lastDate);

        //进行判断
        if(nowDate.equals(lastDate)){
            Log.i(TAG,"当天已更新过数据");

        }else{
            //开启子线程
            Thread t = new Thread(this);//线程运行时，会去寻找当前对象的run方法
            t.start();//写出这个命令才开始用，就会执行run方法
            Log.i(TAG,"当日首次打开，进行数据更新");
        }


        handler = new Handler(){//处理获得的消息
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==5){//判断是哪个线程返回的数据
                    Bundle bdl = (Bundle) msg.obj;
                    dollarRate = bdl.getFloat("dollar-rate");
                    euroRate = bdl.getFloat("euro-rate");
                    wonRate = bdl.getFloat("won-rate");
                    Log.i(TAG, "handleMessage: dollarRate:" + dollarRate);
                    Log.i(TAG, "handleMessage: euroRate:" + euroRate);
                    Log.i(TAG, "handleMessage: wonRate:" + wonRate);
                    Toast.makeText(RateActivity.this, "汇率已更新", Toast.LENGTH_SHORT).show();

                }
                super.handleMessage(msg);

            }
        };
    }

    public void onClick(View btn){
        //获取当前用户输入
        String str = rmb.getText().toString();
        float r = 0;//要放在外面才可以用，如果用户没有输入内容r就等于0
        if(str.length()>0){
            r = Float.parseFloat(str);
        }else{
            //用户没有输入内容
            Toast.makeText(this,"请输入金额",Toast.LENGTH_SHORT).show();
            return;
        }

        if(btn.getId()==R.id.btn_dollar){
            show.setText(String.format("%.2f",r*dollarRate));
        }else if(btn.getId()==R.id.btn_euro){
            show.setText(String.format("%.2f",r*euroRate));
        }else{
            show.setText(String.format("%.2f",r*wonRate));
        }
    }

    public void openOne(View btn){

        openConfig();
    }

    private void openConfig() {
        Intent config = new Intent(this, ConfigActivity.class);
        //第一个参数标记下一个窗口由哪个窗口打开，所以把当前对象给它
        //第二个参数是需要打开的窗口的类名

        config.putExtra("dollar_rate_key", dollarRate);//这个方法可以携带附加的数据到下个页面
        Log.i(TAG, "openOne:dollarRate=" + dollarRate);
        config.putExtra("euro_rate_key", euroRate);
        Log.i(TAG, "openOne:euroRate=" + euroRate);
        config.putExtra("won_rate_key", wonRate);
        Log.i(TAG, "openOne:wonRate=" + wonRate);

        //startActivity(config);
        startActivityForResult(config, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;//返回真或假，真表示有菜单项
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_set){
            openConfig();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    //resultCode可以确定是哪个窗口返回的数据；resultCode可以区分返回的数据按什么格式去拆分
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1 && resultCode==2){
            Bundle bundle = data.getExtras();
            dollarRate = bundle.getFloat("key_dollar",0.1f);
            euroRate = bundle.getFloat("key_euro",0.1f);
            wonRate = bundle.getFloat("key_won",0.1f);
            Log.i(TAG,"onActivityResult:dollarRate=" + dollarRate);
            Log.i(TAG,"onActivityResult:euroRate=" + euroRate);
            Log.i(TAG,"onActivityResult:wonRate=" + wonRate);

            //将新设置的汇率写到SP里
            SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();//要获得一个edit对象才可以改变sharedPreferences
            editor.putFloat("dollar_rate",dollarRate);
            editor.putFloat("euro_rate",euroRate);
            editor.putFloat("won_rate",wonRate);
            editor.putString("lastDate",nowDate);
            //写完之后注意保存
            editor.commit();
            Log.i(TAG,"onActivityResult:数据已保存到sharedPreferences");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void run(){
//        Log.i(TAG,"run:run()...");
//        try {
//            Thread.sleep(2000);//让它停止两秒钟
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        //用户用于保存获取的汇率
        Bundle bundle = new Bundle();


        //获取网络数据
//        URL url = null;
//        try {
//            url = new URL("http://www.usd-cny.com/bankofchina.htm");
//            HttpURLConnection http = (HttpURLConnection) url.openConnection();//打开链接
//            InputStream in = http.getInputStream();
//
//            String html = inputStream2String(in);//获得源文件
//            Log.i(TAG,"run:html="+html);
//            Document doc = Jsoup.parse(html);//把源文件给Jsoup
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {
            Document doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            //doc = Jsoup.parse(html);//把源文件给Jsoup，但可以用上面那句直接获取
            Log.i(TAG, "run:" + doc.title());
            Elements tables = doc.getElementsByTag("table");
//            int i = 1;
//            //用这个找我们需要的是第几个table
//            for(Element table: tables){
//                Log.i(TAG,"run:table["+i+"]="+table);
//                i++;
//            }
            Element table1 = tables.get(0);
            Log.i(TAG,"run:table1="+table1);
            //获取TD中的数据
            Elements tds = table1.getElementsByTag("td");

            //提取td中的数据
            for(int i=0;i<tds.size();i+=6){
                Element td1 = tds.get(i);//第一列数据
                Element td2 = tds.get(i+5);//第六列数据
                Log.i(TAG,"run:"+td1.text()+"==>"+td2.text());
                String str1 = td1.text();
                String val = td2.text();

                if(str1.equals("美元")){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(val));
                }else if(str1.equals("欧元")){
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(val));
                }else if(str1.equals("韩元")){
                    bundle.putFloat("won-rate",100f/Float.parseFloat(val));
                }
            }

//            for(Element td:tds){
//                Log.i(TAG,"run:td="+td);
//
//                //输出后发现当td中含有html元素时，这两个方法的结果就有差别
//                Log.i(TAG,"run:text="+td.text());
//                Log.i(TAG,"run:html="+td.html());
//
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //bundle中保存所获取的汇率
        //获取msg对象，用于返回主线程
        Message msg = handler.obtainMessage();
        msg.what = 5;//也可以写在上面那个方法的参数里
        //msg.obj = "Hello from run()";
        msg.obj = bundle;//带回一个bundle对象
        handler.sendMessage(msg);//由handler把内容发送到消息队列里


    }
    private String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream,"gb2312");//gb2312才能显示中文
        for(;;){
            int rsz = in.read(buffer, 0, buffer.length);
            if(rsz<0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
}
