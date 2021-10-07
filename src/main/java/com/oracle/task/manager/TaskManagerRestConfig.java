package com.oracle.task.manager;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


public class TaskManagerRestConfig extends Configuration {

    @NotNull
    @Valid
    @JsonProperty("database")
    private DataSourceFactory dataSourceFactory
            = new DataSourceFactory();


    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    public void setDatabase(DataSourceFactory dataSourceFactory){
        this.dataSourceFactory  = dataSourceFactory;
    }
}
