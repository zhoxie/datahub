import { Button, Card, Col, Row, Space, Typography } from 'antd';
import React from 'react';
import { useHistory, useLocation } from 'react-router-dom';
import styled from 'styled-components';
import { Datasource, DatasourceConnections } from '../../../../types.generated';
import { navigateToLineageUrl } from '../../../lineage/utils/navigateToLineageUrl';

export type Props = {
    datasource: Datasource;
    connections: DatasourceConnections;
    btns?: (datasource: Datasource, id: number) => any;
};

const ViewRawButtonContainer = styled.div`
    display: flex;
    justify-content: flex-end;
`;

export default function Lineage({ datasource, btns }: Props) {
    const history = useHistory();
    const location = useLocation();
    const getBtns = (id: number) => {
        if (btns) {
            return btns(datasource, id);
        }
        return (
            <Button onClick={() => navigateToLineageUrl({ location, history, isLineageMode: true })}>
                Test Lineage Url
            </Button>
        );
    };

    const getPassword = (pwd: string) => {
        if (!pwd) {
            return '';
        }
        const arr: string[] = [];
        for (let i = 0; i < pwd.length; i++) {
            arr.push('*');
        }
        return arr.join('');
    };
    return (
        <>
            <Space direction="vertical" style={{ width: '100%' }} size="large">
                {datasource.connections?.connections?.map((item, ix: number) => {
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
                                        {getPassword(item?.password || '')}
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
                                    <ViewRawButtonContainer>{getBtns(ix)}</ViewRawButtonContainer>
                                </Col>
                            </Row>
                        </Card>
                    );
                })}
            </Space>
        </>
    );
}
