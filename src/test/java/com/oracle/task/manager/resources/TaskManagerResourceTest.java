package com.oracle.task.manager.resources;

import com.oracle.task.manager.model.Task;
import com.oracle.task.manager.service.TaskManagerService;

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(DropwizardExtensionsSupport.class)
public class TaskManagerResourceTest {

    private static final TaskManagerService service = mock(TaskManagerService.class);

    private static final TaskManagerResource resource = new TaskManagerResource(service);

    private static final ResourceExtension EXT = ResourceExtension.builder()
            .addResource(resource)
            .build();

    @AfterEach
    void tearDown() {
        reset(service);
    }

    @Test
    public void testDeleteTaskUnsuccessful(){
        when(service.deleteTask(Mockito.anyString())).thenThrow(new WebApplicationException("",Response.Status.NOT_FOUND));
        Response response = EXT.target("/api/v1/tasks/1").request().delete();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatusInfo().getStatusCode());
        Assertions.assertThrows(WebApplicationException.class, () -> resource.deleteTask("1"));
    }

    @Test
    public void testDeleteTaskSuccessful(){
        when(service.deleteTask(Mockito.anyString())).thenReturn(1);
        Response response = EXT.target("/api/v1/tasks/2").request().delete();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusInfo().getStatusCode());
        assertEquals("Task deleted successfully.", response.readEntity(String.class));
        resource.deleteTask("2");
    }

    @Test
    public void testSaveTaskSuccessful(){
        Task t = createTask();

        when(service.createTask(t)).thenReturn(t);
        Response response = EXT.target("/api/v1/tasks")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(t, MediaType.APPLICATION_JSON_TYPE));
        assertEquals("random-uuid", response.readEntity(Task.class).getId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusInfo().getStatusCode());
    }

    @Test
    public void testSaveTaskBadRequest(){
        String json = "{\n" +
                "\"date\": \"2021-08-01\",\n" +
                "\"description\": \"Some description\",\n" +
                "\"isDone\": true\n" +
                "}";

        Response response = EXT.target("/api/v1/tasks")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(json, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatusInfo().getStatusCode());
    }

    @Test
    public void testSaveTaskNullDescription(){
        String json = "{\n" +
                "\"date\": \"2021-08-01\",\n" +
                "\"isDone\": true\n" +
                "}";

        Response response2 = EXT.target("/api/v1/tasks")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(json, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response2.getStatusInfo().getStatusCode());
    }

    @Test
    public void testPutTaskBadRequest(){
        String json = "{\n" +
                "\"date\": \"2021-08-01\",\n" +
                "\"description\": \"Some description\",\n" +
                "\"isDone\": true\n" +
                "}";

        Response r = EXT.target("/api/v1/tasks/random-uuid")
                .request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(json));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), r.getStatusInfo().getStatusCode());
    }

    @Test
    public void testPutTaskSuccessful(){
        Task t = createTask();

        when(service.patchTask(t, "random-uuid")).thenReturn(t);
        Response r = EXT.target("/api/v1/tasks/random-uuid")
                .request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(t,MediaType.APPLICATION_JSON_TYPE));

        verify(service).patchTask(t, "random-uuid");
        assertEquals(Response.Status.OK.getStatusCode(), r.getStatusInfo().getStatusCode());
    }

    @Test
    public void testPutTaskWrongId(){
        Task t = createTask();
        when(service.patchTask(t,"wrong-id")).thenThrow(new WebApplicationException("",Response.Status.NOT_FOUND));
        assertThrows(WebApplicationException.class, ()->resource.updateTask(t,"wrong-id"));
    }

    @Test
    public void testDeleteSuccessful(){
        Task t = createTask();
        when(service.deleteTask("random-uuid")).thenReturn(1);
        Response r = EXT.target("/api/v1/tasks/random-uuid")
                .request(MediaType.APPLICATION_JSON_TYPE).delete();

        assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
    }

    @Test
    public void testDeleteNotFound(){
        when(service.deleteTask("wrong-uuid")).thenThrow(WebApplicationException.class);
        assertThrows(WebApplicationException.class, () -> resource.deleteTask("wrong-uuid"));
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
