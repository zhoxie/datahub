import { DeleteOutlined } from '@ant-design/icons';
import { Button, Card, Form, Input, Modal, Space, Cascader } from 'antd';
import React, { useState } from 'react';
import { FormField, IDatasourceSourceInput, IFormConnectionData, IFormData } from '../service/DataSouceType';
import { showMessageByNotification, showRequestResult } from '../service/NotificationUtil';
import { typeDrivers, DbSourceTypeData } from '../service/FormInitValue';
import { useCreateDatasourceMutation } from '../../../../graphql/datasource.generated';
import { DatasourceCreateInput } from '../../../../types.generated';

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

    const initData: IFormData = originData ?? {
        sourceType: typeDrivers[0].value,
        category: '',
        name: '',
        connections: [
            {
                id: 1,
                username: '',
                password: '',
                hostPort: '',
                bootstrap: '',
                schemaPatternAllow: '',
                tablePatternAllow: '',
                topicPatternAllow: '',
                hiveMetastoreUris: '',
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
        const { sourceType, name, category } = formData;
        const isBasicOK = !!sourceType && !!name && !!category;
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
        let dataSource: IDatasourceSourceInput = {};
        formData.connections?.forEach((conn) => {
            switch (formData.sourceType) {
                case DbSourceTypeData.Iceberg: {
                    dataSource = {
                        iceberg: {
                            hiveMetastoreUris: conn.hiveMetastoreUris || '',
                        },
                    };
                    break;
                }
                case DbSourceTypeData.Kafka: {
                    dataSource = {
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
        });
        return dataSource;
    };

    const onSaveBtnClick = () => {
        const isOk = checkFormData();
        if (!isOk) {
            showValidateMsg('Exist some required value missing from form items !');
            return;
        }
        const reqParam: IDatasourceSourceInput = getDataSourceInputData();
        const input: DatasourceCreateInput = {
            name: formData.name,
            category: formData.category,
            connection: reqParam,
        };
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
                console.error(err);
                showRequestResult(500);
            });
    };

    const onCancelBtnClick = () => {
        updateDataSourceFormData(initData);
        onClose();
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

    const handleTypeChange = (value) => {
        const updatedData = {
            ...formData,
        };
        const [sourceType] = value;
        updatedData[FormField.sourceType] = sourceType;
        updateDataSourceFormData(updatedData);
    };

    const dataSourceBasic = () => {
        return (
            <Card title="Data Source">
                <Form.Item label="Type" rules={[{ required: true, message: 'Please input dataSource type!' }]}>
                    <Cascader
                        defaultValue={[formData.sourceType, formData.driver]}
                        options={typeDrivers}
                        onChange={handleTypeChange}
                    />
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
                    title="Connection Info"
                    extra={index >= 1 && <DeleteOutlined onClick={() => removeConnectionItem(index)} />}
                    key={info.id}
                >
                    <Space direction="vertical" style={{ width: '100%', marginTop: 0 }}>
                        <Form.Item
                            name={`hiveUri_${info.id}`}
                            label="hive Metastore Uri"
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
                    title="Connection Info"
                    extra={index >= 1 && <DeleteOutlined onClick={() => removeConnectionItem(index)} />}
                    key={info.id}
                >
                    <Space direction="vertical" style={{ width: '100%', marginTop: 0 }}>
                        <Form.Item
                            name={`topicPatternAllow_${info.id}`}
                            label="Topic PatternAllow"
                            rules={[{ required: true, message: 'Please input connection topic PatternAllow!' }]}
                        >
                            <Input
                                type="text"
                                placeholder="Please input connection topic PatternAllow"
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
                                placeholder="Please input connection bootstrapServer"
                                autoComplete="off"
                                defaultValue={info.bootstrap}
                                onChange={(e) =>
                                    updateDataSourceConnections(e.target.value, FormField.bootstrapServer, index)
                                }
                            />
                        </Form.Item>
                        {/* <Form.Item
                            name={`topicSplitField_${info.id}`}
                            label="Topic Split Field"
                            rules={[{ required: false, message: 'Please input topic split field!' }]}
                        >
                            <Input
                                type="text"
                                placeholder="Please input topic split field!"
                                autoComplete="off"
                                defaultValue={info.topicSplitField}
                                onChange={(e) =>
                                    updateDataSourceConnections(e.target.value, FormField.bootstrapServer, index)
                                }
                            />
                        </Form.Item> */}
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
                    title="Connection Info"
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
                            name={`tablePatternAllow_${info.id}`}
                            label="Table PatternAllow"
                            rules={[{ required: false, message: 'Please input connection table PatternAllow!' }]}
                        >
                            <Input
                                placeholder="Please input connection table PatternAllow"
                                autoComplete="off"
                                defaultValue={info.tablePatternAllow}
                                onChange={(e) =>
                                    updateDataSourceConnections(e.target.value, FormField.tablePatternAllow, index)
                                }
                            />
                        </Form.Item>
                        <Form.Item
                            name={`schemaPatternAllow_${info.id}`}
                            label="Schema PatternAllow"
                            rules={[{ required: false, message: 'Please input connection schema PatternAllow!' }]}
                        >
                            <Input
                                type="text"
                                placeholder="Please input connection url"
                                autoComplete="off"
                                defaultValue={info.schemaPatternAllow}
                                onChange={(e) =>
                                    updateDataSourceConnections(e.target.value, FormField.schemaPatternAllow, index)
                                }
                            />
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
            width={800}
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
                    <Button onClick={onSaveBtnClick}>Save</Button>
                </>
            }
        >
            <Space direction="vertical" style={{ width: '100%', marginTop: 0 }}>
                <Form {...layout} form={form} name="control-ref">
                    {dataSourceBasic()}
                    <Card style={{ marginTop: 16 }} title="Connection Information">
                        {isInKafka() && getKafkaConnection(formData.connections)}
                        {isIceBerge() && getIceBergeConnection(formData.connections)}
                        {!isIceBerge() && !isInKafka() && getJDBCConnections(formData.connections)}
                    </Card>
                </Form>
            </Space>
        </Modal>
    );
}
