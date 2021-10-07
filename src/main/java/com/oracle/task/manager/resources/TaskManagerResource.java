package com.oracle.task.manager.resources;

import com.oracle.task.manager.model.Task;
import com.oracle.task.manager.service.TaskManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/api/v1/tasks")
@Produces(MediaType.APPLICATION_JSON)
public class TaskManagerResource {
    private final TaskManagerService taskManagerServiceImpl;
    private static final Logger LOG = LoggerFactory.getLogger(TaskManagerResource.class);

    public TaskManagerResource(TaskManagerService taskManagerServiceImpl){
        this.taskManagerServiceImpl = taskManagerServiceImpl;
    }

    /**
     * Get a Task by Id.
     * @param id: Id of the task to be fetched.
     * @return javax.ws.rs.core.Response with Task with Id in entity.
     */
    @GET
    @Path("{id}")
    public Response getTask(@NotNull(message = "Task id can not be null.") @PathParam("id") String id) {
         return Response.ok().entity(taskManagerServiceImpl.getTask(Optional.of(id)).get(0)).build();
    }

    /**
     * Get all the Tasks in the database.
     * @return javax.ws.rs.core.Response with all Tasks in entity.
     */
    @GET
    public Response getTask() {
        return Response.ok().entity(taskManagerServiceImpl.getTask(Optional.empty())).build();
    }

    /**
     * Creates a Task and add it to database.
     * @param task: Task object.
     * @return javax.ws.rs.core.Response with newly created Task in entity.
     */
    @POST
    public Response createTask(@NotNull @Valid Task task){
        LOG.info("Create Task {}", task);
        return Response.ok().entity(taskManagerServiceImpl.createTask(task)).build();
    }

    /**
     * Deletes a Task by Id.
     * @param id: id of the Task to be deleted.
     * @return javax.ws.rs.core.Response with success message in entity.
     */
    @Path("{id}")
    @DELETE
    public Response deleteTask(@NotNull(message = "Task id can not be null.") @PathParam("id") String id){
        taskManagerServiceImpl.deleteTask(id);
        System.out.println("Called  delete");
        return Response
                .status(Response.Status.OK).entity("Task deleted successfully.")
                .build();
    }

    /**
     * Updates a Task by Id.
     * @param task: Task object with updated fields to be updated in Database.
     * @param id: id of the Task to be updated.
     * @return javax.ws.rs.core.Response with updated Task in entity.
     */
    @Path("{id}")
    @PUT
    public Response updateTask(@NotNull @Valid Task task, @NotNull(message = "Task id can not be null.") @PathParam("id") String id){
        return Response
                .ok(Response.Status.OK).entity(taskManagerServiceImpl.patchTask(task, id))
                .build();
    }
}