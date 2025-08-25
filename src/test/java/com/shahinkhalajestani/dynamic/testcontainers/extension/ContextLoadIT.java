package com.shahinkhalajestani.dynamic.testcontainers.extension;

import com.shahinkhalajestani.dynamic.testcontainers.extension.base.ContainerExtension;
import com.shahinkhalajestani.dynamic.testcontainers.extension.base.UseContainers;
import com.shahinkhalajestani.dynamic.testcontainers.extension.util.ContainerType;
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@UseContainers(ContainerType.MYSQL)
@ExtendWith(ContainerExtension.class)
@SpringBootTest
public class ContextLoadIT {

  @Autowired
  private Environment env;

  @Autowired
  private DataSource dataSource;

  @Test
  void contextLoads() {
    System.out.println("com.shahinkhalajestani.dynamictestcontainersextention.ContextLoadIT.contextLoads");


    String url = env.getProperty("spring.datasource.url");
    String username = env.getProperty("spring.datasource.username");

    // sanity checks
    assert url != null && url.contains("jdbc:mysql://");
    assert "test".equals(username);
    Assertions.assertNotNull(dataSource);
  }


}
