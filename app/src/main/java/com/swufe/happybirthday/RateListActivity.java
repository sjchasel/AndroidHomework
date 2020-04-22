package com.swufe.happybirthday;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

public class RateListActivity extends ListActivity {
    String data[] = {"one","two","three"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //父类里已经包含了页面布局，因此这句要注释掉
        //setContentView(R.layout.activity_rate_list);

        //第一个参数为当前对象，第二个对象为布局(此时的R是Android提供的R，使用Android平台提供的资源)，第三个参数为数据
        ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        setListAdapter(adapter);//父类中的方法，表示当前界面用adapter管理
    }
}
