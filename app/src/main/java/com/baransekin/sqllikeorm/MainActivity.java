package com.baransekin.sqllikeorm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.baransekin.sqllikelibrary.Config;
import com.baransekin.sqllikelibrary.DBRepo;
import com.baransekin.sqllikelibrary.SqlLikeBuilder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Class> modelList = new ArrayList<>();
        modelList.add(Todo.class);
        Config.setInstance(SqlLikeBuilder.with(modelList).setDatabaseName("main.db").setDatabaseVersion(1));

        DBRepo<Todo> todoDBRepo = new DBRepo<>(this, Todo.class);
        Todo newItem = new Todo();
        newItem.title = "Example";
        newItem.content = "Example todo item";
        todoDBRepo.insert(newItem);

        List<Todo> todoList = todoDBRepo.getList();

        for(Todo todo:todoList){
            Log.d(TAG, todo.title);
        }
    }
}
