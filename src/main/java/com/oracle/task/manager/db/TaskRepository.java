package com.oracle.task.manager.db;

import com.oracle.task.manager.model.Task;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.config.RegisterRowMapperFactories;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

@RegisterBeanMapper(Task.class)
public interface TaskRepository {
    @SqlUpdate("create table task (id varchar(100) primary key, description varchar(200), date Date, isDone boolean)")
    void createTable();

    @SqlQuery("select * from task")
    List<Task> findAll();

    @SqlQuery("select * from task where id= :id")
    Optional<Task> findById(@Bind("id") String id);

    @SqlUpdate("insert into task (id, description, date, isDone) values (:id, :description, :date, :isDone)")
    int save(@BindBean Task task);

    @SqlUpdate("update task set description =:description, date = :date, isDone = :isDone where id =:id")
    int update(@Bind("id") String id, @BindBean Task task);

    @SqlUpdate("delete from task where id = :id ")
    int delete(@Bind("id") String id);
}
