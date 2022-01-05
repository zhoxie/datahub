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
    console.log('datasource edit res...', res);
    const dataSource = res?.data?.datasource;
    const typeName = dataSource?.primaryConn?.connection?.__typename;
    const selectedType = typeDrivers.find((item) => {
        return typeName?.toLocaleLowerCase().includes(item.value);
    });
    const conn = dataSource?.primaryConn;
    const gsbConn = dataSource?.gsbConn;
    const conns = [
        {
            id: 1,
            ...conn?.connection,
            dataCenter: conn?.dataCenter,
        },
    ];
    if (gsbConn) {
        conns.push({
            id: 2,
            ...gsbConn.connection,
            dataCenter: gsbConn?.dataCenter,
        });
    }
    const originData: IFormData = {
        sourceType: selectedType?.value || '',
        name: dataSource?.name || '',
        category: dataSource?.category || '',
        driver: selectedType?.children[0]?.value || '',
        group: dataSource?.group?.urn || '',
        region: dataSource?.region || '',
        connections: conns,
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
