import * as React from 'react';
import { DatabaseFilled, DatabaseOutlined } from '@ant-design/icons';
import { Typography } from 'antd';
import { Datasource, EntityType, RelationshipDirection, SearchResult } from '../../../types.generated';
import { Entity, IconStyleType, PreviewType } from '../Entity';
import { Preview } from './preview/Preview';
import { FIELDS_TO_HIGHLIGHT } from '../dataset/search/highlights';
import { getChildrenFromRelationships } from '../../lineage/utils/getChildren';
import { EntityProfile } from '../shared/containers/profile/EntityProfile';
import {
    GetDatasourceQuery,
    useGetDatasourceQuery,
    useUpdateDatasourceMutation,
} from '../../../graphql/datasource.generated';
import { PropertiesTab } from '../shared/tabs/Properties/PropertiesTab';
import { DocumentationTab } from '../shared/tabs/Documentation/DocumentationTab';
import { Sources } from '../shared/tabs/Datasource/Source/Sources';
import { Datasets } from '../shared/tabs/Datasource/Datasets/Datasets';
import { SidebarAboutSection } from '../shared/containers/profile/sidebar/SidebarAboutSection';
import { SidebarOwnerSection } from '../shared/containers/profile/sidebar/Ownership/SidebarOwnerSection';
import { SidebarTagsSection } from '../shared/containers/profile/sidebar/SidebarTagsSection';
import { SidebarRecommendationsSection } from '../shared/containers/profile/sidebar/Recommendations/SidebarRecommendationsSection';
import { getDataForEntityType } from '../shared/containers/profile/utils';
import DatasourceDelete from './preview/DatasourceDelete';
import DatasourceEdit from './profile/DatasourceEdit';

/**
 * Definition of the DataHub Dataset entity.
 */
export class DatasourceEntity implements Entity<Datasource> {
    type: EntityType = EntityType.Datasource;

    icon = (fontSize: number, styleType: IconStyleType) => {
        if (styleType === IconStyleType.TAB_VIEW) {
            return <DatabaseOutlined style={{ fontSize }} />;
        }

        if (styleType === IconStyleType.HIGHLIGHT) {
            return <DatabaseFilled style={{ fontSize, color: '#B37FEB' }} />;
        }

        if (styleType === IconStyleType.SVG) {
            return (
                <path d="M832 64H192c-17.7 0-32 14.3-32 32v832c0 17.7 14.3 32 32 32h640c17.7 0 32-14.3 32-32V96c0-17.7-14.3-32-32-32zm-600 72h560v208H232V136zm560 480H232V408h560v208zm0 272H232V680h560v208zM304 240a40 40 0 1080 0 40 40 0 10-80 0zm0 272a40 40 0 1080 0 40 40 0 10-80 0zm0 272a40 40 0 1080 0 40 40 0 10-80 0z" />
            );
        }

        return (
            <DatabaseFilled
                style={{
                    fontSize,
                    color: '#BFBFBF',
                }}
            />
        );
    };

    isSearchEnabled = () => true;

    isBrowseEnabled = () => true;

    isLineageEnabled = () => true;

    getAutoCompleteFieldName = () => 'name';

    getPathName = () => 'datasource';

    getEntityName = () => 'Datasource';

    getCollectionName = () => 'Datasources';

    getDelete = (urn: string, name: string) => {
        return <DatasourceDelete urn={urn} name={name} />;
    };

    getEdit = (data: Datasource) => {
        return <DatasourceEdit datasource={data} />;
    };

