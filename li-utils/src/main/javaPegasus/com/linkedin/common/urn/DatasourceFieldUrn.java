package com.linkedin.common.urn;

import com.linkedin.common.FabricType;
import com.linkedin.data.template.Custom;
import com.linkedin.data.template.DirectCoercer;
import com.linkedin.data.template.TemplateOutputCastException;

import java.net.URISyntaxException;


/**
 * Standardized dataset field information identifier
 */
public class DatasourceFieldUrn extends Urn {

  // uniquely identifies urn's key type
  public static final String ENTITY_TYPE = "datasetField";

  /**
   * Dataset urn of the datasetFieldUrn
   */
  private final DatasetUrn _dataset;

  /**
   * Field of datasetFieldUrn
   */
  private final String _fieldPath;

  static {
    Custom.initializeCustomClass(DatasetUrn.class);
    Custom.registerCoercer(new DirectCoercer<DatasourceFieldUrn>() {

      @Override
      public String coerceInput(DatasourceFieldUrn object) throws ClassCastException {
        return object.toString();
      }

      @Override
      public DatasourceFieldUrn coerceOutput(Object object) throws TemplateOutputCastException {
        if (object instanceof String) {
          try {
            return DatasourceFieldUrn.createFromString((String) object);
          } catch (URISyntaxException e) {
            throw new TemplateOutputCastException("Invalid URN syntax: " + e.getMessage(), e);
          }
        }
        throw new TemplateOutputCastException((("Output '" + object) + ("' is not a String, and cannot be coerced to "
            + DatasourceFieldUrn.class.getName())));
      }
    }, DatasourceFieldUrn.class);
  }

  public DatasourceFieldUrn(String dataPlatform, String datasetName, FabricType fabricType, String fieldPath) {
    this(new DatasetUrn(new DataPlatformUrn(dataPlatform), datasetName, fabricType), fieldPath);
  }

  /**
   * Creates a new instance of a {@link DatasourceFieldUrn}.
   *
   * @param dataset dataset that this dataset field belongs to
   * @param fieldPath dataset field path or column name
   */
  public DatasourceFieldUrn(DatasetUrn dataset, String fieldPath) {
    super(ENTITY_TYPE, TupleKey.create(dataset, fieldPath));
    this._dataset = dataset;
    this._fieldPath = fieldPath;
  }

  public static DatasourceFieldUrn createFromString(String rawUrn) throws URISyntaxException {
    return createFromUrn(Urn.createFromString(rawUrn));
  }

  public static DatasourceFieldUrn deserialize(String rawUrn) throws URISyntaxException {
    return createFromString(rawUrn);
  }

  public static DatasourceFieldUrn createFromUrn(Urn urn) throws URISyntaxException {
    if (!"li".equals(urn.getNamespace())) {
      throw new URISyntaxException(urn.toString(), "Urn namespace type should be 'li'.");
    } else if (!ENTITY_TYPE.equals(urn.getEntityType())) {
      throw new URISyntaxException(urn.toString(), "Urn entity type should be 'datasetField'.");
    } else {
      TupleKey key = urn.getEntityKey();
      if (key.size() != 2) {
        throw new URISyntaxException(urn.toString(), "Invalid number of keys.");
      } else {
        try {
          return new DatasourceFieldUrn((DatasetUrn) key.getAs(0, DatasetUrn.class), (String) key.getAs(1, String.class));
        } catch (Exception var3) {
          throw new URISyntaxException(urn.toString(), "Invalid URN Parameter: '" + var3.getMessage());
        }
      }
    }
  }

  public DatasetUrn getDatasetEntity() {
    return _dataset;
  }

  public String getFieldPathEntity() {
    return _fieldPath;
  }
}
