import { EditOutlined } from '@ant-design/icons';
import { Button } from 'antd';
import React, { useState } from 'react';
import { Datasource } from '../../../../types.generated';
import { capitalizeFirstLetter } from '../../../shared/capitalizeFirstLetter';
import AddDataSourceModal from './AddDataSouceModal';

export type Props = {
    datasource: Datasource;
};

export default function DatasourceEdit({ datasource: { name, urn, type, platform, connections } }: Props) {
    const [showEditModal, setShowEditModal] = useState(false);
    const platformName = capitalizeFirstLetter(platform.name);
    const category = connections?.category;
    const dataCenter = connections?.dataCenter;
    const conn = connections?.connections?.map((item, index) => {
        return {
            ...item,
            connName: item?.username,
            connPwd: item?.password,
            id: index,
        };
    });
    const datasource = {
        sourceName: name,
        urn,
        type,
        sourceType: platformName,
        connections: conn,
        category,
        dataCenter,
    };

    return (
        <>
            <Button type="link" onClick={() => setShowEditModal(true)}>
                Edit
                <EditOutlined />
            </Button>
            {showEditModal && (
                <AddDataSourceModal
                    originData={datasource}
                    visible
                    title="Edit DataSource"
                    onClose={() => {
                        setShowEditModal(false);
                    }}
                />
            )}
        </>
    );
}
