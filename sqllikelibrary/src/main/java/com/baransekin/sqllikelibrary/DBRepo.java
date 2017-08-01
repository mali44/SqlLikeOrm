package com.baransekin.sqllikelibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class DBRepo<E> {
    private SqlLikeOrmHelper dbHelper;
    private Class modelClass;
    static String TAG = DBRepo.class.getSimpleName();
    public DBRepo(Context context, Class<E> modelClass) {
        dbHelper = new SqlLikeOrmHelper(context);
        this.modelClass = modelClass;
    }
    public int insert(E model){

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Field[] classFields = modelClass.getFields();
        for(Field classField:classFields){
            if(classField.getName().equals("$change") || classField.getName().equals("serialVersionUID")) {
                Log.i(TAG, "Skipped: "+classField.getName());
                continue;
            }
            try {
                if(modelClass.getField(classField.getName()).get(model) == null)
                    continue;
                String value = modelClass.getField(classField.getName()).get(model).toString();
                PrimaryKey primaryKey = classField.getAnnotation(PrimaryKey.class);
                if( (primaryKey !=null) && Integer.parseInt(value)==0)
                    continue;
                values.put(classField.getName(), value);
            } catch (IllegalAccessException e) {
                //e.printStackTrace();
            } catch (NoSuchFieldException e) {
                //e.printStackTrace();
            } catch (NullPointerException e){
                //e.printStackTrace();
            }
        }
        long favId = db.insert(modelClass.getSimpleName(), null, values);
        db.close();
        return (int) favId;
    }
    public void delete(int id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String primaryKey=null;
        Field[] classFields = modelClass.getFields();
        for(Field classField:classFields){
            PrimaryKey order = classField.getAnnotation(PrimaryKey.class);
            if(order !=null)
                primaryKey = classField.getName();
        }
        if(primaryKey!=null)
            db.delete(modelClass.getSimpleName(), primaryKey + "=?", new String[] {String.valueOf(id)});
        db.close();
    }
    public List<E> getList(){
        return query(null);
    }
    public List<E> get(String where){
        return query(where);
    }
    private List<E> query(String where){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery;
        if(where==null)
            selectQuery = "SELECT * FROM " + modelClass.getSimpleName();
        else
            selectQuery = "SELECT * FROM " + modelClass.getSimpleName() + " WHERE " + where;
        final List<E> modelList=new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Field[] modelFields = modelClass.getFields();
        if(cursor.moveToFirst()){
            do{
                Object data = null;
                try {
                    data = modelClass.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                for(Field modelField:modelFields){
                    if(modelField.getName().equals("$change") || modelField.getName().equals("serialVersionUID"))
                        continue;
                    try {
                        if(modelField.getType().getSimpleName().equals("String"))
                            modelClass.getField(modelField.getName()).set(data, cursor.getString(cursor.getColumnIndex(modelField.getName())));
                        else if(modelField.getType().getSimpleName().equals("int"))
                            modelClass.getField(modelField.getName()).set(data, cursor.getInt(cursor.getColumnIndex(modelField.getName())));
                        else if(modelField.getType().getSimpleName().equals("Date")) {
                            DateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
                            Date date = format.parse(cursor.getString(cursor.getColumnIndex(modelField.getName())));
                            modelClass.getField(modelField.getName()).set(data, date);

                        }else if(modelField.getType().getSimpleName().equals("boolean")) {
                            if(cursor.getString(cursor.getColumnIndex(modelField.getName())).equals("true")){
                                modelClass.getField(modelField.getName()).set(data, true);
                            }else{
                                modelClass.getField(modelField.getName()).set(data, false);
                            }
                        }else if(modelField.getType().getSimpleName().equals("long")) {
                            modelClass.getField(modelField.getName()).set(data, cursor.getLong(cursor.getColumnIndex(modelField.getName())));
                        }else if(modelField.getType().getSimpleName().equals("double")) {
                            modelClass.getField(modelField.getName()).set(data, cursor.getDouble(cursor.getColumnIndex(modelField.getName())));
                        }else{
                            Log.i(TAG,"Invalid Type: " + modelField.getType().getSimpleName());
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
                modelList.add((E)data);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return modelList;
    }
    public boolean has(int id){
        String primaryKey=null;
        Field[] classFields = modelClass.getFields();
        for(Field classField:classFields){
            PrimaryKey order = classField.getAnnotation(PrimaryKey.class);
            if(order !=null)
                primaryKey = classField.getName();
        }
        if(primaryKey==null)
            throw new NullPointerException("Model class doesnt have primary key");

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + modelClass.getSimpleName()+" WHERE "+primaryKey+"=?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});
        if(cursor.moveToFirst()){
            cursor.close();
            db.close();
            return true;
        }else{
            cursor.close();
            db.close();
            return false;
        }
    }
}
