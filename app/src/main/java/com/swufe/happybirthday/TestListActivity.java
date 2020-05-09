package com.swufe.happybirthday;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestListActivity extends ListActivity implements Runnable, AdapterView.OnItemClickListener {
    private String[] list_data = {"one", "tow", "three", "four"};
    int msgWhat = 3;
    Handler handler;
    private ArrayList<HashMap<String, String>> listItems; // 存放文字、图片信息
    private SimpleAdapter listItemAdapter; // 适配器
    String TAG = "TestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_test_list);
        initListView();
        this.setListAdapter(listItemAdapter);


//        ListAdapter adapter = new ArrayAdapter<String>(TestListActivity.this, android.R.layout.simple_list_item_1, list_data);
//        setListAdapter(adapter);

        getListView().setOnItemClickListener(this);

        Thread t=new Thread(this);
        t.start();


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 3) {
                    List<HashMap<String,String>> tList = (List<HashMap<String,String>>) msg.obj;
//                    ListAdapter adapter = new ArrayAdapter<String>(TestListActivity.this, android.R.layout.simple_list_item_1, tList);
//                    setListAdapter(adapter);
//                    Log.i("handler", "reset list...");
                    SimpleAdapter adapter = new SimpleAdapter(TestListActivity.this, tList, // listItems数据源
                            R.layout.test_list_item, // ListItem的XML布局实现
                            new String[] { "Title","url" },
                            new int[] { R.id.Title,R.id.url});
                    setListAdapter(adapter);
                    Log.i("handler","reset list...");

                }
                super.handleMessage(msg);
            }
        };
    }

    private void initListView() {
        listItems = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("Title", "title：" + i); // 标题文字
            listItems.add(map);
        }
        // 生成适配器的Item和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(this, listItems, // listItems数据源
                R.layout.test_list_item,
                new String[] { "Title","url"},
                new int[] {R.id.Title,R.id.url}
        );
    }

    public void run() {

        Log.i("thread", "run... ");
        List<HashMap<String,String>> testList = new ArrayList<HashMap<String,String>>();
        try {
            Thread.sleep(3000);
            Document doc = Jsoup.connect("https://it.swufe.edu.cn/index/tzgg.htm").get();
            Log.i("TestList","run:"+doc.title());

            Elements mains = doc.getElementsByClass("main");

            int i = 0;
            for(Element main:mains){
                Log.i("TestList","run:table["+i+"]="+main);
                i++;
            }

            Element main = mains.get(0);

            //获取链接
            Elements lis = doc.getElementsByTag("li");
            Elements spans = main.getElementsByTag("span");
            for(int j = 65;j<=84;j++){
                String content = lis.get(j).getElementsByTag("a").attr("href");
                Log.i(TAG, "["+j+"]"+content);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("url",content);
                testList.add(map);

                int a = (j-64)*2-1;
                Element span = spans.get(a);
                Log.i("TestList", "run: ["+a+"]"+span);

                String spanStr =span.html();
                Log.i("span", spanStr);
                map.put("Title", spanStr);
                testList.add(map);
            }

//            Elements spans = main.getElementsByTag("span");
//            for (int j = i; j <=39; j += 2) {
//                Element span = spans.get(j);
//                Log.i("TestList", "run: ["+j+"]"+span);
//
//                String spanStr =span.html();
//                Log.i("span", spanStr);
//                HashMap<String, String> map = new HashMap<String, String>();
//                map.put("Title", spanStr);
//                testList.add(map);
//            }


        } catch (MalformedURLException e) {
            Log.e("www", e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("www", e.toString());
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        Message msg = handler.obtainMessage(3);
        msg.obj = testList;
        handler.sendMessage(msg);
        Log.i("thread", "sendMessage.....");

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemClick: parent=" + parent);
        Log.i(TAG, "onItemClick: view=" + view);
        Log.i(TAG, "onItemClick: position=" + position);
        Log.i(TAG, "onItemClick: id=" + id);
try{
        //从ListView中获取选中数据
        HashMap<String,String> map = (HashMap<String, String>) getListView().getItemAtPosition(position);
        String titleStr = map.get("Title");
        String u = map.get("url");
        u = u.substring(2,21);
        String ur = "https://it.swufe.edu.cn"+u;
        Log.i(TAG, "onItemClick: titleStr=" + titleStr);
        Log.i(TAG, "onItemClick: urlStr"+ur);

        //打开新的页面
        Uri uri = Uri.parse(ur);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
}catch (Exception e){
    return;
}








    }


}
