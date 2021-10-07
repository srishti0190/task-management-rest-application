package com.oracle.task.manager;

import com.oracle.task.manager.db.TaskRepository;
import com.oracle.task.manager.resources.TaskManagerResource;
import com.oracle.task.manager.service.TaskManagerService;
import com.oracle.task.manager.service.TaskManagerServiceImpl;
import io.dropwizard.Application;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.jdbi.v3.core.Jdbi;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

public class TaskManagerApplication extends Application<TaskManagerRestConfig> {

    public static void main(final String[] args) throws Exception {
        new TaskManagerApplication().run(args);
    }

    @Override
    public String getName() {
        return "task-manager-rest";
    }

    @Override
    public void initialize(final Bootstrap<TaskManagerRestConfig> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final TaskManagerRestConfig configuration,
                    final Environment environment) {
        // Enable CORS headers
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD,PATCH");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, configuration.getDataSourceFactory(), "mysql");
        final TaskRepository dao = jdbi.onDemand(TaskRepository.class);

        TaskManagerService taskManagerServiceImpl = new TaskManagerServiceImpl(dao);

        TaskManagerResource resource = new TaskManagerResource(taskManagerServiceImpl);

        dao.createTable();
        environment.jersey().register(resource);
    }

}
