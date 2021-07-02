package com.linkedin.metadata.resources.datasource;

import com.linkedin.common.urn.DatasourceUrn;
import com.linkedin.data.template.RecordTemplate;
import com.linkedin.datasource.DatasourceKey;
import com.linkedin.metadata.aspect.DatasourceAspect;
import com.linkedin.metadata.dao.BaseLocalDAO;
import com.linkedin.metadata.entity.EntityService;
import com.linkedin.metadata.restli.BaseVersionedAspectResource;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.EmptyRecord;
import com.linkedin.restli.server.PathKeys;
import com.linkedin.restli.server.annotations.PathKeysParam;
import com.linkedin.restli.server.annotations.RestLiCollection;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;


public class BaseDatasourceVersionedAspectResource<ASPECT extends RecordTemplate>
        extends BaseVersionedAspectResource<DatasourceUrn, DatasourceAspect, ASPECT> {

    private static final String DATASET_KEY = Datasources.class.getAnnotation(RestLiCollection.class).keyName();

    @Inject
    @Named("entityService")
    private EntityService _entityService;

    public BaseDatasourceVersionedAspectResource(@Nonnull Class<ASPECT> aspectClass) {
        super(DatasourceAspect.class, aspectClass);
    }

    @Nonnull
    @Override
    public BaseLocalDAO getLocalDAO() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    protected DatasourceUrn getUrn(@PathKeysParam @Nonnull PathKeys keys) {
        DatasourceKey key = keys.<ComplexResourceKey<DatasourceKey, EmptyRecord>>get(DATASET_KEY).getKey();
        return new DatasourceUrn(key.getPlatform(), key.getName(), key.getOrigin());
    }

    @Nonnull
    protected EntityService getEntityService() {
        return _entityService;
    }
}
