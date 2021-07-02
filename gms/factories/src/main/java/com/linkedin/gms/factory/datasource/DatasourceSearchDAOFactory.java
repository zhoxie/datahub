package com.linkedin.gms.factory.datasource;

import com.linkedin.gms.factory.common.IndexConventionFactory;
import com.linkedin.metadata.configs.DatasourceSearchConfig;
import com.linkedin.metadata.dao.search.ESSearchDAO;
import com.linkedin.metadata.search.DatasourceDocument;
import com.linkedin.metadata.utils.elasticsearch.IndexConvention;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.Nonnull;


@Configuration
public class DatasourceSearchDAOFactory {
  @Autowired
  ApplicationContext applicationContext;

  @Nonnull
  @DependsOn({"elasticSearchRestHighLevelClient", IndexConventionFactory.INDEX_CONVENTION_BEAN})
  @Bean(name = "datasourceSearchDao")
  protected ESSearchDAO createInstance() {
    return new ESSearchDAO(applicationContext.getBean(RestHighLevelClient.class), DatasourceDocument.class,
        new DatasourceSearchConfig(applicationContext.getBean(IndexConvention.class)));
  }
}