package com.baransekin.sqllikelibrary;

import java.util.List;

/**
 * Created by SE on 02.08.2017.
 */

public class Config {
    static List<Class> models;
    static String databaseName = "SqlLike";
    static int databaseVersion = 1;

    private Config() {
    }

    public static Config setInstance(SqlLikeBuilder sqlLikeBuilder){
        models = sqlLikeBuilder.getModels();
        databaseName = sqlLikeBuilder.getDatabaseName();
        databaseVersion = sqlLikeBuilder.getDatabaseVersion();
        return new Config();
    }
}
