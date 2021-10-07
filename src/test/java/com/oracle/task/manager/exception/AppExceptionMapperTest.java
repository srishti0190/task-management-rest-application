package com.oracle.task.manager.exception;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;


class AppExceptionMapperTest {

    private AppExceptionMapper mapper;

    @BeforeEach
    void setUp() {
        this.mapper = new AppExceptionMapper();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void toResponseWhenNotFound() {
        WebApplicationException exe = new WebApplicationException("Task Id Not found", Response.Status.NOT_FOUND);
        Response r = mapper.toResponse(exe);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), r.getStatus());
    }

    @Test
    void toResponseWhen500() {
        WebApplicationException exe = new WebApplicationException();
        Response r = mapper.toResponse(exe);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), r.getStatus());
    }

    @Test
    void toResponseWhenNoMessage() {
        WebApplicationException exe = new WebApplicationException(Response.Status.BAD_REQUEST);
        Response r = mapper.toResponse(exe);
        assertEquals(HttpStatus.getMessage(Response.Status.BAD_REQUEST.getStatusCode()), r.getStatusInfo().getReasonPhrase());
    }
}