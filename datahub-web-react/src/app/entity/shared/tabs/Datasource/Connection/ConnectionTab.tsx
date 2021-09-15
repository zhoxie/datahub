import { Card, Col, Input, Row, Space, Typography } from 'antd';
import React from 'react';
import styled from 'styled-components';
import DatasourceVertify from './DatasourceVertify';
import { useEntityData } from '../../../EntityContext';

const ViewRawButtonContainer = styled.div`
    display: flex;
    justify-content: flex-end;
`;

export const ConnectionTab = () => {
    const { entityData } = useEntityData();
    return (
        <>
            <Space direction="vertical" style={{ width: '100%' }} size="large">
                {entityData?.connections?.connections?.map((item, ix: number) => {
                    return (
                        <Card title="Connection Information" key={item?.url} style={{ width: '100%' }}>
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
                                    <Typography.Text style={{ fontSize: 16 }}>
                                        <Input.Password bordered={false} defaultValue={item?.password || ''} readOnly />
                                    </Typography.Text>
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
                                        <DatasourceVertify connections={entityData?.connections} id={ix} />
                                    </ViewRawButtonContainer>
                                </Col>
                            </Row>
                        </Card>
                    );
                })}
            </Space>
        </>
    );
};
