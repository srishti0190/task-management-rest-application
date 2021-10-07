package com.oracle.task.manager.service;

import com.oracle.task.manager.db.TaskRepository;
import com.oracle.task.manager.model.Task;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.WebApplicationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskManagerServiceImplTest {
    private static TaskRepository taskRepository = mock(TaskRepository.class);

    private static final TaskManagerService service = new TaskManagerServiceImpl(taskRepository);


    @AfterEach
    void tearDown() {
        reset(taskRepository);
    }

    @Test
    public void testSaveSuccessful(){
        final ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        Task task = createTask();
        when(taskRepository.save(anyObject())).thenReturn(1);
        service.createTask(task);
        verify(taskRepository).save(taskCaptor.capture());
        assertEquals(task.getDescription(), taskCaptor.getValue().getDescription());
        assertNotNull(taskCaptor.getValue().getId());
    }

    @Test
    public void testGetUnsuccessful(){
        final ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        assertThrows(WebApplicationException.class , () ->service.getTask(Optional.of("random-uuid")));
        verify(taskRepository).findById(stringCaptor.capture());
        assertNotNull(stringCaptor.getValue());
        assertEquals("random-uuid", stringCaptor.getValue());
    }

    @Test
    public void testGetSuccessful(){
        Task task = createTask();
        final ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        when(taskRepository.save(anyObject())).thenReturn(1);
        when(taskRepository.findById(anyString())).thenReturn(Optional.of(task));

        Task a = service.createTask(task);
        Task b = service.getTask(Optional.of(task.getId())).get(0);
        assertEquals(a,b);

        verify(taskRepository).findById(stringCaptor.capture());
        assertNotNull(stringCaptor.getValue());
        assertEquals(task.getId(), stringCaptor.getValue());
    }

    @Test
    public void testGetAllSuccessful(){
        Task task = createTask();
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        when(taskRepository.save(anyObject())).thenReturn(1);
        when(taskRepository.findAll()).thenReturn(tasks);

        Task a = service.createTask(task);
        Task b = service.getTask(Optional.empty()).get(0);
        assertEquals(a,b);
    }

    @Test
    public void testGetAllEmptySuccessful(){
        List<Task> tasks = new ArrayList<>();

        when(taskRepository.findAll()).thenReturn(tasks);
        assertEquals(0,service.getTask(Optional.empty()).size());
    }

    @Test
    public void testPatchSuccessful(){
        Task task = createTask();
        when(taskRepository.save(anyObject())).thenReturn(1);
        when(taskRepository.update(anyString(),anyObject())).thenReturn(1);
        when(taskRepository.findById(anyString())).thenReturn(Optional.of(task));

        task = service.createTask(task);

        Task b = createTask();
        b.setIsDone(true);

        b = service.patchTask(task, task.getId());
        assertEquals(task.getId(),b.getId());
        assertEquals(task.getIsDone(), b.getIsDone());
    }

    @Test
    public void testPatchBadRequest(){
        final Task task = createTask();

        when(taskRepository.save(anyObject())).thenReturn(1);
        when(taskRepository.findById(anyString())).thenReturn(Optional.empty());

        service.createTask(task);

        Task b = createTask();
        b.setIsDone(true);

        assertThrows(WebApplicationException.class, () -> {
            service.patchTask(task, task.getId());
        });
    }

    @Test
    public void testDeleteSuccessful(){
        final Task task = createTask();
        final ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);

        when(taskRepository.save(anyObject())).thenReturn(1);
        when(taskRepository.findById(anyString())).thenReturn(Optional.of(task));
        when(taskRepository.delete(anyString())).thenReturn(1);

        service.createTask(task);
        service.deleteTask(task.getId());

        verify(taskRepository).delete(stringCaptor.capture());
        assertEquals(task.getId(), stringCaptor.getValue());

    }

    @Test
    public void testDeleteBadRequest(){
        final Task task = createTask();

        when(taskRepository.save(anyObject())).thenReturn(1);
        when(taskRepository.findById(anyString())).thenReturn(Optional.empty());
        when(taskRepository.delete(anyString())).thenReturn(1);

        service.createTask(task);
        assertThrows(WebApplicationException.class,() -> service.deleteTask(task.getId()));

        verify(taskRepository, times(0)).delete(task.getId());
        verify(taskRepository, times(0)).delete("");
    }

    private Task createTask() {
        Task t = new Task();
        t.setId("random-uuid");
        t.setDate(LocalDateTime.of(2014, 1, 1, 10, 10, 30));
        t.setDescription("Test description");
        t.setIsDone(false);
        return t;
    }
}
