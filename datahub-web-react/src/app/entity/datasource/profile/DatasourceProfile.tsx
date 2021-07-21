import React from 'react';
import { Alert } from 'antd';
import {
    useGetDatasourceQuery,
    useUpdateDatasourceMutation,
    GetDatasourceDocument,
} from '../../../../graphql/datasource.generated';
import { Ownership as OwnershipView } from '../../shared/Ownership';
import { EntityProfile } from '../../../shared/EntityProfile';
import { Datasource, DatasourceConnections, EntityType, GlobalTags, GlossaryTerms } from '../../../../types.generated';
import LineageView from './Lineage';
import { Properties as PropertiesView } from '../../shared/Properties';
import DocumentsView from './Documentation';
import DatasourceHeader from './DatasourceHeader';
import { Message } from '../../../shared/Message';
import TagTermGroup from '../../../shared/tags/TagTermGroup';
import useIsLineageMode from '../../../lineage/utils/useIsLineageMode';
import { useEntityRegistry } from '../../../useEntityRegistry';
import { useGetAuthenticatedUser } from '../../../useGetAuthenticatedUser';
import analytics, { EventType, EntityActionType } from '../../../analytics';
import QueriesTab from './QueriesTab';

export enum TabType {
    Ownership = 'Ownership',
    Schema = 'Schema',
    Lineage = 'Connection',
    Properties = 'Properties',
    Documents = 'Documents',
    Queries = 'Queries',
}

const EMPTY_ARR: never[] = [];

/**
 * Responsible for display the Datasource Page
 */
export const DatasourceProfile = ({ urn }: { urn: string }): JSX.Element => {
    const entityRegistry = useEntityRegistry();

    const { loading, error, data } = useGetDatasourceQuery({ variables: { urn } });

    const user = useGetAuthenticatedUser();
    const [updateDatasource] = useUpdateDatasourceMutation({
        update(cache, { data: newDatasource }) {
            cache.modify({
                fields: {
                    datasource() {
                        cache.writeQuery({
                            query: GetDatasourceDocument,
                            data: {
                                datasource: {
                                    ...newDatasource?.updateDatasource,
                                    usageStats: data?.datasource?.usageStats,
                                },
                            },
                        });
                    },
                },
            });
        },
    });
    const isLineageMode = useIsLineageMode();

    if (error || (!loading && !error && !data)) {
        return <Alert type="error" message={error?.message || `Entity failed to load for urn ${urn}`} />;
    }

    const getHeader = (datasource: Datasource) => (
        <DatasourceHeader datasource={datasource} updateDatasource={updateDatasource} />
    );

    const getTabs = (datasource: Datasource) => {
        const { ownership, properties, institutionalMemory, connections } = datasource;

        return [
            {
                name: TabType.Lineage,
                path: TabType.Lineage.toLowerCase(),
                content: <LineageView datasource={datasource} connections={connections as DatasourceConnections} />,
            },
            {
                name: TabType.Ownership,
                path: TabType.Ownership.toLowerCase(),
                content: (
                    <OwnershipView
                        owners={(ownership && ownership.owners) || EMPTY_ARR}
                        lastModifiedAt={(ownership && ownership.lastModified?.time) || 0}
                        updateOwnership={(update) => {
                            analytics.event({
                                type: EventType.EntityActionEvent,
                                actionType: EntityActionType.UpdateOwnership,
                                entityType: EntityType.Datasource,
                                entityUrn: urn,
                            });
                            return updateDatasource({ variables: { input: { urn, ownership: update } } });
                        }}
                    />
                ),
            },
            {
                name: TabType.Queries,
                path: TabType.Queries.toLowerCase(),
                content: <QueriesTab datasource={datasource} />,
            },
            {
                name: TabType.Properties,
                path: TabType.Properties.toLowerCase(),
                content: <PropertiesView properties={properties || EMPTY_ARR} />,
            },
            {
                name: TabType.Documents,
                path: 'docs',
                content: (
                    <DocumentsView
                        authenticatedUserUrn={user?.urn}
                        authenticatedUserUsername={user?.username}
                        documents={institutionalMemory?.elements || EMPTY_ARR}
                        updateDocumentation={(update) => {
                            analytics.event({
                                type: EventType.EntityActionEvent,
                                actionType: EntityActionType.UpdateDocumentation,
                                entityType: EntityType.Datasource,
                                entityUrn: urn,
                            });
                            return updateDatasource({ variables: { input: { urn, institutionalMemory: update } } });
                        }}
                    />
                ),
            },
        ];
    };

    return (
        <>
            {loading && <Message type="loading" content="Loading..." style={{ marginTop: '10%' }} />}
            {data && data.datasource && (
                <EntityProfile
                    titleLink={`/${entityRegistry.getPathName(
                        EntityType.Datasource,
                    )}/${urn}?is_lineage_mode=${isLineageMode}`}
                    title={data.datasource.name}
                    tags={
                        <TagTermGroup
                            editableTags={data.datasource?.globalTags as GlobalTags}
                            glossaryTerms={data.datasource?.glossaryTerms as GlossaryTerms}
                            canAdd
                            canRemove
                            updateTags={(globalTags) => {
                                analytics.event({
                                    type: EventType.EntityActionEvent,
                                    actionType: EntityActionType.UpdateTags,
                                    entityType: EntityType.Datasource,
                                    entityUrn: urn,
                                });
                                return updateDatasource({ variables: { input: { urn, globalTags } } });
                            }}
                        />
                    }
                    tagCardHeader={data.datasource?.glossaryTerms ? 'Tags & Terms' : 'Tags'}
                    tabs={getTabs(data.datasource as Datasource)}
                    header={getHeader(data.datasource as Datasource)}
                    onTabChange={(tab: string) => {
                        analytics.event({
                            type: EventType.EntitySectionViewEvent,
                            entityType: EntityType.Datasource,
                            entityUrn: urn,
                            section: tab,
                        });
                    }}
                />
            )}
        </>
    );
};
