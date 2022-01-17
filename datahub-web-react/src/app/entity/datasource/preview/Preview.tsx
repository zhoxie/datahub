import React from 'react';
import { EntityType, FabricType, GlobalTags, GlossaryTerms, Owner } from '../../../../types.generated';
import DefaultPreviewCard from '../../../preview/DefaultPreviewCard';
import { capitalizeFirstLetter } from '../../../shared/capitalizeFirstLetter';
import { useEntityRegistry } from '../../../useEntityRegistry';

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
    delEle,
    editEle,
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
    delEle?: React.ReactNode | null;
    editEle?: React.ReactNode | null;
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
            editEle={editEle}
            delEle={delEle}
            glossaryTerms={glossaryTerms || undefined}
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
