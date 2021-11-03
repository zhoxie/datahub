package com.linkedin.common.urn;

import com.linkedin.common.FabricType;
import com.linkedin.data.template.Custom;
import com.linkedin.data.template.DirectCoercer;
import com.linkedin.data.template.TemplateOutputCastException;

import java.net.URISyntaxException;


public final class MetadataSourceUrn extends Urn {

  public static final String ENTITY_TYPE = "metadataSource";

  private final DataPlatformUrn _platform;
  private final String _sourceName;
  private final FabricType _origin;

  public MetadataSourceUrn(DataPlatformUrn platform, String name, FabricType origin) {
    super(ENTITY_TYPE, TupleKey.create(platform, name, origin));
    this._platform = platform;
    this._sourceName = name;
    this._origin = origin;
  }

  public DataPlatformUrn getPlatformEntity() {
    return _platform;
  }

  public String getSourceNameEntity() {
    return _sourceName;
  }

  public FabricType getOriginEntity() {
    return _origin;
  }

  public static MetadataSourceUrn createFromString(String rawUrn) throws URISyntaxException {
    return createFromUrn(Urn.createFromString(rawUrn));
  }

  public static MetadataSourceUrn createFromUrn(Urn urn) throws URISyntaxException {
    if (!"li".equals(urn.getNamespace())) {
      throw new URISyntaxException(urn.toString(), "Urn namespace type should be 'li'.");
    } else if (!ENTITY_TYPE.equals(urn.getEntityType())) {
      throw new URISyntaxException(urn.toString(), "Urn entity type should be 'metadataSource'.");
    } else {
      TupleKey key = urn.getEntityKey();
      if (key.size() != 3) {
        throw new URISyntaxException(urn.toString(), "Invalid number of keys.");
      } else {
        try {
          return new MetadataSourceUrn((DataPlatformUrn) key.getAs(0, DataPlatformUrn.class),
              (String) key.getAs(1, String.class), (FabricType) key.getAs(2, FabricType.class));
        } catch (Exception var3) {
          throw new URISyntaxException(urn.toString(), "Invalid URN Parameter: '" + var3.getMessage());
        }
      }
    }
  }

  public static MetadataSourceUrn deserialize(String rawUrn) throws URISyntaxException {
    return createFromString(rawUrn);
  }

  static {
    Custom.initializeCustomClass(DataPlatformUrn.class);
    Custom.initializeCustomClass(MetadataSourceUrn.class);
    Custom.initializeCustomClass(FabricType.class);
    Custom.registerCoercer(new DirectCoercer<MetadataSourceUrn>() {
      public Object coerceInput(MetadataSourceUrn object) throws ClassCastException {
        return object.toString();
      }

      public MetadataSourceUrn coerceOutput(Object object) throws TemplateOutputCastException {
        try {
          return MetadataSourceUrn.createFromString((String) object);
        } catch (URISyntaxException e) {
          throw new TemplateOutputCastException("Invalid URN syntax: " + e.getMessage(), e);
        }
      }
    }, MetadataSourceUrn.class);
  }
}
