package com.example.project.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.project.bus.BusStation;

import java.util.ArrayList;


public class SQLiteBusStation {
    // db명과 table명 설정
    static final String dbName="projectSQL.db";
    static final String tbName="BusStation";
    Context context;

    public SQLiteBusStation(Context context) {
        this.context = context;
    }

    //db 호출하기
    SQLiteDatabase getDatabase(){
        SQLiteDatabase db = context.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);

        //테이블이 존재하지 않으면 새로 생성
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tbName
                + " ( id Integer ,stationName text primary key ,lat real ,lng real);");

        return db;
    }

    //insert
    public boolean insert(BusStation bs){
        boolean res = false;
        SQLiteDatabase db = null;
        try {
            db = getDatabase();
            String sql = String.format("insert into %s(id,stationName,lat,lng) values('%d','%s','%s','%s')",
                    tbName, bs.getID(), bs.toName(), bs.getLatitude(), bs.getLongitude()
            );
            db.execSQL(sql);
            res = true;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(db!=null)
                db.close();
        }
        return res;
    }

    //delete
    public boolean delete(String stationName){
        boolean res = false;
        SQLiteDatabase db = null;
        try {
            db = getDatabase();
            String sql = String.format("delete from %s where stationName='%s'",tbName, stationName);
            db.execSQL(sql);
            res = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(db!=null)
                db.close();
        }

        return res;
    }

    public ArrayList<BusStation> selectBusList() {

        ArrayList<BusStation> list = new ArrayList<BusStation>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String [] col_names = {"id","stationName", "lat", "lng"};

        try {
            db = getDatabase();
            //                                                where
            cursor = db.query(tbName, col_names, null, null, null, null, null);
            if(cursor!=null){

                if(cursor.moveToFirst()){  //첫번째 레코드이동
                    //다음 레코드가 없을 때까지 while문 돌림
                    do{
                        int id     = cursor.getInt(0);
                        String name = cursor.getString(1);
                        list.add(new BusStation(id, name));

                    }while(cursor.moveToNext()); //다음레코드로 이동
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            if (db != null)
                db.close();
        }

        return list;
    }

    public BusStation selectByName(String key){
        // Name == PrimaryKey
        SQLiteDatabase db = null;
        BusStation bs = new BusStation();

        try {
            db = getDatabase();
            String sql = String.format("select * from BusStation where stationName = '%s';", key);
            Cursor cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            bs.setID(cursor.getInt(0));
            bs.setStationName(cursor.getString(1));
            bs.setLatitude(cursor.getFloat(2));
            bs.setLongitude(cursor.getFloat(3));
        }catch (Exception e){
            e.printStackTrace();
        }

        return bs;
    }
}

