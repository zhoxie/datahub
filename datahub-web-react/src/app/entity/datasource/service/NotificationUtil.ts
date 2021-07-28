import { notification } from 'antd';
import { NotificationLevel } from './DataSouceType';

const showMessageByNotification = (msg: string, level: string = NotificationLevel.ERROR) => {
    notification[level]({
        message: 'Notification',
        description: msg,
        onClick: () => {},
    });
};

const showRequestResult = (status: number, tip?: string, stopRefresh?: boolean) => {
    let msg;
    if (status === 200) {
        msg = tip || 'Success';
        showMessageByNotification(msg, NotificationLevel.SUCCESS);
        if (!stopRefresh) {
            const timer = setTimeout(() => {
                clearTimeout(timer);
                window.location.reload();
            }, 1000);
        }
    } else {
        msg = tip || `Error for ${status}`;
        showMessageByNotification(msg, NotificationLevel.ERROR);
    }
};

export { showMessageByNotification, showRequestResult };
