package com.baransekin.sqllikeorm;

import com.baransekin.sqllikelibrary.PrimaryKey;

/**
 * Created by SE on 02.08.2017.
 */

public class Todo {
    @PrimaryKey
    public int id;
    public String title;
    public String content;
}
