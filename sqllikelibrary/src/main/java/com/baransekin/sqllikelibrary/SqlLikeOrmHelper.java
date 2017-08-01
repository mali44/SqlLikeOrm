package com.baransekin.sqllikelibrary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by SE on 27.05.2017.
 */
class SqlLikeOrmHelper extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    private List<Class> models;
    SqlLikeOrmHelper(Context context) {
        super(context, Config.databaseName, null, Config.databaseVersion);
        models = Config.models;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        for(Class aClass: models)
            db.execSQL(getClassTable(aClass));
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed, all data will be gone!!!
        for(Class aClass: models)
            db.execSQL("DROP TABLE IF EXISTS " + aClass.getSimpleName());
        onCreate(db);
    }
    public void deleteAll(){
        for(Class aClass: models)
            this.getWritableDatabase().execSQL("delete from "+ aClass.getSimpleName());
    }
    private String getClassTable(Class classModel){
        String CREATE_TABLE = "";
        Field[] classFields = classModel.getFields();
        CREATE_TABLE += "CREATE TABLE " + classModel.getSimpleName() + "(";
        int i=0;
        for(Field classField:classFields){
            if(classField.getName().equals("$change") || classField.getName().equals("serialVersionUID"))
                continue;
            PrimaryKey primaryKey = classField.getAnnotation(PrimaryKey.class);
            if(primaryKey !=null)
                CREATE_TABLE += classField.getName()  + " INTEGER PRIMARY KEY AUTOINCREMENT";
            else
                CREATE_TABLE += classField.getName()  + " TEXT";
            CREATE_TABLE += ",";
            i++;
        }
        CREATE_TABLE += ")";
        CREATE_TABLE = CREATE_TABLE.replace(",)", ")");
        return CREATE_TABLE;
    }
}
