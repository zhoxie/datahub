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

const typeDrivers = [
    {
        value: 'oracle',
        label: 'Oracle',
        children: [
            {
                value: 'oracle.jdbc.driver.OracleDriver',
                label: 'oracle.jdbc.driver.OracleDriver',
            },
        ],
    },
    {
        value: 'mysql',
        label: 'Mysql',
        children: [
            {
                value: 'com.mysql.jdbc.Driver',
                label: 'com.mysql.jdbc.Driver',
            },
        ],
    },
    {
        value: 'postgres',
        label: 'Postgres',
        children: [
            {
                value: 'org.postgresql.Driver',
                label: 'org.postgresql.Driver',
            },
        ],
    },
    {
        value: 'kafka',
        label: 'Kafka',
        children: [
            {
                label: 'Plaintext Kafka',
                value: 'plaintextKafka',
            },
            {
                label: 'Secured Kafka',
                value: 'securedKafka',
            },
        ],
    },
    {
        value: 'pinot',
        label: 'Pinot',
        children: [],
    },
    {
        value: 'iceberg',
        label: 'Iceberg',
        children: [],
    },
];

const groupList = [
    {
        label: 'MATS',
        value: 'mats',
    },
    {
        label: 'platform-admin',
        value: 'platform-admin',
    },
    {
        label: 'unified-ingestion',
        value: 'unified-ingestion',
    },
    {
        label: 'custom-dashboard',
        value: 'custom-dashboard',
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
    TiDB = 'tidB',
}

export { groupList, initDataCenter, initCluster, typeDrivers, dataCenterList, regionList, DbSourceTypeData };
