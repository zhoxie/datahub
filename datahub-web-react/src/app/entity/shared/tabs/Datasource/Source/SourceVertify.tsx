import { Button } from 'antd';
// import axios from 'axios';
import React from 'react';
import { Datasource } from '../../../../../../types.generated';
// import { capitalizeFirstLetter } from '../../../../../shared/capitalizeFirstLetter';
// import { showRequestResult } from './NotificationUtil';

export type Props = {
    datasource: Datasource;
    id: number;
};

export default function SourceVertify({ datasource: { connection }, id }: Props) {
    // const [showLoading, setLoading] = useState(false);
    // const platformName = capitalizeFirstLetter(connection?.category || 'null');
    // const conn = connection;
    // const sendDataSourceSaveReq = (data) => {
    //     axios
    //         .post('/testconnection?action=ingest', data, {
    //             headers: { 'Content-Type': 'application/json' },
    //         })
    //         .then((res) => {
    //             console.log(res);
    //             showRequestResult(res.status, undefined, true);
    //         })
    //         .catch((error) => {
    //             console.error(error);
    //             showRequestResult(500);
    //         })
    //         .finally(() => {
    //             setLoading(false);
    //         });
    // };

    // const onBtnClick = (e) => {
    //     e.preventDefault();
    //     e.stopPropagation();
    //     setLoading(true);
    //     let connect;
    //     conn?.some((item, index) => {
    //         if (index === id) {
    //             connect = item;
    //             return true;
    //         }
    //         return false;
    //     });
    //     if (!connect) {
    //         return;
    //     }
    //     const reqParam = {
    //         type: platformName,
    //         driver: connect.driver,
    //         url: connect.url,
    //         username: connect.username,
    //         password: connect.password,
    //     };
    //     console.log('test connection req param is:', reqParam);
    //     sendDataSourceSaveReq(reqParam);
    // };

    return (
        <>
            <Button>
                Test Connection
            </Button>
        </>
    );
}
