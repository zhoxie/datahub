export interface IFormConnectionData {
    id: number;
    bootstrapServer?: string;
    topicSplitField?: string;
    username?: string;
    password?: string;
    schemaPattern?: string;
    tablePattern?: string;
    topicPattern?: string;
    database?: string;
    hostPort?: string;
    hiveMetastoreUris?: string;
}

// data type pass to back-end
export interface IDataSourceConnection {
    bootstrapserver?: string;
    topicSplitField?: string;
    username?: string;
    password?: string;
    schemapattern?: string;
    tablepattern?: string;
    topicpattern?: string;
}

export interface IFormData {
    sourceType: string;
    driver: string;
    category: string;
    name: string;
    connections: IFormConnectionData[];
}

export enum FormField {
    name = 'name',
    sourceType = 'sourceType',
    category = 'category',
    username = 'username',
    password = 'password',
    hostPort = 'hostPort',
    database = 'database',
    schemaPattern = 'schemaPattern',
    tablePattern = 'tablePattern',
    topicPattern = 'topicPattern',
    bootstrapServer = 'bootstrapServer',
    topicSplitField = 'topicSplitField',
    hiveMetastoreUris = 'hiveMetastoreUris',
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
    iceberg?: IIcebergSourceInput;
    kafka?: IKafkaMetadataSourceInput;
    mysql?: IMysqlSourceInput;
    oracle?: IOracleSourceInput;
    postgres?: IPostgresSourceInput;
}
