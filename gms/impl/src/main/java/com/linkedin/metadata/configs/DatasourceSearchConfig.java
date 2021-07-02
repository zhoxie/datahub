package com.linkedin.metadata.configs;

import com.google.common.collect.ImmutableList;
import com.linkedin.metadata.dao.utils.SearchUtils;
import com.linkedin.metadata.search.DatasourceDocument;
import com.linkedin.metadata.utils.elasticsearch.IndexConvention;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DatasourceSearchConfig extends BaseSearchConfigWithConvention<DatasourceDocument> {
  public DatasourceSearchConfig() {
  }

  public DatasourceSearchConfig(IndexConvention indexConvention) {
    super(indexConvention);
  }

  @Override
  @Nonnull
  public Set<String> getFacetFields() {
    return Collections.unmodifiableSet(new HashSet<>(Arrays.asList("origin", "platform")));
  }

  @Nonnull
  public Class getSearchDocument() {
    return DatasourceDocument.class;
  }

  @Override
  @Nonnull
  public String getDefaultAutocompleteField() {
    return "name";
  }

  @Override
  @Nonnull
  public String getSearchQueryTemplate() {
    return SearchUtils.readResourceFile(getClass(), "datasourceESSearchQueryTemplate.json");
  }

  @Override
  @Nonnull
  public String getAutocompleteQueryTemplate() {
    return SearchUtils.readResourceFile(getClass(), "datasourceESAutocompleteQueryTemplate.json");
  }

  @Override
  @Nullable
  public List<String> getFieldsToHighlightMatch() {
    return ImmutableList.of("name", "fieldPaths", "description", "tags", "fieldDescriptions", "fieldTags",
        "editedFieldDescriptions", "editedFieldTags");
  }
}
