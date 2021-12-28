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

const driverData = {
    oracle: ['oracle.jdbc.driver.OracleDriver'],
    mysql: ['com.mysql.jdbc.Driver'],
    postgres: ['org.postgresql.Driver'],
};

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

enum DbSourceTypeData {
    Iceberg = 'iceberg',
    Kafka = 'kafka',
    Mysql = 'mysql',
    Oracle = 'oracle',
    Pinot = 'pinot',
    Postgres = 'postgres',
    TiDB = 'tidB',
}

export { initDataCenter, initCluster, driverData, typeDrivers, DbSourceTypeData };