    renderProfile = (urn: string) => (
        <EntityProfile
            urn={urn}
            entityType={EntityType.Datasource}
            useEntityQuery={useGetDatasourceQuery}
            useUpdateQuery={useUpdateDatasourceMutation}
            getOverrideProperties={this.getOverridePropertiesFromEntity}
            tabs={[
                {
                    name: 'Connections',
                    component: Sources,
                },
                {
                    name: 'Documentation',
                    component: DocumentationTab,
                },
                {
                    name: 'Properties',
                    component: PropertiesTab,
                },
                {
                    name: 'Datasets',
                    component: Datasets,
                    display: {
                        visible: (_, _1) => true,
                        enabled: (_, datasource: GetDatasourceQuery) =>
                            (datasource?.datasource?.datasets?.count || 0) > 0,
                    },
                },
            ]}
            sidebarSections={[
                {
                    component: SidebarAboutSection,
                },
                {
                    component: SidebarTagsSection,
                    properties: {
                        hasTags: true,
                        hasTerms: true,
                    },
                },
                {
                    component: SidebarOwnerSection,
                },
                {
                    component: SidebarRecommendationsSection,
                },
            ]}
        />
    );

    getOverridePropertiesFromEntity = () => {
        // if dataset has subTypes filled out, pick the most specific subtype and return it
        return {
            externalUrl: '',
            entityTypeOverride: '',
        };
    };

    renderPreview = (_: PreviewType, data: Datasource) => {
        return (
            <Preview
                urn={data.urn}
                name={data.name}
                origin={data.origin}
                description={data.editableProperties?.description || data.description}
                platformName={data.platform?.displayName || data.platform?.name || ''}
                platformLogo={data.platform?.info?.logoUrl}
                owners={data.ownership?.owners}
                globalTags={data.globalTags}
                glossaryTerms={data.glossaryTerms}
                delEle={this.getDelete(data.urn, data.name)}
                editEle={this.getEdit(data)}
            />
        );
    };

    renderSearch = (result: SearchResult) => {
        const data = result.entity as Datasource;
        return (
            <Preview
                urn={data.urn}
                name={data.name}
                origin={data.origin}
                description={data.editableProperties?.description || data.description}
                platformName={data.platform?.name || ''}
                platformLogo={data.platform?.info?.logoUrl}
                owners={data.ownership?.owners}
                globalTags={data.globalTags}
                glossaryTerms={data.glossaryTerms}
                snippet={
                    // Add match highlights only if all the matched fields are in the FIELDS_TO_HIGHLIGHT
                    result.matchedFields.length > 0 &&
                    result.matchedFields.every((field) => FIELDS_TO_HIGHLIGHT.has(field.name)) && (
                        <Typography.Text>
                            Matches {FIELDS_TO_HIGHLIGHT.get(result.matchedFields[0].name)}{' '}
                            <b>{result.matchedFields[0].value}</b>
                        </Typography.Text>
                    )
                }
            />
        );
    };

    getLineageVizConfig = (entity: Datasource) => {
        return {
            urn: entity?.urn,
            name: entity?.name,
            type: EntityType.Datasource,
            subtype: undefined,
            downstreamChildren: getChildrenFromRelationships({
                // eslint-disable-next-line @typescript-eslint/dot-notation
                incomingRelationships: entity?.['incoming'],
                // eslint-disable-next-line @typescript-eslint/dot-notation
                outgoingRelationships: entity?.['outgoing'],
                direction: RelationshipDirection.Incoming,
            }),
            upstreamChildren: getChildrenFromRelationships({
                // eslint-disable-next-line @typescript-eslint/dot-notation
                incomingRelationships: entity?.['incoming'],
                // eslint-disable-next-line @typescript-eslint/dot-notation
                outgoingRelationships: entity?.['outgoing'],
                direction: RelationshipDirection.Outgoing,
            }),
            icon: entity?.platform?.info?.logoUrl || undefined,
            platform: entity?.platform?.name,
        };
    };

    displayName = (data: Datasource) => {
        return data?.name;
    };

    platformLogoUrl = (data: Datasource) => {
        return data.platform?.info?.logoUrl || undefined;
    };

    getGenericEntityProperties = (data: Datasource) => {
        return getDataForEntityType({
            data,
            entityType: this.type,
            getOverrideProperties: this.getOverridePropertiesFromEntity,
        });
    };
}
