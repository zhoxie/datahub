import { DeleteOutlined } from '@ant-design/icons';
import { Button, Card, Form, Input, Modal, Space, Cascader, Select } from 'antd';
import React, { useState } from 'react';
import { FormField, IDatasourceSourceInput, IFormConnectionData, IFormData } from '../service/DataSouceType';
import { showMessageByNotification, showRequestResult } from '../service/NotificationUtil';
import {
    typeDrivers,
    DbSourceTypeData,
    groupList as defaultGroupList,
    dataCenterList,
    regionList,
} from '../service/FormInitValue';
import { useCreateDatasourceMutation } from '../../../../graphql/datasource.generated';
import { DatasourceCreateInput } from '../../../../types.generated';
import { useListGroupsQuery } from '../../../../graphql/group.generated';

const { Option } = Select;

type AddDataSourceModalProps = {
    visible: boolean;
    onClose: () => void;
    title: string;
    originData?: any;
};

const layout = {
    labelCol: { span: 4 },
    wrapperCol: { span: 20 },
};
export default function AddDataSourceModal({ visible, onClose, title, originData }: AddDataSourceModalProps) {
    let count = 1; // when originData exists ,show the edit
    const [createDatasourceMutation] = useCreateDatasourceMutation();
    const groupRes = useListGroupsQuery({
        variables: {
            input: {
                start: 1,
                count: 200,
            },
        },
    });
    const groupList = groupRes?.data?.listGroups?.groups ?? defaultGroupList;
    const [loading, updateLoading] = useState(false);

    const initData: IFormData = originData ?? {
        sourceType: typeDrivers[0].value,
        drive: typeDrivers[0]?.children[0]?.value,
        category: '',
        name: '',
        group: groupList[0]?.urn,
        region: regionList[0]?.value,
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

    const showValidateMsg = (msg) => {
        showMessageByNotification(msg);
    };

    const isInKafka = () => {
        return formData.sourceType === DbSourceTypeData.Kafka;
    };

    const isIceBerge = () => {
        return formData.sourceType === DbSourceTypeData.Iceberg;
    };

    const checkFormData = () => {
        if (!formData) {
            return false;
        }
        console.log('check form data....', formData);
        const { sourceType, name, category, group, region } = formData;
        const isBasicOK = !!sourceType && !!name && !!category && !!group && !!region;
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
                return item.topicPatternsAllow === '' || item.bootstrapServer === '';
            });
        } else {
            isOk = !formData.connections?.some((item) => {
                return item.username === '' || item.password === '' || item.hostPort === '' || item.database === '';
            });
        }
        return isOk;
    };

    const getDataSourceInputData = () => {
        const dataSources: IDatasourceSourceInput[] = formData.connections?.map((conn) => {
            let dataSource: IDatasourceSourceInput = {
                dataCenter: conn.dataCenter,
            };
            switch (formData.sourceType) {
                case DbSourceTypeData.Iceberg: {
                    dataSource = {
                        ...dataSource,
                        iceberg: {
                            hiveMetastoreUris: conn.hiveMetastoreUris || '',
                        },
                    };
                    break;
                }
                case DbSourceTypeData.Kafka: {
                    dataSource = {
                        ...dataSource,
                        kafka: {
                            topicPatternsAllow: conn.topicPatternsAllow,
                            bootstrap: conn.bootstrapServer || '',
                            schemaRegistryUrl: conn.schemaPatternAllow || '',
                        },
                    };
                    break;
                }
                case DbSourceTypeData.Mysql:
                case DbSourceTypeData.Pinot:
                case DbSourceTypeData.Postgres:
                case DbSourceTypeData.TiDB:
                case DbSourceTypeData.Oracle: {
                    dataSource[`${formData.sourceType}`] = {
                        username: conn.username || '',
                        password: conn.password || '',
                        hostPort: conn.hostPort || '',
                        database: conn.database || '',
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

    const onSaveBtnClick = () => {
        const isOk = checkFormData();
        if (!isOk) {
            showValidateMsg('Exist some required value missing from form items !');
            return;
        }
        updateLoading(true);
        const dataSources: IDatasourceSourceInput[] = getDataSourceInputData();
        console.log('dataSources..on save...', dataSources);
        let input: DatasourceCreateInput = {
            name: formData.name,
            category: formData.category,
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
        const id = ++count;
        const { sourceType } = formData;
        console.log('onAddMoreBtnClick category .....', sourceType);
        let info: IFormConnectionData;
        if (isInKafka()) {
            info = {
                id,
                username: '',
                password: '',
                hostPort: '',
                bootstrap: '',
                schemaPatternAllow: '',
                tablePatternAllow: '',
                topicPatternsAllow: '',
                hiveMetastoreUris: '',
                dataCenter: '',
            };
        } else {
            info = {
                id,
                username: '',
                password: '',
                hostPort: '',
                bootstrap: '',
                schemaPatternAllow: '',
                tablePatternAllow: '',
                topicPatternsAllow: '',
                hiveMetastoreUris: '',
                dataCenter: '',
            };
        }

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
        console.log('dataCenterChangeHandler....', value, field, ix);
        updateDataSourceConnections(value, field, ix);
    };

    const selectChangeHandler = (value: any, field) => {
        console.log('type change...', value, field);
        const updateInfo = {};
        if (field === FormField.sourceType) {
            const [sourceType, driver] = value;
            updateInfo[FormField.sourceType] = sourceType;
            updateInfo[FormField.driver] = driver ?? '';
        } else {
            updateInfo[field] = value;
        }
        const updatedData = {
            ...formData,
            ...updateInfo,
        };
        console.log('updatedData...', updatedData);
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
                    <Cascader
                        defaultValue={[formData.sourceType, formData.driver]}
                        options={typeDrivers}
                        onChange={(value) => {
                            selectChangeHandler(value, FormField.sourceType);
                        }}
                    />
                </Form.Item>
                <Form.Item
                    name="group"
                    label="Group"
                    rules={[{ required: true, message: 'Please choose dataSource Group!' }]}
                >
                    <Select
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
                        defaultValue={regionList[0]?.value}
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
                        placeholder="Please input dataSource name"
                        autoComplete="off"
                        defaultValue={formData.name}
                        onChange={(e) => updateDataSourceBasicInfo(e.target.value, FormField.name)}
                    />
                </Form.Item>
                <Form.Item
                    name="category"
                    label="Category"
                    rules={[{ required: true, message: 'Please input dataSource category!' }]}
                >
                    <Input
                        type="text"
                        placeholder="Please input dataSource category"
                        autoComplete="off"
                        defaultValue={formData.category}
                        onChange={(e) => updateDataSourceBasicInfo(e.target.value, FormField.category)}
                    />
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
                                defaultValue={dataCenterList[0]?.value}
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
                                defaultValue={dataCenterList[0]?.value}
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

                        <Form.Item
                            name={`hostPort_${info.id}`}
                            label="Host port"
                            rules={[{ required: true, message: 'Please input connection host port!' }]}
                        >
                            <Input
                                placeholder="Please input connection host port"
                                autoComplete="off"
                                defaultValue={info.database}
                                onChange={(e) => updateDataSourceConnections(e.target.value, FormField.hostPort, index)}
                            />
                        </Form.Item>
                        <Form.Item
                            name={`database_${info.id}`}
                            label="Database"
                            rules={[{ required: true, message: 'Please input connection database!' }]}
                        >
                            <Input
                                placeholder="Please input connection database"
                                autoComplete="off"
                                defaultValue={info.database}
                                onChange={(e) => updateDataSourceConnections(e.target.value, FormField.database, index)}
                            />
                        </Form.Item>
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
                        <Form.Item
                            name={`dataCenter_${info.id}`}
                            label="Data Center"
                            rules={[{ required: false, message: 'Please input connection data center!' }]}
                        >
                            <Select
                                defaultValue={dataCenterList[0]?.value}
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

    return (
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
                    <Button loading={loading} onClick={onSaveBtnClick}>
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
    );
}
