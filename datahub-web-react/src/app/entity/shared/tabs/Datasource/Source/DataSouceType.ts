export interface IFormConnectionData {
    cluster: string;
    connName: string;
    connPwd: string;
    driver: string;
    url: string;
    id: number;
}

export interface IFormData {
    sourceName: string;
    sourceType: string;
    category: string;
    dataCenter: string;
    connections: IFormConnectionData[];
}

export enum FormField {
    sourceName = 'sourceName',
    sourceType = 'sourceType',
    category = 'category',
    dataCenter = 'dataCenter',
    cluster = 'cluster',
    connName = 'connName',
    connPwd = 'connPwd',
    driver = 'driver',
    url = 'url',
}

export interface IDataSourceAddData {
    'com.linkedin.metadata.snapshot.DatasourceSnapshot': IDataSourceAddEntity;
}

export interface IDataSourceAddEntity {
    aspects: [];
    urn: string;
}
export interface IDataSourceKey {
    name: string;
    origin: string;
    platform: string;
}
export interface IDataSourceConnectionKey {
    category: string;
    dataCenter: string;
    connections: IDataSourceConnection[];
}
export interface IDataSourceConnection {
    cluster: { [key: string]: string };
    password: string;
    driver: string;
    url: string;
    username: string;
}

export enum NotificationLevel {
    SUCCESS = 'success',
    INFO = 'info',
    WARNING = 'warning',
    ERROR = 'error',
}
