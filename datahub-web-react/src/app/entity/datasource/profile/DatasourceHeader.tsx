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

// const HeaderInfoItem = styled.div`
//     display: inline-block;
//     text-align: left;
//     width: 125px;
//     vertical-align: top;
// `;

// const HeaderInfoItems = styled.div`
//     display: inline-block;
//     margin-top: -16px;
//     vertical-align: top;
// `;
// const PreviewImage = styled(Image)`
//     max-height: 20px;
//     padding-top: 3px;
//     width: auto;
//     object-fit: contain;
// `;

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

    console.log(platform);

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
                {/* <HeaderInfoItems>
                    <HeaderInfoItem>
                        <div>
                            <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                                Platform
                            </Typography.Text>
                        </div>
                        <Space direction="horizontal">
                            {platformLogoUrl && (
                                <PreviewImage preview={false} src={platformLogoUrl} placeholder alt={platformName} />
                            )}
                            <Typography.Text style={{ fontSize: 16 }}>{platformName}</Typography.Text>
                        </Space>
                        <div>
                            <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                                category
                            </Typography.Text>
                        </div>
                        <Space direction="horizontal">
                            <Typography.Text style={{ fontSize: 16 }}>{category}</Typography.Text>
                        </Space>
                        <div>
                            <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                                dataCenter
                            </Typography.Text>
                        </div>
                        <Space direction="horizontal">
                            <Typography.Text style={{ fontSize: 16 }}>{dataCenter}</Typography.Text>
                        </Space>
                    </HeaderInfoItem>
                    {usageStats?.aggregations?.totalSqlQueries && (
                        <HeaderInfoItem>
                            <div>
                                <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                                    Queries / month
                                </Typography.Text>
                            </div>
                            <span>
                                <Typography.Text style={{ fontSize: 16 }}>
                                    {usageStats?.aggregations?.totalSqlQueries}
                                </Typography.Text>
                            </span>
                        </HeaderInfoItem>
                    )}
                    {(usageStats?.aggregations?.users?.length || 0) > 0 && (
                        <HeaderInfoItem>
                            <div>
                                <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                                    Top Users
                                </Typography.Text>
                            </div>
                            <div>
                                <UsageFacepile users={usageStats?.aggregations?.users} />
                            </div>
                        </HeaderInfoItem>
                    )}
                </HeaderInfoItems> */}
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
