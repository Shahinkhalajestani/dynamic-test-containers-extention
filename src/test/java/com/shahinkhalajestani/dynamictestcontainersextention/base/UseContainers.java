package com.shahinkhalajestani.dynamictestcontainersextention.base;

import com.shahinkhalajestani.dynamictestcontainersextention.util.ContainerType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UseContainers {
  ContainerType[] value() default {};
  boolean reuse() default false;
  String mysqlImage() default "mysql:8.4";
  String mongoImage() default "mongo:7";
  String rabbitImage() default "rabbitmq:3.13-management";
}
