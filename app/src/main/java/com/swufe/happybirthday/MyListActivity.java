package com.swufe.happybirthday;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MyListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    List<String> data = new ArrayList<String>();
    String TAG = "MyList";
    ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        ListView listView = findViewById(R.id.mylist);//获取控件
        //init data
        for(int i = 0; i < 10; i++){
            data.add("item"+i);
        }


        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);//adapter对象只存在于列表中
        listView.setEmptyView(findViewById(R.id.nodata));//当列表没有数据时就显示textview这个控件
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> listv, View view, int position, long id) {
        Log.i(TAG, "onItemClick: position=" + position);
        Log.i(TAG, "onItemClick: parent=" + listv);
        adapter.remove(listv.getItemAtPosition(position));//移除这个数据
        //adapter.notifyDataSetChanged();//通知数据集被改变了，remove时会自动调用
    }
}
