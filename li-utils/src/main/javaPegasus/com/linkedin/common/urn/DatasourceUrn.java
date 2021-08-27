package com.linkedin.common.urn;

import com.linkedin.common.FabricType;
import com.linkedin.data.template.Custom;
import com.linkedin.data.template.DirectCoercer;
import com.linkedin.data.template.TemplateOutputCastException;

import java.net.URISyntaxException;


public final class DatasourceUrn extends Urn {

  public static final String ENTITY_TYPE = "datasource";

  private final DatasourceCategoryUrn _category;
  private final String _datasourceName;
  private final FabricType _origin;

  public DatasourceUrn(DatasourceCategoryUrn category, String name, FabricType origin) {
    super(ENTITY_TYPE, TupleKey.create(category, name, origin));
    this._category = category;
    this._datasourceName = name;
    this._origin = origin;
  }

  public DatasourceCategoryUrn getCategoryEntity() {
    return _category;
  }

  public String getDatasourceNameEntity() {
    return _datasourceName;
  }

  public FabricType getOriginEntity() {
    return _origin;
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
          return new DatasourceUrn((DatasourceCategoryUrn) key.getAs(0, DatasourceCategoryUrn.class),
              (String) key.getAs(1, String.class), (FabricType) key.getAs(2, FabricType.class));
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
    Custom.initializeCustomClass(DatasourceCategoryUrn.class);
    Custom.initializeCustomClass(DatasourceUrn.class);
    Custom.initializeCustomClass(FabricType.class);
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
