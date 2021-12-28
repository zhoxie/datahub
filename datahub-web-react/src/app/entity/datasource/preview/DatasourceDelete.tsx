import { DeleteOutlined, ExclamationCircleOutlined } from '@ant-design/icons';
import { Button, Modal } from 'antd';
import React from 'react';
import { useDeleteDatasourceMutation } from '../../../../graphql/datasource.generated';
import { showRequestResult } from '../service/NotificationUtil';

export type Props = {
    urn: string;
};

export default function DatasourceDelete({ urn }: Props) {
    const [deleteDatasourceMutation] = useDeleteDatasourceMutation();
    const sendDataSourceSaveReq = () => {
        deleteDatasourceMutation({
            variables: {
                urn,
            },
        })
            .then((res) => {
                console.log('datasource delete res...', res);
                const error = res.errors;
                if (error) {
                    console.log('datasource delete error...', error);
                    showRequestResult(500);
                    return;
                }
                showRequestResult(200);
            })
            .catch((error) => {
                console.log('datasource delete error...', error);
                showRequestResult(500);
            });
    };

    const deleteDataSource = () => {
        sendDataSourceSaveReq();
    };

    const onDeleteBtnClick = (e) => {
        e.preventDefault();
        e.stopPropagation();
        Modal.confirm({
            title: 'Confirm',
            icon: <ExclamationCircleOutlined />,
            content: `Are you confirm Delete ${urn}`,
            okText: 'OK',
            cancelText: 'Cancel',
            onOk: () => {
                deleteDataSource();
            },
        });
    };

    return (
        <>
            <Button type="link" onClick={(e) => onDeleteBtnClick(e)}>
                Delete
                <DeleteOutlined />
            </Button>
        </>
    );
}
