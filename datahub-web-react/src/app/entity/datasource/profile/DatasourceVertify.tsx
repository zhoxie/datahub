import { Button } from 'antd';
import axios from 'axios';
import React, { useState } from 'react';
import { Datasource } from '../../../../types.generated';
import { capitalizeFirstLetter } from '../../../shared/capitalizeFirstLetter';
import { showRequestResult } from '../service/NotificationUtil';

export type Props = {
    datasource: Datasource;
    id: number;
};

export default function DatasourceVertify({ datasource: { name, urn, type, platform, connections }, id }: Props) {
    const [showLoading, setLoading] = useState(false);
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
            })
            .finally(() => {
                setLoading(false);
            });
    };

    const onBtnClick = (e) => {
        e.preventDefault();
        e.stopPropagation();
        setLoading(true);
        console.log(`test dataSource....`, datasource, showLoading, id);
        sendDataSourceSaveReq({});
    };

    console.log(platform, category, type, urn, name, platformName);

    return (
        <>
            <Button loading={showLoading} data-id={id} onClick={(e) => onBtnClick(e)}>
                Test Connection
            </Button>
        </>
    );
}
