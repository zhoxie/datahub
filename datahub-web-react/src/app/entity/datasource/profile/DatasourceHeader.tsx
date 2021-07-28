import { FetchResult, MutationFunctionOptions } from '@apollo/client';
import { Badge, Popover, Space, Table, Typography } from 'antd';
import { ColumnsType } from 'antd/es/table';
import React from 'react';
import { Datasource, StringMapEntry } from '../../../../types.generated';
import { AvatarsGroup } from '../../../shared/avatar';
import { capitalizeFirstLetter } from '../../../shared/capitalizeFirstLetter';
import CompactContext from '../../../shared/CompactContext';
import { useEntityRegistry } from '../../../useEntityRegistry';
import UpdatableDescription from '../../shared/UpdatableDescription';

export type Props = {
    datasource: Datasource;
    updateDatasource: (options?: MutationFunctionOptions<any, any> | undefined) => Promise<FetchResult>;
};

export default function DatasourceHeader({
    datasource: {
        name,
        urn,
        type,
        description: originalDesc,
        ownership,
        deprecation,
        platform,
        editableProperties,
        // usageStats,
        connections,
    },
    updateDatasource,
}: Props) {
    const entityRegistry = useEntityRegistry();
    const isCompact = React.useContext(CompactContext);
    const platformName = capitalizeFirstLetter(platform.name);
    // const platformLogoUrl = platform.info?.logoUrl;
    const category = connections?.category;
    const dataCenter = connections?.dataCenter;

    const datasourceInfoColumns: ColumnsType<StringMapEntry> = [
        {
            title: 'Name',
            dataIndex: 'name',
        },
        {
            title: 'Type',
            dataIndex: 'type',
        },
        {
            title: 'Category',
            dataIndex: 'category',
        },
        {
            title: 'Data Center',
            dataIndex: 'dataCenter',
        },
    ];

    const datasourceDemoValues = [
        {
            key: '1',
            name,
            type: platformName,
            category,
            dataCenter,
        },
    ];

    return (
        <>
            <Space direction="vertical" size="middle" style={{ width: '100%' }}>
                <Table pagination={false} columns={datasourceInfoColumns} dataSource={datasourceDemoValues} />
                <UpdatableDescription
                    isCompact={isCompact}
                    updateEntity={updateDatasource}
                    updatedDescription={editableProperties?.description}
                    originalDescription={originalDesc}
                    urn={urn}
                    entityType={type}
                />
                <AvatarsGroup owners={ownership?.owners} entityRegistry={entityRegistry} size="large" />
                <div>
                    {deprecation?.deprecated && (
                        <Popover
                            placement="bottomLeft"
                            content={
                                <>
                                    <Typography.Paragraph>By: {deprecation?.actor}</Typography.Paragraph>
                                    {deprecation.decommissionTime && (
                                        <Typography.Paragraph>
                                            On: {new Date(deprecation?.decommissionTime).toUTCString()}
                                        </Typography.Paragraph>
                                    )}
                                    {deprecation?.note && (
                                        <Typography.Paragraph>{deprecation.note}</Typography.Paragraph>
                                    )}
                                </>
                            }
                            title="Deprecated"
                        >
                            <Badge count="Deprecated" />
                        </Popover>
                    )}
                </div>
            </Space>
        </>
    );
}
