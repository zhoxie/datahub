package com.linkedin.gms.factory.datasource;

import com.linkedin.common.urn.DatasourceUrn;
import com.linkedin.gms.factory.common.LocalDAOStorageConfigFactory;
import com.linkedin.gms.factory.common.TopicConventionFactory;
import com.linkedin.metadata.aspect.DatasourceAspect;
import com.linkedin.metadata.dao.BaseLocalDAO;
import com.linkedin.metadata.dao.EbeanLocalDAO;
import com.linkedin.metadata.dao.producer.KafkaMetadataEventProducer;
import com.linkedin.metadata.dao.producer.KafkaProducerCallback;
import com.linkedin.metadata.snapshot.DatasourceSnapshot;
import com.linkedin.metadata.urn.datasource.DatasourceUrnPathExtractor;
import com.linkedin.mxe.TopicConvention;
import io.ebean.config.ServerConfig;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;


@Configuration
public class DatasourceDaoFactory {

  @Value("${DATASET_ENABLE_SCSI:false}")
  private boolean enableSCSI;

  @Autowired
  ApplicationContext applicationContext;

  @Bean(name = "datasourceDao")
  @DependsOn({"gmsEbeanServiceConfig", "kafkaEventProducer", TopicConventionFactory.TOPIC_CONVENTION_BEAN})
  protected BaseLocalDAO<DatasourceAspect, DatasourceUrn> createInstance() {
    KafkaMetadataEventProducer<DatasourceSnapshot, DatasourceAspect, DatasourceUrn> producer =
        new KafkaMetadataEventProducer(DatasourceSnapshot.class, DatasourceAspect.class,
            applicationContext.getBean(Producer.class), applicationContext.getBean(TopicConvention.class),
            new KafkaProducerCallback());

    final EbeanLocalDAO<DatasourceAspect, DatasourceUrn> dao =
        new EbeanLocalDAO<>(producer, applicationContext.getBean(ServerConfig.class),
            LocalDAOStorageConfigFactory.getStorageConfig(DatasourceAspect.class, DatasourceDaoFactory.class,
                "datasourceStorageConfig.json"), DatasourceUrn.class);
    dao.setUrnPathExtractor(new DatasourceUrnPathExtractor());
    dao.enableLocalSecondaryIndex(enableSCSI);
    return dao;
  }
}
