import { EditOutlined } from '@ant-design/icons';
import { Button } from 'antd';
import React, { useState } from 'react';
import { Datasource } from '../../../../types.generated';
import AddDataSourceModal from './AddDataSouceModal';

export type Props = {
    datasource: Datasource;
};

export default function DatasourceEdit({ datasource: { name, urn, type } }: Props) {
    const [showEditModal, setShowEditModal] = useState(false);
    const platformName = 'null';
    const categoryName = '';
    const datasource = {
        sourceName: name,
        urn,
        type,
        sourceType: platformName,
        connections: {},
        category: categoryName,
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
