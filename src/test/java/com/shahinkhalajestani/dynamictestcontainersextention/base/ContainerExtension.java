package com.shahinkhalajestani.dynamictestcontainersextention.base;

import com.shahinkhalajestani.dynamictestcontainersextention.util.ContainerType;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.testcontainers.containers.*;
import org.testcontainers.utility.DockerImageName;
import java.util.*;

public class ContainerExtension implements BeforeAllCallback, AfterAllCallback {
  private static final Namespace NAMESPACE = Namespace.create(ContainerExtension.class);

  private static class Started {
    MySQLContainer<?> mysql;
    MongoDBContainer mongo;
    GenericContainer<?> redis;
    RabbitMQContainer rabbit;
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    Class<?> testClass = context.getRequiredTestClass();
    UseContainers spec = testClass.getAnnotation(UseContainers.class);
    if (spec == null) return; // nothing to do

    boolean reuse = spec.reuse();

    Started started = new Started();

    Set<ContainerType> requested = EnumSet.noneOf(ContainerType.class);
    requested.addAll(Arrays.asList(spec.value()));

    if (requested.contains(ContainerType.MYSQL)) {
      started.mysql = new MySQLContainer<>(DockerImageName.parse(spec.mysqlImage()))
          .withDatabaseName("test")
          .withUsername("test")
          .withPassword("test");
      if (reuse) started.mysql.withReuse(true);
      started.mysql.start();

      // Spring DataSource properties
      System.setProperty("spring.datasource.url", started.mysql.getJdbcUrl());
      System.setProperty("spring.datasource.username", started.mysql.getUsername());
      System.setProperty("spring.datasource.password", started.mysql.getPassword());
      System.setProperty("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
      // Prevent Boot from swapping to an embedded test DB
      System.setProperty("spring.test.database.replace", "NONE");
    }

    if (requested.contains(ContainerType.MONGODB)) {
      started.mongo = new MongoDBContainer(DockerImageName.parse(spec.mongoImage()));
      if (reuse) started.mongo.withReuse(true);
      started.mongo.start();

      System.setProperty("spring.data.mongodb.uri", started.mongo.getConnectionString());
    }


    if (requested.contains(ContainerType.RABBITMQ)) {
      started.rabbit = new RabbitMQContainer(DockerImageName.parse(spec.rabbitImage()));
      if (reuse) started.rabbit.withReuse(true);
      started.rabbit.start();

      System.setProperty("spring.rabbitmq.host", started.rabbit.getHost());
      System.setProperty("spring.rabbitmq.port", String.valueOf(started.rabbit.getAmqpPort()));
      System.setProperty("spring.rabbitmq.username", started.rabbit.getAdminUsername());
      System.setProperty("spring.rabbitmq.password", started.rabbit.getAdminPassword());
    }

    // Keep reference so we can stop afterwards
    context.getStore(NAMESPACE).put(testClass.getName(), started);
  }

  @Override
  public void afterAll(ExtensionContext context) {
    Started started = context.getStore(NAMESPACE).remove(context.getRequiredTestClass().getName(), Started.class);
    if (started == null) return;

    // Stop only what was started
    Optional.ofNullable(started.rabbit).ifPresent(GenericContainer::stop);
    Optional.ofNullable(started.mongo).ifPresent(GenericContainer::stop);
    Optional.ofNullable(started.mysql).ifPresent(GenericContainer::stop);
  }

}
