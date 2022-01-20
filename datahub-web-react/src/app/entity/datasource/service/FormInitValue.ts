const initDataCenter = [
    {
        label: 'DFW',
        value: 'DFW',
    },
    {
        label: 'SJC',
        value: 'SJC',
    },
];

const initCluster = [
    {
        label: 'PRIMARY',
        value: 'PRIMARY',
    },
    {
        label: 'GSB',
        value: 'GSB',
    },
];

const sourceTypeList = [
    {
        label: 'Mysql',
        value: 'mysql',
    },
    {
        label: 'Oracle',
        value: 'oracle',
    },
    {
        label: 'Postgres',
        value: 'postgres',
    },
    {
        label: 'TiDB',
        value: 'tiDB',
    },
    {
        label: 'presto',
        value: 'presto',
    },
    {
        label: 'trino',
        value: 'trino',
    },
    {
        label: 'hive',
        value: 'hive',
    },
    {
        label: 'Kafka',
        value: 'kafka',
    },
    {
        label: 'Pinot',
        value: 'pinot',
    },
    {
        label: 'Iceberg',
        value: 'iceberg',
    },
];

const groupList = [
    {
        name: 'None',
        urn: 'urn:li:corpGroup:none',
    },
];

const regionList = [
    {
        label: 'AMER',
        value: 'AMER',
    },
    {
        label: 'CANADA',
        value: 'CANADA',
    },
    {
        label: 'EMEA',
        value: 'EMEA',
    },
    {
        label: 'GERMANY',
        value: 'GERMANY',
    },
];

const dataCenterList = [
    {
        label: 'SJC02',
        value: 'SJC02',
    },
    {
        label: 'DFW01',
        value: 'DFW01',
    },
    {
        label: 'DFW02',
        value: 'DFW02',
    },
    {
        label: 'JFK01',
        value: 'JFK01',
    },
    {
        label: 'LHR03',
        value: 'LHR03',
    },
    {
        label: 'AMS01',
        value: 'AMS01',
    },
    {
        label: 'AMS02',
        value: 'AMS02',
    },
    {
        label: 'FRA01',
        value: 'FRA01',
    },
    {
        label: 'SIN01',
        value: 'SIN01',
    },
    {
        label: 'NTR03',
        value: 'NTR03',
    },
    {
        label: 'SYD01',
        value: 'SYD01',
    },
    {
        label: 'BOM01',
        value: 'BOM01',
    },
    {
        label: 'YYZ02',
        value: 'YYZ02',
    },
    {
        label: 'YUL01',
        value: 'YUL01',
    },
    {
        label: 'PEK03',
        value: 'PEK03',
    },
];

enum DbSourceTypeData {
    Iceberg = 'iceberg',
    Kafka = 'kafka',
    Mysql = 'mysql',
    Oracle = 'oracle',
    Pinot = 'pinot',
    Postgres = 'postgres',
    TiDB = 'tiDB',
    Hive = 'hive',
    presto = 'presto',
    trino = 'trino',
}

export { groupList, initDataCenter, initCluster, sourceTypeList, dataCenterList, regionList, DbSourceTypeData };
