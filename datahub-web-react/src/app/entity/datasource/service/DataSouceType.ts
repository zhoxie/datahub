export interface IFormConnectionData {
    id?: number;

    bootstrap?: string;
    schemaRegistryUrl?: string;
    topicPatternsAllow?: string;
    topicPatternsDeny?: string;
    topicPatternsIgnoreCase?: string;

    username?: string;
    password?: string;
    hostPort?: string;
    database?: string;
    dataCenter?: string;
    databaseAlias?: string;
    tablePatternAllow?: string;
    tablePatternDeny?: string;
    tablePatternIgnoreCase?: string;
    schemaPatternAllow?: string;
    schemaPatternDeny?: string;
    schemaPatternIgnoreCase?: string;
    viewPatternAllow?: string;
    viewPatternDeny?: string;
    viewPatternIgnoreCase?: string;
    includeTables?: string;
    includeViews?: string;
    hiveMetastoreUris?: string;
}

export interface IFormData {
    sourceType: string;
    driver: string;
    category: string;
    name: string;
    group: string;
    region: string;
    dataCenter: string;
    connections: any[];
}

export enum FormField {
    bootstrap = 'bootstrap',
    category = 'category',
    database = 'database',
    dataCenter = 'dataCenter',
    driver = 'driver',
    group = 'group',
    hiveMetastoreUris = 'hiveMetastoreUris',
    hostPort = 'hostPort',
    name = 'name',
    password = 'password',
    region = 'region',
    schemaPatternAllow = 'schemaPatternAllow',
    sourceType = 'sourceType',
    tablePatternAllow = 'tablePatternAllow',
    topicPatternsAllow = 'topicPatternsAllow',
    topicSplitField = 'topicSplitField',
    username = 'username',
}

export enum NotificationLevel {
    SUCCESS = 'success',
    INFO = 'info',
    WARNING = 'warning',
    ERROR = 'error',
}

export interface IIcebergSourceInput {
    hiveMetastoreUris: string;
}
export interface IKafkaMetadataSourceInput {
    bootstrap: string;
    schemaRegistryUrl: string;
    topicPatternsAllow?: string;
    topicPatternsDeny?: string;
    topicPatternsIgnoreCase?: boolean;
}
export interface IBasicDataSourceInput {
    username: string;
    password: string;
    hostPort: string;
    database: string;
    databaseAlias?: string;
    tablePatternAllow?: string;
    tablePatternDeny?: string;
    tablePatternIgnoreCase?: boolean;
    schemaPatternAllow?: string;
    schemaPatternDeny?: string;
    schemaPatternIgnoreCase?: boolean;
    viewPatternAllow?: string;
    viewPatternDeny?: string;
    viewPatternIgnoreCase?: boolean;
    includeTables?: boolean;
    includeViews?: boolean;
}
export interface IMysqlSourceInput extends IBasicDataSourceInput {}
export interface IOracleSourceInput extends IBasicDataSourceInput {
    serviceName?: string;
}
export interface IPostgresSourceInput extends IBasicDataSourceInput {}

export interface IDatasourceSourceInput {
    dataCenter: string;
    iceberg?: IIcebergSourceInput;
    kafka?: IKafkaMetadataSourceInput;
    mysql?: IMysqlSourceInput;
    oracle?: IOracleSourceInput;
    postgres?: IPostgresSourceInput;
}
