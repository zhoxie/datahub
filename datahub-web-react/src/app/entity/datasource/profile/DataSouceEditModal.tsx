import { Alert, Button, Card, Form, Input, Modal, Select, Space, Cascader } from 'antd';
import React, { useState } from 'react';
import { initDataCenter, initCluster, typeDrivers } from '../service/FormInitValue';
import { useAllDatasourceCategoriesQuery } from '../../../../graphql/datasourceCategory.generated';

type DataSouceEditModalProps = {
    visible: boolean;
    onClose: () => void;
    title: string;
};

const { Option } = Select;

const layout = {
    labelCol: { span: 4 },
    wrapperCol: { span: 20 },
};
export default function DataSouceEditModal({ visible, onClose, title }: DataSouceEditModalProps) {
    const [sourceName, setName] = useState('');
    const [sourceCategory, setCategory] = useState('');

    const [sourceDataCenter, setDataCenter] = useState('SJC');

    const [cluster, setCluster] = useState(initCluster[0]);

    const { data, loading, error } = useAllDatasourceCategoriesQuery({ variables: {} });

    if (error || (!loading && !error && !data)) {
        return <Alert type="error" message={error?.message || 'Entity failed to load'} />;
    }

    const handleTypeChange = (value) => {
        console.log(value);
    };

    const onSaveBtnClick = () => {};
    const onAddMoreBtnClick = () => {};
    const onCancelBtnClick = () => {
        onClose();
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
                <Form {...layout} name="control-ref">
                    <Card title="Data Source">
                        <Form.Item
                            name="sourceName"
                            label="Name"
                            rules={[{ required: true, message: 'Please input dataSource name!' }]}
                        >
                            <Input
                                placeholder="Please input dataSource name"
                                autoComplete="off"
                                value={sourceName}
                                onChange={(e) => setName(e.target.value)}
                            />
                        </Form.Item>
                        <Form.Item
                            name="sourceType"
                            label="Type"
                            rules={[{ required: true, message: 'Please input dataSource type!' }]}
                        >
                            <Cascader
                                options={typeDrivers}
                                onChange={handleTypeChange}
                                placeholder="Please select DataSource type and driver"
                            />
                        </Form.Item>
                        <Form.Item
                            name="category"
                            label="Category"
                            rules={[{ required: true, message: 'Please choose dataSource category!' }]}
                        >
                            {loading && (
                                <Select
                                    placeholder="Select an option for category"
                                    allowClear
                                    value={sourceCategory}
                                    disabled
                                >
                                    <Option value="loading">loading</Option>
                                </Select>
                            )}
                            {data && data.allDatasourceCategories && (
                                <Select
                                    placeholder="Select an option for category"
                                    onChange={setCategory}
                                    allowClear
                                    value={sourceCategory}
                                >
                                    {data.allDatasourceCategories.categories?.map((item) => {
                                        return (
                                            <Option value={item?.name || 'null'}>{item?.displayName || 'null'}</Option>
                                        );
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
                                onChange={setDataCenter}
                                allowClear
                                value={sourceDataCenter}
                            >
                                {initDataCenter?.map((item) => {
                                    return <Option value={item.value}>{item.label}</Option>;
                                })}
                            </Select>
                        </Form.Item>
                    </Card>
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
                        <Card style={{ marginTop: 16 }} type="inner" size="small" title="Connection Info">
                            <Space direction="vertical" style={{ width: '100%', marginTop: 0 }}>
                                <Form.Item
                                    name="cluster"
                                    label="Cluster"
                                    rules={[{ required: true, message: 'Please choose connection cluster!' }]}
                                >
                                    <Select
                                        placeholder="Select an option for cluster"
                                        value={cluster}
                                        onChange={setCluster}
                                        allowClear
                                    >
                                        {initCluster?.map((item) => {
                                            return <Option value={item.value}>{item.label}</Option>;
                                        })}
                                    </Select>
                                </Form.Item>
                            </Space>
                        </Card>
                    </Card>
                </Form>
            </Space>
        </Modal>
    );
}
