import { DeleteOutlined } from '@ant-design/icons';
import { Checkbox, Button, Card, Form, Input, Modal, Space, Select, Alert } from 'antd';
import React, { useState } from 'react';
import { FormField, IDatasourceSourceInput, IFormConnectionData, IFormData } from '../service/DataSouceType';
import { showMessageByNotification, showRequestResult } from '../service/NotificationUtil';
import {
    sourceTypeList,
    DbSourceTypeData,
    groupList as defaultGroupList,
    dataCenterList,
    regionList,
} from '../service/FormInitValue';
import { useCreateDatasourceMutation, useTestDatasourceMutation } from '../../../../graphql/datasource.generated';
import { CorpGroup, DatasourceCreateInput } from '../../../../types.generated';
import { useGetUserQuery } from '../../../../graphql/user.generated';
import { Message } from '../../../shared/Message';

const messageStyle = { marginTop: '10%' };

const { Option } = Select;

type AddDataSourceModalProps = {
    visible: boolean;
    onClose: () => void;
    title: string;
    originData?: any;
    corpUserUrn: any;
};

const layout = {
    labelCol: { span: 4 },
    wrapperCol: { span: 20 },
};
export default function AddDataSourceModal({
    visible,
    onClose,
    title,
    originData,
    corpUserUrn,
}: AddDataSourceModalProps) {
    let count = 1; // when originData exists ,show the edit
    const [createDatasourceMutation] = useCreateDatasourceMutation();
    const [testDatasourceMutation] = useTestDatasourceMutation();
    const urn = corpUserUrn;
    const { loading, error, data } = useGetUserQuery({ variables: { urn, groupsCount: 20 } });

    const relationships = data?.corpUser?.relationships;
    const groupList = relationships?.relationships?.map((rel) => rel.entity as CorpGroup) || defaultGroupList;
    const [saveLoading, updateLoading] = useState(false);
    const [testLoading, updateTestLoading] = useState(false);

    const initData: IFormData = originData ?? {
        sourceType: sourceTypeList[0].value,
        name: '',
        syncCDAPI: false,
        create: true,
        group: groupList[0]?.urn,
        region: regionList[0]?.value,
        oracleTNSType: 'tns',
        connections: [
            {
                id: 1,
                username: '',
                password: '',
                hostPort: '',
                bootstrap: '',
                schemaPatternAllow: '',
                tablePatternAllow: '',
                topicPatternsAllow: '',
                hiveMetastoreUris: '',
                dataCenter: dataCenterList[0]?.value,
            },
        ],
    };

    const [formData, updateDataSourceFormData] = useState(initData);
    const [form] = Form.useForm();

    if (error || (!loading && !error && !data)) {
        return <Alert type="error" message={error?.message || 'Entity failed to load'} />;
    }

    const showValidateMsg = (msg) => {
        showMessageByNotification(msg);
    };

    const databaseRequired =
        formData.sourceType === DbSourceTypeData.TiDB ||
        formData.sourceType === DbSourceTypeData.Postgres ||
        formData.sourceType === DbSourceTypeData.Hive ||
        formData.sourceType === DbSourceTypeData.Mysql;

    const enableSync =
        formData.sourceType === DbSourceTypeData.Oracle ||
        formData.sourceType === DbSourceTypeData.TiDB ||
        formData.sourceType === DbSourceTypeData.Pinot ||
        formData.sourceType === DbSourceTypeData.Postgres ||
        formData.sourceType === DbSourceTypeData.Hive ||
        formData.sourceType === DbSourceTypeData.presto ||
        formData.sourceType === DbSourceTypeData.trino;

    const isInKafka = () => {
        return formData.sourceType === DbSourceTypeData.Kafka;
    };

    const isIceBerge = () => {
        return formData.sourceType === DbSourceTypeData.Iceberg;
    };

    const isOracle = () => {
        return formData.sourceType === DbSourceTypeData.Oracle;
    };

    const isTrino = () => {
        return formData.sourceType === DbSourceTypeData.trino;
    };

    const isPinot = () => {
        return formData.sourceType === DbSourceTypeData.Pinot;
    };

    const isPresto = () => {
        return formData.sourceType === DbSourceTypeData.presto;
    };

    const isTiDB = () => {
        return formData.sourceType === DbSourceTypeData.TiDB;
    };

    const isHive = () => {
        return formData.sourceType === DbSourceTypeData.Hive;
    };

    const isMysql = () => {
        return formData.sourceType === DbSourceTypeData.Mysql;
    };

    const isPostgres = () => {
        return formData.sourceType === DbSourceTypeData.Postgres;
    };

    const isOracleTNSType = () => {
        return formData.oracleTNSType === 'tns';
    };

    const checkFormData = () => {
        if (!formData) {
            return false;
        }
        const { sourceType, name, group, region } = formData;
        const isBasicOK = !!sourceType && !!name && !!group && !!region;
        let isOk = isBasicOK;
        if (!isBasicOK) {
            return false;
        }
        if (isIceBerge()) {
            isOk = !formData.connections?.some((item) => {
                return item.hiveMetastoreUris === '';
            });
        } else if (isInKafka()) {
            isOk = !formData.connections?.some((item) => {
                return item.topicPatternsAllow === '' || item.bootstrap === '';
            });
        } else if (isPinot() || isTrino() || isPresto()) {
            isOk = !formData.connections?.some((item) => {
                return item.username === '' || item.password === '' || item.hostPort === '';
            });
        } else if (isHive() || isMysql() || isTiDB() || isPostgres()) {
            isOk = !formData.connections?.some((item) => {
                return item.username === '' || item.password === '' || item.hostPort === '' || item.database === '';
            });
        } else if (isOracle()) {
            isOk = !formData.connections?.some((item) => {
                return (
                    item.username === '' ||
                    item.password === '' ||
                    ((item.hostPort === '' || item.serviceName === '') && item.tnsName === '')
                );
            });
        } else {
            isOk = !formData.connections?.some((item) => {
                return item.username === '' || item.password === '' || item.hostPort === '';
            });
        }
        return isOk;
    };

    const getDataSourceInputData = () => {
        const dataSources: IDatasourceSourceInput[] = formData.connections?.map((conn) => {
            const dataSource: IDatasourceSourceInput = {
                dataCenter: conn.dataCenter,
            };
            switch (formData.sourceType) {
                case DbSourceTypeData.Iceberg: {
                    dataSource[`${formData.sourceType}`] = {
                        hiveMetastoreUris: conn.hiveMetastoreUris,
                    };
                    break;
                }
                case DbSourceTypeData.Kafka: {
                    dataSource[`${formData.sourceType}`] = {
                        bootstrap: conn.bootstrap,
                        topicPatternsAllow: conn.topicPatternsAllow,
                    };
                    break;
                }
                case DbSourceTypeData.Mysql:
                case DbSourceTypeData.Postgres:
                case DbSourceTypeData.TiDB:
                case DbSourceTypeData.Hive: {
                    dataSource[`${formData.sourceType}`] = {
                        username: conn.username,
                        password: conn.password,
                        hostPort: conn.hostPort,
                        database: conn.database,
                        tablePatternAllow: conn.tablePatternAllow,
                        schemaPatternAllow: conn.schemaPatternAllow,
                    };
                    if (conn.jdbcParams !== '') {
                        dataSource[`${formData.sourceType}`] = {
                            ...dataSource[`${formData.sourceType}`],
                            jdbcParams: conn.jdbcParams,
                        };
                    }
                    break;
                }
                case DbSourceTypeData.trino:
                case DbSourceTypeData.presto: {
                    dataSource[`${formData.sourceType}`] = {
                        username: conn.username,
                        password: conn.password,
                        hostPort: conn.hostPort,
                        tablePatternAllow: conn.tablePatternAllow,
                        schemaPatternAllow: conn.schemaPatternAllow,
                    };
                    if (conn.catalog !== '') {
                        dataSource[`${formData.sourceType}`] = {
                            ...dataSource[`${formData.sourceType}`],
                            catalog: conn.catalog,
                        };
                    }
                    if (conn.schema !== '') {
                        dataSource[`${formData.sourceType}`] = {
                            ...dataSource[`${formData.sourceType}`],
                            schema: conn.schema,
                        };
                    }
                    if (conn.jdbcParams !== '') {
                        dataSource[`${formData.sourceType}`] = {
                            ...dataSource[`${formData.sourceType}`],
                            jdbcParams: conn.jdbcParams,
                        };
                    }
                    break;
                }
                case DbSourceTypeData.Oracle: {
                    dataSource[`${formData.sourceType}`] = {
                        username: conn.username,
                        password: conn.password,
                        tablePatternAllow: conn.tablePatternAllow,
                        schemaPatternAllow: conn.schemaPatternAllow,
                    };
                    if (conn.hostPort !== '') {
                        dataSource[`${formData.sourceType}`] = {
                            ...dataSource[`${formData.sourceType}`],
                            hostPort: conn.hostPort,
                        };
                    }
                    if (conn.serviceName !== '') {
                        dataSource[`${formData.sourceType}`] = {
                            ...dataSource[`${formData.sourceType}`],
                            serviceName: conn.serviceName,
                        };
                    }
                    if (conn.tnsName !== '') {
                        dataSource[`${formData.sourceType}`] = {
                            ...dataSource[`${formData.sourceType}`],
                            tnsName: conn.tnsName,
                        };
                    }
                    break;
                }
                case DbSourceTypeData.Pinot: {
                    dataSource[`${formData.sourceType}`] = {
                        username: conn.username,
                        password: conn.password,
                        hostPort: conn.hostPort,
                        tablePatternAllow: conn.tablePatternAllow,
                        schemaPatternAllow: conn.schemaPatternAllow,
                    };
                    break;
                }
                default: {
                    break;
                }
            }
            return dataSource;
        });
        return dataSources;
    };

    const onTestBtnClick = (ix: number) => {
        // check form data
        updateTestLoading(true);

        let conn: IDatasourceSourceInput = {
            dataCenter: formData.connections[ix].dataCenter,
        };

        switch (formData.sourceType) {
            case DbSourceTypeData.Postgres: {
                conn = {
                    ...conn,
                    postgres: {
                        username: formData.connections[ix].username || '',
                        password: formData.connections[ix].password || '',
                        hostPort: formData.connections[ix].hostPort || '',
                        database: formData.connections[ix].database || '',
                        jdbcParams: formData.connections[ix].jdbcParams || '',
                    },
                };
                break;
            }
            case DbSourceTypeData.Mysql: {
                conn = {
                    ...conn,
                    mysql: {
                        username: formData.connections[ix].username || '',
                        password: formData.connections[ix].password || '',
                        hostPort: formData.connections[ix].hostPort || '',
                        database: formData.connections[ix].database || '',
                        jdbcParams: formData.connections[ix].jdbcParams || '',
                    },
                };
                break;
            }
            case DbSourceTypeData.Hive: {
                conn = {
                    ...conn,
                    hive: {
                        username: formData.connections[ix].username || '',
                        password: formData.connections[ix].password || '',
                        hostPort: formData.connections[ix].hostPort || '',
                        database: formData.connections[ix].database || '',
                        jdbcParams: formData.connections[ix].jdbcParams || '',
                    },
                };
                break;
            }
            case DbSourceTypeData.TiDB: {
                conn = {
                    ...conn,
                    tiDB: {
                        username: formData.connections[ix].username || '',
                        password: formData.connections[ix].password || '',
                        hostPort: formData.connections[ix].hostPort || '',
                        database: formData.connections[ix].database || '',
                        jdbcParams: formData.connections[ix].jdbcParams || '',
                    },
                };
                break;
            }
            case DbSourceTypeData.Pinot: {
                conn = {
                    ...conn,
                    pinot: {
                        username: formData.connections[ix].username || '',
                        password: formData.connections[ix].password || '',
                        hostPort: formData.connections[ix].hostPort || '',
                    },
                };
                break;
            }
            case DbSourceTypeData.presto: {
                conn = {
                    ...conn,
                    presto: {
                        username: formData.connections[ix].username || '',
                        password: formData.connections[ix].password || '',
                        hostPort: formData.connections[ix].hostPort || '',
                        catalog: formData.connections[ix].catalog || '',
                        schema: formData.connections[ix].schema || '',
                        jdbcParams: formData.connections[ix].jdbcParams || '',
                    },
                };
                break;
            }
            case DbSourceTypeData.trino: {
                conn = {
                    ...conn,
                    trino: {
                        username: formData.connections[ix].username || '',
                        password: formData.connections[ix].password || '',
                        hostPort: formData.connections[ix].hostPort || '',
                        catalog: formData.connections[ix].catalog || '',
                        schema: formData.connections[ix].schema || '',
                        jdbcParams: formData.connections[ix].jdbcParams || '',
                    },
                };
                break;
            }
            case DbSourceTypeData.Oracle: {
                conn = {
                    ...conn,
                    oracle: {
                        username: formData.connections[ix].username || '',
                        password: formData.connections[ix].password || '',
                        hostPort: formData.connections[ix].hostPort || '',
                        serviceName: formData.connections[ix].serviceName || '',
                        tnsName: formData.connections[ix].tnsName || '',
                    },
                };
                break;
            }
            default: {
                break;
            }
        }

        const input = {
            connection: {
                ...conn,
            },
        };

        testDatasourceMutation({
            variables: {
                input,
            },
        })
            .then((res) => {
                console.log('testDatasourceMutation res....', res, input);
                if (!res) {
                    showRequestResult(500, 'Failed', true);
                    return;
                }
                if (res?.data?.testDatasource === true) {
                    showRequestResult(200, 'Success', true);
                } else {
                    showRequestResult(500, 'Failed', true);
                }
            })
            .catch((err) => {
                console.log('testDatasourceMutation error....', err, input);
                showRequestResult(500, 'Failed', true);
            })
            .finally(() => {
                updateTestLoading(false);
            });
    };

    const onSaveBtnClick = () => {
        const isOk = checkFormData();
        if (!isOk) {
            showValidateMsg('Exist some required value missing from form items !');
            return;
        }
        updateLoading(true);
        const dataSources: IDatasourceSourceInput[] = getDataSourceInputData();
        let input: DatasourceCreateInput = {
            name: formData.name,
            syncCDAPI: formData.syncCDAPI,
            create: formData.create,
            primaryConn: dataSources[0],
            group: formData.group,
            region: formData.region,
        };
        if (dataSources?.length > 1) {
            input = {
                ...input,
                gsbConn: dataSources[1],
            };
        }

        createDatasourceMutation({
            variables: {
                input,
            },
        })
            .then((res) => {
                console.log('createDatasourceMutation res....', res, input);
                const errors = res?.errors;
                if (errors) {
                    showRequestResult(500);
                    return;
                }
                onClose();
                showRequestResult(200);
                try {
                    localStorage.setItem('datahub.latestDataSource', JSON.stringify(input));
                } catch (e) {
                    console.log('save latest data source error', e);
                }
            })
            .catch((err) => {
                console.log('createDatasourceMutation error....', err, input);
                showRequestResult(500);
            })
            .finally(() => {
                updateLoading(false);
            });
    };

    const onCancelBtnClick = () => {
        updateDataSourceFormData(initData);
        onClose();
    };

    const onAddMoreBtnClick = () => {
        const info: IFormConnectionData = {
            id: ++count,
            username: '',
            password: '',
            hostPort: '',
            bootstrap: '',
            schemaPatternAllow: '',
            tablePatternAllow: '',
            topicPatternsAllow: '',
            hiveMetastoreUris: '',
            dataCenter: dataCenterList[0]?.value,
        };

        const connections = [...formData.connections];
        connections.push(info);

        const updatedFormData = {
            ...formData,
            connections,
        };
        updateDataSourceFormData(updatedFormData);
    };

    const removeConnectionItem = (index: number) => {
        const { connections } = formData;
        count--;
        if (count <= 1) {
            count = 1;
        }
        const filterConns = connections?.filter((item: IFormConnectionData, ix: number) => {
            if (ix === index) {
                return false;
            }
            return true;
        });
        const updatedFormData = {
            ...formData,
            connections: filterConns,
        };
        updateDataSourceFormData(updatedFormData);
    };

    const updateDataSourceConnections = (value: any, field: FormField, ix: number) => {
        const updatedData = {
            ...formData,
        };
        const { connections } = updatedData;
        const item = connections[ix];
        if (!item) {
            return;
        }
        item[field] = value;
        updateDataSourceFormData(updatedData);
    };

    const updateDataSourceBasicInfo = (value: any, field: FormField) => {
        const updateInfo = {};
        updateInfo[field] = value;
        const updatedData = {
            ...formData,
            ...updateInfo,
        };
        updateDataSourceFormData(updatedData);
    };

    const dataCenterChangeHandler = (value: any, field: FormField, ix: number) => {
        updateDataSourceConnections(value, field, ix);
    };

    const selectChangeHandler = (value: any, field) => {
        const updateInfo = {};
        updateInfo[field] = value;
        const updatedData = {
            ...formData,
            ...updateInfo,
        };
        updateDataSourceFormData(updatedData);
    };

    const getConnectionTitle = (index) => {
        return index < 1 ? 'Connection (Primary)' : 'Connection (GSB)';
    };

    const groupOptions = groupList?.map((item) => {
        return (
            <Option key={item.urn} value={item.urn}>
                {item.name}
            </Option>
        );
    });

    const regionOptions = regionList?.map((item) => {
        return (
            <Option key={item.value} value={item.value}>
                {item.label}
            </Option>
        );
    });

    const sourceTypeOptions = sourceTypeList?.map((item) => {
        return (
            <Option key={item.value} value={item.value}>
                {item.label}
            </Option>
        );
    });

    const dataCenterOptions = dataCenterList?.map((item) => {
        return (
            <Option key={item.value} value={item.value}>
                {item.label}
            </Option>
        );
    });

    const dataSourceBasic = () => {
        return (
            <Card title="Data Source">
                <Form.Item label="Type" rules={[{ required: true, message: 'Please input dataSource type!' }]}>
                    <Select
                        disabled={!formData.create}
                        defaultValue={formData.sourceType}
                        onChange={(value) => {
                            selectChangeHandler(value, FormField.sourceType);
                        }}
                    >
                        {sourceTypeOptions}
                    </Select>
                </Form.Item>
                <Form.Item
                    name="group"
                    label="Group"
                    rules={[{ required: true, message: 'Please choose dataSource Group!' }]}
                >
                    <Select
                        disabled={!formData.create}
                        defaultValue={groupList[0]?.urn}
                        onChange={(value) => {
                            selectChangeHandler(value, FormField.group);
                        }}
                    >
                        {groupOptions}
                    </Select>
                </Form.Item>
                <Form.Item
                    name="region"
                    label="Region"
                    rules={[{ required: true, message: 'Please choose dataSource Region!' }]}
                >
                    <Select
                        disabled={!formData.create}
                        defaultValue={formData.region}
                        onChange={(value) => {
                            selectChangeHandler(value, FormField.region);
                        }}
                    >
                        {regionOptions}
                    </Select>
                </Form.Item>
                <Form.Item
                    name="name"
                    label="Name"
                    rules={[{ required: true, message: 'Please input dataSource name!' }]}
                >
                    <Input
                        disabled={!formData.create}
                        placeholder="Please input dataSource name"
                        autoComplete="off"
                        defaultValue={formData.name}
                        onChange={(e) => updateDataSourceBasicInfo(e.target.value, FormField.name)}
                    />
                </Form.Item>
                <Form.Item
                    name="syncCDAPI"
                    label="Sync"
                    rules={[{ required: false, message: 'sync the Datasource to Custom Dashboard.' }]}
                >
                    <Checkbox
                        disabled={!enableSync || !formData.create}
                        defaultChecked={formData.syncCDAPI}
                        onChange={(e) => updateDataSourceBasicInfo(e.target.checked, FormField.syncCDAPI)}
                    >
                        Sync to Custom Dashboard
                    </Checkbox>
                </Form.Item>
            </Card>
        );
    };

    const getIceBergeConnection = (params: IFormConnectionData[]) => {
        return params.map((info: IFormConnectionData, index: number) => {
            return (
                <Card
                    style={{ marginTop: 16 }}
                    type="inner"
                    size="small"
                    title={getConnectionTitle(index)}
                    extra={index >= 1 && <DeleteOutlined onClick={() => removeConnectionItem(index)} />}
                    key={info.id}
                >
                    <Space direction="vertical" style={{ width: '100%', marginTop: 0 }}>
                        <Form.Item
                            name={`hiveUri_${info.id}`}
                            label="Uri"
                            rules={[{ required: true, message: 'Please input connection hive meta store uri!' }]}
                        >
                            <Input
                                type="text"
                                placeholder="Please input connection hive meta store uri"
                                autoComplete="off"
                                defaultValue={info.hiveMetastoreUris}
                                onChange={(e) =>
                                    updateDataSourceConnections(e.target.value, FormField.hiveMetastoreUris, index)
                                }
                            />
                        </Form.Item>
                        <Form.Item
                            name={`dataCenter_${info.id}`}
                            label="Data Center"
                            rules={[{ required: false, message: 'Please input connection data center!' }]}
                        >
                            <Select
                                defaultValue={info.dataCenter}
                                onChange={(value) => {
                                    dataCenterChangeHandler(value, FormField.dataCenter, index);
                                }}
                            >
                                {dataCenterOptions}
                            </Select>
                        </Form.Item>
                    </Space>
                </Card>
            );
        });
    };

    const getKafkaConnection = (params: IFormConnectionData[]) => {
        return params.map((info: IFormConnectionData, index: number) => {
            return (
                <Card
                    style={{ marginTop: 16 }}
                    type="inner"
                    size="small"
                    title={getConnectionTitle(index)}
                    extra={index >= 1 && <DeleteOutlined onClick={() => removeConnectionItem(index)} />}
                    key={info.id}
                >
                    <Space direction="vertical" style={{ width: '100%', marginTop: 0 }}>
                        <Form.Item
                            name={`topicPattern_${info.id}`}
                            label="Topic Pattern"
                            rules={[{ required: true, message: 'Please input connection topic pattern allow!' }]}
                        >
                            <Input
                                type="text"
                                placeholder="Please input connection topic pattern allow"
                                autoComplete="off"
                                defaultValue={info.topicPatternsAllow}
                                onChange={(e) =>
                                    updateDataSourceConnections(e.target.value, FormField.topicPatternsAllow, index)
                                }
                            />
                        </Form.Item>
                        <Form.Item
                            name={`bootstrapServer_${info.id}`}
                            label="Bootstrap Server"
                            rules={[{ required: true, message: 'Please input connection Bootstrap Server!' }]}
                        >
                            <Input
                                type="text"
                                placeholder="Please input connection bootstrap Server"
                                autoComplete="off"
                                defaultValue={info.bootstrap}
                                onChange={(e) =>
                                    updateDataSourceConnections(e.target.value, FormField.bootstrap, index)
                                }
                            />
                        </Form.Item>
                        <Form.Item
                            name={`dataCenter_${info.id}`}
                            label="Data Center"
                            rules={[{ required: false, message: 'Please input connection data center!' }]}
                        >
                            <Select
                                defaultValue={info.dataCenter}
                                onChange={(value) => {
                                    dataCenterChangeHandler(value, FormField.dataCenter, index);
                                }}
                            >
                                {dataCenterOptions}
                            </Select>
                        </Form.Item>
                    </Space>
                </Card>
            );
        });
    };
    const getUserNamePassForm = (info: IFormConnectionData, index: number) => {
        return (
            <>
                <Form.Item
                    name={`username_${info.id}`}
                    label="User Name"
                    rules={[{ required: true, message: 'Please input connection userName!' }]}
                >
                    {/* username as value ,will input issue */}
                    <Input
                        type="text"
                        placeholder="Please input connection username"
                        autoComplete="off"
                        defaultValue={info.username}
                        onChange={(e) => updateDataSourceConnections(e.target.value, FormField.username, index)}
                    />
                </Form.Item>
                <Form.Item
                    name={`password_${info.id}`}
                    label="Password"
                    rules={[{ required: true, message: 'Please input connection password!' }]}
                >
                    <Input.Password
                        placeholder="Please input connection password"
                        autoComplete="off"
                        defaultValue={info.password}
                        onChange={(e) => updateDataSourceConnections(e.target.value, FormField.password, index)}
                    />
                </Form.Item>
            </>
        );
    };

    const getHostPortForm = (info: IFormConnectionData, index: number) => {
        return (
            <>
                <Form.Item
                    name={`hostPort_${info.id}`}
                    label="Host port"
                    rules={[{ required: true, message: 'Please input connection host port!' }]}
                >
                    <Input
                        placeholder="Please input connection host port"
                        autoComplete="off"
                        defaultValue={info.hostPort}
                        onChange={(e) => updateDataSourceConnections(e.target.value, FormField.hostPort, index)}
                    />
                </Form.Item>
            </>
        );
    };

    const getDatabaseForm = (info: IFormConnectionData, index: number) => {
        return (
            <>
                <Form.Item
                    name={`database_${info.id}`}
                    label="Database"
                    rules={[{ required: databaseRequired, message: 'Please input connection database!' }]}
                >
                    <Input
                        placeholder="Please input connection database"
                        autoComplete="off"
                        defaultValue={info.database}
                        onChange={(e) => updateDataSourceConnections(e.target.value, FormField.database, index)}
                    />
                </Form.Item>
            </>
        );
    };

    const getOracleTNSForm = (info: IFormConnectionData, index: number) => {
        return (
            <>
                <Form.Item
                    name={`tnsName_${info.id}`}
                    label="TNSName"
                    rules={[{ required: true, message: 'Please input connection service name!' }]}
                >
                    <Input
                        placeholder="Please input connection TNS name"
                        autoComplete="off"
                        defaultValue={info.tnsName}
                        onChange={(e) => updateDataSourceConnections(e.target.value, FormField.tnsName, index)}
                    />
                </Form.Item>
            </>
        );
    };

    const getOracleServiceNameForm = (info: IFormConnectionData, index: number) => {
        return (
            <>
                {getHostPortForm(info, index)}
                <Form.Item
                    name={`serviceName_${info.id}`}
                    label="ServiceName"
                    rules={[{ required: true, message: 'Please input connection service name!' }]}
                >
                    <Input
                        placeholder="Please input connection service name"
                        autoComplete="off"
                        defaultValue={info.serviceName}
                        onChange={(e) => updateDataSourceConnections(e.target.value, FormField.serviceName, index)}
                    />
                </Form.Item>
            </>
        );
    };

    const getOracleForm = (info: IFormConnectionData, index: number) => {
        return (
            <>
                <Form.Item
                    name={`oracleTNSType_${info.id}`}
                    label="JDBCFormat"
                    rules={[{ required: true, message: 'Please select JDBC Format!' }]}
                >
                    <Select
                        defaultValue={formData.oracleTNSType}
                        onChange={(value) => {
                            selectChangeHandler(value, FormField.oracleTNSType);
                        }}
                    >
                        <Option key="tns" value="tns">
                            TNS
                        </Option>
                        <Option key="serviceName" value="serviceName">
                            ServiceName
                        </Option>
                    </Select>
                </Form.Item>
                {isOracleTNSType() && getOracleTNSForm(info, index)}
                {!isOracleTNSType() && getOracleServiceNameForm(info, index)}
            </>
        );
    };

    const getJDDBCParamsForm = (info: IFormConnectionData, index: number) => {
        return (
            <>
                <Form.Item
                    name={`jdbcParams_${info.id}`}
                    label="JDBC Params"
                    rules={[{ required: false, message: 'Please input JDBC Params!' }]}
                >
                    <Input
                        placeholder="Please input JDBC Params"
                        autoComplete="off"
                        defaultValue={info.jdbcParams}
                        onChange={(e) => updateDataSourceConnections(e.target.value, FormField.jdbcParams, index)}
                    />
                </Form.Item>
            </>
        );
    };
    const getTrinoForm = (info: IFormConnectionData, index: number) => {
        return (
            <>
                <Form.Item
                    name={`catalog_${info.id}`}
                    label="Catalog"
                    rules={[{ required: false, message: 'Please input connection catalog!' }]}
                >
                    <Input
                        placeholder="Please input connection catalog"
                        autoComplete="off"
                        defaultValue={info.catalog}
                        onChange={(e) => updateDataSourceConnections(e.target.value, FormField.catalog, index)}
                    />
                </Form.Item>
                <Form.Item
                    name={`schema_${info.id}`}
                    label="Schema"
                    rules={[{ required: false, message: 'Please input connection schema!' }]}
                >
                    <Input
                        placeholder="Please input connection schema"
                        autoComplete="off"
                        defaultValue={info.schema}
                        onChange={(e) => updateDataSourceConnections(e.target.value, FormField.schema, index)}
                    />
                </Form.Item>
            </>
        );
    };
    const getJDBCConnections = (params: IFormConnectionData[]) => {
        return params.map((info: IFormConnectionData, index: number) => {
            return (
                <Card
                    style={{ marginTop: 16 }}
                    type="inner"
                    size="small"
                    title={getConnectionTitle(index)}
                    extra={index >= 1 && <DeleteOutlined onClick={() => removeConnectionItem(index)} />}
                    key={info.id}
                >
                    <Space direction="vertical" style={{ width: '100%', marginTop: 0 }}>
                        <Form.Item
                            name={`dataCenter_${info.id}`}
                            label="Data Center"
                            rules={[{ required: false, message: 'Please input connection data center!' }]}
                        >
                            <Select
                                defaultValue={info.dataCenter}
                                onChange={(value) => {
                                    dataCenterChangeHandler(value, FormField.dataCenter, index);
                                }}
                            >
                                {dataCenterOptions}
                            </Select>
                        </Form.Item>
                        {isOracle() && getOracleForm(info, index)}

                        {isTiDB() && getHostPortForm(info, index)}
                        {isPostgres() && getHostPortForm(info, index)}
                        {isPinot() && getHostPortForm(info, index)}
                        {isHive() && getHostPortForm(info, index)}
                        {isTrino() && getHostPortForm(info, index)}
                        {isPresto() && getHostPortForm(info, index)}
                        {isMysql() && getHostPortForm(info, index)}

                        {getUserNamePassForm(info, index)}

                        {isPostgres() && getDatabaseForm(info, index)}
                        {isHive() && getDatabaseForm(info, index)}
                        {isTiDB() && getDatabaseForm(info, index)}
                        {isMysql() && getDatabaseForm(info, index)}
                        {isTrino() && getTrinoForm(info, index)}
                        {isPresto() && getTrinoForm(info, index)}

                        {isHive() && getJDDBCParamsForm(info, index)}
                        {isMysql() && getJDDBCParamsForm(info, index)}
                        {isPostgres() && getJDDBCParamsForm(info, index)}
                        {isTiDB() && getJDDBCParamsForm(info, index)}
                        {isTrino() && getJDDBCParamsForm(info, index)}
                        {isPresto() && getJDDBCParamsForm(info, index)}

                        <Form.Item
                            name={`tablePattern_${info.id}`}
                            label="Table Pattern"
                            rules={[{ required: false, message: 'Please input connection table pattern allow!' }]}
                        >
                            <Input
                                placeholder="Please input connection table pattern allow"
                                autoComplete="off"
                                defaultValue={info.tablePatternAllow}
                                onChange={(e) =>
                                    updateDataSourceConnections(e.target.value, FormField.tablePatternAllow, index)
                                }
                            />
                        </Form.Item>
                        <Form.Item
                            name={`schemaPattern_${info.id}`}
                            label="Schema Pattern"
                            rules={[{ required: false, message: 'Please input connection schema pattern allow!' }]}
                        >
                            <Input
                                type="text"
                                placeholder="Please input connection schema pattern allow"
                                autoComplete="off"
                                defaultValue={info.schemaPatternAllow}
                                onChange={(e) =>
                                    updateDataSourceConnections(e.target.value, FormField.schemaPatternAllow, index)
                                }
                            />
                        </Form.Item>
                        {enableSync && (
                            <Button
                                loading={testLoading}
                                onClick={() => onTestBtnClick(index)}
                                style={{ float: 'right' }}
                            >
                                Test Connection
                            </Button>
                        )}
                    </Space>
                </Card>
            );
        });
    };

    return (
        <>
            {loading && <Message type="loading" content="Loading..." style={messageStyle} />}
            {data && data.corpUser && (
                <Modal
                    title={title}
                    visible={visible}
                    onCancel={onClose}
                    width={900}
                    okText="Add"
                    style={{ paddingTop: 0 }}
                    footer={
                        <>
                            <Button
                                onClick={() => {
                                    onCancelBtnClick();
                                }}
                            >
                                Cancel
                            </Button>
                            <Button loading={saveLoading} onClick={onSaveBtnClick}>
                                Save
                            </Button>
                        </>
                    }
                >
                    <Space direction="vertical" style={{ width: '100%', marginTop: 0 }}>
                        <Form {...layout} form={form} name="control-ref">
                            {dataSourceBasic()}
                            <Card
                                style={{ marginTop: 16 }}
                                title="Connection Information"
                                extra={
                                    <>
                                        {formData.connections?.length < 2 && (
                                            <Button type="link" onClick={onAddMoreBtnClick}>
                                                Add GSB
                                            </Button>
                                        )}
                                    </>
                                }
                            >
                                {isInKafka() && getKafkaConnection(formData.connections)}
                                {isIceBerge() && getIceBergeConnection(formData.connections)}
                                {!isIceBerge() && !isInKafka() && getJDBCConnections(formData.connections)}
                            </Card>
                        </Form>
                    </Space>
                </Modal>
            )}
        </>
    );
}
