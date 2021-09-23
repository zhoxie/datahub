import { DeleteOutlined } from '@ant-design/icons';
import { Alert, Button, Card, Form, Input, Modal, Select, Space, Cascader } from 'antd';
import axios from 'axios';
import React, { useState } from 'react';
import {
    FormField,
    IDataSourceConnection,
    IFormConnectionData,
    IFormData,
} from '../../shared/tabs/Datasource/Connection/DataSourceType';
import { showMessageByNotification, showRequestResult } from '../../shared/tabs/Datasource/Connection/NotificationUtil';
import { initDataCenter, initCluster, typeDrivers } from '../service/FormInitValue';
import { useAllDatasourceCategoriesQuery } from '../../../../graphql/datasourceCategory.generated';
import { capitalizeFirstLetter } from '../../../shared/capitalizeFirstLetter';

type AddDataSourceModalProps = {
    visible: boolean;
    onClose: () => void;
    title: string;
    originData?: any;
};

const { Option } = Select;

const layout = {
    labelCol: { span: 4 },
    wrapperCol: { span: 20 },
};
export default function AddDataSourceModal({ visible, onClose, title, originData }: AddDataSourceModalProps) {
    let count = 1;
    const initData: IFormData = originData ?? {
        sourceName: '',
        sourceType: typeDrivers[0].value,
        category: '',
        dataCenter: '',
        connections: [
            {
                id: 1,
                cluster: 'PRIMARY',
                connName: '',
                connPwd: '',
                driver: typeDrivers[0].children[0].value,
                url: '',
            },
        ],
    };

    const [formData, updateDataSourceFormData] = useState(initData);
    const [form] = Form.useForm();
    const categoryName = capitalizeFirstLetter(formData.category);

    const { data, loading, error } = useAllDatasourceCategoriesQuery({ variables: {} });

    if (error || (!loading && !error && !data)) {
        return <Alert type="error" message={error?.message || 'Entity failed to load'} />;
    }

    const showValidateMsg = (msg) => {
        showMessageByNotification(msg);
    };

    const sendDataSourceSaveReq = (reqData) => {
        axios
            .post('/entities?action=ingest', reqData, {
                headers: { 'Content-Type': 'application/json' },
            })
            .then((res) => {
                const status = res?.status;
                if (status === 200) {
                    onClose();
                }
                showRequestResult(status);
            })
            .catch((err) => {
                console.error(err);
                showRequestResult(500);
            });
    };

    const checkFormData = () => {
        if (!formData) {
            return false;
        }
        const { connections, sourceName, sourceType, category, dataCenter } = formData;
        const isBasicOK = !!sourceName && !!sourceType && !!category && !!dataCenter;
        const isConnectionOk = connections?.every((item) => {
            if (item.cluster && item.driver && item.connPwd && item.connPwd && item.url) {
                return true;
            }
            return false;
        });
        const isReady = isBasicOK && isConnectionOk;
        console.log(`isBasicOK:${isBasicOK}, isConnectionOk:${isConnectionOk},isReady: ${isReady}`);
        return isReady;
    };

    const onSaveBtnClick = () => {
        const isReady = checkFormData();
        if (!isReady) {
            showValidateMsg('Exist some required value missing from form items !');
            return;
        }
        const urn = `urn:li:datasource:(urn:li:datasourceCategory:${formData.category},${formData.sourceName},PROD)`;
        const sourceKey = {
            'com.linkedin.metadata.key.DatasourceKey': {
                name: formData.sourceName,
                origin: 'PROD',
                category: `urn:li:datasourceCategory:${formData.category}`,
            },
        };
        const connections: IDataSourceConnection[] = formData.connections?.map((conn) => {
            return {
                driver: conn.driver,
                url: conn.url,
                customProperties: {},
                cluster: {
                    'com.linkedin.datasource.DatasourceCluster': conn.cluster,
                },
                username: conn.connName,
                password: conn.connPwd,
            };
        });
        const connectionKey = {
            'com.linkedin.datasource.DatasourceConnections': {
                platform: `urn:li:dataPlatform:${formData.sourceType}`,
                dataCenter: formData.dataCenter,
                connections,
            },
        };
        const dataSouceData = {
            urn,
            aspects: [sourceKey, connectionKey],
        };
        const reqParam = {
            entity: {
                value: {
                    'com.linkedin.metadata.snapshot.DatasourceSnapshot': dataSouceData,
                },
            },
        };
        console.log('save btn....', reqParam, formData);
        sendDataSourceSaveReq(reqParam);
    };

    const onCancelBtnClick = () => {
        updateDataSourceFormData(initData);
        onClose();
    };

    const onAddMoreBtnClick = () => {
        const id = ++count;
        const info: IFormConnectionData = {
            cluster: '',
            connName: '',
            connPwd: '',
            driver: '',
            url: '',
            id,
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
        const updatedData = {
            ...formData,
        };
        updatedData[field] = value;
        updateDataSourceFormData(updatedData);
    };

    const handleTypeChange = (value) => {
        updateDataSourceBasicInfo(value[0], FormField.sourceType);
        for (let i = 0; i <= count; i++) {
            updateDataSourceConnections(value[1], FormField.driver, i);
        }
    };

    const dataSourceBasic = () => {
        return (
            <Card title="Data Source">
                <Form.Item
                    name="sourceName"
                    label="Name"
                    rules={[{ required: true, message: 'Please input dataSource name!' }]}
                >
                    <Input
                        placeholder="Please input dataSource name"
                        autoComplete="off"
                        defaultValue={formData.sourceName}
                        onChange={(e) => updateDataSourceBasicInfo(e.target.value, FormField.sourceName)}
                    />
                </Form.Item>
                <Form.Item
                    name="sourceType"
                    label="Type"
                    rules={[{ required: true, message: 'Please input dataSource type!' }]}
                >
                    {formData.sourceType && formData.connections[0].driver && (
                        <Cascader
                            options={typeDrivers}
                            defaultValue={[formData.sourceType, formData.connections[0].driver]}
                            onChange={handleTypeChange}
                        />
                    )}
                </Form.Item>
                <Form.Item
                    name="category"
                    label="Category"
                    rules={[{ required: true, message: 'Please choose dataSource category!' }]}
                >
                    {loading && (
                        <Select
                            placeholder="Select an option for category"
                            onChange={(e) => updateDataSourceBasicInfo(e, FormField.category)}
                            allowClear
                            defaultValue={formData.category}
                            disabled
                        >
                            <Option value="loading">loading</Option>
                        </Select>
                    )}
                    {data && data.allDatasourceCategories && (
                        <Select
                            placeholder="Select an option for category"
                            onChange={(e) => updateDataSourceBasicInfo(e, FormField.category)}
                            allowClear
                            defaultValue={categoryName}
                        >
                            {data.allDatasourceCategories.categories?.map((item) => {
                                return <Option value={item?.name || 'null'}>{item?.displayName || 'null'}</Option>;
                            })}
                        </Select>
                    )}
                </Form.Item>
                <Form.Item
                    name="dataCenter"
                    label="Data Center"
                    rules={[{ required: true, message: 'Please choose dataSource dataCenter!' }]}
                >
                    <Select
                        placeholder="Select an option for data center"
                        onChange={(e) => updateDataSourceBasicInfo(e, FormField.dataCenter)}
                        allowClear
                        defaultValue={formData.dataCenter}
                    >
                        {initDataCenter?.map((item) => {
                            return <Option value={item.value}>{item.label}</Option>;
                        })}
                    </Select>
                </Form.Item>
            </Card>
        );
    };

    const connections = (params: IFormConnectionData[]) => {
        return params.map((info: IFormConnectionData, index: number) => {
            return (
                <Card
                    style={{ marginTop: 16 }}
                    type="inner"
                    size="small"
                    title={`Connection Info ${index + 1}`}
                    extra={index >= 1 && <DeleteOutlined onClick={() => removeConnectionItem(index)} />}
                    key={info.id}
                >
                    <Space direction="vertical" style={{ width: '100%', marginTop: 0 }}>
                        <Form.Item
                            name={`cluster_${info.id}`}
                            label="Cluster"
                            rules={[{ required: true, message: 'Please choose connection cluster!' }]}
                        >
                            <Select
                                placeholder="Select an option for cluster"
                                defaultValue={info.cluster}
                                onChange={(e) => updateDataSourceConnections(e, FormField.cluster, index)}
                                allowClear
                            >
                                {initCluster?.map((item) => {
                                    return <Option value={item.value}>{item.label}</Option>;
                                })}
                            </Select>
                        </Form.Item>
                        <Form.Item
                            name={`connName_${info.id}`}
                            label="userName"
                            rules={[{ required: true, message: 'Please input connection userName!' }]}
                        >
                            {/* username as value ,will input issue */}
                            <Input
                                type="text"
                                placeholder="Please input connection username"
                                autoComplete="off"
                                defaultValue={info.connName}
                                onChange={(e) => updateDataSourceConnections(e.target.value, FormField.connName, index)}
                            />
                        </Form.Item>
                        <Form.Item
                            name={`connPwd_${info.id}`}
                            label="Password"
                            rules={[{ required: true, message: 'Please input connection password!' }]}
                        >
                            <Input.Password
                                placeholder="Please input connection password"
                                autoComplete="off"
                                defaultValue={info.connPwd}
                                onChange={(e) => updateDataSourceConnections(e.target.value, FormField.connPwd, index)}
                            />
                        </Form.Item>
                        <Form.Item
                            name={`url_${info.id}`}
                            label="URL"
                            rules={[{ required: true, message: 'Please Choose DataSource URL!' }]}
                        >
                            <Input
                                type="text"
                                placeholder="Please input connection url"
                                autoComplete="off"
                                defaultValue={info.url}
                                onChange={(e) => updateDataSourceConnections(e.target.value, FormField.url, index)}
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
                    <Card
                        style={{ marginTop: 16 }}
                        title="Connection Information"
                        extra={
                            <>
                                <Button type="link" onClick={onAddMoreBtnClick}>
                                    Add More
                                </Button>
                            </>
                        }
                    >
                        {connections(formData.connections)}
                    </Card>
                </Form>
            </Space>
        </Modal>
    );
}
