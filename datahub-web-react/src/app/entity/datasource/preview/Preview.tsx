import React from 'react';
import { EntityType, FabricType, GlobalTags, GlossaryTerms, Owner } from '../../../../types.generated';
import DefaultPreviewCard from '../../../preview/DefaultPreviewCard';
import { capitalizeFirstLetter } from '../../../shared/capitalizeFirstLetter';
import { useEntityRegistry } from '../../../useEntityRegistry';
import DatasourceDelete from './DatasourceDelete';

export const Preview = ({
    urn,
    name,
    origin,
    description,
    platformName,
    platformLogo,
    owners,
    globalTags,
    snippet,
    glossaryTerms,
}: {
    urn: string;
    name: string;
    origin: FabricType;
    description?: string | null;
    platformName: string;
    platformLogo?: string | null;
    owners?: Array<Owner> | null;
    globalTags?: GlobalTags | null;
    snippet?: React.ReactNode | null;
    glossaryTerms?: GlossaryTerms | null;
}): JSX.Element => {
    const entityRegistry = useEntityRegistry();
    const capitalPlatformName = capitalizeFirstLetter(platformName);
    return (
        <DefaultPreviewCard
            url={entityRegistry.getEntityUrl(EntityType.Datasource, urn)}
            name={name || ''}
            description={description || ''}
            type="Datasource"
            logoUrl={platformLogo || ''}
            platform={capitalPlatformName}
            qualifier={origin}
            tags={globalTags || undefined}
            owners={owners}
            snippet={snippet}
            glossaryTerms={glossaryTerms || undefined}
            itemBtns={<DatasourceDelete urn={urn} />}
        />
    );
};

export const PreviewNoDel = ({
    urn,
    name,
    origin,
    description,
    platformName,
    platformLogo,
    owners,
    globalTags,
    snippet,
    glossaryTerms,
}: {
    urn: string;
    name: string;
    origin: FabricType;
    description?: string | null;
    platformName: string;
    platformLogo?: string | null;
    owners?: Array<Owner> | null;
    globalTags?: GlobalTags | null;
    snippet?: React.ReactNode | null;
    glossaryTerms?: GlossaryTerms | null;
}): JSX.Element => {
    const entityRegistry = useEntityRegistry();
    const capitalPlatformName = capitalizeFirstLetter(platformName);
    return (
        <DefaultPreviewCard
            url={entityRegistry.getEntityUrl(EntityType.Datasource, urn)}
            name={name || ''}
            description={description || ''}
            type="Datasource"
            logoUrl={platformLogo || ''}
            platform={capitalPlatformName}
            qualifier={origin}
            tags={globalTags || undefined}
            owners={owners}
            snippet={snippet}
            glossaryTerms={glossaryTerms || undefined}
        />
    );
};
