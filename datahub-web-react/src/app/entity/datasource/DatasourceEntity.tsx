import * as React from 'react';
import { DatabaseFilled, DatabaseOutlined } from '@ant-design/icons';
import { Tag, Typography } from 'antd';
import styled from 'styled-components';
import { Datasource, EntityType, SearchResult } from '../../../types.generated';
import { DatasourceProfile } from './profile/DatasourceProfile';
import { Entity, IconStyleType, PreviewType } from '../Entity';
import { PreviewNoDel, Preview } from './preview/Preview';
import { FIELDS_TO_HIGHLIGHT } from './search/highlights';
import getChildren from '../../lineage/utils/getChildren';
import { Direction } from '../../lineage/types';

const MatchTag = styled(Tag)`
    &&& {
        margin-bottom: 0px;
        margin-top: 10px;
    }
`;

/**
 * Definition of the DataHub Datasource entity.
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

    getCollectionName = () => 'Datasources';

    renderProfile = (urn: string) => <DatasourceProfile urn={urn} />;

    renderPreview = (_: PreviewType, data: Datasource) => {
        return (
            <Preview
                urn={data.urn}
                name={data.name}
                origin={data.origin}
                description={data.description}
                platformName={data.connections?.platform?.name || 'null'}
                platformLogo={data.connections?.platform?.info?.logoUrl || ''}
                owners={data.ownership?.owners}
                globalTags={data.globalTags}
                glossaryTerms={data.glossaryTerms}
            />
        );
    };

    renderNoDelPreview = (_: PreviewType, data: Datasource) => {
        return (
            <PreviewNoDel
                urn={data.urn}
                name={data.name}
                origin={data.origin}
                description={data.description}
                platformName={data.connections?.platform?.name || 'null'}
                platformLogo={data.connections?.platform?.info?.logoUrl || ''}
                owners={data.ownership?.owners}
                globalTags={data.globalTags}
                glossaryTerms={data.glossaryTerms}
            />
        );
    };

    renderSearch = (result: SearchResult) => {
        const data = result.entity as Datasource;
        return (
            <PreviewNoDel
                urn={data.urn}
                name={data.name}
                origin={data.origin}
                description={data.description}
                platformName={data.connections?.platform?.name || 'null'}
                platformLogo={data.connections?.platform?.info?.logoUrl || ''}
                owners={data.ownership?.owners}
                globalTags={data.globalTags}
                snippet={
                    // Add match highlights only if all the matched fields are in the FIELDS_TO_HIGHLIGHT
                    result.matchedFields.length > 0 &&
                    result.matchedFields.every((field) => FIELDS_TO_HIGHLIGHT.has(field.name)) && (
                        <MatchTag>
                            <Typography.Text>
                                Matches {FIELDS_TO_HIGHLIGHT.get(result.matchedFields[0].name)}{' '}
                                <b>{result.matchedFields[0].value}</b>
                            </Typography.Text>
                        </MatchTag>
                    )
                }
            />
        );
    };

    getLineageVizConfig = (entity: Datasource) => {
        return {
            urn: entity.urn,
            name: entity.name,
            type: EntityType.Datasource,
            upstreamChildren: getChildren({ entity, type: EntityType.Datasource }, Direction.Upstream).map(
                (child) => child.entity.urn,
            ),
            downstreamChildren: getChildren({ entity, type: EntityType.Datasource }, Direction.Downstream).map(
                (child) => child.entity.urn,
            ),
            icon: entity.connections?.platform?.info?.logoUrl || undefined,
            platform: entity.connections?.platform?.name,
        };
    };
}
