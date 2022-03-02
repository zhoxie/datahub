import { List, Typography } from 'antd';
import React from 'react';
import { EntityType } from '../../../../../../types.generated';
import { useEntityRegistry } from '../../../../../useEntityRegistry';
import { useBaseEntity } from '../../../EntityContext';
import { PreviewType } from '../../../../Entity';

export const Datasets = () => {
    const styles = {
        list: { marginTop: '12px', padding: '16px 32px' },
        item: { paddingTop: '20px' },
    };
    const entityRegistry = useEntityRegistry();
    const entity = useBaseEntity() as any;
    const datasource = entity && entity.datasource;
    const datasets = datasource?.datasets?.relationships.map((relationship) => relationship.entity);
    return (
        <List
            style={styles.list}
            bordered
            dataSource={datasets}
            header={<Typography.Title level={3}>Datasets</Typography.Title>}
            renderItem={(item) => (
                <List.Item style={styles.item}>
                    {entityRegistry.renderPreview(EntityType.Dataset, PreviewType.PREVIEW, item)}
                </List.Item>
            )}
        />
    );
};
