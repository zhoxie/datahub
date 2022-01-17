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
    tnsName?: string;
    serviceName?: string;
    catalog?: string;
    schema?: string;
    jdbcParams?: string;
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
    name: string;
    syncCDAPI: boolean;
    create: boolean;
    group: string;
    region: string;
    connections: any[];
    oracleTNSType: string;
}

export enum FormField {
    bootstrap = 'bootstrap',
    database = 'database',
    tnsName = 'tnsName',
    jdbcParams = 'jdbcParams',
    syncCDAPI = 'syncCDAPI',
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
    serviceName = 'serviceName',
    catalog = 'catalog',
    schema = 'schema',
    oracleTNSType = 'oracleTNSType',
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
export interface ITiDBSourceInput extends IBasicDataSourceInput {}
export interface IHiveSourceInput extends IBasicDataSourceInput {}
export interface IPrestoSourceInput extends IBasicDataSourceInput {}
export interface ITrinoSourceInput extends IBasicDataSourceInput {}
export interface IPinotSourceInput extends IBasicDataSourceInput {}

export interface IDatasourceSourceInput {
    dataCenter: string;
    iceberg?: IIcebergSourceInput;
    kafka?: IKafkaMetadataSourceInput;
    mysql?: IMysqlSourceInput;
    oracle?: IOracleSourceInput;
    postgres?: IPostgresSourceInput;
    tiDB?: ITiDBSourceInput;
    hive?: IHiveSourceInput;
    presto?: IPrestoSourceInput;
    trino?: ITrinoSourceInput;
    pinot?: IPinotSourceInput;
}
