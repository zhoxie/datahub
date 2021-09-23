import { List, Typography } from 'antd';
import React from 'react';
import { EntityType, UpstreamEntityRelationships } from '../../../../types.generated';
import { useEntityRegistry } from '../../../useEntityRegistry';
import { PreviewType } from '../../Entity';

const styles = {
    list: { marginTop: '12px', padding: '16px 32px' },
    item: { paddingTop: '20px' },
};

export type Props = {
    upstreamLineage?: UpstreamEntityRelationships | null;
};

export default function DatasourceDatasets({ upstreamLineage }: Props) {
    const entityRegistry = useEntityRegistry();
    const upstreamEntities = upstreamLineage?.entities?.map((entityRelationship) => entityRelationship?.entity);
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
}
