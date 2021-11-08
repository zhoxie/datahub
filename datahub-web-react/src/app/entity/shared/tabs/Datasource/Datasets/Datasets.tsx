import { List, Typography } from 'antd';
import React from 'react';
import { EntityType } from '../../../../../../types.generated';
import { useEntityRegistry } from '../../../../../useEntityRegistry';
import { useLineageData } from '../../../EntityContext';
import { PreviewType } from '../../../../Entity';

export const Datasets = () => {
    const styles = {
        list: { marginTop: '12px', padding: '16px 32px' },
        item: { paddingTop: '20px' },
    };
    const entityRegistry = useEntityRegistry();
    const lineage = useLineageData();
    const upstreamEntities = lineage?.upstreamChildren?.map((result) => result.entity);
    return (
        <List
            style={styles.list}
            bordered
            dataSource={upstreamEntities}
            header={<Typography.Title level={3}>Datasets</Typography.Title>}
            renderItem={(item) => (
                <List.Item style={styles.item}>
                    {entityRegistry.renderPreview(EntityType.Dataset, PreviewType.PREVIEW, item)}
                </List.Item>
            )}
        />
    );
};
