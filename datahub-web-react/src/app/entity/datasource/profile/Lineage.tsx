import { Button, Space, Typography, Table, Card, Row, Col } from 'antd';
import React from 'react';
import { ColumnsType } from 'antd/es/table';
import { useHistory, useLocation } from 'react-router-dom';
import styled from 'styled-components';
import { Datasource, StringMapEntry } from '../../../../types.generated';
import { navigateToLineageUrl } from '../../../lineage/utils/navigateToLineageUrl';

export type Props = {
    datasource: Datasource;
};

const ViewRawButtonContainer = styled.div`
    display: flex;
    justify-content: flex-end;
`;

export default function Lineage({ datasource }: Props) {
    const history = useHistory();
    const location = useLocation();

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
            name: datasource.name,
            type: datasource.platform.name,
            category: datasource.connections?.category,
            dataCenter: datasource.connections?.dataCenter,
        },
    ];

    return (
        <>
            <Space direction="vertical" style={{ width: '100%' }} size="large">
                <Table pagination={false} columns={datasourceInfoColumns} dataSource={datasourceDemoValues} />
                {datasource.connections?.connections?.map((item) => {
                    return (
                        <Card title="Connection Information" style={{ width: '100%' }}>
                            <Row>
                                <Col span={8}>
                                    <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                                        Cluster
                                    </Typography.Text>
                                    <br />
                                    <Typography.Text style={{ fontSize: 16 }}>{item?.cluster}</Typography.Text>
                                </Col>
                                <Col span={8}>
                                    <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                                        Username
                                    </Typography.Text>
                                    <br />
                                    <Typography.Text style={{ fontSize: 16 }}>{item?.username}</Typography.Text>
                                </Col>
                                <Col span={8}>
                                    <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                                        Password
                                    </Typography.Text>
                                    <br />
                                    <Typography.Text style={{ fontSize: 16 }}>{item?.password}</Typography.Text>
                                </Col>
                            </Row>
                            <Row>
                                <Col span={8}>
                                    <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                                        Driver
                                    </Typography.Text>
                                    <br />
                                    <Typography.Text style={{ fontSize: 16 }}>{item?.driver}</Typography.Text>
                                </Col>
                                <Col span={16}>
                                    <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                                        URL
                                    </Typography.Text>
                                    <br />
                                    <Typography.Text style={{ fontSize: 16 }}>{item?.url}</Typography.Text>
                                </Col>
                            </Row>
                            <Row>
                                <Col span={24}>
                                    <ViewRawButtonContainer>
                                        <Button
                                            onClick={() =>
                                                navigateToLineageUrl({ location, history, isLineageMode: true })
                                            }
                                        >
                                            Test Connection
                                        </Button>
                                    </ViewRawButtonContainer>
                                </Col>
                            </Row>
                        </Card>
                    );
                })}
                <Card title="Connection Information" style={{ width: '100%' }}>
                    <Row>
                        <Col span={8}>
                            <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                                Cluster
                            </Typography.Text>
                            <br />
                            <Typography.Text style={{ fontSize: 16 }}>PRIMARY</Typography.Text>
                        </Col>
                        <Col span={8}>
                            <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                                Username
                            </Typography.Text>
                            <br />
                            <Typography.Text style={{ fontSize: 16 }}>postgres</Typography.Text>
                        </Col>
                        <Col span={8}>
                            <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                                Password
                            </Typography.Text>
                            <br />
                            <Typography.Text style={{ fontSize: 16 }}>********</Typography.Text>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={8}>
                            <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                                Driver
                            </Typography.Text>
                            <br />
                            <Typography.Text style={{ fontSize: 16 }}>org.postgresql.Driver</Typography.Text>
                        </Col>
                        <Col span={16}>
                            <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                                URL
                            </Typography.Text>
                            <br />
                            <Typography.Text style={{ fontSize: 16 }}>
                                jdbc:postgresql://10.100.13.100/pg_sql
                            </Typography.Text>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={24}>
                            <ViewRawButtonContainer>
                                <Button
                                    onClick={() => navigateToLineageUrl({ location, history, isLineageMode: true })}
                                >
                                    Test Connection
                                </Button>
                            </ViewRawButtonContainer>
                        </Col>
                    </Row>
                </Card>
                <Card title="Connection Information" style={{ width: '100%' }}>
                    <Row>
                        <Col span={8}>
                            <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                                Cluster
                            </Typography.Text>
                            <br />
                            <Typography.Text style={{ fontSize: 16 }}>GSB</Typography.Text>
                        </Col>
                        <Col span={8}>
                            <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                                Username
                            </Typography.Text>
                            <br />
                            <Typography.Text style={{ fontSize: 16 }}>postgres</Typography.Text>
                        </Col>
                        <Col span={8}>
                            <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                                Password
                            </Typography.Text>
                            <br />
                            <Typography.Text style={{ fontSize: 16 }}>********</Typography.Text>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={8}>
                            <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                                Driver
                            </Typography.Text>
                            <br />
                            <Typography.Text style={{ fontSize: 16 }}>org.postgresql.Driver</Typography.Text>
                        </Col>
                        <Col span={16}>
                            <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                                URL
                            </Typography.Text>
                            <br />
                            <Typography.Text style={{ fontSize: 16 }}>
                                jdbc:postgresql://10.100.13.100/pg_sql
                            </Typography.Text>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={24}>
                            <ViewRawButtonContainer>
                                <Button
                                    onClick={() => navigateToLineageUrl({ location, history, isLineageMode: true })}
                                >
                                    Test Connection
                                </Button>
                            </ViewRawButtonContainer>
                        </Col>
                    </Row>
                </Card>
            </Space>
        </>
    );
}
