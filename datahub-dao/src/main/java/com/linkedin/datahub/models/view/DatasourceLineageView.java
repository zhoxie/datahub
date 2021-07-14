package com.linkedin.datahub.models.view;

import lombok.Data;


@Data
public class DatasourceLineageView {

  private DatasourceView datasource;

  private String type;

  private String actor;

  private String modified;
}