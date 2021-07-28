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

export default function DatasourceVertify({ datasource: { platform, connections }, id }: Props) {
    const [showLoading, setLoading] = useState(false);
    const platformName = capitalizeFirstLetter(platform.name);
    const conn = connections?.connections?.map((item, index) => {
        return {
            ...item,
            connName: item?.username,
            connPwd: item?.password,
            id: index,
        };
    });
    const sendDataSourceSaveReq = (data) => {
        axios
            .post('/testconnection?action=ingest', data, {
                headers: { 'Content-Type': 'application/json' },
            })
            .then((res) => {
                console.log(res);
                showRequestResult(res.status, undefined, true);
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
        let connect;
        conn?.some((item, index) => {
            if (index === id) {
                connect = item;
                return true;
            }
            return false;
        });
        if (!connect) {
            return;
        }
        const reqParam = {
            type: platformName,
            driver: connect.driver,
            url: connect.url,
            username: connect.username,
            password: connect.password,
        };
        console.log('test connection req param is:', reqParam);
        sendDataSourceSaveReq(reqParam);
    };

    return (
        <>
            <Button loading={showLoading} data-id={id} onClick={(e) => onBtnClick(e)}>
                Test Connection
            </Button>
        </>
    );
}
