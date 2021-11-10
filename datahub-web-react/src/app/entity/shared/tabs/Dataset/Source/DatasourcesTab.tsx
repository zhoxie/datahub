import { List, Typography } from 'antd';
import React from 'react';
import { useBaseEntity } from '../../../EntityContext';
import { EntityType } from '../../../../../../types.generated';
import { useEntityRegistry } from '../../../../../useEntityRegistry';
import { PreviewType } from '../../../../Entity';

export const DatasourcesTab = () => {
    const styles = {
        list: { marginTop: '12px', padding: '16px 32px' },
        item: { paddingTop: '20px' },
    };
    const entity = useBaseEntity() as any;
    const dataset = entity && entity.dataset;
    const datasource = dataset?.sources?.relationships.map((relationship) => relationship.entity);
    const entityRegistry = useEntityRegistry();
    return (
        <List
            style={styles.list}
            bordered
            dataSource={datasource}
            header={<Typography.Title level={3}>Sources</Typography.Title>}
            renderItem={(item) => (
                <List.Item style={styles.item}>
                    {entityRegistry.renderPreview(EntityType.Datasource, PreviewType.PREVIEW, item)}
                </List.Item>
            )}
        />
    );
};
