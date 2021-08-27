package com.linkedin.metadata.resources.datasourcecategory;

import com.linkedin.common.urn.DatasourceCategoryUrn;
import com.linkedin.common.urn.Urn;
import com.linkedin.datasourcecategory.DatasourceCategoryInfo;
import com.linkedin.datasourceCategories.DatasourceCategory;
import com.linkedin.entity.Entity;
import com.linkedin.metadata.PegasusUtils;
import com.linkedin.metadata.aspect.DatasourceCategoryAspect;
import com.linkedin.metadata.aspect.DatasourceCategoryAspectArray;
import com.linkedin.metadata.dao.BaseLocalDAO;
import com.linkedin.metadata.dao.utils.ModelUtils;
import com.linkedin.metadata.entity.EntityService;
import com.linkedin.metadata.restli.BaseEntityResource;
import com.linkedin.metadata.restli.RestliUtils;
import com.linkedin.metadata.snapshot.DatasourceCategorySnapshot;
import com.linkedin.parseq.Task;
import com.linkedin.restli.server.PagingContext;
import com.linkedin.restli.server.annotations.Action;
import com.linkedin.restli.server.annotations.ActionParam;
import com.linkedin.restli.server.annotations.Optional;
import com.linkedin.restli.server.annotations.PagingContextParam;
import com.linkedin.restli.server.annotations.QueryParam;
import com.linkedin.restli.server.annotations.RestLiCollection;
import com.linkedin.restli.server.annotations.RestMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.linkedin.metadata.restli.RestliConstants.ACTION_GET_SNAPSHOT;
import static com.linkedin.metadata.restli.RestliConstants.PARAM_ASPECTS;
import static com.linkedin.metadata.restli.RestliConstants.PARAM_URN;


/**
 * Resource provides information about various data platforms.
 */
