package com.linkedin.datahub.dao.table;

import com.linkedin.common.OwnerArray;
import com.linkedin.common.Ownership;
import com.linkedin.common.urn.CorpuserUrn;
import com.linkedin.datahub.models.view.DatasourceOwner;
import com.linkedin.datasource.client.Ownerships;

import javax.annotation.Nonnull;
import java.util.List;

import static com.linkedin.datahub.util.DatasourceUtil.toDatasourceUrn;
import static com.linkedin.datahub.util.OwnerUtil.toTmsOwner;

public class DatasourceOwnerDao {
    private final Ownerships _ownerships;

    public DatasourceOwnerDao(@Nonnull Ownerships ownerships) {
        this._ownerships = ownerships;
    }

    public void updateDatasourceOwners(@Nonnull String datasourceUrn, @Nonnull List<DatasourceOwner> owners,
                                    @Nonnull String user) throws Exception {
        OwnerArray ownerArray = new OwnerArray();
        for (DatasourceOwner owner : owners) {
            ownerArray.add(toTmsOwner(owner));
        }
        _ownerships.createOwnership(toDatasourceUrn(datasourceUrn), new Ownership().setOwners(ownerArray),
                new CorpuserUrn(user));
    }
}
