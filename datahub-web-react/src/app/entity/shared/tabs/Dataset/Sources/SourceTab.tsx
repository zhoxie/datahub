import React, { useCallback } from 'react';
import { Button, List } from 'antd';
import { useHistory } from 'react-router';
import styled from 'styled-components';
import { PartitionOutlined } from '@ant-design/icons';

import { useEntityData } from '../../../EntityContext';
import TabToolbar from '../../../components/styled/TabToolbar';
import { getEntityPath } from '../../../containers/profile/utils';
import { useEntityRegistry } from '../../../../../useEntityRegistry';
import { EntityType } from '../../../../../../types.generated';
import { capitalizeFirstLetter } from '../../../../../shared/capitalizeFirstLetter';
import DefaultPreviewCard from '../../../../../preview/DefaultPreviewCard';

const DatasourceList = styled(List)`
    padding-left: 40px;
    padding-right: 40px;
    margin-top: -1px;
    .ant-list-items > .ant-list-item {
        padding-right: 0px;
        padding-left: 0px;
    }
    > .ant-list-header {
        padding-right: 0px;
        padding-left: 0px;
        font-size: 14px;
        font-weight: 600;
        margin-left: -20px;
        border-bottom: none;
        padding-bottom: 0px;
        padding-top: 15px;
    }
` as typeof List;

export const DatasourceTab = () => {
    const { urn, entityType, entityData } = useEntityData();
    const history = useHistory();
    const entityRegistry = useEntityRegistry();

    const routeToLineage = useCallback(() => {
        history.push(getEntityPath(entityType, urn, entityRegistry, true));
    }, [history, entityType, urn, entityRegistry]);

    const entities = entityData?.sources?.sources || [];

    return (
        <>
            <TabToolbar>
                <Button type="text" onClick={routeToLineage}>
                    <PartitionOutlined />
                    Visualize Lineage
                </Button>
            </TabToolbar>
            <DatasourceList
                bordered
                dataSource={entities}
                header={`${entities?.length} Sources`}
                renderItem={(item) => (
                    <List.Item style={{ paddingTop: '20px' }}>
                        <DefaultPreviewCard
                            url={entityRegistry.getEntityUrl(EntityType.Datasource, item.urn)}
                            name={item.name || ''}
                            description={item.description || ''}
                            type="Datasource"
                            logoUrl={item.connections?.platform?.info?.logoUrl || ''}
                            platform={capitalizeFirstLetter(item.connections?.platform?.name || 'null')}
                            qualifier={item.origin}
                            tags={item.globalTags || undefined}
                            owners={item.ownership?.owners}
                            glossaryTerms={item.glossaryTerms || undefined}
                        />
                    </List.Item>
                )}
            />
        </>
    );
};
