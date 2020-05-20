package com.swufe.happybirthday;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RateListActivity extends ListActivity implements Runnable{
    String data[] = {"wait...."};
    Handler handler;
    private String logDate = "";
    private final String DATE_SP_KEY = "lastRateDateStr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //父类里已经包含了页面布局，因此这句要注释掉
        //setContentView(R.layout.activity_rate_list);

        SharedPreferences sp = getSharedPreferences("myrate", Context.MODE_PRIVATE);
        logDate = sp.getString(DATE_SP_KEY, "");
        Log.i("List","lastRateDateStr=" + logDate);

        List<String> list1 = new ArrayList<String>();
        for(int i = 1; i < 100; i++){
            list1.add("item" + i);
        }
        //第一个参数为当前对象，第二个对象为布局(此时的R是Android提供的R，使用Android平台提供的资源)，第三个参数为数据
        ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        setListAdapter(adapter);//父类中的方法，表示当前界面用adapter管理

        //注意在这开启线程
        Thread t = new Thread(this);
        t.start();

        handler = new Handler(){
          public void handleMessage(Message msg){
              if(msg.what==7){//进行拆包
                  List<String> list2 = (List<String>) msg.obj;
                  ListAdapter adapter = new ArrayAdapter<String>(RateListActivity.this,android.R.layout.simple_list_item_1,list2);
                  setListAdapter(adapter);
              }
              super.handleMessage(msg);
          }
        };
    }

    @Override
    public void run() {
        String TAG = "RateList";
        //在run中获取网络数据，放入list，带回到主线程
        List<String> retList = new ArrayList<String>();
        String curDateStr = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        Log.i("run", "curDateStr:"+curDateStr+"logDate:"+logDate);

        if(curDateStr.equals(logDate)) {
            //如果相等，则不从网络中获取数据
            Log.i("run", "日期相等，从数据库中获取数据");
            RateManager manager = new RateManager(this);
            for(RateItem item : manager.listAll()){
                retList.add(item.getCurName()+"->"+item.getCurRate());
            }
        }else{
            //从网络获取数据
            Log.i("run","日期不相等，从网络中获取在线数据");
            try {
                Thread.sleep(3000);
                Document doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();
                //doc = Jsoup.parse(html);//把源文件给Jsoup，但可以用上面那句直接获取
                Log.i("RateList", "run:" + doc.title());
                Elements tables = doc.getElementsByTag("table");
//            int i = 1;
//            //用这个找我们需要的是第几个table
//            for(Element table: tables){
//                Log.i(TAG,"run:table["+i+"]="+table);
//                i++;
//            }
                Element table1 = tables.get(1);
                Log.i(TAG,"run:table1="+table1);
                //获取TD中的数据
                Elements tds = table1.getElementsByTag("td");

                List<RateItem> rateList = new ArrayList<RateItem>();

                //提取td中的数据
                for(int i=0;i<tds.size();i+=8){
                    Element td1 = tds.get(i);
                    Element td2 = tds.get(i+5);
                    Log.i(TAG,"run:"+td1.text()+"==>"+td2.text());
                    String str1 = td1.text();
                    String val = td2.text();

                    retList.add(str1+"==>"+val);
                    rateList.add(new RateItem(str1,val));
                }

                //把数据写入数据库
                RateManager manager = new RateManager(this);
                manager.deleteAll();//写入数据前要删除原来的数据
                manager.addAll(rateList);

                //记录更新日期
                SharedPreferences sp = getSharedPreferences("myrate", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString(DATE_SP_KEY, curDateStr);
                edit.commit();
                Log.i("run","更新日期结束：" + curDateStr);

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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        Message msg = handler.obtainMessage(7);
        msg.obj = retList;//带回一个bundle对象
        handler.sendMessage(msg);//由handler把内容发送到消息队列里

    }
}
