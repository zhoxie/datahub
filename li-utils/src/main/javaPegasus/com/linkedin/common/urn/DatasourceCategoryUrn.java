package com.linkedin.common.urn;

import com.linkedin.data.template.Custom;
import com.linkedin.data.template.DirectCoercer;
import com.linkedin.data.template.TemplateOutputCastException;

import java.net.URISyntaxException;


public final class DatasourceCategoryUrn extends Urn {

  public static final String ENTITY_TYPE = "datasourceCategory";

  private final String _categoryName;

  public DatasourceCategoryUrn(String categoryName) {
    super(ENTITY_TYPE, TupleKey.create(categoryName));
    this._categoryName = categoryName;
  }

  public String getCategoryNameEntity() {
    return _categoryName;
  }

  public static DatasourceCategoryUrn createFromString(String rawUrn) throws URISyntaxException {
    return createFromUrn(Urn.createFromString(rawUrn));
  }

  public static DatasourceCategoryUrn createFromUrn(Urn urn) throws URISyntaxException {
    if (!"li".equals(urn.getNamespace())) {
      throw new URISyntaxException(urn.toString(), "Urn namespace type should be 'li'.");
    } else if (!ENTITY_TYPE.equals(urn.getEntityType())) {
      throw new URISyntaxException(urn.toString(), "Urn entity type should be 'datasourceCategory'.");
    } else {
      TupleKey key = urn.getEntityKey();
      if (key.size() != 1) {
        throw new URISyntaxException(urn.toString(), "Invalid number of keys.");
      } else {
        try {
          return new DatasourceCategoryUrn((String) key.getAs(0, String.class));
        } catch (Exception e) {
          throw new URISyntaxException(urn.toString(), "Invalid URN Parameter: '" + e.getMessage());
        }
      }
    }
  }

  public static DatasourceCategoryUrn deserialize(String rawUrn) throws URISyntaxException {
    return createFromString(rawUrn);
  }

  static {
    Custom.registerCoercer(new DirectCoercer<DatasourceCategoryUrn>() {
      public Object coerceInput(DatasourceCategoryUrn object) throws ClassCastException {
        return object.toString();
      }

      public DatasourceCategoryUrn coerceOutput(Object object) throws TemplateOutputCastException {
        try {
          return DatasourceCategoryUrn.createFromString((String) object);
        } catch (URISyntaxException e) {
          throw new TemplateOutputCastException("Invalid URN syntax: " + e.getMessage(), e);
        }
      }
    }, DatasourceCategoryUrn.class);
  }
}
