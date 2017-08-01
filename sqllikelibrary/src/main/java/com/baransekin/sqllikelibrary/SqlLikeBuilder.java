package com.baransekin.sqllikelibrary;

import java.util.List;

/**
 * Created by SE on 01.08.2017.
 */

public class SqlLikeBuilder {
    private List<Class> models;
    private String databaseName = "SqlLike";
    private int databaseVersion = 1;

    private SqlLikeBuilder() {
    }

    private SqlLikeBuilder(List<Class> models) {
        this.models = models;
    }

    public static SqlLikeBuilder with(List<Class> models){
        return new SqlLikeBuilder(models);
    }

    public SqlLikeBuilder setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        return this;
    }

    public SqlLikeBuilder setDatabaseVersion(int databaseVersion) {
        this.databaseVersion = databaseVersion;
        return this;
    }

    List<Class> getModels() {
        return models;
    }

    String getDatabaseName() {
        return databaseName;
    }

    int getDatabaseVersion() {
        return databaseVersion;
    }
}