@RestLiCollection(name = "datasourceCategories", namespace = "com.linkedin.datasourcecategory", keyName = "categoryName")
public class DatasoureCategories extends BaseEntityResource<
    // @formatter:off
    String,
    DatasourceCategory,
    DatasourceCategoryUrn,
    DatasourceCategorySnapshot,
    DatasourceCategoryAspect> {
  // @formatter:on

  public DatasoureCategories() {
    super(DatasourceCategorySnapshot.class, DatasourceCategoryAspect.class);
  }

  @Inject
  @Named("entityService")
  private EntityService _entityService;

  /**
   * Get datasource category.
   *
   * @param categorymName name of the category.
   * @param aspectNames list of aspects to be retrieved. Null to retrieve all aspects of the datasourceCategories.
   * @return {@link DatasourceCategory} datasource category value.
   */
  @Nonnull
  @Override
  @RestMethod.Get
  public Task<DatasourceCategory> get(
      @Nonnull String categorymName,
      @QueryParam(PARAM_ASPECTS) @Nullable String[] aspectNames) {
    final Set<String> projectedAspects = aspectNames == null ? Collections.emptySet() : new HashSet<>(
        Arrays.asList(aspectNames).stream().map(PegasusUtils::getAspectNameFromFullyQualifiedName)
            .collect(Collectors.toList()));
    return RestliUtils.toTask(() -> {
      final Entity entity = _entityService.getEntity(
          new DatasourceCategoryUrn(categorymName),
          projectedAspects);
      if (entity != null) {
        return toValue(entity.getValue().getDatasourceCategorySnapshot());
      }
      throw RestliUtils.resourceNotFoundException();
    });
  }

  /**
   * Get all datasource categories.
   *
   * @param pagingContext paging context used for paginating through the results.
   * @return list of all datasource categories.
   */
  @RestMethod.GetAll
  public Task<List<DatasourceCategory>> getAllDatasoureCategories(@Nonnull @PagingContextParam(defaultCount = 100) PagingContext pagingContext) {
    return Task.value(_entityService.listLatestAspects(
        "datasourceCategory",
        "datasourceCategoryInfo",
        pagingContext.getStart(),
        pagingContext.getCount())
            .getValues()
            .stream()
            .map(record -> {
              final DatasourceCategoryInfo info = new DatasourceCategoryInfo(record.data());
              final DatasourceCategory category = new DatasourceCategory();
              category.setDatasourceCategoryInfo(info);
              category.setName(info.getName());
              return category;
            })
            .collect(Collectors.toList())
    );
  }

  /**
   * Get the snapshot of datasource category.
   *
   * @param urnString data datasource category urn.
   * @param aspectNames list of aspects to be returned. null, when all aspects are to be returned.
   * @return snapshot of datasource category with the requested aspects.
   */
  @Action(name = ACTION_GET_SNAPSHOT)
  @Override
  @Nonnull
  public Task<DatasourceCategorySnapshot> getSnapshot(@ActionParam(PARAM_URN) @Nonnull String urnString,
      @ActionParam(PARAM_ASPECTS) @Optional @Nullable String[] aspectNames) {
    final Set<String> projectedAspects = aspectNames == null ? Collections.emptySet() : new HashSet<>(
        Arrays.asList(aspectNames).stream().map(PegasusUtils::getAspectNameFromFullyQualifiedName)
            .collect(Collectors.toList()));
    return RestliUtils.toTask(() -> {
      final Entity entity;
      try {
        entity = _entityService.getEntity(
            Urn.createFromString(urnString), projectedAspects);

        if (entity != null) {
          return entity.getValue().getDatasourceCategorySnapshot();
        }
        throw RestliUtils.resourceNotFoundException();
      } catch (URISyntaxException e) {
        throw new RuntimeException(String.format("Failed to convert urnString %s into an Urn", urnString));
      }
    });  }

  @Nonnull
  @Override
  protected BaseLocalDAO<DatasourceCategoryAspect, DatasourceCategoryUrn> getLocalDAO() {
    throw new UnsupportedOperationException();
  }

  @Nonnull
  @Override
  protected DatasourceCategoryUrn createUrnFromString(@Nonnull String urnString) throws Exception {
    return DatasourceCategoryUrn.deserialize(urnString);
  }

  @Nonnull
  @Override
  protected DatasourceCategoryUrn toUrn(@Nonnull String categoryName) {
    return new DatasourceCategoryUrn(categoryName);
  }

  @Nonnull
  @Override
  protected String toKey(@Nonnull DatasourceCategoryUrn urn) {
    return urn.getCategoryNameEntity();
  }

  @Nonnull
  @Override
  protected DatasourceCategory toValue(@Nonnull DatasourceCategorySnapshot datasourceCategorySnapshot) {
    final DatasourceCategory datasourceCategory = new DatasourceCategory();
    datasourceCategory.setName(datasourceCategorySnapshot.getUrn().getCategoryNameEntity());
    ModelUtils.getAspectsFromSnapshot(datasourceCategorySnapshot).forEach(aspect -> {
      if (aspect instanceof DatasourceCategoryInfo) {
        datasourceCategory.setDatasourceCategoryInfo((DatasourceCategoryInfo) aspect);
      }
    });

    return datasourceCategory;
  }

  @Nonnull
  @Override
  protected DatasourceCategorySnapshot toSnapshot(@Nonnull DatasourceCategory datasourceCategory, @Nonnull DatasourceCategoryUrn urn) {
    final DatasourceCategorySnapshot datasourceCategorySnapshot = new DatasourceCategorySnapshot();
    final DatasourceCategoryAspectArray aspects = new DatasourceCategoryAspectArray();
    datasourceCategorySnapshot.setUrn(urn);
    datasourceCategorySnapshot.setAspects(aspects);
    if (datasourceCategory.getDatasourceCategoryInfo() != null) {
      aspects.add(ModelUtils.newAspectUnion(DatasourceCategoryAspect.class, datasourceCategory.getDatasourceCategoryInfo()));
    }
    return datasourceCategorySnapshot;
  }
}
