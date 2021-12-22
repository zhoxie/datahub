export interface IFormConnectionData {
    id: number;
    cluster: string;
    sourceName: string;
    bootstrapServer?: string;
    connName?: string;
    connPwd?: string;
    schemaPattern?: string;
    tablePattern?: string;
    topicPattern?: string;
    url?: string;
}

// data type pass to back-end
export interface IDataSourceConnection {
    cluster: { [key: string]: string };
    sourcename: string;
    bootstrapserver?: string;
    username?: string;
    password?: string;
    schemapattern?: string;
    tablepattern?: string;
    topicpattern?: string;
    url?: string;
}

export interface IFormData {
    sourceType: string;
    driver: string;
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
    schemaPattern = 'schemaPattern',
    tablePattern = 'tablePattern',
    topicPattern = 'topicPattern',
    bootstrapServer = 'bootstrapServer',
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

export enum NotificationLevel {
    SUCCESS = 'success',
    INFO = 'info',
    WARNING = 'warning',
    ERROR = 'error',
}
