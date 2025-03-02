import React, { useCallback } from 'react';
import { Button, List } from 'antd';
import { useHistory } from 'react-router';
import styled from 'styled-components';
import { PartitionOutlined } from '@ant-design/icons';

import { useEntityData } from '../../../EntityContext';
import TabToolbar from '../../../components/styled/TabToolbar';
import { getEntityPath } from '../../../containers/profile/utils';
import { useEntityRegistry } from '../../../../../useEntityRegistry';
import { PreviewType } from '../../../../Entity';
import { EntityType } from '../../../../../../types.generated';

const DatasetList = styled(List)`
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

export const DatasetTab = () => {
    const { urn, entityType, entityData } = useEntityData();
    const history = useHistory();
    const entityRegistry = useEntityRegistry();

    const routeToLineage = useCallback(() => {
        history.push(getEntityPath(entityType, urn, entityRegistry, true));
    }, [history, entityType, urn, entityRegistry]);

    const entities = entityData?.datasets?.entities?.map((entityRelationship) => entityRelationship?.entity);

    return (
        <>
            <TabToolbar>
                <Button type="text" onClick={routeToLineage}>
                    <PartitionOutlined />
                    Visualize Lineage
                </Button>
            </TabToolbar>
            <DatasetList
                bordered
                dataSource={entities}
                header={`${entities?.length} Datasets`}
                renderItem={(item) => (
                    <List.Item style={{ paddingTop: '20px' }}>
                        {entityRegistry.renderPreview(item?.type || EntityType.Dataset, PreviewType.PREVIEW, item)}
                    </List.Item>
                )}
            />
        </>
    );
};
