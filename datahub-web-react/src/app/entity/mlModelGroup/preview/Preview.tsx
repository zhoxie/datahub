import React from 'react';
import { EntityType, MlModelGroup } from '../../../../types.generated';
import DefaultPreviewCard from '../../../preview/DefaultPreviewCard';
import { capitalizeFirstLetter } from '../../../shared/capitalizeFirstLetter';
import { useEntityRegistry } from '../../../useEntityRegistry';

export const Preview = ({ group }: { group: MlModelGroup }): JSX.Element => {
    const entityRegistry = useEntityRegistry();
    const capitalPlatformName = capitalizeFirstLetter(group?.platform?.name || '');

    return (
        <DefaultPreviewCard
            url={entityRegistry.getEntityUrl(EntityType.MlmodelGroup, group.urn)}
            name={group?.name || ''}
            description={group?.description || ''}
            type="MLModel Group"
            logoUrl={group?.platform?.info?.logoUrl || ''}
            platform={capitalPlatformName}
            qualifier={group?.origin}
            owners={group?.ownership?.owners}
        />
    );
};
