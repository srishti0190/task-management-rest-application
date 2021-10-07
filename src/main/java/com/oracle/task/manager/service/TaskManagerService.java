package com.oracle.task.manager.service;

import com.oracle.task.manager.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskManagerService {
    List<Task> getTask(Optional<String> id);

    Task patchTask(Task task, String id);

    int deleteTask(String id);

    Task createTask(Task task);

}
