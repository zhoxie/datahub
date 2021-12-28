import { EditOutlined } from '@ant-design/icons';
import { Button } from 'antd';
import React, { useState } from 'react';
import { useGetDatasourceQuery } from '../../../../graphql/datasource.generated';
import { Datasource } from '../../../../types.generated';
import { IFormData } from '../service/DataSouceType';
import { typeDrivers } from '../service/FormInitValue';
import AddDataSourceModal from './AddDataSouceModal';

export type Props = {
    datasource: Datasource;
};

export default function DatasourceEdit({ datasource: { urn } }: Props) {
    const [showEditModal, setShowEditModal] = useState(false);
    const [loading, updateLoading] = useState(false);
    const res = useGetDatasourceQuery({
        variables: {
            urn,
        },
    });

    const dataSource = res?.data?.datasource;
    const typeName = dataSource?.connection?.connection?.__typename;
    const selectedType = typeDrivers.find((item) => {
        return typeName?.toLocaleLowerCase().includes(item.value);
    });
    const conn = dataSource?.connection?.connection;
    const originData: IFormData = {
        sourceType: selectedType?.value || '',
        name: dataSource?.name || '',
        category: dataSource?.connection?.category || '',
        driver: selectedType?.children[0]?.value || '',
        connections: [
            {
                ...conn,
            },
        ],
    };

    const updateModalStatus = () => {
        updateLoading(true);
        const timer = setTimeout(() => {
            clearTimeout(timer);
            updateLoading(false);
            setShowEditModal(true);
        }, 1000);
    };

    return (
        <>
            <Button type="link" onClick={updateModalStatus} loading={loading}>
                Edit
                <EditOutlined />
            </Button>
            {showEditModal && (
                <AddDataSourceModal
                    originData={originData}
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
