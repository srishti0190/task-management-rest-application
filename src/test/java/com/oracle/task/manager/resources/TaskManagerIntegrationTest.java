package com.oracle.task.manager.resources;

import com.oracle.task.manager.TaskManagerApplication;
import com.oracle.task.manager.TaskManagerRestConfig;
import com.oracle.task.manager.model.Task;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(DropwizardExtensionsSupport.class)
class TaskManagerIntegrationTest {

    private static final DropwizardAppExtension<TaskManagerRestConfig> EXT = new DropwizardAppExtension<>(
            TaskManagerApplication.class, ResourceHelpers.resourceFilePath("config_for_test.yml")
    );

    private static final String TASK_ID = "random-uuid";
    private static final String TASK_DESCRIPTION = "some description";

    @Test
    void createTaskSuccessful() {
        Client client = EXT.client();

        Response response = client.target(
                String.format("http://localhost:%d/api/v1/tasks", EXT.getLocalPort()))
                .request()
                .post(Entity.json(createTask()));

        assertEquals(TASK_DESCRIPTION, response.readEntity(Task.class).getDescription());
        assertEquals(200, response.getStatus());
    }

    @Test
    void createTaskFailureNullInput() {
        Client client = EXT.client();

        Response response = client.target(
                String.format("http://localhost:%d/api/v1/tasks", EXT.getLocalPort()))
                .request()
                .post(Entity.json(null));

        assertEquals(422, response.getStatus());
    }

    @Test
    void createTaskFailureBadRequest() {
        Client client = EXT.client();
        String jsonRequest = "{\n" +
                "\"date\": \"2021-08-01\",\n" +
                "\"description\": \"Some description\",\n" +
                "\"isDone\": true\n" +
                "}";
        Response response = client.target(
                String.format("http://localhost:%d/api/v1/tasks", EXT.getLocalPort()))
                .request()
                .post(Entity.json(jsonRequest));

        assertEquals(400, response.getStatus());
    }

    @Test
    void createTaskFailureNullDescription() {
        Client client = EXT.client();
        String jsonRequest = "{\n" +
                "\"date\": \"2021-08-01 12:00:00\",\n" +
                "\"isDone\": true\n" +
                "}";
        Response response = client.target(
                String.format("http://localhost:%d/api/v1/tasks", EXT.getLocalPort()))
                .request()
                .post(Entity.json(jsonRequest));

        assertEquals(422, response.getStatus());
    }

    @Test
    void updateTaskSuccessful() {
        Client client = EXT.client();
        String createRequest = "{\n" +
                "\"date\": \"2021-08-01 12:00:00\",\n" +
                "\"description\": \"Some description\",\n" +
                "\"isDone\": false\n" +
                "}";

        String patchRequest = "{\n" +
                "\"date\": \"2021-08-01 12:00:00\",\n" +
                "\"description\": \"Some description\",\n" +
                "\"isDone\": true\n" +
                "}";

        Response response = client.target(
                String.format("http://localhost:%d/api/v1/tasks", EXT.getLocalPort()))
                .request()
                .post(Entity.json(createRequest));

        String id = response.readEntity(Task.class).getId();

        Response patchResponse = client.target(
                String.format("http://localhost:%d/api/v1/tasks/"+id, EXT.getLocalPort()))
                .request()
                .put(Entity.entity(patchRequest, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(200, response.getStatus());
        assertEquals(200, patchResponse.getStatus());
        Task task = patchResponse.readEntity(Task.class);
        assertEquals(id,task.getId());
        assertTrue(task.getIsDone());
    }

    @Test
    void updateTaskNotFound() {
        Client client = EXT.client();

        String patchRequest = "{\n" +
                "\"date\": \"2021-08-01 12:00:00\",\n" +
                "\"description\": \"Some description\",\n" +
                "\"isDone\": true\n" +
                "}";

        Response patchResponse = client.target(
                String.format("http://localhost:%d/api/v1/tasks/some-uuid", EXT.getLocalPort()))
                .request().put(Entity.json(patchRequest));

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), patchResponse.getStatus());
    }

