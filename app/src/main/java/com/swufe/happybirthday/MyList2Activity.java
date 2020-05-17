package com.swufe.happybirthday;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyList2Activity extends ListActivity implements Runnable, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {//ListActivity中已经有listView对象，因此不需要加载布局

    Handler handler;
    String TAG = "MyList2";
    private ArrayList<HashMap<String, String>> listItems;//存放文字、图片信息
    private SimpleAdapter listItemAdapter;//适配器

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initListView();
        this.setListAdapter(listItemAdapter);

        //MyAdapter myAdapter = new MyAdapter(this,R.layout.list_item,listItems);
        //this.setListAdapter(myAdapter);
        //ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        //setListAdapter(adapter);

        Thread t = new Thread(this);
        t.start();

        handler = new Handler() {//处理获得的消息
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 7) {
                    List<HashMap<String,String>> list2 = (List<HashMap<String, String>>) msg.obj;
                    listItemAdapter = new SimpleAdapter(MyList2Activity.this, list2,//listItems数据源
                            R.layout.list_item,//listItem的xml布局实现
                            new String[]{"ItemTitle", "ItemDetail"},
                            new int[]{R.id.itemTitle, R.id.itemDetail}
                    );
                    setListAdapter(listItemAdapter);
                    Log.i(TAG, "handleMessage:list2... ");
                }
                super.handleMessage(msg);
            }
        };
        getListView().setOnItemClickListener(this);//当行数据被点击时会调用this对象的onItemClick方法
        getListView().setOnItemLongClickListener(this);//长按，当前对象做监听器，当前对象一定要实现接口
    }
    private void initListView(){
        listItems = new ArrayList<HashMap<String, String>>();
        for(int i = 0; i < 10; i++){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemTitle","Rate:"+i);//标题文字
            map.put("ItemDetail","detail："+i);//标题文字
            listItems.add(map);
        }
        //生成适配器的Item和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(this,listItems,//listItems数据源
                R.layout.list_item,//listItem的xml布局实现
                new String[]{"ItemTitle","ItemDetail"},//上下顺序要一一对应
                new int[]{R.id.itemTitle,R.id.itemDetail}
        );
    }

    public void run(){
        //获取网络数据，放入list带回到主线程中
        Log.i(TAG, "run...");
        List<HashMap<String,String>> retList = new ArrayList<HashMap<String, String>>();//把字符串转成hashmap
        Document doc = null;
        try {
            Thread.sleep(1);
            doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();
            Log.i(TAG, "run:" + doc.title());
            Elements tables = doc.getElementsByTag("table");

            Element table2 = tables.get(1);
            //获取TD中的数据
            Elements tds = table2.getElementsByTag("td");
            for (int i = 0; i < tds.size(); i += 8) {
                Element td1 = tds.get(i);
                Element td2 = tds.get(i + 5);

                String str1 = td1.text();
                String val = td2.text();

                Log.i(TAG, "run:" + str1 + "==>" + val);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ItemTitle", str1);
                map.put("ItemDetail", val);
                retList.add(map);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        Message msg = handler.obtainMessage(7);
        msg.obj = retList;
        handler.sendMessage(msg);//别忘了这句！
        Log.i("thread", "sendmag... ");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //通过map获得对象
        Log.i(TAG,"onItemClick:parent="+parent);
        Log.i(TAG, "onItemClick: view="+view);
        Log.i(TAG, "onItemClick: position="+position);
        Log.i(TAG, "onItemClick: id="+id);
        HashMap<String,String> map = (HashMap<String,String>) getListView().getItemAtPosition(position);//可以获得当前行的数据项了，是map
        String titleStr = map.get("ItemTitle");
        String detailStr = map.get("ItemDetail");
        Log.i(TAG, "onItemClick: titleStr="+titleStr);
        Log.i(TAG, "onItemClick: ItemDetail="+detailStr);

        //通过view获取数据
        TextView title = view.findViewById(R.id.itemTitle);
        TextView detail = view.findViewById(R.id.itemDetail);
        String title2 = String.valueOf(title.getText());
        String detail2 = String.valueOf(detail.getText());
        Log.i(TAG, "onItemClick: title2="+title2);
        Log.i(TAG, "onItemClick: detail2="+detail2);

        //打开新的页面，传入参数
        Intent rateCal = new Intent(this,RateCalActivity.class);
        rateCal.putExtra("title",titleStr);
        rateCal.putExtra("rate",Float.parseFloat(detailStr));
        startActivity(rateCal);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemLongClick: 长按列表项position=" + position);
        //删除操作
        //。。。
        return true;//如果改成true，就不会再执行短按事件；如果是false，在长按后仍会进行点击
    }
}
