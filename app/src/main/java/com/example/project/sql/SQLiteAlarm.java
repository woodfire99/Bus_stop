package com.example.project.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.project.alarm.Alarm;
import com.example.project.bus.BusStation;

import java.util.ArrayList;

public class SQLiteAlarm {
    static final String dbName="projectSQL.db";
    static final String tbName="Alarm";
    Context context;

    public SQLiteAlarm(Context context) {
        this.context = context;
    }

    //db 호출하기
    SQLiteDatabase getDatabase(){
        SQLiteDatabase db = context.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);

        //테이블이 존재하지 않으면 새로 생성
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tbName
                + " ( stationID Integer, busNO Integer );");

        return db;
    }

    //insert
    public boolean insert(int stationID, int busNO){
        boolean res = false;
        SQLiteDatabase db = null;
        try {
            db = getDatabase();
            String sql = String.format("insert into %s(stationID,busNO) values('%d','%d')",
                    tbName, stationID, busNO
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
    public boolean delete(int stationID, int busNO){
        boolean res = false;
        SQLiteDatabase db = null;
        try {
            db = getDatabase();
            String sql = String.format("delete from %s where stationID='%d' AND busNO ='%d'",tbName, stationID, busNO);
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

    public ArrayList<Alarm> select() {
        ArrayList<Alarm> list = new ArrayList<Alarm>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String [] col_names = {"stationID","busNO"};

        try {
            db = getDatabase();
            //                                                where
            cursor = db.query(tbName, col_names, null, null, null, null, null);
            if(cursor!=null){

                if(cursor.moveToFirst()){  //첫번째 레코드이동
                    //다음 레코드가 없을 때까지 while문 돌림
                    do{
                        int stationID     = cursor.getInt(0);
                        int busNO = cursor.getInt(1);
                        list.add(new Alarm(stationID, busNO));

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
}
