package com.swufe.happybirthday;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class RateManager {//对数据库进行管理，向外界提供方法
    private DBHelper dbHelper;
    private String TBNAME;

    public RateManager(Context context) {
        dbHelper = new DBHelper(context);
        TBNAME = DBHelper.TB_NAME;
    }

    public void add(RateItem item){
        SQLiteDatabase db = dbHelper.getWritableDatabase();//获得数据库对象
        ContentValues values = new ContentValues();//构造对象
        values.put("curname", item.getCurName());//把数据放入类
        values.put("currate", item.getCurRate());//把数据放入类
        db.insert(TBNAME, null, values);//把values作为数据传入
        db.close();
    }

    public void deleteAll(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TBNAME,null,null);
        db.close();
    }

    public void addAll(List<RateItem> list){//添加所有数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (RateItem item : list) {
            ContentValues values = new ContentValues();
            values.put("curname", item.getCurName());
            values.put("currate", item.getCurRate());
            db.insert(TBNAME, null, values);
        }
        db.close();
    }

    public List<RateItem> listAll(){//显示所有数据
        List<RateItem> rateList = null;//用列表返回一个二维的数据
        SQLiteDatabase db = dbHelper.getReadableDatabase();//获得数据库的访问，只读数据库
        //db.query是数据库查询
        //游标第一次打开时是停留在第一行数据之前，即标题行
        Cursor cursor = db.query(TBNAME, null, null, null, null, null, null);
        if(cursor!=null){
            rateList = new ArrayList<RateItem>();//对rateList进行实例化
            while(cursor.moveToNext()){//从游标里取数据；把游标移到下一行
                RateItem item = new RateItem();
                item.setId(cursor.getInt(cursor.getColumnIndex("ID")));
                item.setCurName(cursor.getString(cursor.getColumnIndex("CURNAME")));
                item.setCurRate(cursor.getString(cursor.getColumnIndex("CURRATE")));

                rateList.add(item);//把当前对象添加到列表
            }
            cursor.close();
        }
        db.close();//关闭数据库
        return rateList;//返回列表对象

    }

    public void delete(int id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TBNAME, "ID=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void update(RateItem item){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("curname", item.getCurName());
        values.put("currate", item.getCurRate());
        db.update(TBNAME, values, "ID=?", new String[]{String.valueOf(item.getId())});
        db.close();
    }
    public RateItem findById(int id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TBNAME, null, "ID=?", new String[]{String.valueOf(id)}, null, null, null);
        RateItem rateItem = null;
        if(cursor!=null && cursor.moveToFirst()){
            rateItem = new RateItem();
            rateItem.setId(cursor.getInt(cursor.getColumnIndex("ID")));
            rateItem.setCurName(cursor.getString(cursor.getColumnIndex("CURNAME")));
            rateItem.setCurRate(cursor.getString(cursor.getColumnIndex("CURRATE")));
            cursor.close();
        }
        db.close();
        return rateItem;
    }

}
