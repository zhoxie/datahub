import { DeleteOutlined, ExclamationCircleOutlined } from '@ant-design/icons';
import { Button, Modal } from 'antd';
import axios from 'axios';
import React from 'react';
import { showRequestResult } from '../../shared/tabs/Datasource/Connection/NotificationUtil';

export type Props = {
    urn: string;
};

export default function DatasourceDelete({ urn }: Props) {
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

    const deleteDataSource = () => {
        const reqData = {
            entity: {
                value: {
                    'com.linkedin.metadata.snapshot.DatasourceSnapshot': {
                        aspects: [
                            {
                                'com.linkedin.common.Status': {
                                    removed: true,
                                },
                            },
                        ],
                        urn: `${urn}`,
                    },
                },
            },
        };
        sendDataSourceSaveReq(reqData);
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
