package com.shahinkhalajestani.dynamictestcontainersextention;

import org.springframework.boot.SpringApplication;

public class TestDynamicTestContainersExtentionApplication {

  public static void main(String[] args) {
    SpringApplication.from(DynamicTestContainersExtentionApplication::main)
        .with(TestcontainersConfiguration.class).run(args);
  }

}
