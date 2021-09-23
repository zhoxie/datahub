import { List, Typography } from 'antd';
import React from 'react';
import { Datasource } from '../../../../types.generated';
import { PreviewNoDel } from '../../datasource/preview/Preview';

const styles = {
    list: { marginTop: '12px', padding: '16px 32px' },
    item: { paddingTop: '20px' },
};

export type Props = {
    datasources: Array<Datasource>;
};

export default function DatasetSources({ datasources }: Props) {
    return (
        <List
            style={styles.list}
            bordered
            dataSource={datasources}
            header={<Typography.Title level={3}>Source Datasources</Typography.Title>}
            renderItem={(item) => (
                <List.Item style={styles.item}>
                    <PreviewNoDel
                        urn={item.urn}
                        name={item.name}
                        origin={item.origin}
                        description={item.description}
                        platformName={item.connections?.platform?.name || 'null'}
                        platformLogo={item.connections?.platform?.info?.logoUrl || ''}
                        owners={item.ownership?.owners}
                        globalTags={item.globalTags}
                        glossaryTerms={item.glossaryTerms}
                    />
                </List.Item>
            )}
        />
    );
}
