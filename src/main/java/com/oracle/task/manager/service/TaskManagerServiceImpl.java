package com.oracle.task.manager.service;

import com.oracle.task.manager.db.TaskRepository;
import com.oracle.task.manager.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TaskManagerServiceImpl implements TaskManagerService{

    private final TaskRepository taskRepository;

    private static final String TASK_NOT_FOUND= "Task with id %s not found";

    private static final Logger LOG = LoggerFactory.getLogger(TaskManagerServiceImpl.class);

    public TaskManagerServiceImpl(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task){
        task.setId(UUID.randomUUID().toString());
        int i = taskRepository.save(task);
        if(i==0){
            throw new WebApplicationException(String.format("Error in creating task", Response.Status.INTERNAL_SERVER_ERROR));
        }
        LOG.info("Saved to Repository {}, {}", task, i);
        return task;
    }

    public int deleteTask(String id){
        if(taskRepository.findById(id).isPresent()){
            return taskRepository.delete(id);
        }else{
            throw new WebApplicationException(String.format(TASK_NOT_FOUND, id),  Response.Status.NOT_FOUND);
        }
    }

    public Task patchTask(Task task, String id){
        if(taskRepository.findById(id).isPresent()){
            task.setId(id);
            taskRepository.update(id, task);
            return task;
        }
        throw new WebApplicationException(String.format(TASK_NOT_FOUND, id), Response.Status.NOT_FOUND);
    }

    public List<Task> getTask(Optional<String> id){
        List<Task> tasks = new ArrayList<>();
        if(id.isPresent()){
           Optional<Task> task = taskRepository.findById(id.get());
           if(task.isPresent()){
               Task t = task.get();
               tasks.add(t);
               return tasks;
           }
           throw new WebApplicationException(String.format(TASK_NOT_FOUND, id.get()), Response.Status.NOT_FOUND);
        }
        return taskRepository.findAll();
    }
}