    @Test
    void updateTaskBadRequest() {
        Client client = EXT.client();
        String createRequest = "{\n" +
                "\"date\": \"2021-08-01 12:00:00\",\n" +
                "\"description\": \"Some description\",\n" +
                "\"isDone\": false\n" +
                "}";

        String patchRequest = "{\n" +
                "\"date\": \"2021-08-01\",\n" +
                "\"description\": \"Some description\",\n" +
                "\"isDone\": true\n" +
                "}";

        Response response = client.target(
                String.format("http://localhost:%d/api/v1/tasks", EXT.getLocalPort()))
                .request()
                .post(Entity.json(createRequest));

        String id = response.readEntity(Task.class).getId();

        Response patchResponse = client.target(
                String.format("http://localhost:%d/api/v1/tasks/"+id, EXT.getLocalPort()))
                .request()
                .put(Entity.json(patchRequest));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), patchResponse.getStatus());
    }

    @Test
    void getTaskSuccessful() {
        Client client = EXT.client();
        String createRequest = "{\n" +
                "\"date\": \"2021-08-01 12:00:00\",\n" +
                "\"description\": \"Some description\",\n" +
                "\"isDone\": false\n" +
                "}";

        client.target(
                String.format("http://localhost:%d/api/v1/tasks", EXT.getLocalPort()))
                .request()
                .post(Entity.json(createRequest));

        Response response = client.target(
                String.format("http://localhost:%d/api/v1/tasks", EXT.getLocalPort()))
                .request()
                .get();

        assertEquals(200, response.getStatus());
        assertTrue(response.readEntity(List.class).size()>0);
    }

    @Test
    void getTaskByIdSuccessful() {
        Client client = EXT.client();
        String createRequest = "{\n" +
                "\"date\": \"2021-08-01 12:00:00\",\n" +
                "\"description\": \"Some description\",\n" +
                "\"isDone\": false\n" +
                "}";

        Response createResponse = client.target(
                String.format("http://localhost:%d/api/v1/tasks", EXT.getLocalPort()))
                .request()
                .post(Entity.json(createRequest));

        String id = createResponse.readEntity(Task.class).getId();

        Response response = client.target(
                String.format("http://localhost:%d/api/v1/tasks/"+id, EXT.getLocalPort()))
                .request()
                .get();

        assertEquals(200, response.getStatus());
        assertEquals(id, response.readEntity(Task.class).getId());
    }


    @Test
    void getTaskByIdNotFound() {
        Client client = EXT.client();

        Response response = client.target(
                String.format("http://localhost:%d/api/v1/tasks/wrong-id", EXT.getLocalPort()))
                .request()
                .get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void deleteTaskByIdSuccessful() {
        Client client = EXT.client();
        String createRequest = "{\n" +
                "\"date\": \"2021-08-01 12:00:00\",\n" +
                "\"description\": \"Some description\",\n" +
                "\"isDone\": false\n" +
                "}";

        Response createResponse = client.target(
                String.format("http://localhost:%d/api/v1/tasks", EXT.getLocalPort()))
                .request()
                .post(Entity.json(createRequest));

        String id = createResponse.readEntity(Task.class).getId();

        Response response = client.target(
                String.format("http://localhost:%d/api/v1/tasks/"+id, EXT.getLocalPort()))
                .request()
                .delete();

        assertEquals(200, response.getStatus());

        Response getResponse = client.target(
                String.format("http://localhost:%d/api/v1/tasks/"+id, EXT.getLocalPort()))
                .request()
                .get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), getResponse.getStatus());
    }

    @Test
    void deleteTaskByIdNotFound() {
        Client client = EXT.client();

        Response response = client.target(
                String.format("http://localhost:%d/api/v1/tasks/wrong-id", EXT.getLocalPort()))
                .request()
                .delete();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    private Task createTask() {
        Task t = new Task();
        t.setDate(LocalDateTime.of(2014, 1, 1, 10, 10, 30));
        t.setDescription(TASK_DESCRIPTION);
        t.setIsDone(false);
        return t;
    }
}