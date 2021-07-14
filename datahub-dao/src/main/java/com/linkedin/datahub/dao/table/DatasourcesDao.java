package com.linkedin.datahub.dao.table;

import com.linkedin.dataset.client.Ownerships;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;


public class DatasourcesDao {

    private final Ownerships _ownership;

    public DatasourcesDao(@Nonnull Ownerships ownerships) {
        this._ownership = ownerships;
    }

    public List<String> getDatasourceOwnerTypes() {
        return Arrays.asList("DataOwner", "Producer", "Delegate", "Stakeholder", "Consumer", "Developer");
    }
}