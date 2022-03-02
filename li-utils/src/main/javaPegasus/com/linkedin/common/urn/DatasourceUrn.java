package com.linkedin.common.urn;

import com.linkedin.data.template.Custom;
import com.linkedin.data.template.DirectCoercer;
import com.linkedin.data.template.TemplateOutputCastException;

import java.net.URISyntaxException;


public final class DatasourceUrn extends Urn {

  public static final String ENTITY_TYPE = "datasource";

  private final DataPlatformUrn _platform;
  private final String _datasourceName;
  private final String _region;

  public DatasourceUrn(DataPlatformUrn platform, String name, String region) {
    super(ENTITY_TYPE, TupleKey.create(platform, name, region));
    this._platform = platform;
    this._datasourceName = name;
    this._region = region;
  }

  public DataPlatformUrn getPlatformEntity() {
    return _platform;
  }

  public String getDatasourceNameEntity() {
    return _datasourceName;
  }

  public String getRegionEntity() {
    return _region;
  }

  public static DatasourceUrn createFromString(String rawUrn) throws URISyntaxException {
    return createFromUrn(Urn.createFromString(rawUrn));
  }

  public static DatasourceUrn createFromUrn(Urn urn) throws URISyntaxException {
    if (!"li".equals(urn.getNamespace())) {
      throw new URISyntaxException(urn.toString(), "Urn namespace type should be 'li'.");
    } else if (!ENTITY_TYPE.equals(urn.getEntityType())) {
      throw new URISyntaxException(urn.toString(), "Urn entity type should be 'datasource'.");
    } else {
      TupleKey key = urn.getEntityKey();
      if (key.size() != 3) {
        throw new URISyntaxException(urn.toString(), "Invalid number of keys.");
      } else {
        try {
          return new DatasourceUrn(key.getAs(0, DataPlatformUrn.class),
              key.getAs(1, String.class), key.getAs(2, String.class));
        } catch (Exception var3) {
          throw new URISyntaxException(urn.toString(), "Invalid URN Parameter: '" + var3.getMessage());
        }
      }
    }
  }

  public static DatasourceUrn deserialize(String rawUrn) throws URISyntaxException {
    return createFromString(rawUrn);
  }

  static {
    Custom.initializeCustomClass(DatasourceUrn.class);
    Custom.registerCoercer(new DirectCoercer<DatasourceUrn>() {
      public Object coerceInput(DatasourceUrn object) throws ClassCastException {
        return object.toString();
      }

      public DatasourceUrn coerceOutput(Object object) throws TemplateOutputCastException {
        try {
          return DatasourceUrn.createFromString((String) object);
        } catch (URISyntaxException e) {
          throw new TemplateOutputCastException("Invalid URN syntax: " + e.getMessage(), e);
        }
      }
    }, DatasourceUrn.class);
  }
}
