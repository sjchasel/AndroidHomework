package com.swufe.happybirthday;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RateListActivity extends ListActivity implements Runnable{
    String data[] = {"wait...."};
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //父类里已经包含了页面布局，因此这句要注释掉
        //setContentView(R.layout.activity_rate_list);

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
        //在run中获取网络数据，放入list，带回到主线程
        List<String> retList = new ArrayList<String>();
        String TAG = "RateList";
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

            //提取td中的数据
            for(int i=0;i<tds.size();i+=8){
                Element td1 = tds.get(i);
                Element td2 = tds.get(i+5);
                Log.i(TAG,"run:"+td1.text()+"==>"+td2.text());
                String str1 = td1.text();
                String val = td2.text();

                retList.add(str1+"==>"+val);
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Message msg = handler.obtainMessage(7);
        msg.obj = retList;//带回一个bundle对象
        handler.sendMessage(msg);//由handler把内容发送到消息队列里

    }
}
