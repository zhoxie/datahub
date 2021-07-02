package com.linkedin.gms.factory.datasource;

import com.linkedin.metadata.dao.Neo4jQueryDAO;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.Nonnull;

@Configuration
public class DatasourceQueryDaoFactory {
  @Autowired
  ApplicationContext applicationContext;

  @Nonnull
  @DependsOn({"neo4jDriver"})
  @Bean(name = "datasourceQueryDao")
  protected Neo4jQueryDAO createInstance() {
    return new Neo4jQueryDAO(applicationContext.getBean(Driver.class));
  }
}