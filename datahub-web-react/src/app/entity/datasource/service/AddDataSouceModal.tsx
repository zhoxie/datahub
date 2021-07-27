import { DeleteOutlined } from '@ant-design/icons';
import { Button, Card, Form, Input, Modal, notification, Select, Space } from 'antd';
import axios from 'axios';
import React, { useState } from 'react';
import { FormField, IDataSourceConnection, IFormConnectionData, IFormData } from './dataSouceType';

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
        sourceType: '',
        category: '',
        dataCenter: '',
        connections: [
            {
                id: 1,
                cluster: 'PRIMARY',
                connName: '',
                connPwd: '',
                driver: '',
                url: '',
            },
        ],
    };
    const [formData, updateDataSourceFormData] = useState(initData);
    const [form] = Form.useForm();

    const showMessageByNotification = (msg: string) => {
        notification.open({
            message: 'Notification',
            description: msg,
            onClick: () => {},
        });
    };

    const showValidateMsg = (msg) => {
        showMessageByNotification(msg);
    };

    const showRequestResult = (status: number) => {
        let msg;
        if (status === 200) {
            msg = 'Success';
            onClose();
            showMessageByNotification(msg);
            window.location.reload();
        } else {
            msg = `Error for ${status}`;
            showMessageByNotification(msg);
        }
    };

    const sendDataSourceSaveReq = (data) => {
        axios
            .post('/entities?action=ingest', data, {
                headers: { 'Content-Type': 'application/json' },
            })
            .then((res) => {
                console.log(res);
                showRequestResult(res.status);
            })
            .catch((error) => {
                console.error(error);
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
            showValidateMsg('Exist Require Msg Missing , Pls Add/Choose ');
            return;
        }
        const urn = `urn:li:datasource:(urn:li:dataPlatform:${formData.sourceType},${formData.sourceName},PROD)`;
        const sourceKey = {
            'com.linkedin.metadata.key.DatasourceKey': {
                name: formData.sourceName,
                origin: 'PROD',
                platform: `urn:li:dataPlatform:${formData.sourceType}`,
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
                category: formData.category,
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

    const dataSourceBasic = () => {
        return (
            <Card title="Data Source">
                <Form.Item
                    name="sourceName"
                    label="Name"
                    rules={[{ required: true, message: 'Please input DataSource Name!' }]}
                >
                    <Input
                        autoComplete="off"
                        defaultValue={formData.sourceName}
                        onChange={(e) => updateDataSourceBasicInfo(e.target.value, FormField.sourceName)}
                    />
                </Form.Item>
                <Form.Item
                    name="sourceType"
                    label="Type"
                    rules={[{ required: true, message: 'Please input DataSource Type!' }]}
                >
                    <Input
                        autoComplete="off"
                        defaultValue={formData.sourceType}
                        onChange={(e) => updateDataSourceBasicInfo(e.target.value, FormField.sourceType)}
                    />
                </Form.Item>
                <Form.Item
                    name="category"
                    label="Category"
                    rules={[{ required: true, message: 'Please input DataSource Category!' }]}
                >
                    <Select
                        placeholder="Select a option"
                        onChange={(e) => updateDataSourceBasicInfo(e, FormField.category)}
                        allowClear
                        defaultValue={formData.category}
                    >
                        <Option value="category-1">Category-1</Option>
                        <Option value="category-2">Category-2</Option>
                    </Select>
                </Form.Item>
                <Form.Item
                    name="dataCenter"
                    label="Data Center"
                    rules={[{ required: true, message: 'Please input DataSource DataCenter!' }]}
                >
                    <Select
                        placeholder="Select a option"
                        onChange={(e) => updateDataSourceBasicInfo(e, FormField.dataCenter)}
                        allowClear
                        defaultValue={formData.dataCenter}
                    >
                        <Option value="DFW">DFW</Option>
                        <Option value="SJC">SJC</Option>
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
                            rules={[{ required: true, message: 'Please Choose DataSource Cluster!' }]}
                        >
                            <Select
                                placeholder="Select a option"
                                defaultValue={info.cluster}
                                onChange={(e) => updateDataSourceConnections(e, FormField.cluster, index)}
                                allowClear
                            >
                                <Option value="PRIMARY">PRIMARY</Option>
                                <Option value="GSB">GSB</Option>
                            </Select>
                        </Form.Item>
                        <Form.Item
                            name={`connName_${info.id}`}
                            label="userName"
                            rules={[{ required: true, message: 'Please Choose DataSource UserName!' }]}
                        >
                            {/* username as value ,will input issue */}
                            <Input
                                type="text"
                                autoComplete="off"
                                defaultValue={info.connName}
                                onChange={(e) => updateDataSourceConnections(e.target.value, FormField.connName, index)}
                            />
                        </Form.Item>
                        <Form.Item
                            name={`connPwd_${info.id}`}
                            label="Password"
                            rules={[{ required: true, message: 'Please Choose DataSource Password!' }]}
                        >
                            <Input
                                type="text"
                                autoComplete="off"
                                defaultValue={info.connPwd}
                                onChange={(e) => updateDataSourceConnections(e.target.value, FormField.connPwd, index)}
                            />
                        </Form.Item>
                        <Form.Item
                            name={`driver_${info.id}`}
                            label="Driver"
                            rules={[{ required: true, message: 'Please Choose DataSource Driver!' }]}
                        >
                            <Select
                                placeholder="Select a option"
                                defaultValue={info.driver}
                                onChange={(e) => updateDataSourceConnections(e, FormField.driver, index)}
                                allowClear
                            >
                                <Option value="com.mysql.jdbc.Driver">com.mysql.jdbc.Driver</Option>
                                <Option value="oracle.jdbc.driver.OracleDriver">oracle.jdbc.driver.OracleDriver</Option>
                                <Option value="org.postgresql.Driver">org.postgresql.Driver</Option>
                            </Select>
                        </Form.Item>
                        <Form.Item
                            name={`url_${info.id}`}
                            label="URL"
                            rules={[{ required: true, message: 'Please Choose DataSource URL!' }]}
                        >
                            <Input
                                type="text"
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
