package com.linkedin.datahub.util;

import com.linkedin.common.AuditStamp;
import com.linkedin.common.urn.DatasourceUrn;
import com.linkedin.datahub.models.view.DatasourceView;
import com.linkedin.datahub.models.view.DatasourceLineageView;
import com.linkedin.datasource.Datasource;

import javax.annotation.Nonnull;
import java.net.URISyntaxException;

import static com.linkedin.datahub.util.UrnUtil.splitWhUrn;


public class DatasourceUtil {
  private DatasourceUtil() {

  }

  /**
   * Convert WhereHows datasource URN to DatasourceUrn, set datasource origin as PROD.
   * @param urn String WH datasource URN
   * @return DatasourceUrn
   */
  public static DatasourceUrn toDatasourceUrnFromWhUrn(@Nonnull String urn) {
    String[] urnParts = splitWhUrn(urn);
    return com.linkedin.common.urn.UrnUtils.toDatasourceUrn(urnParts[0], urnParts[1], "PROD");
  }

  /**
   * Check input string to determine if WH URN or TMS URN, then convert to DatasourceUrn
   */
  public static DatasourceUrn toDatasourceUrn(@Nonnull String datasourceUrn) throws URISyntaxException {
    if (datasourceUrn.contains(":///")) { // wherehows URN
      return toDatasourceUrnFromWhUrn(datasourceUrn);
    } else {  // TMS URN
      return DatasourceUrn.createFromString(datasourceUrn);
    }
  }

  /**
   * Convert TMS Datasource to WH DatasourceView
   * @param datasource Datasource
   * @return DatasourceView
   */
  public static DatasourceView toDatasourceView(Datasource datasource) {
    DatasourceView view = new DatasourceView();
    view.setPlatform(datasource.getPlatform().getPlatformNameEntity());
    view.setNativeName(datasource.getName());
    view.setFabric(datasource.getOrigin().name());
    view.setDescription(datasource.getDescription());
    view.setTags(datasource.getTags());
    // construct DatasourceUrn and overwrite URI field for frontend use
    view.setUri(new DatasourceUrn(datasource.getPlatform(), datasource.getName(), datasource.getOrigin()).toString());

    if (datasource.hasPlatformNativeType()) {
      view.setNativeType(datasource.getPlatformNativeType().name());
    }
    if (datasource.getStatus() != null) {
      view.setRemoved(datasource.getStatus().isRemoved());
    }
    if (datasource.hasDeprecation()) {
      view.setDeprecated(datasource.getDeprecation().isDeprecated());
      view.setDeprecationNote(datasource.getDeprecation().getNote());
      if (datasource.getDeprecation().hasDecommissionTime()) {
        view.setDecommissionTime(datasource.getDeprecation().getDecommissionTime());
      }
    }
    if (datasource.hasCreated()) {
      view.setCreatedTime(datasource.getCreated().getTime());
    }
    if (datasource.hasLastModified()) {
      view.setModifiedTime(datasource.getLastModified().getTime());
    }
    if (datasource.hasProperties()) {
      view.setCustomProperties(datasource.getProperties());
    }
    return view;
  }

  /**
   * Converts TMS lineage response to WH LineageView which requires datasourceView conversion
   * for the datasource in the lineage response
   * @param datasource datasource
   * @param lineageType type of lineage
   * @param auditStamp audit stamp
   * @return LineageView
   */
  public static DatasourceLineageView toLineageView(Datasource datasource, String lineageType, AuditStamp auditStamp) {
    DatasourceLineageView view = new DatasourceLineageView();

    DatasourceView datasourceView = toDatasourceView(datasource);
    datasourceView.setModifiedTime(auditStamp.getTime());

    view.setDatasource(datasourceView);
    view.setType(lineageType);
    view.setActor(auditStamp.getActor().toString());

    return view;
  }
}
