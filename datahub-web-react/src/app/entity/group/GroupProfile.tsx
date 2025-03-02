import { Alert } from 'antd';
import React, { useMemo } from 'react';
import GroupHeader from './GroupHeader';
import { useGetGroupQuery } from '../../../graphql/group.generated';
import { useGetAllEntitySearchResults } from '../../../utils/customGraphQL/useGetAllEntitySearchResults';
import useUserParams from '../../shared/entitySearch/routingUtils/useUserParams';
import { EntityRelationshipsResult, EntityType, SearchResult } from '../../../types.generated';
import RelatedEntityResults from '../../shared/entitySearch/RelatedEntityResults';
import { Message } from '../../shared/Message';
import GroupMembers from './GroupMembers';
import { LegacyEntityProfile } from '../../shared/LegacyEntityProfile';

const messageStyle = { marginTop: '10%' };

export enum TabType {
    Members = 'Members',
    Ownership = 'Ownership',
}

const ENABLED_TAB_TYPES = [TabType.Members, TabType.Ownership];

const MEMBER_PAGE_SIZE = 20;

/**
 * Responsible for reading & writing users.
 */
export default function GroupProfile() {
    const { urn } = useUserParams();
    const { loading, error, data } = useGetGroupQuery({ variables: { urn, membersCount: MEMBER_PAGE_SIZE } });

    const name = data?.corpGroup?.name;

    const ownershipResult = useGetAllEntitySearchResults({
        query: `owners:${name}`,
    });

    const contentLoading =
        Object.keys(ownershipResult).some((type) => {
            return ownershipResult[type].loading;
        }) || loading;

    const ownershipForDetails = useMemo(() => {
        const filteredOwnershipResult: {
            [key in EntityType]?: Array<SearchResult>;
        } = {};

        Object.keys(ownershipResult).forEach((type) => {
            const entities = ownershipResult[type].data?.search?.searchResults;

            if (entities && entities.length > 0) {
                filteredOwnershipResult[type] = ownershipResult[type].data?.search?.searchResults;
            }
        });
        return filteredOwnershipResult;
    }, [ownershipResult]);

    if (error || (!loading && !error && !data)) {
        return <Alert type="error" message={error?.message || 'Group failed to load :('} />;
    }

    const groupMemberRelationships = data?.corpGroup?.relationships as EntityRelationshipsResult;

    const getTabs = () => {
        return [
            {
                name: TabType.Members,
                path: TabType.Members.toLocaleLowerCase(),
                content: (
                    <GroupMembers
                        urn={urn}
                        initialRelationships={groupMemberRelationships}
                        pageSize={MEMBER_PAGE_SIZE}
                    />
                ),
            },
            {
                name: TabType.Ownership,
                path: TabType.Ownership.toLocaleLowerCase(),
                content: <RelatedEntityResults searchResult={ownershipForDetails} />,
            },
        ].filter((tab) => ENABLED_TAB_TYPES.includes(tab.name));
    };

    const description = data?.corpGroup?.info?.description;

    return (
        <>
            {contentLoading && <Message type="loading" content="Loading..." style={messageStyle} />}
            {data && data?.corpGroup && (
                <LegacyEntityProfile
                    title=""
                    tags={null}
                    header={
                        <GroupHeader
                            name={data?.corpGroup?.name}
                            description={description}
                            email={data?.corpGroup?.info?.email}
                        />
                    }
                    tabs={getTabs()}
                />
            )}
        </>
    );
}
