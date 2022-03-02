import { Space, Card, Row, Col, Typography } from 'antd';
import React from 'react';
import { GetDatasourceQuery } from '../../../../../../graphql/datasource.generated';
import { useBaseEntity } from '../../../EntityContext';
import { Platforms } from './DataSourceType';

export const Sources = () => {
    const baseEntity = useBaseEntity<GetDatasourceQuery>();

    const isKafka = () => {
        return baseEntity?.datasource?.platform?.name === Platforms.kafka;
    };

    const isIceBerge = () => {
        return baseEntity?.datasource?.platform?.name === Platforms.iceberg;
    };

    const isOracle = () => {
        return baseEntity?.datasource?.platform?.name === Platforms.oracle;
    };

    const isTrino = () => {
        return baseEntity?.datasource?.platform?.name === Platforms.trino;
    };

    const isPinot = () => {
        return baseEntity?.datasource?.platform?.name === Platforms.pinot;
    };

    const isPresto = () => {
        return baseEntity?.datasource?.platform?.name === Platforms.presto;
    };

    const isTiDB = () => {
        return baseEntity?.datasource?.platform?.name === Platforms.tiDB;
    };

    const isHive = () => {
        return baseEntity?.datasource?.platform?.name === Platforms.hive;
    };

    const isMysql = () => {
        return baseEntity?.datasource?.platform?.name === Platforms.mysql;
    };

    const isPostgres = () => {
        return baseEntity?.datasource?.platform?.name === Platforms.postgres;
    };

    const getCatalogSchemaRows = (conn: any) => {
        return (
            <>
                <Row>
                    <Col span={24}>
                        <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                            Catalog
                        </Typography.Text>
                        <br />
                        <Typography.Text style={{ fontSize: 16 }}>{conn.catalog}</Typography.Text>
                    </Col>
                </Row>
                <Row>
                    <Col span={24}>
                        <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                            Schema
                        </Typography.Text>
                        <br />
                        <Typography.Text style={{ fontSize: 16 }}>{conn.schema}</Typography.Text>
                    </Col>
                </Row>
            </>
        );
    };

    const getTNSRow = (conn: any) => {
        return (
            <>
                <Row>
                    <Col span={24}>
                        <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                            TNS
                        </Typography.Text>
                        <br />
                        <Typography.Text style={{ fontSize: 16 }}>{conn.tnsName}</Typography.Text>
                    </Col>
                </Row>
            </>
        );
    };

    const getServiceNameRow = (conn: any) => {
        return (
            <>
                <Row>
                    <Col span={24}>
                        <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                            Service Name
                        </Typography.Text>
                        <br />
                        <Typography.Text style={{ fontSize: 16 }}>{conn.serviceName}</Typography.Text>
                    </Col>
                </Row>
            </>
        );
    };

    const getJDBCParamsRow = (conn: any) => {
        return (
            <>
                <Row>
                    <Col span={24}>
                        <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                            JDBC Params
                        </Typography.Text>
                        <br />
                        <Typography.Text style={{ fontSize: 16 }}>{conn.jdbcParams}</Typography.Text>
                    </Col>
                </Row>
            </>
        );
    };

    const getDatabaseRow = (conn: any) => {
        return (
            <>
                <Row>
                    <Col span={24}>
                        <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                            Database
                        </Typography.Text>
                        <br />
                        <Typography.Text style={{ fontSize: 16 }}>{conn.database}</Typography.Text>
                    </Col>
                </Row>
            </>
        );
    };

    const getHostPortRow = (conn: any) => {
        return (
            <>
                <Row>
                    <Col span={24}>
                        <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                            Host Port
                        </Typography.Text>
                        <br />
                        <Typography.Text style={{ fontSize: 16 }}>{conn.hostPort}</Typography.Text>
                    </Col>
                </Row>
            </>
        );
    };

    const getKafkaRows = (conn: any) => {
        return (
            <>
                <Row>
                    <Col span={24}>
                        <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                            Bootstrap Servers
                        </Typography.Text>
                        <br />
                        <Typography.Text style={{ fontSize: 16 }}>{conn.bootstrap}</Typography.Text>
                    </Col>
                </Row>
                <Row>
                    <Col span={24}>
                        <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                            Schema Registry URL
                        </Typography.Text>
                        <br />
                        <Typography.Text style={{ fontSize: 16 }}>{conn.schemaRegistryUrl}</Typography.Text>
                    </Col>
                </Row>
            </>
        );
    };

    const getIceBergeRows = (conn: any) => {
        return (
            <>
                <Row>
                    <Col span={24}>
                        <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                            URI
                        </Typography.Text>
                        <br />
                        <Typography.Text style={{ fontSize: 16 }}>{conn.hiveMetastoreUris}</Typography.Text>
                    </Col>
                </Row>
            </>
        );
    };

    const primary = baseEntity?.datasource?.primaryConn;
    const primaryConn = baseEntity?.datasource?.primaryConn?.connection;
    const gsb = baseEntity?.datasource?.gsbConn;
    const gsbConn = baseEntity?.datasource?.gsbConn?.connection;

    return (
        <>
            <Space direction="vertical" style={{ width: '100%' }} size="large">
                {primaryConn && (
                    <Card title="Primary Connection Information" key="53463463" style={{ width: '100%' }}>
                        <Row>
                            <Col span={24}>
                                <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                                    Data Center
                                </Typography.Text>
                                <br />
                                <Typography.Text style={{ fontSize: 16 }}>{primary?.dataCenter}</Typography.Text>
                            </Col>
                        </Row>

                        {isOracle() && (primaryConn as any).serviceName && getHostPortRow(primaryConn)}
                        {isOracle() && (primaryConn as any).serviceName && getServiceNameRow(primaryConn)}
                        {isOracle() && (primaryConn as any).tnsName && getTNSRow(primaryConn)}

                        {isKafka() && getKafkaRows(primaryConn)}

                        {isIceBerge() && getIceBergeRows(primaryConn)}

                        {isTrino() && getHostPortRow(primaryConn)}
                        {isTrino() && getCatalogSchemaRows(primaryConn)}
                        {isTrino() && getJDBCParamsRow(primaryConn)}

                        {isPinot() && getHostPortRow(primaryConn)}

                        {isPresto() && getHostPortRow(primaryConn)}
                        {isPresto() && getCatalogSchemaRows(primaryConn)}
                        {isPresto() && getJDBCParamsRow(primaryConn)}

                        {isTiDB() && getHostPortRow(primaryConn)}
                        {isTiDB() && getDatabaseRow(primaryConn)}
                        {isTiDB() && getJDBCParamsRow(primaryConn)}

                        {isHive() && getHostPortRow(primaryConn)}
                        {isHive() && getDatabaseRow(primaryConn)}
                        {isHive() && getJDBCParamsRow(primaryConn)}

                        {isMysql() && getHostPortRow(primaryConn)}
                        {isMysql() && getDatabaseRow(primaryConn)}
                        {isMysql() && getJDBCParamsRow(primaryConn)}

                        {isPostgres() && getHostPortRow(primaryConn)}
                        {isPostgres() && getDatabaseRow(primaryConn)}
                        {isPostgres() && getJDBCParamsRow(primaryConn)}
                    </Card>
                )}
                {gsb && (
                    <Card title="GSB Connection Information" key="53463463" style={{ width: '100%' }}>
                        <Row>
                            <Col span={24}>
                                <Typography.Text strong type="secondary" style={{ fontSize: 11 }}>
                                    Data Center
                                </Typography.Text>
                                <br />
                                <Typography.Text style={{ fontSize: 16 }}>{gsb?.dataCenter}</Typography.Text>
                            </Col>
                        </Row>

                        {isOracle() && (gsbConn as any).serviceName && getHostPortRow(gsbConn)}
                        {isOracle() && (gsbConn as any).serviceName && getServiceNameRow(gsbConn)}
                        {isOracle() && (gsbConn as any).tnsName && getTNSRow(gsbConn)}

                        {isKafka() && getKafkaRows(gsbConn)}

                        {isIceBerge() && getIceBergeRows(gsbConn)}

                        {isTrino() && getHostPortRow(gsbConn)}
                        {isTrino() && getCatalogSchemaRows(gsbConn)}
                        {isTrino() && getJDBCParamsRow(gsbConn)}

                        {isPinot() && getHostPortRow(gsbConn)}

                        {isPresto() && getHostPortRow(gsbConn)}
                        {isPresto() && getCatalogSchemaRows(gsbConn)}
                        {isPresto() && getJDBCParamsRow(gsbConn)}

                        {isTiDB() && getHostPortRow(gsbConn)}
                        {isTiDB() && getDatabaseRow(gsbConn)}
                        {isTiDB() && getJDBCParamsRow(gsbConn)}

                        {isHive() && getHostPortRow(gsbConn)}
                        {isHive() && getDatabaseRow(gsbConn)}
                        {isHive() && getJDBCParamsRow(gsbConn)}

                        {isMysql() && getHostPortRow(gsbConn)}
                        {isMysql() && getDatabaseRow(gsbConn)}
                        {isMysql() && getJDBCParamsRow(gsbConn)}

                        {isPostgres() && getHostPortRow(gsbConn)}
                        {isPostgres() && getDatabaseRow(gsbConn)}
                        {isPostgres() && getJDBCParamsRow(gsbConn)}
                    </Card>
                )}
            </Space>
        </>
    );
};
